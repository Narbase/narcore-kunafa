plugins {
    alias(libs.plugins.kotlinMultiplatform)
}
kotlin {
    jvm()
    js(IR) {
        browser()
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
