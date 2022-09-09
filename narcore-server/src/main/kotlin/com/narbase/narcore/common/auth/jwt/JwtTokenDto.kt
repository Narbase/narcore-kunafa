package com.narbase.narcore.common.auth.jwt

import com.google.gson.annotations.SerializedName

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class JwtTokenDto(
    @SerializedName(value = "access_token")
    var token: String
)