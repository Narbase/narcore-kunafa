package com.narbase.narcore.domain.utils

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.InvalidRequestException
import com.narbase.narcore.data.access.clients.ClientsDao
import com.narbase.narcore.data.models.clients.Client
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

val AuthorizedClientData.client: Client?
    get() = runCatching { authenticatedClient }.getOrNull()

val AuthorizedClientData?.authenticatedClient: Client
    get() = transaction { ClientsDao.get(UUID.fromString(this@authenticatedClient?.id)) }

fun <T : Table> T.updateMustAffectRows(
    where: SqlExpressionBuilder.() -> Op<Boolean>,
    limit: Int? = null,
    errorMessage: String = "No rows match the update criteria",
    body: T.(UpdateStatement) -> Unit
): Int {
    val numOfUpdatedRows = update(where, limit, body)
    if (numOfUpdatedRows == 0)
        throw InvalidRequestException(errorMessage)
    return numOfUpdatedRows
}

fun Map<String, String>.getFromDto(key: String, isOptional: Boolean = true): String? {
    return if (isOptional) {
        if (containsKey(key)) {
            this[key]
        } else null
    } else {
        this[key] ?: throw InvalidRequestException("Request doesn't include $key filter")
    }
}


