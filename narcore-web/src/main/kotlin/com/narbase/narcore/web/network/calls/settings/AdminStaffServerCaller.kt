package com.narbase.narcore.web.network.calls.settings

import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.crud.CrudServerCaller
import com.narbase.narcore.web.utils.DataResponse

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object AdminStaffServerCaller :
    CrudServerCaller<AdminStaffServerCaller.StaffDto, AdminStaffServerCaller.Filters>("/api/admin/v1/settings/users") {

    suspend fun setUserActive(dto: EnableUserDto.RequestDto) =
        ServerCaller.synchronousPost<DataResponse<Unit>>(
            url = "/api/admin/v1/settings/enable_user",
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = dto
        )


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
