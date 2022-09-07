buildscript {

    repositories {
        jcenter()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
    }
}
allprojects {
    version = "1.0.0"
}

plugins {
    kotlin("jvm") version "1.4.32" apply false
    kotlin("js") version "1.4.32" apply false
    kotlin("plugin.serialization") version "1.4.32" apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.4.32" apply false
}

tasks.register("buildRelease") {
    val outputJarName = "narcore"
    val serverProjectName = "narcore-server"
    val webProjectName = "narcore-web"
    dependsOn(":$serverProjectName:jar")
    dependsOn(":$webProjectName:build")
    doLast {
        println("Building release")
        val releaseDir = File("./releases/$version/")
        val webReleaseDir = File("./releases/$version/web/")
        releaseDir.deleteRecursively()
        releaseDir.mkdirs()
        webReleaseDir.mkdirs()
        File("./$webProjectName/build/distributions/").copyRecursively(webReleaseDir, overwrite = true)
        File("./$serverProjectName/build/libs/$serverProjectName-$version.jar").copyTo(
            File("${releaseDir.path}/$outputJarName.jar"),
            overwrite = true
        )
    }
}