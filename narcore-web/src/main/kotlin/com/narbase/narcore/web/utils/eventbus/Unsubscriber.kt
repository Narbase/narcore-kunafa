package com.narbase.narcore.web.utils.eventbus

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2019/10/11.
 */

class Unsubscriber(private val unsubscribeCallback: () -> Unit) {
    fun unsubscribe() = unsubscribeCallback()
}
