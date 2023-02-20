/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


package com.narbase.narcore.dto.domain.hello_world

import com.narbase.narcore.router.EndPoint


object HelloWorldEndPoint : EndPoint<HelloWorldEndPoint.Request, HelloWorldEndPoint.Response>() {
    class Request(
        val data: String? = null,
    )

    class Response(
        val data: String? = null,
    )
}

