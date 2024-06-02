package com.narbase.narcore.web.common

import com.narbase.narcore.web.storage.StoredBooleanValue
import kotlinx.browser.window
import org.w3c.dom.url.URLSearchParams

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object AppConfig {
    private var storedIsDev by StoredBooleanValue("IS_DEV", defaultValue = false)
    val isDev: Boolean
        get() {
            val urlParam = URLSearchParams(window.location.search).get("IS_DEV")?.let { it == "true" }
            if (urlParam != null) {
                storedIsDev = urlParam
            }
            return urlParam ?: storedIsDev
        }

    val isMobile: Boolean
        get() {
            val mQ = window.matchMedia("(max-width: 1280px)");
            return if (mQ.media === "(max-width: 1280px)") {
                !!mQ.matches;
            } else false
        }
}