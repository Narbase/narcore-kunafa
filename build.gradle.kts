allprojects {
    version = "0.0.1"
}


plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

tasks.register("buildRelease") {
    val webAppProject = projects.narcoreWeb

    val outputJarName = project.name
    val serverProjectName = projects.narcoreServer.name
    val webProjectName = webAppProject.name
    dependsOn(":$serverProjectName:jar")
    dependsOn(":$webProjectName:assemble")
    doLast {
        println("Building release")
        val releaseDir = File("./releases/$version/")
        val webReleaseDir = File("./releases/$version/web/")
        releaseDir.deleteRecursively()
        releaseDir.mkdirs()
        webReleaseDir.mkdirs()//fixme: Doesn't work!
        File("${webAppProject.dependencyProject.layout.buildDirectory.asFile.get()}/dist/js/productionExecutable").copyRecursively(webReleaseDir, overwrite = true)
        File("${projects.narcoreServer.dependencyProject.layout.buildDirectory.asFile.get()}/libs/$serverProjectName-$version.jar").copyTo(
            File("${releaseDir.path}/$outputJarName.jar"),
            overwrite = true
        )
    }
}

tasks.register("copyHook") {
    File("./scripts/pre-commit").copyTo(
        File("./.git/hooks/pre-commit"),
        overwrite = true
    )
    mustRunAfter("build")
}




