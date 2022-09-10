package com.narbase.narcore.common.auth.basic

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.data.access.clients.ClientsDao
import com.narbase.narcore.data.access.roles.ClientRolesDao
import com.narbase.narcore.data.models.clients.Client
import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class BasicAuthenticator {

    fun validate(credentials: UserPasswordCredential): AuthorizedClientData? {
        sanitizeInputs(UserPasswordCredential(credentials.name, credentials.password)) ?: return null
        val client = verifyPassword(credentials.name, credentials.password) ?: return null
        return generateProfile(client)
    }

    private fun sanitizeInputs(credentials: UserPasswordCredential?): Boolean? {
        when {
            credentials == null -> return null
            credentials.name.isEmpty() -> return null
            credentials.password.isEmpty() -> return null
        }
        return true
    }

    private fun verifyPassword(username: String, password: String): Client? {
        val client = findClientByUsername(username) ?: return null
        return if (isPasswordCorrect(password, client)) client else null
    }

    private fun findClientByUsername(username: String): Client? {
        return runCatching { transaction { ClientsDao.getByUsername(username) } }.getOrNull()
    }

    private fun isPasswordCorrect(password: String, client: Client): Boolean {
        val hashedPassword = client.passwordHash
        return PasswordEncoder().checkPassword(password, hashedPassword)
    }

    private fun generateProfile(client: Client): AuthorizedClientData {

        val dynamicRoles = transaction { ClientRolesDao.getClientRoles(client.id) }
        val privileges = dynamicRoles.map { it.privileges }.flatten()

        return AuthorizedClientData(
            client.id.toString(),
            Date().time,
            privileges,
        )
    }

}

