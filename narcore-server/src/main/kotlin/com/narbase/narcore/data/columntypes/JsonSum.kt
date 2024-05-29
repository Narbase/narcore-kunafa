@file:Suppress("unused")

package com.narbase.narcore.data.columntypes

import org.jetbrains.exposed.sql.*
import java.math.BigDecimal

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


class JsonSum(
    private val tableName: String,
    private val columnName: String,
    private val jsonFieldName: String
) : Expression<BigDecimal?>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit =
        queryBuilder { append("SUM(cast(${tableName}.${columnName}->>'${jsonFieldName}' as decimal))") }
}

class JsonSumString(
    private val expression: String
) : Expression<BigDecimal?>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder { append("SUM($expression)") }
}

fun Column<*>.jsonDecimalValue(jsonFieldName: String) =
    "cast(${this.table.tableName}.${this.name}->>'${jsonFieldName}' as decimal)"

fun jsonDecimalValue(tableName: String, columnName: String, jsonFieldName: String) =
    "cast(${tableName}.${columnName}->>'${jsonFieldName}' as decimal)"


fun Column<*>.jsonStringValue(jsonFieldName: String) = JsonStringColumn(this.table, "${this.name}->>'${jsonFieldName}'")

class JsonStringColumn(
    val table: Table,
    val name: String
) : ExpressionWithColumnType<String>() {
    override val columnType: IColumnType<String> = JsonStringColumnType()

    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(table.tableName)
        append('.')
        append(name)
    }
}

class JsonStringColumnType : StringColumnType(null) {
    override fun sqlType() = "text"
}