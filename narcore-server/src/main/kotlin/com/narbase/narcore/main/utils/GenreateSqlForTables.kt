package com.narbase.narcore.main.utils

import com.narbase.narcore.common.db.DatabaseConnector
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/04/24.
 */

fun generateSqlForTables(vararg tables: Table) {
    DatabaseConnector.connect()
    transaction {
        println("\n\nPrint statements\n\n")
        tables.forEach { table ->
            table.createStatement().forEach {
                println("$it ;")
            }
        }
    }

}