package com.narbase.narcore.web.views.user.profile

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/01/30.
 */

object UpdateUserProfileDto {
    class RequestDto(
        val fullName: String,
        val callingCode: String,
        val localPhone: String
    )

    class ResponseDto
}
