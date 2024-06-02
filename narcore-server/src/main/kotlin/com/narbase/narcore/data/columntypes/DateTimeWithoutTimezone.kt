package com.narbase.narcore.data.columntypes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class DateTimeWithoutTimezoneColumn(val time: Boolean) : ColumnType<DateTime>() {
    private val DEFAULT_DATE_TIME_STRING_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSS")
    private val DEFAULT_DATE_STRING_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd")

    override fun sqlType(): String = if (time) "TIMESTAMP" else "DATE"

    override fun nonNullValueToString(value: DateTime): String {

        val dateTime = value.withZone(DateTimeZone.UTC)

        return if (time)
            "'${dateTime.toString(DEFAULT_DATE_TIME_STRING_FORMATTER)}'"
        else
            "'${dateTime.toString(DEFAULT_DATE_STRING_FORMATTER)}'"
    }

    override fun valueFromDB(value: Any): DateTime? = when (value) {
        is DateTime -> value.withZoneRetainFields(DateTimeZone.UTC)
        is java.sql.Date -> DateTime.parse(value.toString(), DEFAULT_DATE_TIME_STRING_FORMATTER)
            .withZoneRetainFields(DateTimeZone.UTC)

        is java.sql.Timestamp -> DateTime.parse(value.toString(), DEFAULT_DATE_TIME_STRING_FORMATTER)
            .withZoneRetainFields(DateTimeZone.UTC)

        is Int -> DateTime(value.toLong()).withZoneRetainFields(DateTimeZone.UTC)
        is Long -> DateTime(value).withZoneRetainFields(DateTimeZone.UTC)
        is String -> DEFAULT_DATE_TIME_STRING_FORMATTER.parseDateTime(value.toString())
        // REVIEW
        else -> DEFAULT_DATE_TIME_STRING_FORMATTER.parseDateTime(value.toString())
    }

    override fun notNullValueToDB(value: DateTime): Any {
        return if (time) {
            java.sql.Timestamp.valueOf(
                value.withZone(DateTimeZone.UTC).toString(DEFAULT_DATE_TIME_STRING_FORMATTER)
            )
        } else {
            java.sql.Date.valueOf(value.withZone(DateTimeZone.UTC).toString(DEFAULT_DATE_STRING_FORMATTER))
        }
    }
}

fun Table.dateTimeWithoutTimezone(name: String): Column<DateTime> =
    registerColumn(name, DateTimeWithoutTimezoneColumn(true))


/*
fun Table.dateWithoutTimezone(name: String): Column<DateTime> =
        registerColumn(name, DateTimeWithoutTimezoneColumn(false))
*/
