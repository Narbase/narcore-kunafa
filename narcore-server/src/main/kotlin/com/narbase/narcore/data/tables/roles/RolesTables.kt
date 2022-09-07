package com.narbase.narcore.data.tables.roles

import com.narbase.narcore.data.columntypes.RolePrivilegesColumn
import com.narbase.narcore.data.columntypes.jsonColumn
import com.narbase.narcore.data.tables.*
import org.jetbrains.exposed.dao.id.UUIDTable

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/07/02.
 */

object RolesTable : LoggedTable, DeletableTable, UUIDTable("roles") {
    override val createdOn = createdOnColumn()
    override val isDeleted = deletedColumn()
    val name = text("name")
    val role = jsonColumn<RolePrivilegesColumn>("role")
}

object ClientsRolesTable : LoggedTable, UUIDTable("clients_roles") {
    val clientId = reference("client_id", ClientsTable)
    val dynamicRoleId = reference("role_id", RolesTable)
    override val createdOn = createdOnColumn()

}
