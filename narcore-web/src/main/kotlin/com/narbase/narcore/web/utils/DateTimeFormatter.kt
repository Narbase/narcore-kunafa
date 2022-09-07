package com.narbase.narcore.web.utils

import com.narbase.narcore.web.storage.StorageManager
import kotlin.js.Date

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2020] Narbase Technologies
 * All Rights Reserved.
 * Created by Mohammad Abbas
 * On: 3/25/20.
 */

fun Date.toHoursAndMinutes(): String { //10:30
    val options = dateLocaleOptions { hour = "2-digit"; minute = "2-digit"; hour12 = false; }
    return toLocaleTimeString(StorageManager.language.locale, options)
}

fun Date.toMonthAndDay(): String { // Jan 20
    val options = dateLocaleOptions { day = "numeric"; month = "short"; }
    return toLocaleDateString(StorageManager.language.locale, options)
}

fun Date.toDayAndLongMonth(): String { // 20 Jan
    val options = dateLocaleOptions { day = "numeric"; month = "long"; }
    return toLocaleDateString(StorageManager.language.locale, options)
}

fun Date.toYearMonthDay(): String { //12 Nov 2019
    val options = dateLocaleOptions { day = "numeric"; month = "short"; year = "numeric"; }
    return toLocaleDateString(StorageManager.language.locale, options)
}

fun Date.toYearLongMonthDay(): String { //12 November 2019
    val options = dateLocaleOptions { day = "numeric"; month = "long"; year = "numeric"; }
    return toLocaleDateString(StorageManager.language.locale, options)
}

fun Date.toNumericMonthDayYear(): String { //12/1/2019
    val options = dateLocaleOptions { day = "numeric"; month = "numeric"; year = "numeric"; }
    return toLocaleDateString(StorageManager.language.locale, options)
}

fun Date.toShortMonthAndYear(): String { // Jan 2020
    val options = dateLocaleOptions { month = "short"; year = "numeric"; }
    return toLocaleDateString(StorageManager.language.locale, options)
}

