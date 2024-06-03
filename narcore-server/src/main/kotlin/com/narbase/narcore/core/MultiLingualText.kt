package com.narbase.narcore.core

import com.narbase.narcore.dto.common.utils.MultiLingualTextDto


data class MultiLingualText(val en: String, val ar: String) {
    fun toDto(): MultiLingualTextDto = MultiLingualTextDto(en, ar)
}

fun MultiLingualTextDto.toDs(): MultiLingualText = MultiLingualText(en, ar)


