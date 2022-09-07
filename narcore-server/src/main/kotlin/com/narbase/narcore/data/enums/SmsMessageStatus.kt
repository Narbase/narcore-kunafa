package com.narbase.narcore.data.enums

import com.narbase.narcore.data.columntypes.EnumPersistenceName
import com.narbase.narcore.dto.common.EnumDtoName

enum class SmsMessageStatus(override val persistenceName: String, override val dtoName: String) : EnumPersistenceName,
    EnumDtoName {
    /**
     * Message not send and its timeToSend has not passed
     */
    Pending("Pending", "Pending"),

    Sent("Sent", "Sent"),

    Failed("Failed", "Failed"),
}