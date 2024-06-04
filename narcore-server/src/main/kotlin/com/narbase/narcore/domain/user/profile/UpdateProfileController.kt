package com.narbase.narcore.domain.user.profile

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.UnauthenticatedException
import com.narbase.narcore.data.tables.UsersTable
import com.narbase.narcore.dto.domain.user.profile.UpdateUserProfileDto
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

class UpdateProfileController : Handler<UpdateUserProfileDto.RequestDto, Unit>(
    UpdateUserProfileDto.RequestDto::class
) {
    @Suppress("RemoveRedundantQualifierName")
    override fun process(requestDto: UpdateUserProfileDto.RequestDto, clientData: AuthorizedClientData?): DataResponse<Unit> {
        val clientId = UUID.fromString(clientData?.id ?: throw UnauthenticatedException())
        transaction {
            UsersTable.update({ UsersTable.clientId eq clientId }) {
                it[UsersTable.callingCode] = requestDto.callingCode
                it[UsersTable.localPhone] = requestDto.localPhone
                it[UsersTable.fullName] = requestDto.fullName
            }
        }
        return DataResponse()
    }
}