package com.narbase.narcore.web.network.calls.settings

import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.web.network.crud.CrudServerCaller

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object AdminRolesServerCaller :
    CrudServerCaller<DynamicRoleDto, Unit>("/api/admin/v1/settings/roles") {

}
