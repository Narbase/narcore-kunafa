package com.narbase.narcore.data.conversions.roles

import com.narbase.narcore.core.valueOfDto
import com.narbase.narcore.data.conversions.id.toDto
import com.narbase.narcore.data.models.roles.Role
import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.dto.models.roles.RoleDto

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun Role.toDto() = RoleDto(id.toDto(), name, privileges.map { it.dtoName })

val RoleDto.privilegesEnums get() = privileges.mapNotNull { valueOfDto<Privilege>(it) }

