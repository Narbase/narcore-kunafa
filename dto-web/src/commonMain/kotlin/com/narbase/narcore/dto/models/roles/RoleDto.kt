package com.narbase.narcore.dto.models.roles

import com.narbase.narcore.dto.common.IdDto

typealias PrivilegeName = String

data class RoleDto(
    val id: IdDto?,
    val name: String,
    val privileges: List<PrivilegeName>,
)