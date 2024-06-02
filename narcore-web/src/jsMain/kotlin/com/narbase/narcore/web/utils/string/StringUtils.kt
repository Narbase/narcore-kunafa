package com.narbase.narcore.web.utils.string

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun String.splitCamelCase() = replace("([A-Z])".toRegex(), " $1")
    .replace("^.".toRegex()) { it.value.toUpperCase() }
