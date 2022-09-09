package com.narbase.narcore.domain.admin.staff

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.data.tables.UsersTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class EnableStaffController :
    Handler<EnableStaffController.RequestDto, EnableStaffController.ResponseDto>(RequestDto::class) {
    override fun process(requestDto: RequestDto, clientData: AuthorizedClientData?): DataResponse<ResponseDto> {

        transaction {
            UsersTable.update({
                (UsersTable.id eq requestDto.userId)
            }) {
                it[isInactive] = requestDto.isActive.not()
            }
        }
        return DataResponse()
    }

    class RequestDto(
        val userId: UUID,
        val isActive: Boolean

    )

    class ResponseDto()
}