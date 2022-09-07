package com.narbase.narcore.dto.models.roles

import com.narbase.narcore.dto.common.EnumDtoName

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
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