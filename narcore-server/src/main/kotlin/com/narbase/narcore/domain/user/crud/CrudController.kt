package com.narbase.narcore.domain.user.crud

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.narbase.narcore.common.DataResponse
import com.narbase.narcore.common.Handler
import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.common.exceptions.InvalidRequestException
import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.domain.utils.addPrivilegeVerificationInterceptor
import com.narbase.narcore.dto.models.roles.Privilege
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import java.util.*
import kotlin.reflect.KClass

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

abstract class CrudController<DtoType : Any, ListRequestDtoType : Any>(
    val dtoClass: KClass<DtoType>,
    val ListRequestDtoClass: KClass<ListRequestDtoType>
) {

    open val defaultPageSize = 10

    open fun getItem(id: UUID?, clientData: AuthorizedClientData?): DtoType {
        throw InvalidRequestException("Item cannot be returned")
    }

    abstract fun getItemsList(
        pageNo: Long,
        pageSize: Int,
        searchTerm: String,
        filters: Map<String, String>,
        data: ListRequestDtoType?,
        clientData: AuthorizedClientData?
    ): ListAndTotal<DtoType>

//    abstract fun getItemsCount(pageNo: Long, pageSize: Int, searchTerm: String?, data: String?, queryParameters: Parameters): Int

    abstract fun createItem(item: DtoType, clientData: AuthorizedClientData?): DtoType

    // todo: Why update returns D?
    abstract fun updateItem(item: DtoType, clientData: AuthorizedClientData?): DtoType

    abstract fun deleteItem(id: UUID?, clientData: AuthorizedClientData?)

    class CreateItemException(val status: String, override val message: String) : Exception()
}


class CreateController<T : Any>(
    private val createItem: (T, clientData: AuthorizedClientData?) -> T,
    requestDtoClass: KClass<T>
) : Handler<T, T>(requestDtoClass) {

    override fun process(requestDto: T, clientData: AuthorizedClientData?): DataResponse<T> {
        try {
            val createdItem = createItem(requestDto, clientData)
            return DataResponse(createdItem)
        } catch (e: CrudController.CreateItemException) {
            return DataResponse(status = e.status, message = e.message)
        }
    }
}


class UpdateController<T : Any>(
    private val updateItem: (T, clientData: AuthorizedClientData?) -> T,
    requestDtoClass: KClass<T>
) : Handler<T, T>(requestDtoClass) {

    override fun process(requestDto: T, clientData: AuthorizedClientData?): DataResponse<T> {
        val updatedItem = updateItem(requestDto, clientData)
        return DataResponse(updatedItem)
    }
}


class GetListController<DtoType, ListRequestDtoType : Any>(
    private val ListRequestDtoClass: KClass<ListRequestDtoType>?,
    private val getItemsList: (pageNo: Long, pageSize: Int, searchTerm: String, filters: Map<String, String>, data: ListRequestDtoType?, clientData: AuthorizedClientData?) -> ListAndTotal<DtoType>,
    private val defaultPageSize: Int = 50
) : Handler<GetListController.RequestDto, GetListController<DtoType, ListRequestDtoType>.ResponseDto>(RequestDto::class) {

    override fun process(requestDto: RequestDto, clientData: AuthorizedClientData?): DataResponse<ResponseDto> {
        val data =
            if (ListRequestDtoClass != null) requestDto.data?.let { parseData(requestDto.data.toString()) } else null
        val items = getItemsList(
            requestDto.pageNo, requestDto.pageSize ?: defaultPageSize,
            requestDto.searchTerm ?: "", requestDto.filters ?: mapOf(), data, clientData
        )
        return DataResponse(ResponseDto(items.list, items.total))
    }

    private fun parseData(text: String): ListRequestDtoType? {
        if (ListRequestDtoClass == null) return null
        return try {
            gson.fromJson(text, ListRequestDtoClass.java) ?: throw GsonParsingContentTransformationException()
        } catch (e: UnsupportedMediaTypeException) {
            Gson().fromJson("{}", ListRequestDtoClass.java)
        } catch (e: ContentTransformationException) {
            Gson().fromJson("{}", ListRequestDtoClass.java)
        }

    }

    @Suppress("unused")
    class RequestDto(
        val pageNo: Long = 0L,
        val pageSize: Int? = null,
        val searchTerm: String? = "",
        val filters: Map<String, String>?,
        val data: JsonElement? = null
    )

    @Suppress("unused")
    inner class ResponseDto(
        val list: List<DtoType>,
        val total: Long
    )

}

class GetItemController<T>(private val getItem: (UUID?, clientData: AuthorizedClientData?) -> T) :
    Handler<GetItemController.RequestDto, GetItemController<T>.ResponseDto>(RequestDto::class) {
    override fun process(requestDto: RequestDto, clientData: AuthorizedClientData?): DataResponse<ResponseDto> {
        val item = getItem(requestDto.id, clientData)
        return DataResponse(ResponseDto(item = item))
    }

    @Suppress("unused")
    class RequestDto(val id: UUID?)

    @Suppress("unused")
    inner class ResponseDto(val item: T)

}

class DeleteController(private val deleteItem: (UUID?, AuthorizedClientData?) -> Unit) :
    Handler<DeleteController.RequestDto, Unit>(RequestDto::class) {
    @Suppress("unused")
    class RequestDto(
        val id: UUID?
    )

    override fun process(requestDto: RequestDto, clientData: AuthorizedClientData?): DataResponse<Unit> {
        if (requestDto.id != null) {
            deleteItem(requestDto.id, clientData)
        }
        return DataResponse()
    }

}


fun <DtoType : Any, ListRequestDtoType : Any> Route.crud(
    path: String,
    controller: CrudController<DtoType, ListRequestDtoType>,
    privilege: Privilege? = null,
) {
    route(path) {

        if (privilege != null) {
            addPrivilegeVerificationInterceptor(privilege)
        }

        post("/add") {
            CreateController(controller::createItem, controller.dtoClass).handle(call)
        }
        post("/list") {
            GetListController(
                controller.ListRequestDtoClass,
                controller::getItemsList,
                controller.defaultPageSize
            ).handle(call)
        }
        post("/details") {
            GetItemController(controller::getItem).handle(call)
        }
        post("/update") {
            UpdateController(controller::updateItem, controller.dtoClass).handle(call)
        }
        post("/delete") {
            DeleteController(controller::deleteItem).handle(call)
        }
    }
}

fun <DtoType : Any, ListRequestDtoType : Any> Route.privilegedCrud(
    path: String,
    controller: CrudController<DtoType, ListRequestDtoType>,
    privilege: Privilege
) {
    route("/edit_${path.trimStart('/')}") {
        addPrivilegeVerificationInterceptor(privilege)
        post("/add") {
            CreateController(controller::createItem, controller.dtoClass).handle(call)
        }
        post("/update") {
            UpdateController(controller::updateItem, controller.dtoClass).handle(call)
        }
        post("/delete") {
            DeleteController(controller::deleteItem).handle(call)
        }
    }
    post("$path/list") {
        GetListController(controller.ListRequestDtoClass, controller::getItemsList, controller.defaultPageSize).handle(
            call
        )
    }
    post("$path/details") {
        GetItemController(controller::getItem).handle(call)
    }
}
