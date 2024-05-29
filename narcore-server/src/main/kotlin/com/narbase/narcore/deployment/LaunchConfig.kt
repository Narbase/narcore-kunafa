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
    val developmentMode by lazy { appConf.propertyOrNull("ktor.development")?.getString()?.toBooleanStrictOrNull() ?: false }

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
    var isFileDoesNotExists = file.exists().not()
    var counter = 0
    val maxDepth = 5
    while (isFileDoesNotExists && counter < maxDepth) {
        val depthPath = "../".repeat(counter)
        val path = "${depthPath}narcore.conf"
        println("Attempts left ${maxDepth - counter}: Checking upper path: $path")
        file = File(path)
        isFileDoesNotExists = file.exists().not()
        counter += 1
        if (isFileDoesNotExists.not()){
            println("File found at $path")
        }
    }

    val conf = ConfigFactory.parseFile(file)
    HoconApplicationConfig(
        ConfigFactory.load(conf)
    )
}