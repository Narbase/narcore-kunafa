package com.narbase.narcore.common.db.migrations

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/12/26.
 */

abstract class Migration(val name: String, val version: String) {
    abstract fun up()
    abstract fun down()
}

fun migration(name: String, version: String, up: () -> Unit, down: () -> Unit) =
    object : Migration(name, version) {
        override fun up() = up()
        override fun down() = down()
    }

fun version(year: Int, month: Int, day: Int, hour: Int, minutes: Int) =
    "$year.${month.leadingZero()}.${day.leadingZero()}.${hour.leadingZero()}.${minutes.leadingZero()}"

fun Int.leadingZero() = toString().padStart(2, '0')