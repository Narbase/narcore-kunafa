package com.narbase.narcore.web.network.calls.settings

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object EnableUserDto {

    class RequestDto(
        val userId: String,
        val isActive: Boolean
    )
}
