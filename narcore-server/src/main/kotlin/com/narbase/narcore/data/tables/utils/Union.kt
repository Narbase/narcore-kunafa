package com.narbase.narcore.data.tables.utils

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.vendors.currentDialect

@Suppress("UNREACHABLE_CODE")
class Union(vararg val queries: Query) : Query(queries[0].set, null) {
    var count_: Boolean = false
    override fun arguments(): List<ArrayList<Pair<IColumnType<*>, Any?>>> {
        val allArgs = queries.map { it.arguments() }
        var answer: List<ArrayList<Pair<IColumnType<*>, Any?>>> = listOf()
        allArgs.forEach {
            answer = answer.zipLongest(it) { a, b ->
                a + b
            }
        }
        return answer
    }

    override fun count(): Long {
        count_ = true
        return super.count()
    }

    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
        val joinedQueriesWithUnion = queries.joinToString(" UNION ")
        {
            it.prepareSQL(QueryBuilder(true))
        }

        if (count_) {
            count_ = false
            return "SELECT COUNT(*) FROM (${joinedQueriesWithUnion}) x"
        }

        if (limit != null) {
            count_ = false
            return "$joinedQueriesWithUnion ${
                currentDialect.functionProvider.queryLimit(
                    limit!!,
                    offset,
                    orderByExpressions.isNotEmpty()
                )
            }"
        }

        return joinedQueriesWithUnion
    }
}

fun union(firstQuery: Query, secondQuery: Query): Union = Union(firstQuery, secondQuery)

private operator fun <T> Collection<T>?.plus(other: Collection<T>?): ArrayList<T> {
    val result = ArrayList<T>(this?.size + other?.size)
    if (this != null) {
        result.addAll(this)
    }
    if (other != null) {
        result.addAll(other)
    }
    return result
}

private operator fun Int?.plus(other: Int?) = when {
    this == null && other == null -> 10
    this == null -> other!!
    other == null -> this
    else -> this + other
}

private inline fun <T, R, V> Iterable<T>.zipLongest(other: Iterable<R>, transform: (a: T?, b: R?) -> V): List<V> {
    val first = iterator()
    val second = other.iterator()
    val list = ArrayList<V>(minOf(collectionSizeOrDefault(10), other.collectionSizeOrDefault(10)))
    while (first.hasNext() || second.hasNext()) {
        if (first.hasNext() && second.hasNext()) {
            list.add(transform(first.next(), second.next()))
        } else if (first.hasNext()) {
            list.add(transform(first.next(), null))
        } else {
            list.add(transform(null, second.next()))
        }
    }
    return list
}

private fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int =
    if (this is Collection<*>) this.size else default