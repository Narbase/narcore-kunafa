package com.narbase.narcore.common.exceptions

import com.narbase.narcore.common.BasicResponse
import com.narbase.narcore.common.CommonCodes
import com.narbase.narcore.data.columntypes.DbEnumCorruptedException
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun Application.handleExceptions() {
    install(StatusPages) {
        exception<UnauthenticatedException> { cause ->
            call.respond(HttpStatusCode.Unauthorized, UnauthenticatedResponse(
                cause.message.takeUnless { it.isNullOrEmpty() } ?: "Unauthenticated"))
        }

        exception<OutdatedAppException> { call.respond(HttpStatusCode.BadRequest, OutdatedAppResponse()) }
        exception<DisabledUserException> { call.respond(HttpStatusCode.Unauthorized, DisabledUserResponse()) }
        exception<DbEnumCorruptedException> {
            call.respond(
                HttpStatusCode.BadRequest,
                DbEnumCorruptedResponse(it.message)
            )
        }
        exception<InvalidRequestException> { cause ->
            cause.printStackTrace()
            call.respond(
                HttpStatusCode.BadRequest,
                InvalidRequestResponse(cause.message ?: "Invalid request")
            )
        }

        status(HttpStatusCode.Unauthorized) {
            call.respond(HttpStatusCode.Unauthorized, UnauthenticatedResponse("Unauthenticated"))
        }

        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, NotFoundRequestResponse("Resource not found"))
        }

        exception<Throwable> { cause ->
            cause.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, InvalidRequestResponse(cause.message ?: "Invalid request"))
        }

    }

}


class NotFoundRequestResponse(message: String) : BasicResponse(CommonCodes.NOT_FOUND_ERROR, message)

class UnauthenticatedException(message: String = "") : Exception(message)

class UnauthenticatedResponse(message: String = "Unauthenticated") : BasicResponse(CommonCodes.UNAUTHENTICATED, message)

class OutdatedAppResponse : BasicResponse(CommonCodes.OUTDATED_APP)
class DbEnumCorruptedResponse(message: String?) : BasicResponse(CommonCodes.OUTDATED_APP, message)

class DisabledUserException(message: String = "") : Exception(message)
class DisabledUserResponse(message: String = "This account is disabled") :
    BasicResponse(CommonCodes.USER_DISABLED, message)
