package com.narbase.narcore.data.dto.roles

import com.narbase.narcore.dto.common.valueOfDto
import com.narbase.narcore.dto.models.roles.DynamicRoleDto
import com.narbase.narcore.dto.models.roles.Privilege

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

typealias PrivilegeName = String

val DynamicRoleDto.privilegesEnums
    get() = privileges.mapNotNull { valueOfDto<Privilege>(it) }
