allprojects {
    version = "1.0.0"

    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-dev/")
        maven(url = "https://dl.bintray.com/kotlin/kotlinx/")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven/")
        mavenCentral()
    }
    apply(plugin = "io.gitlab.arturbosch.detekt")

}

plugins {
    kotlin("jvm") version "1.7.10" apply false
    kotlin("js") version "1.7.10" apply false
    kotlin("multiplatform") version "1.7.10" apply false
    id("io.gitlab.arturbosch.detekt").version("1.22.0")
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


repositories {
    mavenCentral()
}
