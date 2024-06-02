package com.narbase.narcore.web.utils.state

import com.narbase.kunafa.core.components.Checkbox
import com.narbase.kunafa.core.components.TextInput
import com.narbase.kunafa.core.components.TextView
import com.narbase.kunafa.core.lifecycle.Observable

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun TextView.bind(state: Observable<String>) {
    element.onkeyup = {
        if (state.value != this.text) {
            state.value = this.text
        }
        Unit
    }

    state.observe {
        if (it != this.text) {
            text = it ?: ""
        }
    }
}

fun TextInput.bind(state: Observable<String>) {
    element.onkeyup = {
        if (state.value != this.text) {
            state.value = this.text
        }
        Unit
    }

    state.observe {
        if (it != this.text) {
            text = it ?: ""
        }
    }
}

fun Checkbox.bind(state: Observable<Boolean>) {
    onChange = {
        if (isChecked != state.value) {
            state.value = this.isChecked
        }
        Unit
    }

    state.observe {
        if (isChecked != it) {
            isChecked = it ?: false
        }
    }
}


/*

fun TextView.bind(state: MutableState<String>) {
    element.onkeyup = {
        if (state.observable.value != this.text) {
            state.observable.value = this.text
        }
        Unit
    }

    state.observable.observe {
        if (it != this.text) {
            text = it ?: ""
        }
    }
}

fun Checkbox.bind(state: MutableState<Boolean>) {
    onChange = {
        if (isChecked != state.observable.value) {
            state.observable.value = this.isChecked
        }
        Unit
    }

    state.observable.observe {
        if (isChecked != it) {
            isChecked = it ?: false
        }
    }
}


class MutableState<T> {
    val observable = Observable<T>()
}

*/
