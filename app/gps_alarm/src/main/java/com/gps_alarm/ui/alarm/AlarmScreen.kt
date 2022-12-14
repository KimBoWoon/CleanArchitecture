package com.gps_alarm.ui.alarm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gps_alarm.data.Address
import com.gps_alarm.ui.NavigationScreen
import com.gps_alarm.ui.dialog.GpsAlarmDialog
import com.gps_alarm.ui.theme.Purple700
import com.gps_alarm.ui.viewmodel.AlarmVM
import util.DataStatus
import util.Log

@Composable
fun AlarmScreen(onNavigate: NavHostController) {
    AlarmCompose(onNavigate)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AlarmCompose(
    onNavigate: NavHostController,
    viewModel: AlarmVM = hiltViewModel()
) {
    val geocodeList by viewModel.geocodeList.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val fabVisibility by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }
    val density = LocalDensity.current
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.setList()
        })

//    viewModel.setList()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.TopCenter
    ) {
        when (geocodeList) {
            is DataStatus.Loading -> {
                Log.d("alarm list geocode data loading...")
                CircularProgressIndicator(
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
            is DataStatus.Success -> {
                isRefreshing = false
                (geocodeList as? DataStatus.Success)?.data?.let {
                    if (it.isEmpty()) {
                        Text(text = "????????? ????????? ????????????.")
                    } else {
                        AlarmContent(onNavigate, listState, it)
                    }
                }
            }
            is DataStatus.Failure -> {
                isRefreshing = false
                GpsAlarmDialog(
                    "???????????? ??????????????? ????????? ??????????????????.\n?????? ?????????????????????????",
                    "?????????",
                    { viewModel.setList() },
                    "??????",
                    {}
                )
            }
        }
        PullRefreshIndicator(refreshing = isRefreshing, state = pullRefreshState)

        AnimatedVisibility(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(bottom = 10.dp, end = 10.dp)
                .align(alignment = Alignment.BottomEnd),
            visible = fabVisibility,
            enter = slideInVertically {
                with(density) { 40.dp.roundToPx() }
            } + fadeIn(),
            exit = fadeOut(
                animationSpec = keyframes {
                    this.durationMillis = 120
                }
            )
        ) {
            FloatingActionButton(
                onClick = { onNavigate.navigate(NavigationScreen.CreateAlarm.route) },
                backgroundColor = Purple700,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, "add alarm")
            }
        }
    }
}

@Composable
fun AlarmContent(
    onNavigate: NavHostController,
    listState: LazyListState,
    addresses: List<Address>
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        itemsIndexed(addresses) { index, address ->
            AddressItem(onNavigate, address, addresses.lastIndex == index)
        }
    }
}

@Composable
fun AddressItem(
    onNavigate: NavHostController,
    address: Address,
    isLast: Boolean
) {
    val viewModel: AlarmVM = hiltViewModel()
    var checkedState by remember { mutableStateOf(address.isEnable ?: false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = if (isLast) 10.dp else 0.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(8.dp))
            .clickable { onNavigate.navigate("${NavigationScreen.AlarmDetail.route}/${address.longitude}/${address.latitude}") },
    ) {
        Box(
            modifier = Modifier.padding(5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(text = address.roadAddress ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = address.jibunAddress ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "?????? ?????? ??????", maxLines = 1, overflow = TextOverflow.Ellipsis)
                Switch(
                    checked = checkedState,
                    onCheckedChange = {
                        checkedState = it
                        address.isEnable = it
                        viewModel.changeData(address)
                    }
                )
            }
            Button(
                modifier = Modifier
                    .wrapContentSize()
                    .align(alignment = Alignment.BottomEnd),
                onClick = {
                    viewModel.deleteAlarm(address.longitude, address.latitude)
                }
            ) {
                Text(text = "??????")
            }
        }
    }
}