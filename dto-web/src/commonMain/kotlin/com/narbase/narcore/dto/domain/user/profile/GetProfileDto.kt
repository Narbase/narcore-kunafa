package com.narbase.narcore.dto.domain.user.profile

import kotlin.js.JsExport

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@JsExport
object GetProfileDto {

    class Request

    class Response(
        val profile: UserProfile
    )

    class UserProfile(
        val clientId: String,
        val userId: String,
        val fullName: String,
        val username: String,
        val callingCode: String,
        val localPhone: String,
        val privileges: Array<String>
    )
}