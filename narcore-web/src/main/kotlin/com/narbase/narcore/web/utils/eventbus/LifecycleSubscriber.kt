package com.narbase.narcore.web.utils.eventbus

import com.narbase.kunafa.core.lifecycle.LifecycleObserver
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import kotlin.reflect.KClass

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/10/11.
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
