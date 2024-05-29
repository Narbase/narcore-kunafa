package com.narbase.narcore.web.utils.views

import org.w3c.dom.HTMLInputElement

class TimePicker(vararg optionsMap: Pair<String, Any>) {
    val options: dynamic
    var picker: flatpickr? = null

    init {
        options = object {}
        options["enableTime"] = true
        options["noCalendar"] = true
        options["dateFormat"] = "H:i"
        options["time_24hr"] = true
        optionsMap.forEach {
            options[it.first] = it.second
        }
    }

    fun setup(field: HTMLInputElement?) {
        field ?: return
        picker = flatpickr(field, options)

    }
}
