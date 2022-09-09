package com.narbase.narcore.web.views.user.profile

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.dto.domain.user.profile.UpdatePasswordDto
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.ServerCaller
import com.narbase.narcore.web.network.basicNetworkCall
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.BasicUiState
import com.narbase.narcore.web.utils.dialog.labeledTextInput
import com.narbase.narcore.web.utils.views.pointerCursor

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class PasswordUpdateComponent(private val onUpdated: () -> Unit) : Component() {
    val viewModel = PasswordUpdateViewModel()
    private val errorStyle = classRuleSet {
        color = AppColors.redDark
    }

    private var messageView: TextView? = null
    private var submitButton: TextView? = null
    private var loadingIndicator: ImageView? = null
    private var passwordViews: View? = null

    override fun View?.getView() = verticalLayout {
        passwordViews = verticalLayout {

            passwordView("Old Password".localized()) {
                viewModel.oldPassword = it
            }
            passwordView("New Password".localized()) {
                viewModel.firstNewPassword = it
            }
            passwordView("Repeat New Password".localized()) {
                viewModel.secondNewPassword = it
            }
        }
        messageView = textView {
            isVisible = false
        }
        submitButton = textView {
            text = "Change password".localized()
            style {
                padding = "2px 12px".dimen()
                border = "1px solid ${AppColors.borderColorHex}"
                borderRadius = 12.px
                pointerCursor()
            }
            onClick = {
                viewModel.updatePassword()
            }
        }
        loadingIndicator = imageView {
            id = "loading"
            isVisible = false
            style {
                width = 40.px
                height = 40.px
                alignSelf = Alignment.Center
            }

            element.src = "/public/img/loading.gif"
        }

    }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        viewModel.uiState.observe {
            makeNotVisible(messageView, loadingIndicator)
            when (it) {
                BasicUiState.Loading -> {
                    makeNotVisible(submitButton)
                    makeVisible(loadingIndicator)
                }

                BasicUiState.Loaded -> {
                    makeVisible(messageView)
                    messageView?.text = "Your password has been updated successfully".localized()
                    messageView?.removeRuleSet(errorStyle)
                    passwordViews?.isVisible = false
                    submitButton?.isVisible = false
                    onUpdated()
                }

                BasicUiState.Error -> {
                    makeVisible(messageView, submitButton)
                    messageView?.text = viewModel.errorMessage
                    messageView?.addRuleSet(errorStyle)
                }
                null -> {}
            }
        }

    }

    fun reset() {
        passwordViews?.isVisible = true
        submitButton?.isVisible = true
        makeNotVisible(messageView)

    }

    private fun LinearLayout.passwordView(title: String, updateViewModelString: (String) -> Unit) {
        val input = labeledTextInput(title) {
            element.type = "password"
        }
        input.onChange = {
            updateViewModelString(input.text)
        }
    }
}

class PasswordUpdateViewModel {

    val uiState = Observable<BasicUiState>()
    var oldPassword: String = ""
    var firstNewPassword: String = ""
    var secondNewPassword: String = ""
    var errorMessage: String = ""

    class ErrorException(msg: String) : Exception(msg)

    fun updatePassword() {
        basicNetworkCall(uiState) {
            if (validatePasswordsNotEmpty().not()) throw ErrorException("some passwords are empty") //
            if (validateNewPasswordsMatch().not()) throw ErrorException("new passwords don't match")
            val updateResult =
                ServerCaller.updatePassowrd(UpdatePasswordDto.Request(oldPassword, firstNewPassword)).data.didUpdate
            if (updateResult.not()) {
                errorMessage = "Old Password is incorrect".localized()
                throw ErrorException("Old Password is incorrect")
            }
        }
    }

    private fun validatePasswordsNotEmpty(): Boolean {
        val messages = mutableListOf<String>()
        if (oldPassword.isEmpty()) {
            messages += "Old Password is empty".localized()
        }
        if (firstNewPassword.isEmpty()) {
            messages += "First New Password is empty".localized()
        }
        if (secondNewPassword.isEmpty()) {
            messages += "Second New Password is empty".localized()
        }
        return if (messages.isEmpty())
            true
        else {
            errorMessage = messages.joinToString()
            false
        }
    }

    private fun validateNewPasswordsMatch(): Boolean =
        if (firstNewPassword != secondNewPassword) {
            errorMessage = "New Passwords don't match".localized()
            false
        } else
            true


}