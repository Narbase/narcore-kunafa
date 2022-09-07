package com.narbase.narcore.domain.user.profile

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.UnauthenticatedException
import com.narbase.narcore.data.access.users.UsersRepository
import com.narbase.narcore.data.conversions.users.toProfileDto
import com.narbase.narcore.dto.domain.user.profile.GetProfileDto
import java.util.*

class GetProfileController : Handler<GetProfileDto.Request, GetProfileDto.Response>(GetProfileDto.Request::class) {

    override fun process(requestDto: GetProfileDto.Request, clientData: AuthorizedClientData?)
            : DataResponse<GetProfileDto.Response> {

        val clientId = UUID.fromString(clientData?.id ?: throw UnauthenticatedException())
        val userRm = UsersRepository.get(clientId)
        return DataResponse(GetProfileDto.Response(userRm.toProfileDto()))
    }
}