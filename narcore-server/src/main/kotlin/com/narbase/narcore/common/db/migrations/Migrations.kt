package com.narbase.narcore.common.db.migrations

import com.narbase.narcore.data.columntypes.EnumPersistenceName
import com.narbase.narcore.data.columntypes.InvalidPersistedEnumValueException
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object Migrations {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    lateinit var userMigrations: List<Migration>

    const val MIGRATIONS_TABLE = "db_migrations"

    enum class MigrationDirection(override val persistenceName: String) : EnumPersistenceName {
        Up("Up"),
        Down("Down")
    }

    private fun initialize() {
        log.info("Initializing db migrations")
        transaction {

            val tables = connection.metadata {
                tableNames
            }["public"]
            val tableExists = tables?.contains("public.${MIGRATIONS_TABLE}") == true
            if (tableExists.not()) {
                log.info("Creating migrations table")
                createTable()
            }
            addDownColumn()
            log.info("Migrations table initialized successfully")
            fixVersionNamingBug()
        }

    }

    private fun Transaction.addDownColumn() {
        val directionColumnExists = exec(
            """
                    select column_name from information_schema.columns where table_name ='$MIGRATIONS_TABLE' and column_name = 'direction';
                """.trimIndent()
        ) { it.next() } == true

        if (directionColumnExists.not()) {
            exec(
                """
                ALTER TABLE $MIGRATIONS_TABLE ADD column direction text not null default '${MigrationDirection.Up.persistenceName}' ;
                alter table $MIGRATIONS_TABLE alter COLUMN direction drop default ;
            """.trimMargin()
            )
        }

    }

    private fun Transaction.fixVersionNamingBug() {
        exec(
            """
    update db_migrations SET version  = (select concat_ws('.', split_part(version, '.', 1), lpad(split_part(version, '.', 2), 2, '0'), lpad(split_part(version, '.', 3), 2, '0'), lpad(split_part(version, '.', 4), 2, '0'), lpad(split_part(version, '.', 5), 2, '0')));  
              """.trimMargin()
        )
    }

    private fun Transaction.createTable() {
        exec(
            "CREATE TABLE $MIGRATIONS_TABLE ("
                    + "id SERIAL,"
                    + "name VARCHAR(1000) NULL,"
                    + "version VARCHAR(128) NOT NULL,"
                    + "installed_on timestamp NOT NULL default now(),"
                    + "direction text NOT NULL,"
                    + "PRIMARY KEY (id))"
        )
    }

    fun getLastAppliedMigration(): String? { //version
        val existingMigrations = getMigrationsInDb()
        val currentlyAppliedMigrations = mutableListOf<ExistingMigration>()
        existingMigrations.sortedBy { it.installedOn }.forEach { existingMigration ->
            if (existingMigration.direction == MigrationDirection.Up) {
                currentlyAppliedMigrations.add(existingMigration)
            } else {
                currentlyAppliedMigrations.removeAll { existingMigration.version == it.version }
            }
        }
        return currentlyAppliedMigrations.minBy { it.version }.version
    }

    fun migrate(targetVersion: String? = null) {
        initialize()
        val duplicatedVersions = userMigrations.groupBy { it.version }.filter { it.value.size > 1 }
        if (duplicatedVersions.isNotEmpty()) {
            throw RuntimeException(
                "Duplicate migrations error: ${
                    duplicatedVersions.map { it.key }.joinToString()
                } has duplicated migrations"
            )
        }

        val existingMigrations = getMigrationsInDb()
        val relevantMigrations = userMigrations
            .sortedBy { it.version }
        val targetMigration = targetVersion?.let { target -> relevantMigrations.firstOrNull { it.version == target } }
            ?: relevantMigrations.last()
        val currentlyAppliedMigrations = mutableListOf<ExistingMigration>()
        existingMigrations.sortedBy { it.installedOn }.forEach { existingMigration ->
            if (existingMigration.direction == MigrationDirection.Up) {
                currentlyAppliedMigrations.add(existingMigration)
            } else {
                currentlyAppliedMigrations.removeAll { existingMigration.version == it.version }
            }
        }
        val shouldMigrateUp =
            (relevantMigrations.filter { it.version <= targetMigration.version }.map { it.version }.toSet() -
                    currentlyAppliedMigrations.map { it.version }
                        .toSet()).isNotEmpty() //i.e. there are relevant migrations that are not currently applied

        if (shouldMigrateUp) {
            var newlyApplied = false
            val uniqueVersions = mutableListOf<String>()
            relevantMigrations
                .forEach {
                    if (it.version in uniqueVersions) {
                        log.error("Version ${it.version} is duplicated")
                        throw RuntimeException("Version ${it.version} is duplicated")
                    }
                    uniqueVersions.add(it.version)
                    if (it.version in currentlyAppliedMigrations.map { it.version }) {
                        if (newlyApplied) {
                            log.warn("Out of order migration detected")
                        }
                    } else {
                        newlyApplied = true
                        log.info("Migrating up version ${it.version}: ${it.name}")
                        transaction {
                            it.up()
                            val sql =
                                "INSERT INTO $MIGRATIONS_TABLE(name, version, direction) values(\'${it.name}\', \'${it.version}\',\'${MigrationDirection.Up.persistenceName}\');"
                            exec(sql)
                        }
                    }
                }
        } else {
            var newlyApplied = false
            val uniqueVersions = mutableListOf<String>()
            val migrationVersionsToDowngrade =
                currentlyAppliedMigrations.filter { it.version > targetMigration.version }
                    .sortedByDescending { it.installedOn }.map { it.version }
                    .toSet()
            val migrationsToDowngrade = migrationVersionsToDowngrade.toList().map { version ->
                userMigrations.firstOrNull { it.version == version }
                    ?: throw UnknownError("There is no migration in the code with version = $version")

            }
            migrationsToDowngrade
                .forEach {
                    if (it.version in uniqueVersions) {
                        log.error("Version ${it.version} is duplicated")
                        throw RuntimeException("Version ${it.version} is duplicated")
                    }
                    uniqueVersions.add(it.version)
                    if (it.version in currentlyAppliedMigrations.map { it.version }) { // if it is currently up
                        newlyApplied = true
                        log.info("Migrating down version ${it.version}: ${it.name}")
                        transaction {
                            it.down()
                            val sql =
                                "INSERT INTO $MIGRATIONS_TABLE(name, version, direction) values(\'${it.name}\', \'${it.version}\',\'${MigrationDirection.Down.persistenceName}\');"
                            exec(sql)
                        }
                        if (newlyApplied) {
                            log.warn("Out of order migration detected")
                        }
                    } else {
                        if (newlyApplied) {
                            log.warn("Out of order migration detected")
                        }
                    }
                }

        }
    }

    private class ExistingMigration(
        val version: String,
        val installedOn: DateTime,
        val direction: MigrationDirection
    )

    private fun getMigrationsInDb(): List<ExistingMigration> {
        val existingMigrationsList = mutableListOf<ExistingMigration>()
        transaction {
            exec("SELECT version, direction, installed_on from $MIGRATIONS_TABLE") { results ->

                while (results.next()) {
                    val direction = results.getString("direction")
                    val version = results.getString("version")
                    val installedOn = DateTime(results.getTimestamp("installed_on").time)
                    existingMigrationsList.add(
                        ExistingMigration(
                            version,
                            installedOn,
                            MigrationDirection.values().firstOrNull {
                                it.persistenceName == direction
                            }
                                ?: throw InvalidPersistedEnumValueException("$direction is invalid value for MigrationDirection Enum")
                        )
                    )
                }
            }
        }
        return existingMigrationsList.sortedBy { it.version }
    }
}
