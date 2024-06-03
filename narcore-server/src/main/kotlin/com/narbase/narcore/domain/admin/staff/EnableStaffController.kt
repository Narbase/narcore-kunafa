package com.narbase.narcore.domain.admin.staff

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.toUUID
import com.narbase.narcore.data.tables.UsersTable
import com.narbase.narcore.dto.domain.admin.EnableStaffDtos
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class EnableStaffController :
    Handler<EnableStaffDtos.RequestDto, EnableStaffDtos.ResponseDto>(EnableStaffDtos.RequestDto::class) {
    override fun process(requestDto: EnableStaffDtos.RequestDto, clientData: AuthorizedClientData?): DataResponse<EnableStaffDtos.ResponseDto> {

        transaction {
            UsersTable.update({
                (UsersTable.id eq requestDto.userId.toUUID())
            }) {
                it[isInactive] = requestDto.isActive.not()
            }
        }
        return DataResponse()
    }

}