package com.narbase.narcore.data.models.users

import org.joda.time.DateTime
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

data class User(
    val id: UserId,
    val createdOn: DateTime,
    val clientId: UUID,
    val fullName: String,
    val callingCode: String,
    val localPhone: String,
    val isInactive: Boolean,
    val isDeleted: Boolean,
)

@JvmInline
value class UserId(val value: UUID)
