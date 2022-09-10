package com.narbase.narcore.common.exceptions

import com.narbase.narcore.common.BasicResponse
import com.narbase.narcore.common.CommonCodes
import com.narbase.narcore.data.columntypes.DbEnumCorruptedException
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun Application.handleExceptions() {
    install(StatusPages) {
        exception<UnauthenticatedException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, UnauthenticatedResponse(
                cause.message.takeUnless { it.isNullOrEmpty() } ?: "Unauthenticated"))
        }

        exception<OutdatedAppException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                OutdatedAppResponse()
            )
        }
        exception<DisabledUserException> { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                DisabledUserResponse()
            )
        }
        exception<DbEnumCorruptedException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                DbEnumCorruptedResponse(cause.message)
            )
        }
        exception<InvalidRequestException> { call, cause ->
            cause.printStackTrace()
            call.respond(
                HttpStatusCode.BadRequest,
                InvalidRequestResponse(cause.message ?: "Invalid request")
            )
        }

        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, UnauthenticatedResponse("Unauthenticated"))
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(HttpStatusCode.NotFound, NotFoundRequestResponse("Resource not found"))
        }

        exception<Throwable> { call, cause ->
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
