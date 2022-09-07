package com.narbase.narcore.main.properties

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2021/04/10.
 */
object VersionProperties : PropertiesReader("version.properties") {
    val versionName = getValid("versionName")
    val versionNumber = getValid("versionNumber").toInt()
}