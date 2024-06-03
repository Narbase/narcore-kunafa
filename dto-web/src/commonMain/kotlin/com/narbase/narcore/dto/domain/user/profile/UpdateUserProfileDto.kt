package com.narbase.narcore.dto.domain.user.profile

import kotlin.js.JsExport

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@JsExport
object UpdateUserProfileDto {
    class RequestDto(
        val fullName: String,
        val callingCode: String,
        val localPhone: String
    )

    class ResponseDto
}
