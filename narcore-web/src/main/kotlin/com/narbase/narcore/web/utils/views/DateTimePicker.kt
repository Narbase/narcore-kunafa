@file:Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")

package com.narbase.narcore.web.utils.views

import org.w3c.dom.HTMLInputElement
import kotlin.js.Date

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class DateTimePicker(
    field: HTMLInputElement,
    enableTime: Boolean,
    mode: String?,
    onClose: ((selectedDates: Array<Date>, dateStr: String, instance: flatpickr) -> Unit)?,
    onChange: ((selectedDates: Array<Date>, dateStr: String, instance: flatpickr) -> Unit)?
) {
    val options: dynamic
    var picker: flatpickr? = null

    init {
        options = object {}
        options["enableTime"] = enableTime
        mode?.let { options["mode"] = it }
        onClose?.let { options["onClose"] = it }
        onChange?.let { options["onChange"] = it }
        setup(field)
    }

    fun setup(field: HTMLInputElement) {
        picker = flatpickr(field, options)
    }


}

@JsModule("flatpickr")
@JsNonModule
external class flatpickr(element: HTMLInputElement, options: dynamic) {
    var selectedDates: Array<Date>
}

fun TimePicker.getDate() = picker?.selectedDates?.firstOrNull()
fun TimePicker.getTime() = picker?.selectedDates?.firstOrNull()?.toTimeString()

fun dateTimePicker(
    field: HTMLInputElement,
    enableTime: Boolean = false,
    mode: String? = null,
    onClose: ((selectedDates: Array<Date>, dateStr: String, instance: flatpickr) -> Unit)? = null,
    onChange: ((selectedDates: Array<Date>, dateStr: String, instance: flatpickr) -> Unit)? = null
): DateTimePicker {
    return DateTimePicker(
        field = field,
        enableTime = enableTime,
        mode = mode,
        onClose = onClose,
        onChange = onChange
    )
}
