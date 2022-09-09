package com.narbase.narcore.data.models.roles

import com.narbase.narcore.dto.models.roles.Privilege
import org.joda.time.DateTime
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
data class Role(
    val id: UUID,
    val createdOn: DateTime,
    val name: String,
    val privileges: List<Privilege>,
)