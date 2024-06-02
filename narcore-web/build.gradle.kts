import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig


group = "com.narbase"
version = "1.0.0"

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {
            runTask {
                mainOutputFileName.set("main.bundle.js")
                sourceMaps = false
                devServerProperty.set(
                    KotlinWebpackConfig.DevServer(
                        open = true,
                        port = 8080,
                        static = mutableListOf("${layout.buildDirectory.asFile.get()}/processedResources/js/main")
                    )
                )
            }
            webpackTask {
                mainOutputFileName.set("main.bundle.js")
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }


    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val jsMain by getting {
            dependencies {
                implementation(projects.dtoWeb)
                implementation(libs.narbase.kunafa.core)

                implementation(libs.kotlinx.coroutines.core.js)

                implementation(npm("material-design-icons-iconfont", "5.0.1"))
                implementation(npm("css-loader", "3.4.2"))
                implementation(npm("style-loader", "1.1.3"))
                implementation(npm("file-loader", "5.0.2"))
                implementation(npm("typeface-open-sans", "0.0.75"))
                implementation(npm("tippy.js", "4.3.4"))
                implementation(npm("pikaday", "1.8.0"))
                implementation(npm("flatpickr", "4.6.3"))


                //Down-- needed for jsonwebtoken/sign to work
                implementation(npm("jsonwebtoken", "8.5.1"))
                implementation(npm("buffer", "6.0.3"))
                implementation(npm("crypto-browserify", "3.12.0"))
                implementation(npm("stream-browserify", "3.0.0"))
                implementation(npm("util", "0.12.5"))
                implementation(npm("vm-browserify", "1.1.2"))
                implementation(npm("events", "3.3.0"))
                implementation(npm("process", "0.11.10"))
                //Up-- needed for jsonwebtoken/sign to work


            }
        }
    }
}
