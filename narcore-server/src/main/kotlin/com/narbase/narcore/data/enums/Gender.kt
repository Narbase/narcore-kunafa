package com.narbase.narcore.data.enums

import com.narbase.narcore.data.columntypes.EnumPersistenceName
import com.narbase.narcore.dto.common.EnumDtoName

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2019] Narbase Technologies
 * All Rights Reserved.
 * Created by Mohammad Abbas
 * On: 5/1/19.
 */

enum class Gender(override val persistenceName: String, override val dtoName: String) : EnumPersistenceName,
    EnumDtoName {
    Male("Male", "Male"),
    Female("Female", "Female")
}