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

tasks.register("deployRelease") {
    dependsOn("buildRelease")
    doLast {
        exec {
            workingDir = File("$projectDir/releases/")
            executable = "$projectDir/releases/deploy.sh"
            args = listOf("$version")
        }
    }
}

tasks.register("buildEclaim") {
    val releaseDirectory = "releases-eclaim"
    val outputJarName = "eclaim"
    val serverProjectName = "apps:e-claim-link"
    val serverArtifact = "e-claim-link"
    val webProjectName = "apps:e-claim-link-web"
    val serverProjectDir = serverProjectName.replace(':', '/')
    val webProjectDir = webProjectName.replace(':', '/')
    dependsOn(":$serverProjectName:jar")
    dependsOn(":$webProjectName:build")
    doLast {
        println("Building release")
        val releaseDir = File("./$releaseDirectory/$version/")
        val webReleaseDir = File("./$releaseDirectory/$version/web/")
        releaseDir.deleteRecursively()
        releaseDir.mkdirs()
        webReleaseDir.mkdirs()
        File("./$webProjectDir/build/distributions/").copyRecursively(webReleaseDir, overwrite = true)
        File("./$serverProjectDir/build/libs/$serverArtifact-$version.jar").copyTo(
            File("${releaseDir.path}/$outputJarName.jar"),
            overwrite = true
        )
    }
}

tasks.register("deployEclaim") {
    dependsOn("buildEclaim")
    val releaseDirectory = "releases-eclaim"
    doLast {
        exec {
            workingDir = File("$projectDir/$releaseDirectory/")
            executable = "$projectDir/$releaseDirectory/deploy.sh"
            args = listOf("$version")
        }
    }
}
