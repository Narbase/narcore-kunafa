package com.narbase.narcore.web.storage

import com.narbase.narcore.web.common.models.Language
import kotlinx.browser.window
import org.w3c.dom.get


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object StorageManager {

    private const val LOGGED_IN = "LOGGED_IN"
    private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    private const val IS_FIRST_LOGIN = "IS_FIRST_LOGIN"

    private const val LANGUAGE = "LANGUAGE"


    fun setUserLoggedIn(isLoggedIn: Boolean) {
        window.localStorage.setItem(LOGGED_IN, if (isLoggedIn) "true" else "false")
    }

    fun isUserLoggedIn(): Boolean {
        return window.localStorage[LOGGED_IN] == "true"
    }

    var accessToken: String?
        get() = window.localStorage[ACCESS_TOKEN]
        set(value) {
            window.localStorage.setItem(ACCESS_TOKEN, value ?: "")
        }

    var isFirstLogin: Boolean
        get() = window.localStorage[IS_FIRST_LOGIN] == "true"
        set(value) {
            window.localStorage.setItem(IS_FIRST_LOGIN, if (value) "true" else "false")
        }

    var language: Language
        get() = Language.valueOf(window.localStorage[LANGUAGE] ?: Language.En.name)
        set(value) {
            window.localStorage.setItem(LANGUAGE, value.name)
        }
}
