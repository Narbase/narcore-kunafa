package com.narbase.narcore.data.models.clients

import org.joda.time.DateTime
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
data class Client(
    val id: UUID,
    val createdOn: DateTime,
    val username: String,
    val passwordHash: String,
    val lastLogin: DateTime?
)