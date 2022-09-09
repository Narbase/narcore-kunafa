package com.narbase.narcore.main.properties

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object VersionProperties : PropertiesReader("version.properties") {
    val versionName = getValid("versionName")
    val versionNumber = getValid("versionNumber").toInt()
}