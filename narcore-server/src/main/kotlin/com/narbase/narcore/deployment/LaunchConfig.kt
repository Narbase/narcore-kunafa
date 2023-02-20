package com.narbase.narcore.deployment

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import java.io.File

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

object LaunchConfig {
    var environment: Environment = if (System.getenv("IS_TEST")?.toBoolean() == true) Environment.Testing
    else appConf.propertyOrNull("launchConfig.environment")?.getString()
        ?.let { value -> Environment.values().firstOrNull { it.name == value } } ?: Environment.Dev
}

object EmailConfig {
    val email by lazy { appConf.propertyOrNull("emailConfig.email")?.getString() ?: "" }
    val password by lazy { appConf.propertyOrNull("emailConfig.password")?.getString() ?: "" }
}

object FirstRunConfig {
    val adminUsername by lazy { appConf.propertyOrNull("firstRun.adminUsername")?.getString() ?: "" }
    val adminPassword by lazy { appConf.propertyOrNull("firstRun.adminPassword")?.getString() ?: "" }
}

object JwtConf {
    val jwtSecret by lazy {
        appConf.propertyOrNull("jwt.jwtSecret")?.getString()?.takeUnless { it.isBlank() }
            ?: throw RuntimeException("Missing JWT secret in conf")
    }
}

val appConf: HoconApplicationConfig by lazy {
    println("Getting narcore.conf")
    var file = File("narcore.conf")
    if (file.exists().not()) {
        System.err.println("Config file does not exist")
        println("Checking dev path")
        file = File("../narcore.conf")
    }
    val conf = ConfigFactory.parseFile(file)
    HoconApplicationConfig(
        ConfigFactory.load(conf)
    )
}