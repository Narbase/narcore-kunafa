package com.narbase.narcore.web.common.data.roles

import com.narbase.narcore.dto.models.roles.Privilege
import kotlin.js.Date

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
data class DynamicRole(
    val id: String,
    val createdOn: Date,
    val name: String,
    val privileges: List<Privilege>,
    val isDoctor: Boolean,
)