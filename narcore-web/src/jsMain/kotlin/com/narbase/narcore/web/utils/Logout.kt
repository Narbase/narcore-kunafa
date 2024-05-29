package com.narbase.narcore.web.utils

import com.narbase.narcore.web.AppViewController
import com.narbase.narcore.web.storage.StorageManager

fun logoutUser() {
    StorageManager.accessToken = null
    StorageManager.setUserLoggedIn(false)
    AppViewController.loginState.value = false
}
