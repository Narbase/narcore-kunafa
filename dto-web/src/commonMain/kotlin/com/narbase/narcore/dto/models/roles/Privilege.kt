package com.narbase.narcore.dto.models.roles

import com.narbase.narcore.dto.common.EnumDtoName

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

enum class Privilege(override val dtoName: String) : EnumDtoName {
    // User
    BasicUser("BasicUser"),

    //Admin
    UsersManagement("UsersManagement"),


    ;

    companion object {

        val adminAreaPrivileges by lazy {
            listOf(
                UsersManagement,
            )
        }
    }
}