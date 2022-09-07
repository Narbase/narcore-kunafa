buildscript {

    repositories {
        jcenter()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        classpath(kotlin("gradle-plugin", version = "1.4.32"))
    }
}

group = "com.narbase"
version = "1.0.0"

val kotlinVersion = "1.4.32"
val coroutinesVersion = "1.4.3"

plugins {
    id("org.jetbrains.kotlin.js")
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion")
    implementation(project(":dto-web"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion")
    implementation("com.narbase:kunafa:0.2.26")
    implementation(npm("material-design-icons-iconfont", "5.0.1"))
    implementation(npm("css-loader", "3.4.2"))
    implementation(npm("style-loader", "1.1.3"))
    implementation(npm("file-loader", "5.0.2"))
    implementation(npm("typeface-open-sans", "0.0.75"))
    implementation(npm("tippy.js", "4.3.4"))
    implementation(npm("pikaday", "1.8.0"))
    implementation(npm("flatpickr", "4.6.3"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-js:$kotlinVersion")

}

kotlin {
    js {
        browser {
            testTask {
//                enabled = false
                useKarma {
//                    useSourceMapSupport()
                    useFirefox()
//                    useChrome()
//                    useChromeHeadless()
                }
            }
        }

        compilations.all {
            kotlinOptions {
                sourceMap = true
                sourceMapEmbedSources = "always"
            }
        }
        binaries.executable()
    }
}
