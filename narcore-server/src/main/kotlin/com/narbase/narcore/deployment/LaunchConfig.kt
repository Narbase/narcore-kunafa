package com.narbase.narcore.deployment

import com.typesafe.config.ConfigFactory
import io.ktor.config.*
import java.io.File

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/05/11.
 */

object LaunchConfig {
    var environment: Environment = appConf.propertyOrNull("launchConfig.environment")?.getString()
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
    val file = File("narcore.conf")
    if (file.exists().not()) {
        println("Config file does not exist")
    }
    val conf = ConfigFactory.parseFile(file)
    HoconApplicationConfig(
        ConfigFactory.load(conf)
    )
}