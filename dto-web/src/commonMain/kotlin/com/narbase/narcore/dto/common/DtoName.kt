package com.narbase.narcore.dto.common

import kotlin.jvm.JvmInline

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

interface EnumDtoName {
    val dtoName: String
}

@JvmInline
expect value class DtoName<E>(private val name: String) where E : EnumDtoName, E : Enum<E>