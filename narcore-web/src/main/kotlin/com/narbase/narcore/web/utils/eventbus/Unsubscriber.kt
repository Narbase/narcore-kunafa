package com.narbase.narcore.web.utils.eventbus

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class Unsubscriber(private val unsubscribeCallback: () -> Unit) {
    fun unsubscribe() = unsubscribeCallback()
}
