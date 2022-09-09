package com.narbase.narcore.dto.domain.usersmanagement

import com.narbase.narcore.dto.common.IdDto
import com.narbase.narcore.dto.models.roles.RoleDto

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
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