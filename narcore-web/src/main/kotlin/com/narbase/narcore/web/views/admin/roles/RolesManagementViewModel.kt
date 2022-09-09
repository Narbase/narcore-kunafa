package com.narbase.narcore.web.views.admin.roles

import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.web.network.UnknownErrorException
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.network.calls.settings.AdminRolesServerCaller
import com.narbase.narcore.web.network.crud.CrudDto
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.DataResponse

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class RolesManagementViewModel {

    private var searchTerm = ""
    val uiState = Observable<BasicUiState>()
    val upsertUiState = Observable<BasicUiState>()
    var data: List<DynamicRoleDto> = listOf()

    var pageNo = 0
    var pageSize = 20
    var total = 0

    fun getList() {
        upsertUiState.value = null
        basicNetworkCall(uiState) {
            val dto = CrudDto.GetList.Request(pageNo, pageSize, searchTerm = searchTerm, data = Unit)
            val response = AdminRolesServerCaller.getList(dto).data
            data = response.list.toList()
            total = response.total
        }
    }

    fun searchFor(term: String) {
        pageNo = 0
        searchTerm = term
        uiState.value = BasicUiState.Loaded
        getList()
    }

    fun getNextPage() {
        pageNo++
        getList()
    }

    fun getPreviousPage() {
        pageNo--
        getList()
    }

    fun setUpsertUiStateToNull() {
        upsertUiState.value = null
    }

    fun addItem(dto: DynamicRoleDto) {
        basicNetworkCall(upsertUiState) {
            val response = AdminRolesServerCaller.add(dto)
            if (response.status != "${DataResponse.BASIC_SUCCESS}") {
                throw UnknownErrorException()
            }
        }

    }

    fun editItem(dto: DynamicRoleDto) {
        basicNetworkCall(upsertUiState) {
            val response = AdminRolesServerCaller.update(dto)
            if (response.status != "${DataResponse.BASIC_SUCCESS}") {
                throw UnknownErrorException()
            }
        }

    }
}
