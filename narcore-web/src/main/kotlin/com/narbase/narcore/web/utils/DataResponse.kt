package com.narbase.narcore.web.utils

import com.narbase.narcore.web.network.BasicResponse

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by ${user}
 * On: ${Date}.
 */


class DataResponse<T>(
    val data: T
) : BasicResponse() {
    companion object {
        const val BASIC_SUCCESS = 0
        const val UNAUTHENTICATED = 10
        const val INVALID_REQUEST = "11"
        const val UNKNOWN_ERROR = "12"
        const val NOT_FOUND_ERROR = "13"
        const val OUTDATED_APP = "14"
    }
}
