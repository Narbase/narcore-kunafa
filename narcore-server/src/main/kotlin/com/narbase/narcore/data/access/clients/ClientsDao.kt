package com.narbase.narcore.data.access.clients

import com.narbase.narcore.common.auth.basic.PasswordEncoder
import com.narbase.narcore.data.models.clients.Client
import com.narbase.narcore.data.tables.ClientsTable
import org.jetbrains.exposed.sql.*
import org.joda.time.DateTime
import java.util.*


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object ClientsDao {
    fun create(username: String, password: String): UUID {
        val id = ClientsTable.insert {
            it[ClientsTable.username] = username
            it[passwordHash] = PasswordEncoder().encode(password)
        } get ClientsTable.id
        return id.value
    }

    fun update(id: UUID, username: String?, password: String?) {
        ClientsTable.update({ ClientsTable.id eq id }) { row ->
            username?.let { row[ClientsTable.username] = it }
            password?.let { row[ClientsTable.username] = PasswordEncoder().encode(it) }
        }
    }

    fun getClients(pageNo: Long, pageSize: Int): List<Client> {
        return ClientsTable
            .selectAll()
            .limit(pageSize, pageNo * pageSize)
            .map(::toModel)
    }

    fun get(id: UUID) = ClientsTable
        .select { ClientsTable.id eq id }
        .map(::toModel)
        .first()

    fun getByUsername(username: String) = ClientsTable
        .select { ClientsTable.username eq username }
        .map(::toModel)
        .first()

    fun updateLastLogin(id: UUID) {
        ClientsTable.update({ (ClientsTable.id eq id) }) {
            it[lastLogin] = DateTime.now()
        }
    }

    fun toModel(row: ResultRow): Client {
        return Client(
            row[ClientsTable.id].value,
            row[ClientsTable.createdOn],
            row[ClientsTable.username],
            row[ClientsTable.passwordHash],
            row[ClientsTable.lastLogin],
        )
    }

}