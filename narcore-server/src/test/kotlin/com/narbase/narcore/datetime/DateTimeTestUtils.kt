package com.narbase.narcore.datetime

import com.narbase.narcore.common.db.DatabaseConnector
import com.narbase.narcore.data.tables.createdOnColumn
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by nezuma
 * On: 5/10/21.
 */
object DateTimeTestUtils {
    fun connectToDb() {
        DatabaseConnector.connect()
    }

    fun createDateTimeTestTable() {
        transaction {
            SchemaUtils.create(DateTimeTestTable)
        }
    }

    fun dropDateTimeTestTable() {
        transaction {
            SchemaUtils.drop(DateTimeTestTable)
        }
    }

    fun resetDateTimeTestTable() {
        transaction {
            DateTimeTestTable.deleteAll()
        }
    }

    object DateTimeTestTable : Table("datetime_test") {
        val createdOn = createdOnColumn()
    }


}