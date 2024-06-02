package com.narbase.narcore.dto.models.roles

import com.narbase.narcore.dto.common.IdDto
import kotlin.js.JsExport

typealias PrivilegeName = String
@JsExport
data class RoleDto(
    val id: IdDto?,
    val name: String,
    val privileges: List<PrivilegeName>,
)