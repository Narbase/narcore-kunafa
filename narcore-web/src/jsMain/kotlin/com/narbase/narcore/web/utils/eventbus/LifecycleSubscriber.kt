package com.narbase.narcore.web.utils.eventbus

import com.narbase.kunafa.core.lifecycle.LifecycleObserver
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import kotlin.reflect.KClass

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

inline fun <reified P : Any> LifecycleSubscriber(noinline onEvent: (P) -> Unit) = LifecycleSubscriber(onEvent, P::class)

class LifecycleSubscriber<P : Any>(
    private val onEvent: (P) -> Unit,
    val payloadClass: KClass<P>
) : LifecycleObserver {
    private var unsubscriber: Unsubscriber? = null

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        unsubscriber = EventBus.subscribe(onEvent, payloadClass)
    }

    override fun onViewRemoved(lifecycleOwner: LifecycleOwner) {
        super.onViewRemoved(lifecycleOwner)
        unsubscriber?.unsubscribe()
    }
}
