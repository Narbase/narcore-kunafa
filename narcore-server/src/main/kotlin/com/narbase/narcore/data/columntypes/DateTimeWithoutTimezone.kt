package com.narbase.narcore.data.columntypes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class DateTimeWithoutTimezoneColumn(val time: Boolean) : ColumnType() {
    private val DEFAULT_DATE_TIME_STRING_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSSSSS")
    private val DEFAULT_DATE_STRING_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd")

    override fun sqlType(): String = if (time) "TIMESTAMP" else "DATE"

    override fun nonNullValueToString(value: Any): String {
        if (value is String) return value

        val dateTime = when (value) {
            is DateTime -> value.withZone(DateTimeZone.UTC)
            is java.sql.Date -> DateTime(value.time).withZone(DateTimeZone.UTC)
            is java.sql.Timestamp -> DateTime(value.time).withZone(DateTimeZone.UTC)
            else -> error("Unexpected value: $value of ${value::class.qualifiedName}")
        }

        return if (time)
            "'${dateTime.toString(DEFAULT_DATE_TIME_STRING_FORMATTER)}'"
        else
            "'${dateTime.toString(DEFAULT_DATE_STRING_FORMATTER)}'"
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is DateTime -> value.withZoneRetainFields(DateTimeZone.UTC)
        is java.sql.Date -> DateTime.parse(value.toString(), DEFAULT_DATE_TIME_STRING_FORMATTER)
            .withZoneRetainFields(DateTimeZone.UTC)

        is java.sql.Timestamp -> DateTime.parse(value.toString(), DEFAULT_DATE_TIME_STRING_FORMATTER)
            .withZoneRetainFields(DateTimeZone.UTC)

        is Int -> DateTime(value.toLong()).withZoneRetainFields(DateTimeZone.UTC)
        is Long -> DateTime(value).withZoneRetainFields(DateTimeZone.UTC)
        is String -> value
        // REVIEW
        else -> DEFAULT_DATE_TIME_STRING_FORMATTER.parseDateTime(value.toString())
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is DateTime) {
            return if (time) {
                java.sql.Timestamp.valueOf(
                    value.withZone(DateTimeZone.UTC).toString(DEFAULT_DATE_TIME_STRING_FORMATTER)
                )
            } else {
                java.sql.Date.valueOf(value.withZone(DateTimeZone.UTC).toString(DEFAULT_DATE_STRING_FORMATTER))
            }
        }
        return value
    }
}

fun Table.dateTimeWithoutTimezone(name: String): Column<DateTime> =
    registerColumn(name, DateTimeWithoutTimezoneColumn(true))


/*
fun Table.dateWithoutTimezone(name: String): Column<DateTime> =
        registerColumn(name, DateTimeWithoutTimezoneColumn(false))
*/
