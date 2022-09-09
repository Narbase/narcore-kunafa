package com.narbase.narcore.common.db.migrations.usermigrations

import com.narbase.narcore.common.db.migrations.Migration
import com.narbase.narcore.common.db.migrations.version
import org.jetbrains.exposed.sql.transactions.transaction

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object InitialMigration : Migration("Initial schema", version(2022, 9, 5, 5, 5)) {
    override fun up() {
        transaction {
            exec(initialSchema)

            //Restore search_path config
            exec(""" SELECT pg_catalog.set_config('search_path', '"${'$'}user", public', false);""")

        }

    }

    override fun down() {
        transaction {
            val truncateSql = """
            DROP SCHEMA public CASCADE;
            CREATE SCHEMA public;
            GRANT ALL ON SCHEMA public TO postgres;
            GRANT ALL ON SCHEMA public TO public;
            COMMENT ON SCHEMA public IS 'standard public schema';
        """.trimIndent()
            exec(truncateSql)
        }

    }

}

private const val initialSchema: String = """
    
CREATE TABLE IF NOT EXISTS clients (id uuid PRIMARY KEY, username TEXT NOT NULL, password_hash TEXT NOT NULL, last_login TIMESTAMP NULL, created_on TIMESTAMP DEFAULT (CURRENT_TIMESTAMP at time zone 'UTC') NOT NULL) ;
CREATE TABLE IF NOT EXISTS staff (id uuid PRIMARY KEY, client_id uuid NOT NULL, full_name TEXT NOT NULL, calling_code TEXT NOT NULL, local_phone TEXT NOT NULL, is_inactive BOOLEAN DEFAULT false NOT NULL, is_deleted BOOLEAN DEFAULT false NOT NULL, created_on TIMESTAMP DEFAULT (CURRENT_TIMESTAMP at time zone 'UTC') NOT NULL, CONSTRAINT fk_staff_client_id_id FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT ON UPDATE RESTRICT) ;
CREATE TABLE IF NOT EXISTS device_tokens (id uuid PRIMARY KEY, token TEXT NOT NULL, client_id uuid NOT NULL, created_on TIMESTAMP DEFAULT (CURRENT_TIMESTAMP at time zone 'UTC') NOT NULL, CONSTRAINT fk_device_tokens_client_id_id FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE RESTRICT ON UPDATE RESTRICT) ;
CREATE TABLE IF NOT EXISTS app_config (id uuid PRIMARY KEY, permissive_user_code INT NOT NULL, minimum_user_code INT NOT NULL) ;
CREATE TABLE IF NOT EXISTS sms_record (id uuid PRIMARY KEY, message TEXT NOT NULL, phones jsonb NOT NULL, status VARCHAR(32) NOT NULL, created_on TIMESTAMP DEFAULT (CURRENT_TIMESTAMP at time zone 'UTC') NOT NULL) ;

"""