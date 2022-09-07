package com.narbase.narcore.data.models.users

import com.narbase.narcore.data.models.clients.Client
import com.narbase.narcore.data.models.roles.Role

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */
class UserRm(
    val client: Client,
    val user: User,
    val roles: List<Role>
)
