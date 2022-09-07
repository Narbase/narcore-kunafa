package com.narbase.narcore.dto.domain.user.profile

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */
object UpdatePasswordDto {

    data class Request(
        val oldPassword: String,
        val newPassword: String
    )

    class Response(
        val didUpdate: Boolean
    )
}