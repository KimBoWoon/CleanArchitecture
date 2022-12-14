package com.lol.ui.fragments.vm

import androidx.lifecycle.viewModelScope
import com.data.gpsAlarm.local.LocalDatastore
import com.lol.base.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingVM @Inject constructor(
    private val dataStoreUseCase: DataStoreUseCase
) : BaseVM() {
    suspend fun setVersion(version: String) {
        viewModelScope.launch {
            dataStoreUseCase.set(LocalDatastore.Keys.LOL_VERSION, version)
        }
    }

    suspend fun getVersion(): String? =
        withContext(viewModelScope.coroutineContext) {
            dataStoreUseCase.get(LocalDatastore.Keys.LOL_VERSION)
        }
}