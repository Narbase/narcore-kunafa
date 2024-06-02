/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by shalaga44
 * On: 20/Dec/2022.
 */


package com.narbase.narcore.web.network

import kotlinx.browser.window
import org.w3c.xhr.FormData
import com.narbase.narcore.dto.models.RequestType
import com.narbase.narcore.router.*
import com.narbase.narcore.web.network.crud.CrudDto
import com.narbase.narcore.web.utils.DataResponse

suspend fun <V : Any, D : Any> EndPoint<V, D>.remoteProcess(dto: V): DataResponse<D> {
    val endPoint = this
    val headers = authHeaders(endPoint)
    return ServerCaller.synchronousPost(
        url = endPoint.path,
        headers = headers,
        body = dto
    )
}


suspend fun <V : Any, D : Any> CrudEndPoint<V, D>.remoteAdd(dto: V): DataResponse<D> {
    return newSubEndPoint("/add", this).remoteProcess(dto)

}

suspend fun <V : Any, D : Any> CrudEndPoint<V, D>.remoteUpdate(dto: V): DataResponse<D> {
    return newSubEndPoint("/update", this).remoteProcess(dto)
}

suspend fun <V : Any, D : Any> CrudEndPoint<V, D>.remoteDelete(dto: V): DataResponse<D> {
    return newSubEndPoint("/delete", this).remoteProcess(dto)
}


class CrudEndPointResponseDto<T>(val item: T)

suspend fun <V : Any, D : Any> CrudEndPoint<V, D>.remoteDetails(dto: V): DataResponse<CrudEndPointResponseDto<D>> {
    return newSubEndPointWithDifferentType<V, CrudEndPointResponseDto<D>>("/details", this).remoteProcess(dto)
}

suspend fun <V : Any, D : Any> CrudEndPoint<V, D>.remoteList(dto: CrudDto.GetList.Request<D>): DataResponse<CrudDto.GetList.Response<V>> {
    return newSubEndPointWithDifferentType<CrudDto.GetList.Request<D>, CrudDto.GetList.Response<V>>(
        "/list",
        this
    ).remoteProcess(dto)
}

suspend fun <D : Any> EndPoint<RequestType.FormData, D>.remoteProcess(formData: FormData): DataResponse<D> {
    val endPoint = this
    return ServerCaller.synchronousPost(
        url = endPoint.path,
        headers = authHeaders(this),
        body = formData,
        stringify = false,
        setContentType = false,
    )
}

private fun authHeaders(endPoint: EndPoint<*, *>): Map<String, String> {
    val headers = endPoint.authentication.map {
        when (it) {
            Authentication.JWT -> mapOf("Authorization" to "Bearer ${ServerCaller.accessToken}")
            Authentication.Basic -> mapOf("Authorization" to "Basic " + window.btoa("${/*ServerCaller.username*/ "admin"}:${/*ServerCaller.password*/"password"}"))
        }
    }.flatMap { it.asSequence() }
        .associate { it.key to it.value }
    return headers
}
