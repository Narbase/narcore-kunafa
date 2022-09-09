package com.narbase.narcore.domain.admin.staff

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.data.access.clients.ClientsDao
import com.narbase.narcore.data.access.common.ListFilter
import com.narbase.narcore.data.access.users.UsersRepository
import com.narbase.narcore.data.conversions.common.toDto
import com.narbase.narcore.data.conversions.id.toModel
import com.narbase.narcore.data.conversions.users.toCrudDto
import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.domain.user.crud.CrudController
import com.narbase.narcore.dto.domain.usersmanagement.UsersCrudDto
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class UsersCrudController : CrudController<UsersCrudDto.User, UsersCrudDto.Filters>(
    UsersCrudDto.User::class, UsersCrudDto.Filters::class
) {

    override fun getItemsList(
        pageNo: Long,
        pageSize: Int,
        searchTerm: String,
        filters: Map<String, String>,
        data: UsersCrudDto.Filters?,
        clientData: AuthorizedClientData?
    ): ListAndTotal<UsersCrudDto.User> {
        val list = UsersRepository.getList(ListFilter(pageNo, pageSize, searchTerm, data))

        return list.toDto { it.toCrudDto() }
    }

    override fun createItem(item: UsersCrudDto.User, clientData: AuthorizedClientData?): UsersCrudDto.User {

        if (usernameExist(item.username)) {
            throw CreateItemException(USER_EXIST, USER_EXIST_MSG)
        }
        if (phoneIsValid(item.callingCode, item.localPhone).not()) {
            throw CreateItemException(WRONG_PHONE, WRONG_PHONE_MSG)
        }

        val userId = UsersRepository.create(
            item.username,
            item.password,
            item.fullName,
            item.callingCode,
            item.localPhone,
            item.dynamicRoles.mapNotNull { it.id?.toModel() }
        )

        val user = UsersRepository.get(userId)
        return user.toCrudDto()
    }

    override fun updateItem(item: UsersCrudDto.User, clientData: AuthorizedClientData?): UsersCrudDto.User {

        if (phoneIsValid(item.callingCode, item.localPhone).not()) throw CreateItemException(
            WRONG_PHONE,
            WRONG_PHONE_MSG
        )

        val clientId = item.clientId?.toModel() ?: throw RuntimeException("Client ID cannot be null")

        transaction {
            UsersRepository.update(
                clientId,
                item.username,
                item.password.takeUnless { it.isBlank() },
                item.fullName,
                "+${item.callingCode.trimStart('+')}",
                item.localPhone.trimStart('0'),
                item.dynamicRoles.mapNotNull { it.id?.toModel() }
            )
        }
        return item
    }

    override fun deleteItem(id: UUID?, clientData: AuthorizedClientData?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    companion object {
        const val USER_EXIST = "1"
        const val USER_EXIST_MSG = "User exists"

        const val WRONG_PHONE = "3"
        const val WRONG_PHONE_MSG = "Wrong phone format. Phone should be 12 digits and does not start with 0"
    }

    private fun phoneIsValid(callingCode: String, phone: String) = callingCode.isNotBlank() && phone.isNotBlank()

    private fun usernameExist(username: String) = transaction {
        runCatching { ClientsDao.getByUsername(username) }.getOrNull() != null
    }

}