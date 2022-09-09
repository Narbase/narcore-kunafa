package com.narbase.narcore.web.views.admin.roles

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.dimensions.vw
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.data.dto.roles.privilegesEnums
import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.translations.display.displayName
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.dialog.labeledTextInput
import com.narbase.narcore.web.utils.horizontalFiller
import com.narbase.narcore.web.utils.scrollable.scrollable
import com.narbase.narcore.web.utils.table.headerCell
import com.narbase.narcore.web.utils.table.listTable
import com.narbase.narcore.web.utils.table.tableCell
import com.narbase.narcore.web.utils.table.tableRow
import com.narbase.narcore.web.utils.verticalFiller
import com.narbase.narcore.web.utils.verticalSeparator
import com.narbase.narcore.web.utils.views.*
import com.narbase.narcore.web.utils.views.customViews.customCheckBox

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class RolesManagementComponent : Component() {

    private var paginationControls: PaginationControls? = null
    private val viewModel = RolesManagementViewModel()
    private var privilegesDropDownListVm: MultiSelectionDropDownListViewModel<Privilege>? = null

    //    private val upsertDialog = UpsertStaffMemberDialog(viewModel, showCurrent)
    private val popup by lazy { popUpDialog() }

    private var listTableBody: View? = null
    private var contentLayout: View? = null

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        contentLayout?.withLoadingAndError(viewModel.uiState, onRetryClicked = {
            viewModel.getList()
        }, onLoaded = {
            onListLoaded()
        })
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        viewModel.getList()
    }

    private fun onListLoaded() {
        paginationControls?.update(viewModel.pageNo, viewModel.pageSize, viewModel.total)
        listTableBody?.clearAllChildren()
        listTableBody?.apply {
            viewModel.data.forEachIndexed { index, item ->
                tableRow {
                    id = item.name
                    tableCell(item.name, 3, 16.px)
                    tableCell(if (item.isDoctor) "Doctor role".localized() else "", 1, 16.px)

                    onClick = {
                        upsertDialog(item)
                    }
                }

                if (index != viewModel.data.lastIndex) {
                    verticalSeparator()
                }
            }
        }
    }

    override fun View?.getView() = view {
        id = "staffManagementRootView"

        style {
            matchParentDimensions
        }
        scrollable {
            style {
                matchParentDimensions
            }

            contentLayout = verticalLayout {
                style {
                    width = matchParent
                    height = wrapContent
                    padding = 32.px
                }
                horizontalLayout {
                    style {
                        width = matchParent
                    }
                    textView {
                        text = "Roles".localized()
                        style {
                            width = wrapContent
                            fontSize = 20.px
                            fontWeight = "bold"
                        }
                    }

                    horizontalFiller()

                    addItemButton()

                }

                horizontalLayout {
                    style {
                        width = matchParent
                        marginTop = 16.px
                    }

                    horizontalFiller()
                    searchTextInput("Search".localized()) {
                        viewModel.searchFor(it)
                    }
                }

                listTableBody = listTable {
                    headerCell("Role".localized(), 3)
                    headerCell("Is a doctor role".localized(), 1)
                }
                paginationControls = setupPaginationControls(viewModel::getNextPage, viewModel::getPreviousPage)
            }
        }
    }


    private fun LinearLayout.addItemButton() {
        textView {
            style {
                color = Color.white
                padding = "2px 12px".dimen()
                backgroundColor = AppColors.narcoreColor
                borderRadius = 12.px
                pointerCursor()
                hover {
                    backgroundColor = AppColors.narcoreDarkColor
                }
            }

            onClick = {
                upsertDialog()
            }

            id = "AddRoleButton"
            text = "+ Add new role"
        }
    }

    private var upsertNameField: TextInput? = null
    private fun upsertDialog(dto: DynamicRoleDto? = null) {
        val rolePrivileges = dto?.privilegesEnums?.toMutableList() ?: mutableListOf()
        viewModel.setUpsertUiStateToNull()
        popup.showDialog {
            verticalLayout {
                style {
                    height = wrapContent
                    width = 70.vw
                    backgroundColor = Color.white
                    padding = 20.px
                    borderRadius = 8.px
                }
                textView {
                    style {
                        fontWeight = "bold"
                        marginBottom = 8.px
                        fontSize = 16.px
                    }
                    text = if (dto == null) "Add role".localized() else "Edit role".localized()
                }

                val nameInput = labeledTextInput("Name".localized())

                verticalFiller(8)

                privilegesListLayout(rolePrivileges)

                verticalFiller(8)

                val isDoctorCheckBox = customCheckBox("Is a doctor role".localized(), dto?.isDoctor ?: false)

                nameInput.text = dto?.name ?: ""


                upsertNameField = nameInput
                val errorText = textView {
                    style {
                        marginBottom = 8.px
                        fontSize = 14.px
                        color = AppColors.redLight
                    }
                    isVisible = false
                    text = "Please enter valid fields values".localized()
                }

                horizontalLayout {
                    style {
                        width = matchParent
                        height = wrapContent
                        justifyContent = JustifyContent.End
                        marginTop = 10.px
                    }

                    val saveButton = button {
                        style {
                            border = "none"
                            color = Color.white
                            padding = "2px 12px".dimen()
                            backgroundColor = AppColors.narcoreColor
                            borderRadius = 12.px
                            pointerCursor()
                            fontSize = 18.px
                            hover {
                                backgroundColor = AppColors.narcoreDarkColor
                            }
                        }
                        text = "Save".localized()
                        onClick = {
                            onSaveButtonClicked(
                                dto,
                                nameInput,
                                privilegesDropDownListVm?.selectedItems ?: listOf(),
                                isDoctorCheckBox.isSelected,
                                errorText
                            )
                        }
                    }
                    saveButton.withLoadingAndError(viewModel.upsertUiState,
                        onRetryClicked = {
                            onSaveButtonClicked(
                                dto,
                                nameInput,
                                privilegesDropDownListVm?.selectedItems ?: listOf(),
                                isDoctorCheckBox.isSelected,
                                errorText
                            )
                        },
                        onLoaded = {
                            popup.dismissDialog()
                            viewModel.getList()
                        }
                    )
                }
            }
        }
        upsertNameField?.element?.focus()
    }

    private fun View.privilegesListLayout(rolePrivileges: List<Privilege>) = horizontalLayout {
        style {
            width = matchParent
            height = wrapContent
            marginBottom = 16.px
            alignItems = Alignment.Center
        }

        privilegesDropDownListVm = MultiSelectionDropDownListViewModel(
            itemToId = { it.name },
            getRemoteItems = { _, searchTerm ->
                val list = Privilege.values()
                val filteredList = if (searchTerm.isNotBlank()) {
                    list.filter {
                        it.displayName.contains(searchTerm, ignoreCase = true)
                    }.toTypedArray()
                } else list
                appendItems(filteredList, endOfList = true)
            }
        )

        setupRemoteMultiSelectionDropDownList(
            name = "Privileges".localized(),
            viewModel = privilegesDropDownListVm!!,
            itemToString = {
                it.displayName
            },
            showAutoComplete = true,
            rootStyle = classRuleSet {
                width = weightOf(1)
            },
            defaultItems = rolePrivileges,
            isDisabled = false
        )
    }


    private fun onSaveButtonClicked(
        dto: DynamicRoleDto?,
        nameInput: TextInput,
        privileges: List<Privilege>,
        isDoctor: Boolean,
        errorText: TextView
    ) {

        if (nameInput.text.trim().isBlank()) {
            makeVisible(errorText)
            errorText.text = "Please enter a valid name".localized()
            return
        }

        makeNotVisible(errorText)
        val name = nameInput.text.trim()

        if (dto == null) {
            viewModel.addItem(DynamicRoleDto(null, name, privileges.map { it.dtoName }.toTypedArray(), isDoctor))
        } else {
            viewModel.editItem(DynamicRoleDto(dto.id, name, privileges.map { it.dtoName }.toTypedArray(), isDoctor))
        }
    }

}