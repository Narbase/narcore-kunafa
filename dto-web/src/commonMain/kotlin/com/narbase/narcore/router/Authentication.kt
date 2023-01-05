package com.narbase.narcore.router


enum class Authentication(val configurationName: String) {
    JWT(AuthenticationConstants.JWT_AUTH),
    Basic(AuthenticationConstants.BASIC_AUTH)
}

object AuthenticationConstants {
    const val BASIC_AUTH = "BasicAuth"
    const val JWT_AUTH = "JwtAuth"
}