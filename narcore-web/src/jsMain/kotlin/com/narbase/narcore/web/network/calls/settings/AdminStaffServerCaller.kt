package com.narbase.narcore.web.network.calls.settings

import com.narbase.narcore.dto.domain.admin.AdminStaffServerCallerDtos
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.crud.CrudServerCaller
import com.narbase.narcore.dto.common.network.DataResponse

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object AdminStaffServerCaller :
    CrudServerCaller<AdminStaffServerCallerDtos.StaffDto, AdminStaffServerCallerDtos.Filters>("/api/admin/v1/settings/users") {

    suspend fun setUserActive(dto: EnableUserDto.RequestDto) =
        ServerCaller.synchronousPost<DataResponse<Unit>>(
            url = "/api/admin/v1/settings/enable_user",
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = dto
        )


}

