package com.narbase.narcore.common


import com.google.gson.annotations.SerializedName
import com.narbase.narcore.common.CommonCodes.BASIC_SUCCESS
import com.narbase.narcore.common.CommonCodes.INVALID_REQUEST

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 5/19/17.
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
