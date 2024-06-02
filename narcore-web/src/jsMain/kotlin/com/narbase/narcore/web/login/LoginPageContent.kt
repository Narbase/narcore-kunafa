package com.narbase.narcore.web.login

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.views.mediumScreen
import com.narbase.narcore.web.utils.views.pointerCursor


class LoginPageContent(
    private val loginViewController: LoginViewController
) : Component() {

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        loginViewController.onViewCreated()
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        loginViewController.onViewMounted()
    }

    override fun View?.getView() = horizontalLayout {
        style {
            backgroundColor = Color.white
            margin = 0.px
            padding = 0.px
            width = matchParent
            height = matchParent
        }

        id = "horizontalLayout"

        loginDialog()
    }

    private fun LinearLayout.loginDialog() = verticalLayout {
        style {
            backgroundColor = Color(255, 255, 255)
            width = weightOf(1)
            height = wrapContent
            padding = 64.px
            justifyContent = JustifyContent.Center
            mediumScreen {
                alignItems = Alignment.Center
                padding = 32.px
            }
        }

        imageView {
            style {
                width = wrapContent
                height = 50.px
                padding = 2.px
                backgroundColor = Color.white
                mediumScreen {
                    height = 80.px
                }
            }

            element.src = "/public/img/logo.png"
        }

        textView {
            style {
                fontSize = 30.px
                color = AppColors.black
                marginTop = 100.px
                fontWeight = "bold"
                mediumScreen {
                    fontSize = 18.px
                    marginTop = 64.px
                    color = AppColors.text
                    fontWeight = "unset"
                }
            }
            text = "Welcome to Narcore".localized()
        }

        textView {
            style {
                fontSize = 16.px
                color = AppColors.textDarkGrey
                marginTop = 20.px
                mediumScreen {
                    fontSize = 18.px
                    display = "none"
                }
            }
            text = "This is a starter project".localized()
        }

        verticalLayout {
            style {
                marginTop = 64.px
            }
            form {
                style {
                    width = matchParent
                }
                element.onsubmit = {
                    it.preventDefault()
                }
                element.method = "post"
                horizontalLayout {
                    addRuleSet(LoginStyles.loginFormElementStyle)
                    imageView {
                        addRuleSet(LoginStyles.loginIconStyle)
                        element.src = "/public/img/username.png"
                    }
                    loginViewController.usernameTextInput = textInput {
                        addRuleSet(LoginStyles.loginTextInputStyle)
                        placeholder = "Email".localized()
                        element.name = "name"
                    }

                }
                horizontalLayout {
                    addRuleSet(LoginStyles.loginFormElementStyle)
                    imageView {
                        addRuleSet(LoginStyles.loginIconStyle)
                        element.src = "/public/img/password.png"
                    }
                    loginViewController.passwordTextInput = textInput {
                        addRuleSet(LoginStyles.loginTextInputStyle)
                        placeholder = "Password".localized()
                        element.name = "password"
                        element.type = "password"
                    }

                }
                verticalLayout {
                    style {
                        width = matchParent
                    }
                    isVisible = false
                }
                loginViewController.loginButton = button {
                    id = "LoginButton"
                    addRuleSet(LoginStyles.loginButtonStyle)
                    text = "Login".localized()
                    element.type = "submit"
                }
            }

            loginViewController.loadingImageView = imageView {
                style {
                    marginTop = 18.px
                    width = 40.px
                    height = 40.px
                    alignSelf = Alignment.Center
                }
                isVisible = false
                element.src = "/public/img/loading.gif"
            }

            loginViewController.statusTextView = textView {
                style {
                    marginTop = 8.px
                    width = matchParent
                    height = wrapContent
                    fontSize = 14.px
                    textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
                }
                isVisible = true
            }
        }

    }

}

object LoginStyles {
    val loginButtonStyle = classRuleSet {
        color = Color.white
        backgroundColor = AppColors.narcoreColor
        borderColor = AppColors.white
        width = matchParent
        padding = 4.px
        fontSize = 18.px
        borderRadius = 4.px
        marginTop = 32.px
        pointerCursor()
        hover {
            backgroundColor = AppColors.narcoreColor
        }
    }

    val loginTextInputStyle = classRuleSet {
        width = weightOf(1, 200.px)
        height = wrapContent
        fontSize = 16.px
        border = "0px solid #ffffff"
        borderWidth = "0px"
        outline = "none"
        padding = 6.px
    }

    val loginIconStyle = classRuleSet {
        alignSelf = Alignment.Center
        width = 24.px
        height = 24.px
        padding = 6.px
    }

    val loginFormElementStyle = classRuleSet {
        alignItems = Alignment.Center
        border = "1px solid ${AppColors.borderColor}"
        borderRadius = 4.px
        marginBottom = 16.px
        width = matchParent
        overflow = "hidden"
    }
}


interface LoginPageNavigator {
    fun onLoggedInSuccessful()
}
