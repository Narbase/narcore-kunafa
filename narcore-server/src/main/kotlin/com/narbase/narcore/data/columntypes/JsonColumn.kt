package com.narbase.narcore.data.columntypes

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.narbase.narcore.core.MultiLingualText
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject
import java.lang.reflect.Type

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


inline fun <reified T : Any> JsonColumn() = JsonColumn<T>(T::class.java)
class JsonColumn<T : Any>(val type: Type) : ColumnType<T>() {
    val gson = createGson()
    override fun sqlType(): String = "jsonb"


    override fun notNullValueToDB(value: T): Any {
        if (value is String) {
            return value
        }
        return gson.toJson(value)
    }

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val obj = PGobject()
        obj.type = "jsonb"
        obj.value = value as String?
        stmt[index] = obj
    }


    override fun valueFromDB(value: Any): T {
        val json: String = when (value) {
            is String -> value
            is PGobject -> value.value ?: "{}"
            else -> error("Unexpected value for json: $value of ${value::class.qualifiedName}")
        }

        return gson.fromJson(json, type)
    }

}

fun Table.array(name: String): Column<List<String>> =
    registerColumn(name, JsonColumn<List<String>>(object : TypeToken<List<String>>() {}.type))

fun Table.multiLingualText(name: String): Column<MultiLingualText> =
    registerColumn(name, JsonColumn<MultiLingualText>(object : TypeToken<MultiLingualText>() {}.type))


inline fun <reified T : Any> Table.jsonColumn(name: String): Column<T> = registerColumn(name, JsonColumn<T>())


fun createGson(): Gson {

    return GsonBuilder()
//            .registerTypeAdapterFactory(entryFactory)
        .create()
}





