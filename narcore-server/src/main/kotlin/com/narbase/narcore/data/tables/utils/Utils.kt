package com.narbase.narcore.data.tables.utils


import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.joda.time.DateTime
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.*

fun <T : Any> String.execAndMap(transform: (ResultSet) -> T): List<T> {
    val result = arrayListOf<T>()
    TransactionManager.current().exec(this) { rs ->
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}


private fun <T : Any> Transaction.preparedExecAndMap(
    sql: String,
    args: Iterable<Pair<IColumnType, Any?>>,
    transform: (ResultSet) -> T
): List<T> {
    val result = arrayListOf<T>()
    val rs = connection.prepareStatement(sql, true)
        .apply { fillParameters(args) }.run {
            if (sql.lowercase(Locale.getDefault()).startsWith("select "))
                executeQuery()
            else {
                executeUpdate()
                resultSet
            }
        }
    rs?.let {
        while (rs.next()) {
            result += transform(rs)
        }
    }
    return result
}

fun <T : Any> String.preparedExecAndMap(args: Iterable<Pair<IColumnType, Any?>>, transform: (ResultSet) -> T): List<T> {
    return TransactionManager.current().preparedExecAndMap(this, args, transform)
}

fun Column<DateTime>.absTimeFromNow() = AbsIntervalExpression("${this.name} - now()")

class AbsIntervalExpression(
    private val expression: String
) : Expression<BigDecimal?>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit =
        queryBuilder { append("abs(extract(epoch from $expression))") }
}


class ILikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(expr1, expr2, "ILIKE")

infix fun <T : String?> Expression<T>.ilike(pattern: String): ILikeOp = ILikeOp(this, stringParam(pattern))

fun Column<*>.jsonbArrayElementsExpression(elementName: String = "j") =
    JsonbArrayElementsExpression(this.name, elementName)

class JsonbArrayElementsExpression(
    private val expression: String, val elementName: String
) : Expression<String?>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit =
        queryBuilder { append("jsonb_array_elements(\"$expression\") $elementName") }
}


class existsString(
    val query: String
) : Op<Boolean>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder): Unit = queryBuilder {
        append("EXISTS (")
        append(query)
        append(")")
    }
}

