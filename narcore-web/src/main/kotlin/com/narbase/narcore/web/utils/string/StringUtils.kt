package com.narbase.narcore.web.utils.string

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/07/04.
 */

fun String.splitCamelCase() = replace("([A-Z])".toRegex(), " $1")
    .replace("^.".toRegex()) { it.value.toUpperCase() }
