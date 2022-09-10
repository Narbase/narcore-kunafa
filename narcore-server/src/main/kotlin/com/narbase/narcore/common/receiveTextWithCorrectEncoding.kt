package com.narbase.narcore.common


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.Charset

/**
 * https://github.com/ktorio/ktor/issues/384
 * Receive the request as String.
 * If there is no Content-Type in the HTTP header specified use ISO_8859_1 as default charset, see https://www.w3.org/International/articles/http-charset/index#charset.
 * But use UTF-8 as default charset for application/json, see https://tools.ietf.org/html/rfc4627#section-3
 */
suspend fun ApplicationCall.receiveTextWithCorrectEncoding(): String {
    fun ContentType.defaultCharset(): Charset = when (this) {
        ContentType.Application.Json -> Charsets.UTF_8
        else -> Charsets.ISO_8859_1
    }

    val contentType = request.contentType()
    val suitableCharset = contentType.charset() ?: contentType.defaultCharset()

    return withContext(Dispatchers.IO) {
        receiveStream().bufferedReader(charset = suitableCharset).readText()
    }
}
