package com.narbase.narcore.main.provisioning

import com.narbase.narcore.data.access.clients.ClientsDao
import com.narbase.narcore.data.access.roles.RolesDao
import com.narbase.narcore.data.access.users.UsersRepository
import com.narbase.narcore.deployment.FirstRunConfig
import com.narbase.narcore.dto.models.roles.Privilege
import org.jetbrains.exposed.sql.transactions.transaction

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/06.
 */

fun registerFirstAdmin() {
    val isClientsTableEmpty = transaction { ClientsDao.getClients(0, 1).isEmpty() }
    if (isClientsTableEmpty.not()) return
    val roleId = transaction { RolesDao.create("Super admin", Privilege.values().toList()) }
    UsersRepository.create(
        FirstRunConfig.adminUsername,
        FirstRunConfig.adminPassword,
        "First run admin",
        "249",
        "9999999999",
        listOf(roleId)
    )
}