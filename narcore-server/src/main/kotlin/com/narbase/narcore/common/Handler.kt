package com.narbase.narcore.common


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.request.ContentTransformationException
import io.ktor.response.*
import kotlin.reflect.KClass

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2018] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 5/19/17.
 */

abstract class Handler<V : Any, out D : Any>(
    private val requestDtoClass: KClass<V>
) {

    suspend fun handle(call: ApplicationCall) {
        val requestDto = call.extractDto()
        val clientData = call.principal<AuthorizedClientData>()
        val dataResponse = process(requestDto, clientData)
        call.respond(dataResponse)
    }

    abstract fun process(requestDto: V, clientData: AuthorizedClientData?): DataResponse<D>


    open suspend fun ApplicationCall.extractDto(): V {
        return try {
            val text = receiveTextWithCorrectEncoding()
            gson.fromJson(text, requestDtoClass.java) ?: throw GsonParsingContentTransformationException()
        } catch (e: UnsupportedMediaTypeException) {
            Gson().fromJson("{}", requestDtoClass.java)
        } catch (e: ContentTransformationException) {
            Gson().fromJson("{}", requestDtoClass.java)
        }
    }

    companion object {

        val gson = GsonBuilder()
            .registerAdapters()
            .create()


        fun GsonBuilder.registerAdapters() = this
//                .registerTypeAdapterFactory(practiceSettings)
    }


    class GsonParsingContentTransformationException :
        ContentTransformationException("Cannot transform this request's content to the desired type")
}
