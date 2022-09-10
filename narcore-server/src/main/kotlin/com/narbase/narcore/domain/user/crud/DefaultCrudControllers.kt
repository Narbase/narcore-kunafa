package com.narbase.narcore.domain.user.crud

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.InvalidRequestException
import com.narbase.narcore.common.exceptions.UnauthenticatedException
import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.data.tables.DeletableTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.reflect.KClass

abstract class DefaultCrudController<D : DefaultCrudController.UUIDItem>(
    dtoClass: KClass<D>,
    val dbTable: UUIDTable,
    val searchableDbFields: List<Column<out String?>>
) : CrudController<D, Unit>(dtoClass, Unit::class) {
    open class UUIDItem(val id: UUID?)

    override fun getItemsList(
        pageNo: Long,
        pageSize: Int,
        searchTerm: String,
        filters: Map<String, String>,
        data: Unit?,
        clientData: AuthorizedClientData?
    ): ListAndTotal<D> {
        return transaction {
            getListFromDB(pageNo, pageSize, searchTerm, filters, data.toString())
        }
    }

    open fun getListFromDB(
        pageNo: Long,
        pageSize: Int,
        searchTerm: String,
        filters: Map<String, String>,
        data: String?
    ): ListAndTotal<D> {
        val query = dbTable.selectAll()
        if (dbTable is DeletableTable)
            query.andWhere { dbTable.isDeleted eq false }
        if (searchableDbFields.isNotEmpty()) {
            query.searchMultipleColumns(searchableDbFields, searchTerm)
        }
        if (filters.isNotEmpty())
            query.applyFilters(filters)
        val total = query.count()
        query.limit(pageSize, pageNo * pageSize)
        val list = query.map { dbToDto(it) }
        return ListAndTotal(list, total)
    }

    open fun Query.applyFilters(filters: Map<String, String>) {

    }

    abstract fun dbToDto(resultRow: ResultRow): D


    override fun deleteItem(id: UUID?, clientData: AuthorizedClientData?) {
        if (dbTable is DeletableTable) {
            val updatedRows = transaction {
                dbTable.update({ dbTable.id eq id }) {
                    it[dbTable.isDeleted] = true
                }
            }
            if (updatedRows == 0) throw InvalidRequestException("No rows match the id sent by the request, nothing was deleted")
        } else throw InvalidRequestException("Table is not deletable")
    }

    override fun createItem(item: D, clientData: AuthorizedClientData?): D {
        clientData ?: throw UnauthenticatedException()
        return transaction {
            val itemId = dbTable.insertAndGetId {
                insertValuesInFields(item, it, clientData)
            }
            dbToDto(dbTable.select { dbTable.id eq itemId }.first())
        }
    }

    abstract fun Table.insertValuesInFields(
        requestDto: D,
        insertStatement: UpdateBuilder<Number>,
        clientData: AuthorizedClientData
    )

    override fun updateItem(item: D, clientData: AuthorizedClientData?): D {
        clientData ?: throw UnauthenticatedException()
        return transaction {
            val updatedRows = dbTable.update({ dbTable.id eq item.id }) {
                updateValuesInFields(item, it, clientData)
            }
            if (updatedRows == 0) throw InvalidRequestException("No rows match the id sent by the request, nothing was updated")
            dbToDto(dbTable.select { dbTable.id eq item.id }.first())
        }
    }

    open fun Table.updateValuesInFields(
        requestDto: D,
        it: UpdateStatement,
        clientData: AuthorizedClientData
    ) = insertValuesInFields(
        requestDto,
        it,
        clientData
    )

}

fun Query.andWhere(andPart: SqlExpressionBuilder.() -> Op<Boolean>) = adjustWhere {
    val expr = Op.build { andPart() }
    if (this == null) expr
    else this and expr
}

fun Query.searchMultipleColumns(columns: List<ExpressionWithColumnType<out String?>>, searchTerm: String) =
    adjustWhere {
        val searchWords = searchTerm.split(" ")
        val expr =
            searchWords.map { word ->
                columns.map { Op.build { it.lowerCase() like "%${word.lowercase(Locale.getDefault())}%" } }
                    .reduce { acc, op -> acc or op }
            }.reduce { acc, op -> acc and op }
        if (this == null) expr
        else this and expr
    }


