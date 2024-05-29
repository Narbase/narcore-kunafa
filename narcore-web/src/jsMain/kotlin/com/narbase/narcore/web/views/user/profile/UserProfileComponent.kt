package com.narbase.narcore.web.views.user.profile

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.dto.domain.user.profile.GetProfileDto
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.horizontalFiller
import com.narbase.narcore.web.utils.scrollable.scrollable
import com.narbase.narcore.web.utils.verticalFiller
import com.narbase.narcore.web.utils.views.customViews.EditableTextView
import com.narbase.narcore.web.utils.views.matchParentDimensions
import com.narbase.narcore.web.utils.views.pointerCursor
import com.narbase.narcore.web.utils.views.withLoadingAndError
import com.narbase.narcore.web.views.basePage.BasePageViewModel

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class UserProfileComponent : Component() {

    private val passwordUpdateComponent: PasswordUpdateComponent = PasswordUpdateComponent(onUpdated = {
    })
    private var profileViewHolder: LinearLayout? = null
    private var editButtonView: View? = null
    private var saveButtonView: View? = null
    private var saveButtonLayout: View? = null

    private val viewModel = UserProfileViewModel()

    private val fullNameEditableText = EditableTextView {
        text = "Doctor".localized()
        style {
            width = 420.px
            padding = 6.px
            fontSize = 16.px
        }
    }
    private val phoneEditableView = EditablePhoneNumberView()

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        this.profileViewHolder?.withLoadingAndError(viewModel.getProfileUiState,
            startWithLoading = true,
            onRetryClicked = {
                viewModel.getProfile()
            },
            onLoaded = {
                profileViewHolder?.clearAllChildren()
                profileViewHolder?.apply {
                    profileViewContent()
                }
                populateProfile(viewModel.loadedProfile)
            })
        viewModel.getProfile()
        saveButtonLayout?.withLoadingAndError(viewModel.updateProfileUiState,
            onRetryClicked = {
                saveSettings()
            },
            onLoaded = {
                onProfileSaved()
            }
        )
    }

    private fun populateProfile(loadedProfile: GetProfileDto.UserProfile?) {
        loadedProfile ?: return
        fullNameEditableText.text = loadedProfile.fullName
        phoneEditableView.code = loadedProfile.callingCode
        phoneEditableView.phoneNumber = loadedProfile.localPhone
    }

    override fun View?.getView() = view {
        style {
            matchParentDimensions
        }
        scrollable {
            style {
                matchParentDimensions
            }

            verticalLayout {
                style {
                    width = matchParent
                    height = wrapContent
                    padding = 32.px
                    marginBottom = 20.px
                }
                horizontalLayout {
                    style {
                        width = matchParent
                    }
                    textView {
                        text = "Your profile".localized()
                        style {
                            width = wrapContent
                            fontSize = AppFontSizes.titleText
                            fontWeight = "bold"
                        }
                    }

                    horizontalFiller()
                    horizontalLayout {
                        style {
                            width = wrapContent
                        }
                        saveButtonLayout = view {
                            saveButtonView = saveButton()
                        }
                        makeNotVisible(saveButtonView)
                    }
                    editButtonView = editButton()
                }

                verticalFiller(42.px)

                profileViewHolder = verticalLayout {
                    style {
                        width = matchParent
                        height = wrapContent
                    }
                }

                textView {
                    text = "Change password".localized()
                    addRuleSet(editButtonStyle)
                    onClick = {
                        if (passwordUpdateComponent.rootView?.isVisible == false) {
                            makeVisible(passwordUpdateComponent.rootView)
                            passwordUpdateComponent.reset()
                        } else
                            hidePasswordView()
                    }
                }
                verticalLayout {
                    style {
                        width = matchParent
                        justifyContent = JustifyContent.Center
                        padding = 16.px
                    }
                    mount(passwordUpdateComponent)
                }
                hidePasswordView()
            }
        }
    }

    private fun hidePasswordView() {
        makeNotVisible(passwordUpdateComponent.rootView)
    }

    private fun LinearLayout.profileViewContent() = verticalLayout {
        textView {
            text = "Basic information".localized()
            style {
                width = wrapContent
                fontSize = 16.px
                fontWeight = "600"
                marginBottom = 20.px
                color = AppColors.black
            }
        }
        profileEntry("Full name".localized(), fullNameEditableText)
        profileEntry("Phone number".localized(), phoneEditableView)

    }

    private fun LinearLayout.profileEntry(label: String, component: Component) {
        horizontalLayout {
            style {
                width = matchParent
            }

            textView {
                text = label
                style {
                    width = 200.px
                    fontSize = 16.px
                    textAlign = TextAlign.Right
                    color = AppColors.narcoreColor
                    padding = 4.px
                }
            }

            view {
                style {
                    width = 1.px
                    height = matchParent
                    margin = "0px 20px".dimen()
                    backgroundColor = AppColors.narcoreColor
                }
            }

            view {
                style {
                    width = weightOf(1)
                    marginBottom = 16.px
                }
                mount(component)
            }
        }
    }

    private val editButtonStyle = classRuleSet {
        color = Color.white
        padding = "2px 12px".dimen()
        backgroundColor = AppColors.narcoreColor
        borderRadius = 12.px
        pointerCursor()
        hover {
            backgroundColor = AppColors.narcoreDarkColor
        }
    }

    private fun LinearLayout.editButton() = textView {
        text = "Edit profile".localized()
        addRuleSet(editButtonStyle)
        onClick = {
            onEditClicked()
        }
    }

    private fun View.saveButton() = textView {
        text = "Save profile".localized()
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
            saveSettings()
        }
    }

    private fun onEditClicked() {
        makeNotVisible(editButtonView)
        makeVisible(saveButtonView)
        fullNameEditableText.enableEdit()
        phoneEditableView.enableEdit()
    }

    private fun saveSettings() {
        viewModel.updateProfile(
            fullNameEditableText.text,
            phoneEditableView.code,
            phoneEditableView.phoneNumber
        )
    }


    private fun onProfileSaved() {
        makeVisible(editButtonView)
        makeNotVisible(saveButtonView)
        fullNameEditableText.disableEdit()
        phoneEditableView.disableEdit()
    }

    companion object {
        val routeDetails = BasePageViewModel.RouteDetails("/profile", "Profile".localized())
    }

}
