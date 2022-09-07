package com.narbase.narcore.web.storage

import com.narbase.narcore.dto.models.roles.Privilege

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/18.
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
