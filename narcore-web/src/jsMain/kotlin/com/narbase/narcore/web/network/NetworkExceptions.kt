package com.narbase.narcore.web.network

class ConnectionErrorException(msg: String = "") : Exception(msg)
class UnknownErrorException(msg: String = "") : Exception(msg)
class UnauthorizedException(msg: String = "") : Exception(msg)
class InvalidRequestException(msg: String = "") : Exception(msg)
class DisabledUserException(message: String = "") : Exception(message)

const val INVALID_REQUEST = "11"
const val UNKNOWN_ERROR = "12"
const val USER_DISABLED = "15"
