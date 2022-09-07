package com.narbase.narcore.common.db.migrations.usermigrations

import com.narbase.narcore.common.db.migrations.Migration
import com.narbase.narcore.common.db.migrations.version
import org.jetbrains.exposed.sql.transactions.transaction

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */
object AddRolesTable : Migration("AddRolesTable", version(2022, 9, 6, 10, 30)) {
    override fun up() {
        transaction {
            exec(
                """
                    CREATE TABLE IF NOT EXISTS roles (id uuid PRIMARY KEY, created_on TIMESTAMP DEFAULT (CURRENT_TIMESTAMP at time zone 'UTC') NOT NULL, is_deleted BOOLEAN DEFAULT false NOT NULL, "name" TEXT NOT NULL, "role" jsonb NOT NULL) ;
                    CREATE TABLE IF NOT EXISTS clients_roles (id uuid PRIMARY KEY, client_id uuid NOT NULL, role_id uuid NOT NULL, created_on TIMESTAMP DEFAULT (CURRENT_TIMESTAMP at time zone 'UTC') NOT NULL, CONSTRAINT fk_clients_roles_client_id_id FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT ON UPDATE RESTRICT, CONSTRAINT fk_clients_roles_role_id_id FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT ON UPDATE RESTRICT) ;
                """.trimIndent()
            )

        }

    }

    override fun down() {
        transaction {
            exec(
                """
            DROP table clients_roles;
            DROP table roles;
        """
            )
        }

    }

}
