package com.narbase.narcore.data.tables.utils

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.QueryBuilder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class AtTimeZone(
    val expression: Expression<DateTime>,
    val zone: String
) : Expression<DateTime>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append(expression)
        append("AT TIME ZONE")
        append(zone)
    }
}

infix fun Expression<DateTime>.atTimeZone(zone: String): AtTimeZone = AtTimeZone(this, zone)

private val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

class IsOnDateAtTimeZone(
    val expression: Expression<DateTime>,
    val dateTime: DateTime,
    val zone: String
) : Expression<DateTime>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append(expression)
        append("AT TIME ZONE UTC >")
        append(dateFormatter.print(dateTime))
        append("AT TIME ZONE $zone")
        append("AND")
        append(expression)
        append("AT TIME ZONE UTC <")
        append(dateFormatter.print(dateTime.plusDays(1)))
        append("AT TIME ZONE $zone")
    }
}

fun Expression<DateTime>.isOnDateAtTimeZone(dateTime: DateTime, zone: String): IsOnDateAtTimeZone =
    IsOnDateAtTimeZone(this, dateTime, zone)

infix fun Expression<DateTime>.isTodayAtTimeZone(zone: String): IsOnDateAtTimeZone =
    IsOnDateAtTimeZone(this, DateTime.now(), zone)


