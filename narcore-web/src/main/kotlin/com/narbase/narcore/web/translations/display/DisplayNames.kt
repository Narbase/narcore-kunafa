package com.narbase.narcore.web.translations.display

import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.utils.string.splitCamelCase

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */

val Privilege.displayName: String
    get() {
        return when (this) {
            Privilege.BasicUser -> "Basic User"
            Privilege.UsersManagement -> "Users Management"
            else -> name.splitCamelCase()
        }
    }