package com.narbase.narcore.web.network

import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.utils.BasicUiState
import kotlinx.coroutines.CoroutineScope

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/01/31.
 */

fun basicNetworkCall(uiState: Observable<BasicUiState>, call: suspend CoroutineScope.() -> Unit) {
    networkCall(
        before = { uiState.value = BasicUiState.Loading },
        onConnectionError = { uiState.value = BasicUiState.Error }
    ) {
        try {
            call()
            uiState.value = BasicUiState.Loaded
        } catch (t: Throwable) {
            uiState.value = BasicUiState.Error
            t.printStackTrace()
        }
    }
}
