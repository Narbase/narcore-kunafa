package com.narbase.narcore.core

import com.narbase.narcore.dto.common.EnumDtoName


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun <E> E.dto(): DtoName<E> where E : EnumDtoName, E : Enum<E> {
    return DtoName(this)
}

//DtoName does not work in Lists
@JvmInline
value class DtoName<E>(val name: String) where E : EnumDtoName, E : Enum<E> {
    constructor(enumDto: EnumDtoName) : this(enumDto.dtoName)
}

inline fun <reified E> DtoName<E>.enum(): E where E : EnumDtoName, E : Enum<E> {
    return enumValues<E>().first { it.dtoName == name }
}


inline fun <reified E> valueOfDto(str: String): E? where E : Enum<E>, E : EnumDtoName {
    return valueOfDto(str, E::class.java)
}

fun <E> valueOfDto(str: String, enumClass: Class<E>): E? where E : Enum<E>, E : EnumDtoName {
    val values = enumClass.enumConstants
    return values.firstOrNull { it.dtoName == str }
}
