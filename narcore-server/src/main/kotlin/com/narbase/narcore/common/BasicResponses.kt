package com.narbase.narcore.common


import com.google.gson.annotations.SerializedName
import com.narbase.narcore.common.CommonCodes.BASIC_SUCCESS
import com.narbase.narcore.common.CommonCodes.INVALID_REQUEST

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

open class BasicResponse(
    @SerializedName("status")
    var status: String = BASIC_SUCCESS,

    @SerializedName("msg")
    var message: String? = null
)

class DataResponse<out T>(
    @SerializedName("data")
    val dto: T? = null,

    status: String = BASIC_SUCCESS,

    message: String? = null
) : BasicResponse(status, message)

class InvalidRequestResponse : BasicResponse(INVALID_REQUEST)
