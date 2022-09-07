package com.narbase.narcore.domain.utils

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.DisabledUserException
import com.narbase.narcore.data.tables.UsersTable
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by ${user}
 * On: ${date}.
 */


fun Route.addInactiveUserInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        val authorizedClientData = call.principal<AuthorizedClientData>()
        val isInactive = transaction {
            authorizedClientData?.id?.let { clientId ->
                UsersTable.select { UsersTable.clientId eq UUID.fromString(clientId) }.firstOrNull()
                    ?.get(UsersTable.isInactive)
            }
        }
        if (isInactive == true)
            throw DisabledUserException()
    }
}