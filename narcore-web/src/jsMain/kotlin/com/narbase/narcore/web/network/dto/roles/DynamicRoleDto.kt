package com.narbase.narcore.data.dto.roles

import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.network.valueOfDto

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

typealias PrivilegeName = String
@JsExport
data class DynamicRoleDto(
    val id: String?,
    val name: String,
    val privileges: Array<PrivilegeName>,
    val isDoctor: Boolean
) {

}

val DynamicRoleDto.privilegesEnums
    get() = privileges.mapNotNull { valueOfDto<Privilege>(it) }
