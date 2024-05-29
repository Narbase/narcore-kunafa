package com.narbase.narcore.dto.domain.user.login

import kotlin.js.JsExport

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@JsExport
object LoginDto {
    class Request

    class Response(val isFirstLogin: Boolean)
}