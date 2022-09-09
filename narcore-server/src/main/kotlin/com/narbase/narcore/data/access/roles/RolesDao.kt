package com.narbase.narcore.data.access.roles

import com.narbase.narcore.data.columntypes.RolePrivilegesColumn
import com.narbase.narcore.data.models.roles.Role
import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.data.tables.roles.RolesTable
import com.narbase.narcore.data.tables.utils.ilike
import com.narbase.narcore.domain.user.crud.andWhere
import com.narbase.narcore.dto.models.roles.Privilege
import org.jetbrains.exposed.sql.*
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object RolesDao {
    fun getList(pageNo: Long, pageSize: Int, searchTerm: String): ListAndTotal<Role> {
        val query = RolesTable.select { RolesTable.isDeleted eq false }
        if (searchTerm.isNotBlank()) {
            query.andWhere {
                (RolesTable.name ilike "%$searchTerm%")
            }
        }
        val count = query.count()
        val list = query
            .orderBy(RolesTable.createdOn to SortOrder.DESC)
            .limit(pageSize, pageNo * pageSize)
            .map(::toModel)
        return ListAndTotal(list, count)
    }

    fun get(id: UUID) = RolesTable
        .select { RolesTable.id eq id }
        .map(::toModel)
        .first()


    fun toModel(row: ResultRow): Role {
        val roleData = row[RolesTable.role]
        return Role(
            row[RolesTable.id].value,
            row[RolesTable.createdOn],
            row[RolesTable.name],
            roleData.privileges,
        )
    }

    fun create(name: String, privileges: List<Privilege>): UUID {
        val id = RolesTable.insert {
            it[RolesTable.name] = name
            it[role] = RolePrivilegesColumn(privileges)
        } get RolesTable.id
        return id.value
    }

    fun update(id: UUID, name: String, privileges: List<Privilege>) {
        RolesTable.update({
            RolesTable.id eq id
        }) {
            it[RolesTable.name] = name
            it[role] = RolePrivilegesColumn(privileges)
        }
    }

    fun delete(id: UUID) {
        RolesTable.update({
            RolesTable.id eq id
        }) {
            it[isDeleted] = true
        }
    }
}