package com.narbase.narcore.data.tables.utils

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

fun UUID.toEntityId(table: UUIDTable) = EntityID(this, table)
