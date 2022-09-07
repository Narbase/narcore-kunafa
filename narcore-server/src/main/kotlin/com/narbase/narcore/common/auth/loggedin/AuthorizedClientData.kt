package com.narbase.narcore.common.auth.loggedin

import com.narbase.narcore.dto.models.roles.Privilege
import io.ktor.auth.*

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 5/21/17.
 */

data class AuthorizedClientData(
    val id: String,
    val timestamp: Long,
    val privileges: List<Privilege> = listOf(),
) : Principal