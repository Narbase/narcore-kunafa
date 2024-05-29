package com.narbase.narcore.data.access.roles

import com.narbase.narcore.data.models.roles.Role
import com.narbase.narcore.data.tables.ClientsTable
import com.narbase.narcore.data.tables.roles.ClientsRolesTable
import com.narbase.narcore.data.tables.roles.RolesTable
import com.narbase.narcore.data.tables.utils.toEntityId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object ClientRolesDao {
    fun getClientRoles(clientId: UUID): List<Role> {
        val dynamicRolesDao = RolesDao
        return ClientsRolesTable
            .leftJoin(RolesTable, { dynamicRoleId }, { RolesTable.id })
            .selectAll().where { ClientsRolesTable.clientId eq clientId }.map(dynamicRolesDao::toModel)
    }

    fun saveClientRoles(clientId: UUID, rolesIds: List<UUID>) {
        ClientsRolesTable.deleteWhere {
            ClientsRolesTable.clientId eq clientId
        }
        ClientsRolesTable.batchInsert(rolesIds) { roleId ->
            this[ClientsRolesTable.clientId] = clientId.toEntityId(ClientsTable)
            this[ClientsRolesTable.dynamicRoleId] = roleId.toEntityId(RolesTable)
        }
    }
}