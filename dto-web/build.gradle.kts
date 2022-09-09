
plugins {
    kotlin("multiplatform")
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(LEGACY) {
        browser {
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.narbase.kunafa:core:0.3.0")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("com.narbase.kunafa:core:0.3.0")
            }
        }
        val jsTest by getting
    }
}
