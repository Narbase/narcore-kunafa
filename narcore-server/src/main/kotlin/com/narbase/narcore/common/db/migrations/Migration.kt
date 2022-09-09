package com.narbase.narcore.common.db.migrations

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
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