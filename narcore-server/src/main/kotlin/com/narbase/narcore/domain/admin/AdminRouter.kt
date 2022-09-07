package com.narbase.narcore.domain.admin

import com.narbase.narcore.common.auth.AuthenticationConstants
import com.narbase.narcore.domain.admin.staff.EnableStaffController
import com.narbase.narcore.domain.admin.staff.UsersCrudController
import com.narbase.narcore.domain.admin.staff.roles.RolesCurdController
import com.narbase.narcore.domain.user.crud.crud
import com.narbase.narcore.domain.utils.addInactiveUserInterceptor
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

fun Routing.setupAdminRoutes() {
    authenticate(AuthenticationConstants.JWT_AUTH) {
        route("/api/admin") {
            addInactiveUserInterceptor()
            route("/v1") {

                route("/settings") {
                    crud(
                        "/users",
                        UsersCrudController(),
                        Privilege.UsersManagement,
                    )
                    crud(
                        "/roles",
                        RolesCurdController(),
                        Privilege.UsersManagement,
                    )
                    post("/enable_user") {
                        EnableStaffController().handle(call)
                    }
                }
            }
        }
    }
}