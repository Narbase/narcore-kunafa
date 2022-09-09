package com.narbase.narcore.dto.domain.user.profile

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object UpdatePasswordDto {

    data class Request(
        val oldPassword: String,
        val newPassword: String
    )

    class Response(
        val didUpdate: Boolean
    )
}