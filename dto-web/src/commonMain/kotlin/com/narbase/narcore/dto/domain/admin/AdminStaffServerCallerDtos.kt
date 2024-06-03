package com.narbase.narcore.dto.domain.admin

import com.narbase.narcore.dto.models.roles.DynamicRoleDto
import kotlin.js.JsExport

@JsExport
object AdminStaffServerCallerDtos {
    @Suppress("unused")
    class Filters(
        val getInactive: Boolean?,
        val clientId: String?
    )


    @Suppress("unused")
    class StaffDto(
        val clientId: String?,
        val userId: String?,
        val username: String,
        val password: String,
        val fullName: String,
        val callingCode: String,
        val localPhone: String,
        val dynamicRoles: Array<DynamicRoleDto>
    )
}