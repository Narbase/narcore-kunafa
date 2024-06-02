/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
package com.narbase.narcore.dto.models


sealed interface RequestType {
    object FormData : RequestType
}