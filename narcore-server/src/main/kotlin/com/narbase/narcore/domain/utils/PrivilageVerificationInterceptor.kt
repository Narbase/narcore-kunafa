package com.narbase.narcore.domain.utils

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.UnauthenticatedException
import com.narbase.narcore.dto.models.roles.Privilege
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by ${user}
 * On: ${date}.
 */

fun Route.addPrivilegeVerificationInterceptor(privilege: Privilege) {
    addPrivilegeVerificationInterceptor(listOf(privilege))
}

fun Route.addPrivilegeVerificationInterceptor(privileges: List<Privilege>) {
    intercept(ApplicationCallPipeline.Call) {
        val authorizedClientData = call.principal<AuthorizedClientData>()
        if (authorizedClientData?.privileges?.firstOrNull { it in privileges } == null)
            throw UnauthenticatedException()
    }
}