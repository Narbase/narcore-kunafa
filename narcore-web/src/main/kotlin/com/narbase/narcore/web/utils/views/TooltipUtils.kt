package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.narcore.web.common.TimeFormats
import kotlin.js.Date

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2021/06/08.
 */


fun View.dateTooltip(date: Date, delay: Int = 100) {
    tooltip("${TimeFormats.yearMonthDay(date)} ${TimeFormats.hoursAndMinutes(date)}", delay)
}
