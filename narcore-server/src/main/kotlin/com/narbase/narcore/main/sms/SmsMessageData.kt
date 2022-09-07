package com.narbase.narcore.main.sms

@Suppress("ArrayInDataClass")
data class SmsMessageData(
    val message: String,
    val phones: Array<Long>
)
