package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.button
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.BasicUiState

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */


fun showOfflineDeleteConfirmationDialog(message: String, action: () -> Unit) {
    showRemoteDeleteConfirmationDialog(message, null, action, {})
}

fun showRemoteConfirmationDialog(
    message: String,
    actionButtonText: String,
    dismissButtonText: String? = null,
    uiState: Observable<BasicUiState>?,
    action: () -> Unit,
    onConfirmed: () -> Unit
) {
    showConfirmationDialogWithRemoteAction(
        null, message, actionButtonText, dismissButtonText,
        actionButtonStyle = {
            border = "none"
            color = AppColors.redLight
            padding = "2px 12px".dimen()
            backgroundColor = AppColors.white
            borderRadius = 12.px
            pointerCursor()
            fontSize = 18.px
            hover {
                color = AppColors.redDark
            }
        },
        uiState = uiState,
        action = action,
        onConfirmed = onConfirmed
    )
}

fun showRemoteDeleteConfirmationDialog(
    message: String,
    uiState: Observable<BasicUiState>?,
    action: () -> Unit,
    onDeleted: () -> Unit
) {
    showConfirmationDialogWithRemoteAction(
        "Delete confirmation".localized(), message, "Delete".localized(),
        actionButtonStyle = {
            border = "none"
            color = AppColors.redLight
            padding = "2px 12px".dimen()
            backgroundColor = AppColors.white
            borderRadius = 12.px
            pointerCursor()
            fontSize = 18.px
            hover {
                color = AppColors.redDark
            }
        },
        uiState = uiState,
        action = action,
        onConfirmed = onDeleted
    )
}

fun showOfflineConfirmationDialog(
    title: String?,
    message: String,
    actionButtonText: String? = null,
    dismissButtonText: String? = null,
    actionButtonStyle: (RuleSet.() -> Unit)?,
    onConfirmed: () -> Unit
) {
    showConfirmationDialogWithRemoteAction(
        title,
        message,
        actionButtonText,
        dismissButtonText,
        actionButtonStyle,
        null,
        {},
        { onConfirmed() })
}

fun showConfirmationDialogWithRemoteAction(
    title: String?,
    message: String,
    actionButtonText: String? = null,
    dismissButtonText: String? = null,
    actionButtonStyle: (RuleSet.() -> Unit)?,
    uiState: Observable<BasicUiState>?,
    action: () -> Unit,
    onConfirmed: () -> Unit
) {
    uiState?.value = null
    PopUpDialog.confirmationPopUpDialog.showDialog {
        verticalLayout {
            addClass("confirmationDialogRootView")
            style {
                height = wrapContent
                minWidth = 300.px
                backgroundColor = Color.white
                padding = 20.px
                borderRadius = 8.px
            }
            title?.let {
                textView {
                    style {
                        marginBottom = 8.px
                        fontSize = 16.px
                        fontWeight = "bold"
                    }
                    text = title
                }
            }
            textView {
                style {
                    marginBottom = 8.px
                    fontSize = 16.px
                }
                text = message
            }

            val buttonsView = horizontalLayout {
                style {
                    width = matchParent
                    height = wrapContent
                    justifyContent = JustifyContent.End
                    marginTop = 10.px
                }

                button {
                    addClass("confirmationDialogDismiss")
                    style {
                        border = "none"
                        color = AppColors.text
                        padding = "2px 12px".dimen()
                        backgroundColor = Color.transparent
                        borderRadius = 12.px
                        pointerCursor()
                        fontSize = 18.px
                        hover {
                            color = Color.black
                        }
                    }
                    text = dismissButtonText ?: "Dismiss".localized()
                    onClick = {
                        PopUpDialog.confirmationPopUpDialog.dismissDialog()
                    }
                    id = "confirmationDialogDismissButton"
                }
                button {
                    id = "confirmationDialogActionButton"
                    if (actionButtonStyle != null) {
                        style {
                            actionButtonStyle()
                        }
                    } else {
                        style {
                            border = "none"
                            color = AppColors.text
                            padding = "2px 12px".dimen()
                            backgroundColor = Color.transparent
                            borderRadius = 12.px
                            pointerCursor()
                            fontSize = 18.px
                            hover {
                                color = Color.black
                            }
                        }
                    }
                    text = actionButtonText
                    onClick = {
                        action()
                        if (uiState == null)
                            PopUpDialog.confirmationPopUpDialog.dismissDialog()
                    }
                }
            }
            uiState?.let {
                buttonsView.withLoadingAndError(uiState,
                    onRetryClicked = action,
                    onLoaded = {
                        PopUpDialog.confirmationPopUpDialog.dismissDialog()
                        onConfirmed()
                    }
                )
            }
        }
    }
}
