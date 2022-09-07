package com.narbase.narcore.common.auth.jwt

import com.google.gson.annotations.SerializedName

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 5/21/17.
 */
class JwtTokenDto(
    @SerializedName(value = "access_token")
    var token: String
)