package com.narbase.narcore.domain.client.token

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.UnauthenticatedException
import com.narbase.narcore.data.tables.ClientsTable
import com.narbase.narcore.data.tables.DeviceTokensTable
import com.narbase.narcore.data.tables.utils.toEntityId
import com.narbase.narcore.dto.domain.client.token.AddTokenDtos.RequestDto
import com.narbase.narcore.dto.domain.client.token.AddTokenDtos.ResponseDto
import com.narbase.narcore.domain.client.token.RemoveTokenController.Companion.removeClientToken
import com.narbase.narcore.domain.utils.client
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class AddTokenController : Handler<RequestDto, ResponseDto>(RequestDto::class) {

    override fun process(requestDto: RequestDto, clientData: AuthorizedClientData?): DataResponse<ResponseDto> {
        val client = clientData?.client ?: throw UnauthenticatedException()
        transaction {
            removeClientToken(client, requestDto.token)
            DeviceTokensTable.insert {
                it[token] = requestDto.token
                it[clientId] = client.id.toEntityId(ClientsTable)
                it[createdOn] = DateTime()
            }
        }
        return DataResponse(ResponseDto())
    }
}