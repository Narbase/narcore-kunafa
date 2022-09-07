package com.narbase.narcore.main.properties

import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

open class PropertiesReader(val propertiesFileName: String) {

    var inputStream: InputStream? = null

    private val properties by lazy {

        inputStream = javaClass.classLoader.getResourceAsStream(propertiesFileName)

        if (inputStream == null) throw FileNotFoundException("property file '$propertiesFileName' not found in the classpath")

        val prop = Properties()
        inputStream.use {
            prop.load(inputStream)
        }
        prop
    }

    fun get(value: String): String? = properties.getProperty(value)

    fun getValid(value: String): String = properties.getProperty(value)
        ?: throw Exception("Unknown property $value in $propertiesFileName")
}