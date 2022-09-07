package com.narbase.narcore.web.network.calls.settings

import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.crud.CrudServerCaller
import com.narbase.narcore.web.utils.DataResponse

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/04.
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
