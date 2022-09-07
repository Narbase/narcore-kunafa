package com.narbase.narcore.data.models.users

import org.joda.time.DateTime
import java.util.*

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/06.
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

inline class UserId(val value: UUID)
