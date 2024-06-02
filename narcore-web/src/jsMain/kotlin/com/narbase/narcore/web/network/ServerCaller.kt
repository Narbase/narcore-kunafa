package com.narbase.narcore.web.network

import com.narbase.narcore.dto.domain.user.profile.GetProfileDto
import com.narbase.narcore.dto.domain.user.profile.UpdatePasswordDto
import com.narbase.narcore.web.common.AppConfig
import com.narbase.narcore.web.login.LoginResponseDto
import com.narbase.narcore.web.storage.StorageManager
import com.narbase.narcore.web.utils.DataResponse
import com.narbase.narcore.web.utils.json
import com.narbase.narcore.web.utils.uploaders.UploadFileResponseDto
import com.narbase.narcore.web.views.user.profile.UpdateUserProfileDto
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import org.w3c.xhr.BLOB
import org.w3c.xhr.FormData
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

var ServerCaller = RemoteServerCaller()

open class RemoteServerCaller {

    val BASE_URL = if (AppConfig.isDev) "http://localhost:8010" else ""

    internal val accessToken
        get() = StorageManager.accessToken

    fun authenticateUser(
        username: String?,
        password: String?,
        onSuccess: (XMLHttpRequest) -> Unit,
        onError: () -> Unit
    ) {
        post(
            url = "/oauth/token",
            headers = mapOf("Authorization" to "Basic " + window.btoa("$username:$password")),
            onSuccess = onSuccess,
            onError = onError
        )

    }

    suspend fun login() =
        synchronousPost<DataResponse<LoginResponseDto>>(
            url = "/api/user/v1/login",
            headers = mapOf("Authorization" to "Bearer $accessToken")
        )

    suspend fun uploadFile(formData: FormData): DataResponse<UploadFileResponseDto> {
        return synchronousPost(
            url = "/api/user/v1/upload_file",
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = formData,
            stringify = false,
            setContentType = false
        )
    }

    suspend fun uploadRawFile(formData: FormData): DataResponse<UploadFileResponseDto> {
        return synchronousPost(
            url = "/api/user/v1/upload_raw_file",
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = formData,
            stringify = false,
            setContentType = false
        )
    }

    suspend fun <D> getItems(url: String, dto: GetItemsRequestDto<*>) =
        synchronousPost<DataResponse<GetItemsResponseDto<D>>>(
            url = url,
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = dto
        )


    suspend fun getUserProfiles() =
        ServerCaller.synchronousPost<DataResponse<GetProfileDto.Response>>(
            url = "/api/user/v1/profile/details",
            headers = mapOf("Authorization" to "Bearer $accessToken")
        )

    suspend fun updateUserProfile(dto: UpdateUserProfileDto.RequestDto) =
        ServerCaller.synchronousPost<DataResponse<UpdateUserProfileDto.ResponseDto>>(
            url = "/api/user/v1/profile/update",
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = dto
        )

    suspend fun updatePassowrd(dto: UpdatePasswordDto.Request) =
        ServerCaller.synchronousPost<DataResponse<UpdatePasswordDto.Response>>(
            url = "/api/user/v1/profile/update_password",
            headers = mapOf("Authorization" to "Bearer $accessToken"),
            body = dto
        )


    private fun post(
        url: String,
        headers: Map<String, String>? = null,
        onSuccess: (XMLHttpRequest) -> Unit,
        onError: () -> Unit,
        body: String? = null
    ) = makeRequest(
        HTTP_POST_VERB,
        url,
        headers,
        onSuccess,
        onError,
        body
    )

    private fun binaryPost(
        url: String,
        headers: Map<String, String>? = null,
        onSuccess: (XMLHttpRequest) -> Unit,
        onError: () -> Unit,
        body: String? = null
    ) = makeBinaryRequest(
        HTTP_POST_VERB,
        url,
        headers,
        onSuccess,
        onError,
        body
    )

    companion object {

    }

    suspend fun <T : BasicResponse> synchronousPost(
        url: String,
        headers: Map<String, String> = mapOf(),
        body: Any? = null,
        stringify: Boolean = true,
        setContentType: Boolean = true
    ) = makeSynchronousRequest<T>(
        HTTP_POST_VERB, url, headers, body, stringify, setContentType
    )

    fun clientLanguageString(): String = StorageManager.language.locale


    private fun get(
        url: String,
        headers: Map<String, String>? = null,
        onSuccess: (XMLHttpRequest) -> Unit,
        onError: () -> Unit
    ) {
        makeRequest(
            HTTP_GET_VERB,
            url,
            headers,
            onSuccess,
            onError
        )
    }

