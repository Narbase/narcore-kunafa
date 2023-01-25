package com.narbase.narcore.common.db

import com.narbase.narcore.deployment.Environment
import com.narbase.narcore.deployment.LaunchConfig
import com.narbase.narcore.deployment.appConf
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

@Suppress("EXPERIMENTAL_API_USAGE")
object ConnectionProvider {
    private val configs = appConf
    private var hikariDataSource: HikariDataSource? = null
    val dataSource: DataSource
        get() {
            if (hikariDataSource?.isClosed != false) {
                hikariDataSource = createHikariDataSource()
            }
            return hikariDataSource ?: throw AssertionError("Set to null by another thread")
        }

    private fun createHikariDataSource(): HikariDataSource {
        return HikariDataSource().apply {

            jdbcUrl = Credentials.jdbcUrl
            username = Credentials.username
            password = Credentials.password
            driverClassName = Credentials.driverClassName
            connectionTimeout = 34_000
            idleTimeout = 28_740_000
            maxLifetime = 28_740_000
            leakDetectionThreshold = 3000
            maximumPoolSize = 20
        }
    }

    object Credentials {
        private val confPath = when (LaunchConfig.environment) {
            Environment.Dev -> "dataSource.dev"
            Environment.Prod -> "dataSource"
            Environment.Staging -> "dataSource.staging"
            Environment.Testing -> "dataSource.testing"
        }
        val jdbcUrl: String
            get() = configs.property("$confPath.jdbcUrl").getString()
        val username: String
            get() = configs.property("$confPath.user").getString()
        val password: String
            get() = configs.property("$confPath.password").getString()
        val driverClassName: String
            get() = configs.property("$confPath.driverClassName").getString()
        val dbName: String
            get() = configs.property("$confPath.dbName").getString()
    }
}