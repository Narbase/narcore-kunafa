package com.narbase.narcore.data.models.clients

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
data class Client(
    val id: UUID,
    val createdOn: DateTime,
    val username: String,
    val passwordHash: String,
    val lastLogin: DateTime?
)