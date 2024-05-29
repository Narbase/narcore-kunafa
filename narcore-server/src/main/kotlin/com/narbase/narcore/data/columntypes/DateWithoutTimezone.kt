package com.narbase.narcore.data.columntypes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class DateWithoutTimezoneColumn : ColumnType<LocalDate>() {
    private val formatter = DateTimeFormat.forPattern("YYYY-MM-dd")

    override fun sqlType(): String = "DATE"

    override fun nonNullValueToString(value: LocalDate): String {

        return "'${value.toString(formatter)}'"
    }

    override fun valueFromDB(value: Any): LocalDate? = when (value) {
        is LocalDate -> value
        is java.sql.Date -> LocalDate.fromDateFields(value)
        is java.sql.Timestamp -> LocalDate.fromDateFields(value)
        // REVIEW
        else -> formatter.parseLocalDate(value.toString())
    }

    override fun notNullValueToDB(value: LocalDate): Any {
        val sql: java.sql.Date = java.sql.Date.valueOf(value.toString())
        return sql
    }
}

fun Table.dateWithoutTimezone(name: String): Column<LocalDate> =
    registerColumn(name, DateWithoutTimezoneColumn())
