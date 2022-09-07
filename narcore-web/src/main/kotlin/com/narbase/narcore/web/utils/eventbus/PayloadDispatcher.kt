package com.narbase.narcore.web.utils.eventbus

import kotlin.reflect.KClass

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/10/11.
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
