package com.narbase.narcore.web.views.admin.staff


import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.dimensions.vh
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.data.dto.roles.DynamicRoleDto
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.network.calls.settings.AdminRolesServerCaller
import com.narbase.narcore.web.network.calls.settings.AdminStaffServerCaller
import com.narbase.narcore.web.network.crud.CrudDto
import com.narbase.narcore.web.storage.SessionInfo
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.dialog.labeledTextInput
import com.narbase.narcore.web.utils.dialog.textInputStyle
import com.narbase.narcore.web.utils.dialog.titleField
import com.narbase.narcore.web.utils.horizontalFiller
import com.narbase.narcore.web.utils.scrollable.ScrollableView
import com.narbase.narcore.web.utils.scrollable.scrollable
import com.narbase.narcore.web.utils.views.*

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class UpsertStaffMemberDialog(val viewModel: StaffManagementViewModel, private val isActive: Boolean) : Component() {
    private var doctorSpecificInfoView: View? = null
    private var popUp: PopUpDialog? = null
    private var popupScrollable: ScrollableView? = null
    private var rolesDropDownListVm: MultiSelectionDropDownListViewModel<DynamicRoleDto>? = null

    private var errorTextView: TextView? = null
    private var fullNameTextInput: TextInput? = null
    private var usernameTextInput: TextInput? = null
    private var passwordTextInput: TextInput? = null
    private var countryCodeTextInput: TextInput? = null
    private var phoneTextInput: TextInput? = null

    override fun View?.getView() = view {
        style {
            width = 0.px
            height = 0.px
        }
        popUp = popUpDialog { }
    }


    private fun upsertDialog(staffDto: AdminStaffServerCaller.StaffDto? = null) {
        popUp?.showDialog {
            verticalLayout {
                id = "upsertMemberRootView"
                style {
                    height = wrapContent
                    minWidth = 800.px
                    width = matchParent
                    backgroundColor = Color.white
                    borderRadius = 8.px
                }
                horizontalLayout {
                    style {
                        width = matchParent
                    }
                    textView {
                        style {
                            fontWeight = "bold"
                            padding = 20.px
                            fontSize = 16.px
                        }
                        text = if (staffDto == null) "Add staff member".localized() else "Edit staff member".localized()
                    }

                    horizontalFiller()


                    staffDto?.let {
                        if (it.fullName != SessionInfo.loggedInUser.fullName || it.localPhone != SessionInfo.loggedInUser.localPhone) {
                            textView {
                                text =
                                    if (isActive) "Disable user".localized() else "Enable user".localized()
                                        .localized()
                                style {
                                    margin = 12.px
                                    padding = "4px 8px".dimen()
                                    fontSize = 14.px
                                    pointerCursor()
                                    borderRadius = 4.px
                                    hover {
                                        backgroundColor = AppColors.lightBackground
                                    }
                                }
                                if (isActive) style {
                                    color = AppColors.redLight
                                    border = "1px solid ${AppColors.redLight}"
                                    hover {
                                        color = AppColors.redDark
                                        border = "1px solid ${AppColors.redDark}"
                                    }
                                } else style {
                                    color = AppColors.greenLight
                                    border = "1px solid ${AppColors.greenLight}"
                                    hover {
                                        color = AppColors.greenDark
                                        border = "1px solid ${AppColors.greenDark}"
                                    }
                                }
                                id = "enableUser"
                                onClick = {
                                    showConfirmationDialogWithRemoteAction(
                                        if (isActive) DISABLE_USER_TITLE else ENABLE_USER_TITLE,
                                        if (isActive) DISABLE_USER_BODY else ENABLE_USER_BODY,
                                        if (isActive) "Disable user".localized()
                                            .localized() else "Enable user".localized(),
                                        "Dismiss".localized(),
                                        actionButtonStyle = {
                                            border = "none"
                                            color = if (isActive) AppColors.redLight else AppColors.greenLight
                                            padding = "2px 12px".dimen()
                                            backgroundColor = AppColors.white
                                            borderRadius = 12.px
                                            pointerCursor()
                                            fontSize = 18.px
                                            hover {
                                                color = if (isActive) AppColors.redDark else AppColors.greenDark
                                            }
                                        },
                                        uiState = viewModel.userActiveUiState,
                                        action = {
                                            viewModel.setUserActive(
                                                staffDto.userId
                                                    ?: "", isActive = isActive.not()
                                            )
                                        },
                                        onConfirmed = {
                                            popUp?.dismissDialog()
                                            viewModel.getStaff()
                                        }
                                    )
                                }

                            }
                        }
                    }
                }

                verticalLayout {
                    style {
                        width = matchParent
                        maxHeight = 60.vh
                    }

                    popupScrollable = scrollable {
                        style {
                            width = matchParent
                            maxHeight = 60.vh
                        }
                        verticalLayout {
                            style {
                                width = matchParent
                                height = wrapContent
                                padding = 20.px
                            }
                            fullNameAndPreferredName()
                            usernameAndPassword()
                            phoneField()
                            titleField("Role".localized())
                            dynamicRolesView(staffDto?.dynamicRoles?.toList() ?: listOf())
                        }

                    }
                }

                errorTextView = textView {
                    style {
                        marginBottom = 8.px
                        fontSize = 14.px
                        color = AppColors.redLight
                        padding = 20.px
                    }
                    isVisible = false
                    text = "Please enter valid fields values".localized()
                }

                horizontalLayout {
                    style {
                        width = matchParent
                        height = wrapContent
                        justifyContent = JustifyContent.End
                        padding = 20.px
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
                        id = "SaveButton"
                        onClick = {
                            onSaveButtonClicked(staffDto)
                        }
                    }
                    viewModel.upsertUiState.clearObservers()
                    saveButton.withLoadingAndError(viewModel.upsertUiState,
                        onRetryClicked = {
                            onSaveButtonClicked(staffDto)
                        },
                        onLoaded = {
                            popUp?.dismissDialog()
                            viewModel.getStaff()
                        }
                    )
                }
            }
        }
        fullNameTextInput?.element?.focus()
        updateDoctorVisibilityWithDynamicRoles()
    }

    private fun onSaveButtonClicked(staffDto: AdminStaffServerCaller.StaffDto? = null) {
        isDataValid = true
        val username = usernameTextInput.validateAndGetText().trim()
        val password = if (staffDto == null) passwordTextInput.validateAndGetText() else passwordTextInput?.text ?: ""
        val fullName = fullNameTextInput.validateAndGetText()
        val callingCode = countryCodeTextInput.validateAndGetText()
        val localPhone = phoneTextInput.validateAndGetText()

        errorTextView?.isVisible = isDataValid.not()
        if (isDataValid.not()) return

        val dto = AdminStaffServerCaller.StaffDto(
            staffDto?.clientId,
            staffDto?.userId,
            username,
            password,
            fullName,
            callingCode,
            localPhone,
            rolesDropDownListVm?.selectedItems?.toTypedArray() ?: arrayOf()
        )
        if (staffDto == null) {
            viewModel.addStaffMember(dto)
        } else {
            viewModel.editStaffMember(dto)
        }

    }

    private var isDataValid = false
    private fun TextInput?.validateAndGetText(): String {
        val text = this?.text ?: ""
        if (text.isBlank()) {
            isDataValid = false
            this?.addErrorStyle()
        } else {
            this?.resetStyle()
        }
        return text
    }

    private fun View.fullNameAndPreferredName() {
        horizontalLayout {
            style {
                width = matchParent
                marginBottom = 12.px
            }
            verticalLayout {
                style {
                    width = weightOf(3)
                }

                fullNameTextInput = labeledTextInput("Full name".localized())
                fullNameTextInput?.element?.oninput = {
                    fullNameTextInput?.handleOnFullNameChanged()
                }
                fullNameTextInput?.id = "FullNameInput"
            }
            verticalLayout {
                style {
                    width = weightOf(1)
                    marginStart = 12.px
                }
            }
        }

    }

    private fun View.usernameAndPassword() {
        horizontalLayout {
            style {
                width = matchParent
                marginBottom = 12.px
            }
            verticalLayout {
                style {
                    width = weightOf(1)
                }
                usernameTextInput = labeledTextInput("Email".localized())
                usernameTextInput?.id = "UsernameInput"
            }
            verticalLayout {
                style {
                    width = weightOf(1)
                    marginStart = 12.px
                }
                passwordTextInput =
                    labeledTextInput("Password".localized(), onChange = { passwordTextInput?.resetStyle() }) {
                        element.type = "password"
                    }
                passwordTextInput?.id = "PasswordInput"
            }
        }

    }

    private fun TextInput.handleOnFullNameChanged() {
        resetStyle()
    }

    private fun View.phoneField() {
        titleField("Phone".localized())

        horizontalLayout {
            style {
                alignItems = Alignment.Center
                marginBottom = 16.px
                direction = "ltr"
            }

            textView {
                style {
                    color = Color.black
                    fontSize = 22.px
                }

                text = "+"
            }

            countryCodeTextInput = textInput {
                id = "CountryCodeInput"
                style {
                    width = 58.px
                    marginStart = 6.px
                    type = "tel"
                    addRuleSet(textInputStyle)
                }
                onChange = {
                    resetStyle()
                }
                text = "249"
            }
            phoneTextInput = textInput {
                style {
                    marginStart = 6.px
                    type = "tel"
                    addRuleSet(textInputStyle)
                }
                onChange = {
                    resetStyle()
                }
                id = "PhoneInput"
            }
        }
    }

    private fun View.addErrorStyle() {
        removeRuleSet(textInputStyle)
        addRuleSet(textInputErrorStyle)
    }

    private fun View.resetStyle() {
        removeRuleSet(textInputErrorStyle)
        addRuleSet(textInputStyle)
    }

    private fun setDoctorLayoutVisibility(isVisible: Boolean) {
        doctorSpecificInfoView?.isVisible = isVisible
        popupScrollable?.refreshScrollHandler()
    }

    private fun LinearLayout.dynamicRolesView(defaultRoles: List<DynamicRoleDto>) {
        horizontalLayout {
            style {
                width = matchParent
                height = wrapContent
                marginBottom = 16.px
                alignItems = Alignment.Center
            }

            rolesDropDownListVm = MultiSelectionDropDownListViewModel(
                itemToId = { it.name },
                getRemoteItems = { pageNo, searchTerm ->
                    basicNetworkCall(loadingListState) {
                        val response = AdminRolesServerCaller.getList(CrudDto.GetList.Request(pageNo, 20, searchTerm))
                        appendItems(response.data.list, endOfList = response.data.list.isEmpty())
                    }
                }
            )

            setupRemoteMultiSelectionDropDownList(
                name = "Roles".localized(),
                viewModel = rolesDropDownListVm!!,
                itemToString = { it.name },
                onSelectedItemsUpdated = { updateDoctorVisibilityWithDynamicRoles() },
                showAutoComplete = true,
                rootStyle = classRuleSet {
                    width = weightOf(1)
                },
                defaultItems = defaultRoles,
                isDisabled = false
            )
        }
    }

    private fun updateDoctorVisibilityWithDynamicRoles() {
        val selected = rolesDropDownListVm?.selectedItems ?: return
        val hasDoctorRole = selected.any { it.isDoctor }
        setDoctorLayoutVisibility(hasDoctorRole)
    }

    fun add() {
        upsertDialog()
    }

    fun edit(dto: AdminStaffServerCaller.StaffDto) {
        upsertDialog(dto)
        fullNameTextInput?.text = dto.fullName
        usernameTextInput?.text = dto.username
        passwordTextInput?.placeholder = "(Enter new password or keep empty)"
        passwordTextInput?.text = ""
        countryCodeTextInput?.text = dto.callingCode.trim('+')
        phoneTextInput?.text = dto.localPhone
    }

    private val textInputErrorStyle = classRuleSet {
        padding = 4.px
        fontSize = 14.px
        padding = "6px 12px".dimen()
        border = "1px solid ${AppColors.redLight}"
        borderRadius = 4.px
    }

    companion object {
        private val DISABLE_USER_TITLE = "Are you sure you want to disable this user access to the system?".localized()
        private val ENABLE_USER_TITLE = "Are you sure you want to enable this user access to the system?".localized()
        private val DISABLE_USER_BODY =
            "The user will not be deleted but they will not be able to login to their account or perform any actions.".localized()

        private val ENABLE_USER_BODY =
            "The user will be able to access the system and login to their account normally.".localized()
    }

}
