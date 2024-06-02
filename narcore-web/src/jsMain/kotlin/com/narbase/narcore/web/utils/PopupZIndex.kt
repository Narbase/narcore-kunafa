package com.narbase.narcore.web.utils

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object PopupZIndex {
    private var index = 100
    fun getTopIndex(): Int {
        return index++
    }

    fun restoreTopIndex() {
    }
}