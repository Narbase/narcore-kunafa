package com.narbase.narcore.common.db.migrations

import com.narbase.narcore.common.db.migrations.usermigrations.AddRolesTable
import com.narbase.narcore.common.db.migrations.usermigrations.InitialMigration

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/12/26.
 */

fun initializeUserMigrations() {
    Migrations.userMigrations = listOf(
        InitialMigration,
        AddRolesTable,

        )
}