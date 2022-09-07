package com.narbase.narcore.web.utils.datetime

import com.narbase.narcore.web.utils.roundToTwoDigits
import kotlin.js.Date

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/03/13.
 */

fun Date.dto(): DateTimeDto = DateTimeDto(this.getTime().roundToTwoDigits())

fun Date.stringDate() = "${getFullYear()}-${(getMonth() + 1).lz()}-${getDate().lz()}"


/**
 * Formats Date to
 * yyyy-MM-dd*/
fun Date.dateDto() =
    DateDto("${getFullYear()}-${(getMonth() + 1).lz()}-${getDate().lz()}")


/**
 * Formats DateTime from yyyy-MM-dd hh:mm:ss or Date from yyyy-MM-dd
 */
fun String.toDate(): Date {
    val parts = split(' ')
    return if (parts.size > 1) {
        val (date, time) = parts
        val (year, month, day) = date.split('-').map { it.toInt() }
        val (hour, minute, second) = time.split(':').map { it.toInt() }
        Date(year, month - 1, day, hour, minute, second)
    } else {
        val (date) = parts
        val (year, month, day) = date.split('-').map { it.toInt() }
        Date(year, month - 1, day, 0, 0, 0)
    }
}

fun DateTimeDto.toDate(): Date = Date(this.milliSeconds.toString().toLong())

private fun Int.lz() = toString().padStart(2, '0')

fun Date.copyDateOnly(year: Int? = null, month: Int? = null, day: Int? = null) = copy(year, month, day, 0, 0, 0)

fun Date.copy(
    year: Int? = null,
    month: Int? = null,
    day: Int? = null,
    hour: Int? = null,
    minute: Int? = null,
    second: Int? = null
) = Date(
    year ?: getFullYear(),
    month ?: getMonth(),
    day ?: getDate(),
    hour ?: getHours(),
    minute ?: getMinutes(),
    second ?: getSeconds()
)

data class DateTimeDto(val milliSeconds: Double)
class DateDto(val date: String)

fun DateDto.toDate() = date.toDate()


