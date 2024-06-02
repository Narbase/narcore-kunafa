package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.narcore.web.common.TimeFormats
import kotlin.js.Date

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun View.dateTooltip(date: Date, delay: Int = 100) {
    tooltip("${TimeFormats.yearMonthDay(date)} ${TimeFormats.hoursAndMinutes(date)}", delay)
}
