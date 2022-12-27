package com.gps_alarm.ui.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.data.gpsAlarm.dto.AddressElement
import com.data.gpsAlarm.local.LocalDatastore
import com.data.gpsAlarm.mapper.mapper
import com.domain.gpsAlarm.dto.Addresses
import com.domain.gpsAlarm.dto.Geocode
import com.domain.gpsAlarm.usecase.MapsApiUseCase
import com.gps_alarm.base.BaseVM
import com.gps_alarm.paging.room.AppDatabase
import com.gps_alarm.paging.source.GeocodeSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.DataStatus
import util.coroutineIOCallbackTo
import javax.inject.Inject

@HiltViewModel
class AlarmVM @Inject constructor(
    private val mapsApiUseCase: MapsApiUseCase,
    private val localDatastore: LocalDatastore,
    private val database: AppDatabase
) : BaseVM() {
    val geocodeList = MutableStateFlow<DataStatus<List<Addresses>>>(DataStatus.Loading)
    val geocode = MutableStateFlow<DataStatus<Geocode?>>(DataStatus.Loading)
    val addressDao = database.addressDao()

    @ExperimentalPagingApi
    val pager = Pager(
        config = PagingConfig(pageSize = 50),
        remoteMediator = GeocodeSource(localDatastore, database)
    ) {
        addressDao.pagingSource()
    }.flow

    init {
        setList()
    }

    fun getGeocode(address: String) {
        viewModelScope.launch {
            coroutineIOCallbackTo(
                block = { mapsApiUseCase.getGeocode(address) },
                success = {
                    geocode.value = DataStatus.Success(it)
                    setDataStore(it?.addresses)
                },
                error = { e -> geocode.value = DataStatus.Failure(e) }
            )
        }
    }

    fun setDataStore(addresses: List<Addresses>?) {
        viewModelScope.launch {
            addresses?.firstOrNull()?.let { addresses ->
                localDatastore.get(LocalDatastore.Keys.alarmList)?.let { addressesSet ->
                    addressesSet.find {
                        val data = Json.decodeFromString<com.data.gpsAlarm.dto.Addresses>(it)
                        data.x == addresses.x && data.y == addresses.y
                    }?.let {
                        val result = mutableSetOf<String>()
                        result.addAll(addressesSet.map { it })
                        result.add(it)
                        localDatastore.set(LocalDatastore.Keys.alarmList, result)
                    } ?: run {
                        val data = dataMapper(addresses)
                        val result = mutableSetOf<String>()
                        result.addAll(addressesSet.map { it })
                        result.add(Json.encodeToString(data))
                        localDatastore.set(LocalDatastore.Keys.alarmList, result)
                    }
                } ?: run {
                    val data = dataMapper(addresses)
                    val result = mutableSetOf<String>()
                    result.add(Json.encodeToString(data))
                    localDatastore.set(LocalDatastore.Keys.alarmList, result)
                }
            }
        }
    }

    fun setList() {
        geocodeList.value = DataStatus.Loading
        viewModelScope.launch {
            localDatastore.get(LocalDatastore.Keys.alarmList)?.let { addressesSet ->
                val result = mutableListOf<Addresses>()
                addressesSet.forEach {
                    val address = Json.decodeFromString<com.data.gpsAlarm.dto.Addresses>(it)
                    result.add(address.mapper())
                }
                geocodeList.value = DataStatus.Success(result)
            } ?: run {
                geocodeList.value = DataStatus.Success(emptyList())
            }
        }
    }

    private fun dataMapper(addresses: Addresses): com.data.gpsAlarm.dto.Addresses =
        com.data.gpsAlarm.dto.Addresses(
            addresses.addressElements?.map {
                AddressElement(
                    it.code,
                    it.longName,
                    it.shortName,
                    it.types
                )
            },
            addresses.distance,
            addresses.englishAddress,
            addresses.jibunAddress,
            addresses.roadAddress,
            addresses.x,
            addresses.y
        )
}