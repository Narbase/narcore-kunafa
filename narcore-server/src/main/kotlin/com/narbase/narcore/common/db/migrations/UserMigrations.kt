package com.narbase.narcore.common.db.migrations

import com.narbase.narcore.common.db.migrations.usermigrations.AddRolesTable
import com.narbase.narcore.common.db.migrations.usermigrations.InitialMigration

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun initializeUserMigrations() {
    Migrations.userMigrations = listOf(
        InitialMigration,
        AddRolesTable,

        )
}