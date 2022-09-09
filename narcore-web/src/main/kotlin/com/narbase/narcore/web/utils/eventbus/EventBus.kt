package com.narbase.narcore.web.utils.eventbus

import kotlin.reflect.KClass

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object EventBus {

    private val payloadDispatchers = mutableListOf<PayloadDispatcher<*>>()

    inline fun <reified P : Any> subscribe(noinline subscriber: (P) -> Unit) = subscribe(subscriber, P::class)

    @Suppress("UNCHECKED_CAST")
    fun <P : Any> subscribe(subscriber: (P) -> Unit, payloadClass: KClass<P>): Unsubscriber {
        val dispatcher = payloadDispatchers.firstOrNull {
            it.classType == payloadClass
        } as? PayloadDispatcher<P> ?: PayloadDispatcher(payloadClass).apply {
            payloadDispatchers.add(this)
        }
        dispatcher.subscribe(subscriber)
        return Unsubscriber {
            dispatcher.unsubscribe(subscriber)
        }
    }

    inline fun <reified P : Any> publish(payload: P) = publish(payload, P::class)

    @Suppress("UNCHECKED_CAST")
    fun <P : Any> publish(payload: P, payloadClass: KClass<P>) {
        val dispatcher = payloadDispatchers.firstOrNull { it.classType == payloadClass } as? PayloadDispatcher<P>
            ?: return
        dispatcher.publish(payload)
    }

    fun clearAll() {
        payloadDispatchers.forEach { it.clearAll() }
        payloadDispatchers.clear()
    }
}
