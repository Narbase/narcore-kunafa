package com.narbase.narcore.common.exceptions

import com.narbase.narcore.common.BasicResponse
import com.narbase.narcore.common.CommonCodes

open class InvalidRequestException(message: String = "") : Exception(message)

class MissingArgumentException(parameter: String = "") : InvalidRequestException("parameter '$parameter' is missing")

class OutdatedAppException : Exception()

class InvalidRequestResponse(message: String) : BasicResponse(CommonCodes.INVALID_REQUEST, message)
