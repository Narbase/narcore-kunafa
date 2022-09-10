val kotlin_version: String by project
val ktor_version: String by project
val mokk_version: String by project

plugins {
    kotlin("jvm")
    application
}

val versionNumber = 1


dependencies {
    implementation(project(":dto-web"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("org.jetbrains.exposed:exposed-core:0.39.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.39.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.39.2")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.39.2")
    implementation("org.postgresql:postgresql:42.5.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")


    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")


//    implementation("io.ktor:ktor-network:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")

    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-forwarded-header:$ktor_version")
    implementation("io.ktor:ktor-server-call-id:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-partial-content:$ktor_version")
    implementation("io.ktor:ktor-server-compression:$ktor_version")


    implementation("io.ktor:ktor-server-jetty:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-double-receive:$ktor_version")
    implementation("ch.qos.logback:logback-classic:1.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
    implementation("com.sun.mail:javax.mail:1.5.5")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("io.mockk:mockk:$mokk_version")
}
configure<SourceSetContainer> {
    main {
        java.srcDir("src/main/kotlin")
    }
    test {
        java.srcDir("src/test/kotlin/")
    }
}
tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("com.narbase.narcore.main.MainKt")
}


tasks.jar {
    manifest {
        attributes("Main-Class" to "com.narbase.narcore.main.MainKt")
        attributes("Class-Path" to ".")
    }
    from(configurations.compileClasspath.map { config -> config.map { if (it.isDirectory) it else zipTree(it) } })
}

tasks {
    val createProperties by registering(WriteProperties::class) {
        description = "Write project properties in a file."

        outputFile = file("$buildDir/resources/main/version.properties")
        encoding = "UTF-8"
        comment = "Version and name of project"

        properties(
            mapOf(
                "versionName" to project.version.toString(),
                "versionNumber" to versionNumber.toString()
            )
        )

        writeProperties()
    }
    processResources {
        from(createProperties)
        dependsOn(createProperties)
    }
    classes{
        dependsOn(createProperties)
    }
}
