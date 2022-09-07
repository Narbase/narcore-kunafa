package com.narbase.narcore.web.utils.views

import kotlinx.browser.window

class DelayBouncer<T>(private val bounceDelayInMillis: Int, private val doAction: ((T) -> Unit)? = null) {

    private var handlerId: Int? = null

    fun onInputChanged(input: T, actionParam: ((T) -> Unit)? = null) {
        handlerId?.let { window.clearTimeout(it) }
        handlerId = window.setTimeout({
            actionParam?.invoke(input)
            doAction?.invoke(input)
        }, bounceDelayInMillis)
    }

    fun buffer(input: T, actionParam: ((T) -> Unit)? = null) {
        onInputChanged(input, actionParam)
    }

    fun cancel() {
        handlerId?.let { window.clearTimeout(it) }
    }
}
