package com.narbase.narcore.data.access.users

import com.narbase.narcore.data.access.clients.ClientsDao
import com.narbase.narcore.data.access.common.ListFilter
import com.narbase.narcore.data.access.roles.ClientRolesDao
import com.narbase.narcore.data.conversions.id.toModel
import com.narbase.narcore.data.models.users.UserId
import com.narbase.narcore.data.models.users.UserRm
import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.data.tables.ClientsTable
import com.narbase.narcore.data.tables.UsersTable
import com.narbase.narcore.dto.domain.usersmanagement.UsersCrudDto
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object UsersRepository {

    fun create(
        username: String,
        password: String,
        fullName: String,
        callingCode: String,
        localPhone: String,
        rolesIds: List<UUID>
    ): UserId {
        return transaction {
            val clientId = ClientsDao.create(username, password)
            ClientRolesDao.saveClientRoles(clientId, rolesIds)
            val userId = UsersDao.create(clientId, fullName, callingCode, localPhone)
            userId
        }
    }

    fun update(
        clientId: UUID,
        username: String?,
        password: String?,
        fullName: String?,
        callingCode: String?,
        localPhone: String?,
        rolesIds: List<UUID>?,
    ) {
        return transaction {
            ClientsDao.update(clientId, username, password)
            UsersDao.update(clientId, fullName, callingCode, localPhone)
            rolesIds?.let { ClientRolesDao.saveClientRoles(clientId, rolesIds) }
        }
    }

    fun get(id: UserId): UserRm {
        return transaction {
            val user = UsersDao.get(id)
            val client = ClientsDao.get(user.clientId)
            val roles = ClientRolesDao.getClientRoles(user.clientId)
            UserRm(client, user, roles)
        }
    }

    fun get(clientId: UUID): UserRm {
        return transaction {
            val client = ClientsDao.get(clientId)
            val user = UsersDao.get(clientId)
            val roles = ClientRolesDao.getClientRoles(user.clientId)
            UserRm(client, user, roles)
        }
    }


    fun getList(filter: ListFilter<UsersCrudDto.Filters>): ListAndTotal<UserRm> {

        val searchTerm = filter.searchTerm
        val data = filter.customFilter

        return transaction {
            val query = ClientsTable
                .innerJoin(UsersTable)
                .select {
                    (UsersTable.isInactive eq (data?.getInactive ?: false))
                }
            if (data?.clientId != null) {
                query.andWhere { ClientsTable.id eq data.clientId?.toModel() }
            }
            if (searchTerm?.isNotBlank() == true) {
                query.andWhere {
                    (UsersTable.fullName.lowerCase() like "%${searchTerm.lowercase(Locale.getDefault())}%") or
                            (UsersTable.localPhone.lowerCase() like "%${searchTerm.lowercase(Locale.getDefault())}%") or
                            (UsersTable.callingCode.lowerCase() like "%${searchTerm.lowercase(Locale.getDefault())}%")
                }
            }
            val count = query.count()
            val list = query
                .orderBy(UsersTable.createdOn, SortOrder.DESC)
                .limit(filter.pageSize, filter.pageNo * filter.pageSize)
                .map {
                    val dynamicRoles = ClientRolesDao.getClientRoles(it[ClientsTable.id].value)
                    UserRm(ClientsDao.toModel(it), UsersDao.toModel(it), dynamicRoles)
                }
            ListAndTotal(list, count)
        }
    }
}
