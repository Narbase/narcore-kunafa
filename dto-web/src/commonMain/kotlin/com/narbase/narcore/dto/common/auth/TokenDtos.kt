package com.narbase.narcore.dto.common.auth

import kotlin.js.JsExport

@JsExport
object TokenDtos {

    data class JwtTokenDto(
        val access_token: String
    )

    data class TokenResponse(
        val status: Int,
        val data: JwtTokenDto?
    )
}
