package com.narbase.narcore.web.utils

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/08/18.
 */
object PopupZIndex {
    private var index = 100
    fun getTopIndex(): Int {
        return index++
    }

    fun restoreTopIndex() {
    }
}