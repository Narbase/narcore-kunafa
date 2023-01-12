/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by shalaga44
 * On: 20/Dec/2022.
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

