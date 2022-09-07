package com.narbase.narcore.web.network.calls.settings

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/02/21.
 */
object EnableUserDto {

    class RequestDto(
        val userId: String,
        val isActive: Boolean
    )
}
