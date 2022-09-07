package com.narbase.narcore.data.tables.utils

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2021/01/15.
 */

fun UUID.toEntityId(table: UUIDTable) = EntityID(this, table)
