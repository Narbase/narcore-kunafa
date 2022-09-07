package com.narbase.narcore.domain.user.login

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.data.access.clients.ClientsDao
import com.narbase.narcore.domain.utils.authenticatedClient
import com.narbase.narcore.dto.domain.user.login.LoginDto
import org.jetbrains.exposed.sql.transactions.transaction

class LoginController : Handler<LoginDto.Request, LoginDto.Response>(LoginDto.Request::class) {

    override fun process(
        requestDto: LoginDto.Request,
        clientData: AuthorizedClientData?
    ): DataResponse<LoginDto.Response> {
        val client = clientData.authenticatedClient

        val clientLastLogin = client.lastLogin
        transaction { ClientsDao.updateLastLogin(client.id) }

        return DataResponse(LoginDto.Response(clientLastLogin == null))
    }

}