package com.narbase.narcore.web

import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.login.LoginPageNavigator
import com.narbase.narcore.web.storage.StorageManager
import com.narbase.narcore.web.views.basePage.HomePageNavigator


class AppViewController : LoginPageNavigator,
    HomePageNavigator {

    companion object {
        val loginState = Observable<Boolean>()
    }

    override fun onLoggedInSuccessful() {
        StorageManager.setUserLoggedIn(true)
        loginState.value = true
    }

    override fun onLogoutSelected() {
        StorageManager.setUserLoggedIn(false)
        loginState.value = false
    }

    fun onViewCreated() {
        loginState.value = StorageManager.isUserLoggedIn()
    }

}
