package com.narbase.narcore.data.columntypes

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.VarCharColumnType
import kotlin.reflect.KClass
import kotlin.reflect.full.safeCast

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class EnumColumnType<E>(
    colLength: Int = 32,
    val enumClass: KClass<E>,
    collate: String? = null
) : VarCharColumnType(colLength, collate)
        where E : Enum<E>,
              E : EnumPersistenceName {
    override fun nonNullValueToString(value: Any): String {
        val enum = enumClass.safeCast(value)
        return if (enum != null) {
            super.nonNullValueToString(enum.persistenceName)
        } else
            value.toString()
    }

    override fun notNullValueToDB(value: Any): Any {
        val enum = enumClass.safeCast(value)
        return if (enum != null) {
            super.notNullValueToDB(enum.persistenceName)
        } else
            value.toString()
    }

    override fun valueFromDB(value: Any) = when {
        enumClass.isInstance(value) -> value
        value is String -> valueOfPersisted(value, enumClass.java) ?: throw DbEnumCorruptedException()
        else -> valueOfPersisted(value.toString(), enumClass.java) ?: throw DbEnumCorruptedException()
    }
}

inline fun <reified E> Table.enum(name: String): Column<E> where E : Enum<E>, E : EnumPersistenceName =
    registerColumn(name, EnumColumnType<E>(32, E::class))


fun <E> Table.enum(name: String, enumClass: KClass<E>): Column<E> where E : Enum<E>, E : EnumPersistenceName =
    registerColumn(name, EnumColumnType<E>(32, enumClass))

interface EnumPersistenceName {
    val persistenceName: String
}

class InvalidPersistedEnumValueException(message: String) : Exception(message)

inline fun <reified E> valueOfPersisted(str: String): E? where E : Enum<E>, E : EnumPersistenceName {
    return valueOfPersisted(str, E::class.java)
}

fun <E> valueOfPersisted(str: String, enumClass: Class<E>): E? where E : Enum<E>, E : EnumPersistenceName {
    val values = enumClass.enumConstants
    return values.firstOrNull { it.persistenceName == str }
}

class DbEnumCorruptedException(message: String = "The value returned from Db doesn't match any of the enum constants") :
    Exception(message)
