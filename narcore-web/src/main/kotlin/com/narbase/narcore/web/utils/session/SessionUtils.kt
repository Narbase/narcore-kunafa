package com.narbase.narcore.web.utils.session

import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.storage.SessionInfo

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/23.
 */

inline fun authorized(privileges: List<Privilege> = listOf(), block: () -> Unit) {
    if (isAuthorized(privileges)) {
        block()
    }
}

fun isAuthorized(privileges: List<Privilege> = listOf()): Boolean {
    return SessionInfo.loggedInUser.privileges.any { it in privileges }
}
