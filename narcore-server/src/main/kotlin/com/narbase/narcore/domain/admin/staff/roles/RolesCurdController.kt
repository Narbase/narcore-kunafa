package com.narbase.narcore.domain.admin.staff.roles

import com.narbase.narcore.common.auth.loggedin.AuthorizedClientData
import com.narbase.narcore.data.access.roles.RolesDao
import com.narbase.narcore.data.conversions.id.toModel
import com.narbase.narcore.data.conversions.roles.privilegesEnums
import com.narbase.narcore.data.conversions.roles.toDto
import com.narbase.narcore.data.models.utils.ListAndTotal
import com.narbase.narcore.domain.user.crud.CrudController
import com.narbase.narcore.dto.models.roles.RoleDto
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2022/07/02.
 */


class RolesCurdController : CrudController<RoleDto, Unit>(RoleDto::class, Unit::class) {

    private val dao = RolesDao

    override fun getItemsList(
        pageNo: Long,
        pageSize: Int,
        searchTerm: String,
        filters: Map<String, String>,
        data: Unit?,
        clientData: AuthorizedClientData?
    ): ListAndTotal<RoleDto> {
        val listAndCount = transaction { dao.getList(pageNo, pageSize, searchTerm) }
        return ListAndTotal(listAndCount.list.map { it.toDto() }, listAndCount.total)
    }

    override fun createItem(item: RoleDto, clientData: AuthorizedClientData?): RoleDto {
        return transaction {
            val id = dao.create(item.name, item.privilegesEnums)
            val model = dao.get(id)
            model.toDto()
        }
    }

    override fun updateItem(item: RoleDto, clientData: AuthorizedClientData?): RoleDto {
        return transaction {
            val id = item.id!!
            dao.update(id.toModel(), item.name, item.privilegesEnums)
            val model = dao.get(id.toModel())
            model.toDto()
        }
    }

    override fun deleteItem(id: UUID?, clientData: AuthorizedClientData?) {
        transaction { dao.delete(id!!) }
    }
}