package com.narbase.narcore.common.auth.loggedin

import com.narbase.narcore.dto.models.roles.Privilege
import io.ktor.server.auth.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

data class AuthorizedClientData(
    val id: String,
    val timestamp: Long,
    val privileges: List<Privilege> = listOf(),
) : Principal