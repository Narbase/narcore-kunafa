package com.narbase.narcore.data.access.users

import com.narbase.narcore.data.models.users.User
import com.narbase.narcore.data.models.users.UserId
import com.narbase.narcore.data.tables.ClientsTable
import com.narbase.narcore.data.tables.UsersTable
import com.narbase.narcore.data.tables.utils.toEntityId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object UsersDao {

    fun create(clientId: UUID, fullName: String, callingCode: String, localPhone: String): UserId {
        val id = UsersTable.insert {
            it[UsersTable.clientId] = clientId.toEntityId(ClientsTable)
            it[UsersTable.fullName] = fullName
            it[UsersTable.callingCode] = callingCode
            it[UsersTable.localPhone] = localPhone
        } get UsersTable.id
        return UserId(id.value)
    }

    fun update(clientId: UUID, fullName: String?, callingCode: String?, localPhone: String?) {
        UsersTable.update({ UsersTable.clientId eq clientId }) { row ->
            fullName?.let { row[UsersTable.fullName] = it }
            callingCode?.let { row[UsersTable.callingCode] = it }
            localPhone?.let { row[UsersTable.localPhone] = it }
        }
    }

    fun update(id: UserId, fullName: String?, callingCode: String?, localPhone: String?) {
        UsersTable.update({ UsersTable.id eq id.value }) { row ->
            fullName?.let { row[UsersTable.fullName] = it }
            callingCode?.let { row[UsersTable.callingCode] = it }
            localPhone?.let { row[UsersTable.localPhone] = it }
        }
    }

    fun get(id: UserId) = UsersTable
        .select { UsersTable.id eq id.value }
        .map(::toModel)
        .first()

    fun get(clientId: UUID) = UsersTable
        .select { UsersTable.clientId eq clientId }
        .map(::toModel)
        .first()


    fun toModel(row: ResultRow): User {
        return User(
            UserId(row[UsersTable.id].value),
            row[UsersTable.createdOn],
            row[UsersTable.clientId].value,
            row[UsersTable.fullName],
            row[UsersTable.callingCode],
            row[UsersTable.localPhone],
            row[UsersTable.isInactive],
            row[UsersTable.isDeleted],
        )
    }

}

