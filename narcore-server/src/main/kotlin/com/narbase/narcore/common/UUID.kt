package com.narbase.narcore.common

import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)