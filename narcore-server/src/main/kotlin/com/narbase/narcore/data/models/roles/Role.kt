package com.narbase.narcore.data.models.roles

import com.narbase.narcore.dto.models.roles.Privilege
import org.joda.time.DateTime
import java.util.*

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/07/02.
 */
data class Role(
    val id: UUID,
    val createdOn: DateTime,
    val name: String,
    val privileges: List<Privilege>,
)