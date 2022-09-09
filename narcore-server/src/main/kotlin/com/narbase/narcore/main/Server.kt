package com.narbase.narcore.main


import com.narbase.narcore.common.auth.setupAuthenticators
import com.narbase.narcore.common.db.DatabaseConnector
import com.narbase.narcore.common.db.migrations.Migrations
import com.narbase.narcore.common.db.migrations.initializeUserMigrations
import com.narbase.narcore.common.exceptions.handleExceptions
import com.narbase.narcore.deployment.appConf
import com.narbase.narcore.domain.admin.setupAdminRoutes
import com.narbase.narcore.domain.client.setupClientRoutes
import com.narbase.narcore.domain.user.setupUserRoutes
import com.narbase.narcore.main.files.filesWithThumbnailsGenerator
import com.narbase.narcore.main.properties.VersionProperties
import com.narbase.narcore.main.provisioning.registerFirstAdmin
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.websocket.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.io.File
import java.text.DateFormat
import java.time.Duration

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

object Server {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun run() {
        logger.info("Launching server. App version: ${VersionProperties.versionName}, ${VersionProperties.versionNumber}")
        val appConfig = appConf
        printHeader(appConfig)
        DatabaseConnector.connect()
        initializeUserMigrations()
        Migrations.migrate()
        registerFirstAdmin()

        val serverPort = appConfig.propertyOrNull("ktor.deployment.port")?.getString()?.toInt()
            ?: throw RuntimeException("Port not found")

        val server = embeddedServer(Jetty,
            applicationEngineEnvironment {
                watchPaths = listOf("classes")
                config = appConfig
                module {
                    appModule()
                }
                connector {
                    port = serverPort
                    host = "0.0.0.0"
                }
            }
        )

        server.start(wait = true)
    }


    private fun printHeader(config: HoconApplicationConfig) {
        config.propertyOrNull("header")?.getString()?.let {
            println(it)
        }
    }

    private fun Application.appModule() {
        val config = appConf
        val jwtIssuer = config.property("jwt.domain").getString()
        val jwtAudience = config.property("jwt.audience").getString()
        val jwtRealm = config.property("jwt.realm").getString()

        enableCors()
        install(Compression)
        install(PartialContent) {
            maxRangeCount = 10
        }
        install(CallLogging) {
            level = Level.INFO
        }
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
                setPrettyPrinting()
            }
        }
        install(WebSockets)
        install(XForwardedHeaderSupport)

        setupAuthenticators(jwtRealm, jwtIssuer, jwtAudience)

        handleExceptions()
        routing {
            setupClientRoutes(jwtIssuer, jwtAudience)
            setupAdminRoutes()
            setupUserRoutes()
            createDirectoriesIfMissing("files", "web")
            static("files") {
                filesWithThumbnailsGenerator("files")
            }
            static("voiceNotes") {
                files("files/voiceNotes")
            }
            static("public") {
                files("web/public")
            }
            static("js") {
                files("web/js")
            }
            static("fonts") {
                files("web/fonts")
            }
            static("/") {
                file("narcore-web.js", "web/narcore-web.js")
            }

            get("/{path...}") {
                call.respondFile(File("./web/index.html"))
            }
        }
    }

    private fun createDirectoriesIfMissing(vararg fileNames: String) {
        fileNames.forEach { name ->
            val directory = File(name)
            if (directory.exists().not()) {
                directory.mkdir()
            }
        }

    }

    private fun Application.enableCors() {
        install(CORS) {
            method(HttpMethod.Options)
            method(HttpMethod.Put)
            anyHost()
            header("Authorization")
            header("DNT")
            header("X-CustomHeader")
            header("Keep-Alive")
            header("User-Agent")
            header("X-Requested-With")
            header("If-Modified-Since")
            header("Cache-Control")
            header("Content-Type")
            header("Content-Range")
            header("Accept-Ranges")
            header("Range")
            header("Client-Language")

            allowCredentials = true
            maxAgeInSeconds = Duration.ofDays(1).seconds
        }
    }

}
