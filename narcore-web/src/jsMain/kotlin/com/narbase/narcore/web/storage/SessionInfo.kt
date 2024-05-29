package com.narbase.narcore.web.storage

import com.narbase.narcore.dto.models.roles.Privilege

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object SessionInfo {
    var currentUser: CurrentUserProfile? = null
    val loggedInUser
        get() = currentUser ?: throw RuntimeException("loggedInUser can only be accessed when user is logged in")
}

class CurrentUserProfile(
    val clientId: String,
    val userId: String,
    val fullName: String,
    val userName: String,
    val callingCode: String,
    val localPhone: String,
    val privileges: List<Privilege>
)
