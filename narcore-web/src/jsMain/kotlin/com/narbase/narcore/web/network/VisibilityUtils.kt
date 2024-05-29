package com.narbase.narcore.web.network

import com.narbase.kunafa.core.components.View

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun makeVisible(vararg views: View?) {
    views.forEach { it?.isVisible = true }
}

fun makeNotVisible(vararg views: View?) {
    views.forEach { it?.isVisible = false }
}
