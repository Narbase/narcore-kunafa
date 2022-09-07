package com.narbase.narcore.data.dto.roles

import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.common.data.roles.DynamicRole
import com.narbase.narcore.web.network.valueOfDto

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/07/02.
 */

typealias PrivilegeName = String

data class DynamicRoleDto(
    val id: String?,
    val name: String,
    val privileges: Array<PrivilegeName>,
    val isDoctor: Boolean
) {

    constructor(role: DynamicRole) : this(
        role.id,
        role.name,
        role.privileges.map { it.dtoName }.toTypedArray(),
        role.isDoctor
    )

}

val DynamicRoleDto.privilegesEnums
    get() = privileges.mapNotNull { valueOfDto<Privilege>(it) }
