package com.narbase.narcore.web.network

import com.narbase.narcore.web.utils.logoutUser
import kotlinx.coroutines.*

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2013] - [2017] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2018/04/18.
 */

fun networkCall(
    before: () -> Unit = {},
    final: suspend CoroutineScope.() -> Unit = { },
    onConnectionError: suspend CoroutineScope.() -> Unit = { },
    onUnknownError: suspend CoroutineScope.() -> Unit = onConnectionError,
    onUnauthorized: suspend CoroutineScope.() -> Unit = onConnectionError,
    onInvalidRequest: suspend CoroutineScope.() -> Unit = onConnectionError,
    onUserDisabled: suspend CoroutineScope.() -> Unit = { logoutUser() },
    call: suspend CoroutineScope.() -> Unit
): Job {
    before()
    return GlobalScope.launch(Dispatchers.Default) {
        try {
            withTimeout(30_000) {
                call()
            }
        } catch (e: ConnectionErrorException) {
            withContext(Dispatchers.Main) { onConnectionError() }
        } catch (e: UnknownErrorException) {
            withContext(Dispatchers.Main) { onUnknownError() }
        } catch (e: UnauthorizedException) {
            withContext(Dispatchers.Main) { onUnauthorized() }
        } catch (e: DisabledUserException) {
            withContext(Dispatchers.Main) { onUserDisabled() }
        } catch (e: InvalidRequestException) {
            withContext(Dispatchers.Main) { onInvalidRequest() }
        } catch (e: TimeoutCancellationException) {
            withContext(Dispatchers.Main) { onConnectionError() }
        } catch (e: Throwable) {
            withContext(Dispatchers.Main) { onInvalidRequest() }
        } finally {
            withContext(Dispatchers.Main) { final() }
        }
    }
}


