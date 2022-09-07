package com.narbase.narcore.web.network.calls.settings

import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.web.network.crud.CrudServerCaller

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/04.
 */
object AdminRolesServerCaller :
    CrudServerCaller<DynamicRoleDto, Unit>("/api/admin/v1/settings/roles") {

}