    private fun makeRequest(
        requestVerb: String,
        url: String,
        headers: Map<String, String>? = null,
        onSuccess: (XMLHttpRequest) -> Unit,
        onError: () -> Unit,
        body: String? = null
    ): XMLHttpRequest {

        val xmlHttp = XMLHttpRequest()
        xmlHttp.open(requestVerb, "$BASE_URL$url", true)
        xmlHttp.setRequestHeader("Content-Type", "application/json; charset=utf-8")

        headers?.forEach { xmlHttp.setRequestHeader(it.key, it.value) }
        xmlHttp.onerror = { onError() }
        xmlHttp.onload = { onSuccess(xmlHttp) }
        xmlHttp.send(body)
        return xmlHttp
    }

    private fun makeBinaryRequest(
        requestVerb: String,
        url: String,
        headers: Map<String, String>? = null,
        onSuccess: (XMLHttpRequest) -> Unit,
        onError: () -> Unit,
        body: String? = null
    ): XMLHttpRequest {

        val xmlHttp = XMLHttpRequest()
        xmlHttp.open(requestVerb, "$BASE_URL$url", true)
        xmlHttp.setRequestHeader("Content-Type", "application/json; charset=utf-8")

        xmlHttp.responseType = XMLHttpRequestResponseType.BLOB
        headers?.forEach { xmlHttp.setRequestHeader(it.key, it.value) }
        xmlHttp.onerror = { onError() }
        xmlHttp.onload = { onSuccess(xmlHttp) }
        xmlHttp.send(body)
        return xmlHttp
    }

    @Suppress("UnnecessaryVariable")
    private suspend fun <T : BasicResponse> makeSynchronousRequest(
        requestVerb: String,
        url: String,
        headers: Map<String, String>? = null,
        body: Any? = null,
        stringify: Boolean = true,
        setContentType: Boolean = true
    ): T {

//        val xmlHttp = XMLHttpRequest()
//        xmlHttp.open(requestVerb, "$BASE_URL$url", true)
//        if (setContentType) {
//            xmlHttp.setRequestHeader("Content-Type", "application/json")
//        }

//        headers?.forEach { xmlHttp.setRequestHeader(it.key, it.value) }
//        if (requestVerb == HTTP_POST_VERB)
//            xmlHttp.setRequestHeader("Client-Language", clientLanguageString())
        val bodyToSend = body?.let {
            if (stringify) JSON.stringify(it) else it
        }

//        return xmlHttp.executeCall(bodyToSend)

        val headersJson = json {
            headers?.forEach { header ->
                header.key to header.value
            }
            if (setContentType) {
                "Content-Type" to "application/json"
            }
            if (requestVerb == HTTP_POST_VERB)
                "Client-Language" to clientLanguageString()

        }
        val httpResponse = window.fetch(
            "$BASE_URL$url", RequestInit(
                method = requestVerb,
                headers = headersJson,
                body = bodyToSend
            )
        ).await()

        val response: T = validateResponse(httpResponse)
        return response
    }

    private suspend fun <T : BasicResponse> validateResponse(httpResponse: Response): T {
        val response: T = when {
            httpResponse.status >= 200.toShort() && httpResponse.status < 400.toShort() -> {
                httpResponse.json().await().unsafeCast<T>()
            }

            httpResponse.status == 401.toShort() -> {
                val status = httpResponse.json().await().unsafeCast<T>().status
                if (status == USER_DISABLED) {
                    throw DisabledUserException()
                } else {
                    throw UnauthorizedException()
                }
            }

            httpResponse.status == 400.toShort() -> throw InvalidRequestException()
            httpResponse.status == 500.toShort() -> throw UnknownErrorException()
            httpResponse.status == 405.toShort() -> throw UnknownErrorException()
            else -> throw UnknownErrorException()
        }
        return response
    }

    private val HTTP_GET_VERB = "GET"
    val HTTP_POST_VERB = "POST"


}

/*
@Suppress("UnnecessaryVariable")
suspend fun <T : BasicResponse> XMLHttpRequest?.executeCall(body: Any? = null): T {
    if (this == null) throw ConnectionErrorException()

    val response = suspendCancellableCoroutine<T> { continuation ->

        this.onerror = {
            console.log("this.onerror")
            continuation?.resumeWithException(ConnectionErrorException())
        }
        this.onload = {
            console.log("this.onload")
            try {
                val response: T = when {
                    this.status >= 200.toShort() && this.status < 400.toShort() -> {
                        JSON.parse(this.responseText) ?: throw UnknownErrorException()
                    }
                    this.status == 401.toShort() -> {
                        val status = JSON.parse<BasicResponse>(this.responseText).status
                        if (status == USER_DISABLED) {
                            throw DisabledUserException()
                        } else {
                            throw UnauthorizedException()
                        }
                    }
                    this.status == 400.toShort() -> throw InvalidRequestException()
                    this.status == 500.toShort() -> throw UnknownErrorException()
                    this.status == 405.toShort() -> throw UnknownErrorException()
                    else -> throw UnknownErrorException()
                }
                continuation?.resume(response)
            } catch (e: Exception) {
                continuation?.resumeWithException(e)
            }
            asDynamic()
        }

        this.send(body)
    }
    return response
}
*/
