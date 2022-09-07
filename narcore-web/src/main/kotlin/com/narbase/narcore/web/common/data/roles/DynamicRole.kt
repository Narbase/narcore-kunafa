package com.narbase.narcore.web.common.data.roles

import com.narbase.narcore.dto.models.roles.Privilege
import kotlin.js.Date

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/07/02.
 */
data class DynamicRole(
    val id: String,
    val createdOn: Date,
    val name: String,
    val privileges: List<Privilege>,
    val isDoctor: Boolean,
)