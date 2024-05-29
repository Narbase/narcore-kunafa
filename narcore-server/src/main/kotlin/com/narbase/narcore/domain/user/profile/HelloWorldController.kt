/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
package com.narbase.narcore.domain.user.profile

import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.EndpointHandler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.dto.domain.hello_world.HelloWorldEndPoint


class HelloWorldController : EndpointHandler<HelloWorldEndPoint.Request, HelloWorldEndPoint.Response>(
    HelloWorldEndPoint.Request::class,
    HelloWorldEndPoint
) {
    override fun process(
        requestDto: HelloWorldEndPoint.Request,
        clientData: AuthorizedClientData?,
    ): DataResponse<HelloWorldEndPoint.Response> {
        return DataResponse(HelloWorldEndPoint.Response("${requestDto.data}:Hehe"))
    }


}
