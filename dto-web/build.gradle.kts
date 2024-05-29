import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}
kotlin {
    jvm()
    js(IR) {
        browser {
            runTask(Action {
                mainOutputFileName = "main.bundle.js"
                sourceMaps = false
                devServerProperty = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 8080,
                    static = mutableListOf("${layout.buildDirectory.asFile.get()}/processedResources/js/main"),
                )
            })
            webpackTask(Action {
                mainOutputFileName = "main.bundle.js"
            })
            testTask(Action {
                useKarma {
                    useChromeHeadless()
                }
            })
        }
        binaries.executable()
    }

    sourceSets.all {
        languageSettings {
            optIn("kotlin.js.ExperimentalJsExport")
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.narbase.kunafa.core)
        }
        val jsMain by getting {
            dependencies {
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }

    }
}
