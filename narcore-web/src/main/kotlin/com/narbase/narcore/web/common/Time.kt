package com.narbase.narcore.web.common

import com.narbase.narcore.web.storage.StorageManager
import kotlin.js.Date

object TimeFormats {
    fun hoursAndMinutes(dateTime: Date): String { //10:30
        val options = dateLocaleOptions { hour = "2-digit"; minute = "2-digit"; hour12 = false }
        return dateTime.toLocaleTimeString(displayLocale, options)
    }

    fun monthAndDay(dateTime: Date): String { // Jan 20
        val options = dateLocaleOptions {
            day = "numeric"
            month = "short"
            if (dateTime.getFullYear() != Date().getFullYear()) year = "numeric"
        }
        return dateTime.toLocaleDateString(displayLocale, options)
    }

    fun dayAndLongMonth(dateTime: Date): String { // 20 Jan
        val options = dateLocaleOptions {
            day = "numeric"
            month = "long"
            if (dateTime.getFullYear() != Date().getFullYear()) year = "numeric"
        }
        return dateTime.toLocaleDateString(displayLocale, options)
    }

    fun yearMonthDay(dateTime: Date): String { //12 Nov 2019
        val options = dateLocaleOptions { day = "numeric"; month = "short"; year = "numeric" }
        return dateTime.toLocaleDateString(displayLocale, options)
    }

    fun yearLongMonthDay(dateTime: Date): String { //12 November 2019
        val options = dateLocaleOptions { day = "numeric"; month = "long"; year = "numeric" }
        return dateTime.toLocaleDateString(displayLocale, options)
    }

    fun numericMonthDayYear(dateTime: Date): String { //12/1/2019
        val options = dateLocaleOptions { day = "numeric"; month = "numeric"; year = "numeric"; }
        return dateTime.toLocaleDateString("en-GB", options)
    }

    fun shortMonthAndYear(dateTime: Date): String { // Jan 2020
        val options = dateLocaleOptions { month = "short"; year = "numeric" }
        return dateTime.toLocaleDateString(displayLocale, options)
    }

    fun shortMonthDayAndYear(dateTime: Date): String { // Jan 2020
        val options = dateLocaleOptions { weekday = "short"; month = "short"; day = "numeric"; year = "numeric" }
        return dateTime.toLocaleDateString(displayLocale, options)
    }
}

val displayLocale = StorageManager.language.locale

