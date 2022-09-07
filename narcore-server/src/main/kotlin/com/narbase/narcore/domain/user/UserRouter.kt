package com.narbase.narcore.domain.user

import com.narbase.narcore.common.auth.AuthenticationConstants
import com.narbase.narcore.domain.user.files.CreateFileController
import com.narbase.narcore.domain.user.login.LoginController
import com.narbase.narcore.domain.user.profile.GetProfileController
import com.narbase.narcore.domain.user.profile.UpdateProfileController
import com.narbase.narcore.domain.user.profile.UserPasswordController
import com.narbase.narcore.domain.user.websocket.WebSocketController
import com.narbase.narcore.domain.utils.addInactiveUserInterceptor
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.websocket.*

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by ${user}
 * On: ${date}.
 */
const val myWsCustomAuthHeader = "ws_custom_auth"

fun Routing.setupUserRoutes() {
    route("/public/api/user/v1") {
        webSocket("/socket", myWsCustomAuthHeader) {
            call.request.header("")
            WebSocketController.handle(this)
        }
    }

    authenticate(AuthenticationConstants.JWT_AUTH) {
        route("/api/user") {
            addInactiveUserInterceptor()
            route("/v1") {
                post("/login") { LoginController().handle(call) }

                post("/upload_file") { CreateFileController(shouldCompress = true).handle(call) }
                post("/upload_raw_file") { CreateFileController(shouldCompress = false).handle(call) }

                route("/profile") {
                    post("/details") {
                        GetProfileController().handle(call)
                    }
                    post("/update") {
                        UpdateProfileController().handle(call)
                    }
                    post("/update_password") { UserPasswordController().handle(call) }
                }
            }
        }
    }
}

