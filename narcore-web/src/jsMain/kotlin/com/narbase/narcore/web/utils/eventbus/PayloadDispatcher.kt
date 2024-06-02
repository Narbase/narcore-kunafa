package com.narbase.narcore.web.utils.eventbus

import kotlin.reflect.KClass

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class PayloadDispatcher<P : Any>(val classType: KClass<P>) {
    private val subscribers = mutableListOf<(P) -> Unit>()

    fun subscribe(subscriber: (P) -> Unit) {
        subscribers.add(subscriber)
    }

    fun unsubscribe(subscriber: (P) -> Unit) {
        subscribers.remove(subscriber)
    }

    fun publish(payload: P) {
        subscribers.forEach { it(payload) }
    }

    fun clearAll() {
        subscribers.clear()
    }
}
