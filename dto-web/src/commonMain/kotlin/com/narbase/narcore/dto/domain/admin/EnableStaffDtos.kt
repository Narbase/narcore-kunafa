package com.narbase.narcore.dto.domain.admin

import com.narbase.narcore.dto.common.StringUUID
import kotlin.js.JsExport

@JsExport
object EnableStaffDtos {
    class RequestDto(
        val userId: StringUUID,
        val isActive: Boolean

    )
    class ResponseDto()
}