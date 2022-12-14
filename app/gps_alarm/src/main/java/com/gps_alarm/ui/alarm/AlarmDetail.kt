package com.gps_alarm.ui.alarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gps_alarm.data.Address
import com.gps_alarm.ui.viewmodel.AlarmVM
import util.Log

@Composable
fun AlarmDetailScreen(
    onNavigate: NavHostController,
    longitude: String,
    latitude: String,
    viewModel: AlarmVM = hiltViewModel()
) {
    if (longitude.isNotEmpty() && latitude.isNotEmpty()) {
        viewModel.getAddress(longitude, latitude)

        viewModel.findAddress.value?.let {
            Log.d(it.toString())
            AlarmDetailCompose(it)
        } ?: run {

        }
    }
}

@Composable
fun AlarmDetailCompose(address: Address?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        address?.let {
            Column {
                Text(text = it.jibunAddress.orEmpty())
                Text(text = it.roadAddress.orEmpty())
            }
        }
    }
}