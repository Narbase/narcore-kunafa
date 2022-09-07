package com.narbase.narcore.dto.domain.usersmanagement

import com.narbase.narcore.dto.common.IdDto
import com.narbase.narcore.dto.models.roles.RoleDto

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */
object UsersCrudDto {

    class Filters(
        val getInactive: Boolean?,
        val clientId: IdDto?,
    )


    class User(
        var clientId: IdDto?,
        var userId: IdDto?,
        val username: String,
        val password: String,
        val fullName: String,
        val callingCode: String,
        val localPhone: String,
        val dynamicRoles: Array<RoleDto>
    )
}