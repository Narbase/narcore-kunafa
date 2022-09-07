package com.narbase.narcore.dto.common

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2021/01/10.
 */

interface EnumDtoName {
    val dtoName: String
}

expect inline class DtoName<E>(private val name: String) where E : EnumDtoName, E : Enum<E>