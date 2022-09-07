package com.narbase.narcore.dto.domain.user.profile

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/09/07.
 */
object GetProfileDto {

    class Request

    class Response(
        val profile: UserProfile
    )

    class UserProfile(
        val clientId: String,
        val userId: String,
        val fullName: String,
        val username: String,
        val callingCode: String,
        val localPhone: String,
        val privileges: Array<String>
    )
}