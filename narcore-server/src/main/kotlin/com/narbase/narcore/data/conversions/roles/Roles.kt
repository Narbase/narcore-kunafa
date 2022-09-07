package com.narbase.narcore.data.conversions.roles

import com.narbase.narcore.core.valueOfDto
import com.narbase.narcore.data.conversions.id.toDto
import com.narbase.narcore.data.models.roles.Role
import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.dto.models.roles.RoleDto

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/06.
 */

fun Role.toDto() = RoleDto(id.toDto(), name, privileges.map { it.dtoName })

val RoleDto.privilegesEnums get() = privileges.mapNotNull { valueOfDto<Privilege>(it) }

