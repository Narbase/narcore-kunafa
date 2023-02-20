package com.narbase.narcore.web.views.basePage

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.routing.Router
import com.narbase.kunafa.core.routing.redirect
import com.narbase.kunafa.core.routing.route
import com.narbase.kunafa.core.routing.routeComponent
import com.narbase.narcore.dto.domain.hello_world.HelloWorldEndPoint
import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.common.models.Language
import com.narbase.narcore.web.events.ServerConnectionEvent
import com.narbase.narcore.web.network.networkCall
import com.narbase.narcore.web.network.remoteProcess
import com.narbase.narcore.web.storage.SessionInfo
import com.narbase.narcore.web.storage.StorageManager
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.colors.gray
import com.narbase.narcore.web.utils.eventbus.LifecycleSubscriber
import com.narbase.narcore.web.utils.horizontalFiller
import com.narbase.narcore.web.utils.logoutUser
import com.narbase.narcore.web.utils.notifications.NotificationsController
import com.narbase.narcore.web.utils.session.authorized
import com.narbase.narcore.web.utils.views.pointerCursor
import com.narbase.narcore.web.utils.views.tooltip
import com.narbase.narcore.web.views.admin.AdminPageComponent
import com.narbase.narcore.web.views.user.profile.UserProfileComponent
import disableBlurOptions
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import tippy
import kotlin.js.json

class BasePageComponent(
    private val basePageViewModel: BasePageViewModel
) : Component() {

    private var serverConnectionStatusIndicator: View? = null

    private var mainContainer: LinearLayout? = null
    private var navigationBar: LinearLayout? = null


    private val adminPageComponent = AdminPageComponent()
    private val userProfileComponent = UserProfileComponent()

    private var tippyInstance: dynamic = null

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        basePageViewModel.onViewCreated()

        lifecycleOwner.bind(LifecycleSubscriber<ServerConnectionEvent> {
            onServerConnectionUpdated(it.isConnected)
        })

    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        onServerConnectionUpdated(NotificationsController.isConnectedToServer)
    }

    override fun View?.getView() = verticalLayout {
        id = "basePage"
        style {
            margin = 0.px
            padding = 0.px
            width = matchParent
            height = matchParent
            backgroundColor = Color.white
        }

        navigationBar = horizontalLayout {
            style {
                width = matchParent
                boxShadow = "0px 1px 3px 1px rgba(0,0,0,0.07)"
                borderBottom = "1px solid ${Color(220, 220, 220)}"
                zIndex = 1
                alignItems = Alignment.Center
            }

            textView {
                text = "Narcore"
                style {
                    fontSize = 18.px
                    fontWeight = "bold"
                    padding = 8.px
                    color = AppColors.narcoreColor
                    pointerCursor()
                }
                onClick = {
                    Router.navigateTo("/home")
                }
            }

            horizontalFiller()

            horizontalLayout {
                style {
                    paddingEnd = 8.px
                }
                userNameAndMenuLayout()

                serverConnectionStatusIndicator = view {
                    style {
                        width = 16.px
                        height = 16.px
                        alignSelf = Alignment.Center
                        marginEnd = 8.px
                        borderRadius = 8.px
                        backgroundColor = AppColors.redLight
                    }
                    tooltip("Server status".localized(), delay = 0)
                }
            }
        }

        mainContainer = verticalLayout {
            id = "mainContainer"
            style {
                alignItems = Alignment.Center
                width = matchParent
                height = weightOf(1)
            }
            routeComponents()
        }
    }


    private fun getTippyOptions(content: HTMLElement) = json(
        "interactive" to true,
        "trigger" to "click",
        "theme" to "light-border",
        "placement" to "bottom",
        "arrow" to "large",
        "content" to content,
        disableBlurOptions
    )

    private fun LinearLayout.userNameAndMenuLayout() = horizontalLayout {
        id = "doctorNameAndImageLayout"
        val options = getTippyOptions(settingsDropDownMenu())

        tippyInstance = tippy(
            this.element, options
        )

        style {
            alignSelf = Alignment.Center
            marginEnd = 8.px
            padding = "8px 12px".dimen()
            borderRadius = 4.px
            pointerCursor()
            hover {
                backgroundColor = gray(0.98)
            }
        }

        textView {
            style {
                marginEnd = 12.px
                alignSelf = Alignment.Center
                fontSize = AppFontSizes.smallText
            }

            text = SessionInfo.loggedInUser.fullName
        }

        imageView {
            style {
                width = 24.px
                height = 24.px
                borderRadius = 50.percent
                opacity = 0.8
            }

            element.src = "/public/img/settings.svg"
        }
    }


    private fun settingsDropDownMenu(): HTMLElement {
        return detached.verticalLayout {
            style {
                width = matchParent
                height = matchParent
            }
            textView {
                text = "Profile".localized()
                menuItemStyle()
                onClick = {
                    tippyInstance.hide()
                    Router.navigateTo(UserProfileComponent.routeDetails.href)
                }
            }
            authorized(Privilege.adminAreaPrivileges) {

                textView {
                    id = "adminArea"
                    text = "Admin Area".localized()
                    menuItemStyle()
                    onClick = {
                        tippyInstance.hide()
                        Router.navigateTo(AdminPageComponent.routeDetails.href)
                    }

                }
            }

            textView {
                id = "logoutMenuItem"
                text = "Logout".localized()
                menuItemStyle()
                onClick = {
                    tippyInstance.hide()
                    logoutUser()
                }
            }

            val supportedLanguage: Array<Language> = Language.values()

            val currentLanguage = StorageManager.language
            supportedLanguage.filterNot { it == currentLanguage }.forEach { language ->
                horizontalLayout {
                    style {
                        width = matchParent
                        padding = 8.px
                        pointerCursor()
                        textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
                        hover {
                            backgroundColor = gray(0.98)
                        }
                    }
                    val langImage = imageView {
                        id = "${language.locale}-image"
                        style {
                            height = 16.px
                            width = 16.px
                        }
                        element.src = language.imageSrc
                    }
                    textView {
                        id = "${language.locale}-label"
                        style {
                            fontSize = AppFontSizes.smallerText
                            marginStart = 8.px
                        }
                        text = language.label
                        onClick = {
                            tippyInstance.hide()
                            changeLanguage(this, langImage, language)
                        }
                    }
                }

            }

        }.element
    }

    private fun changeLanguage(textView: TextView, imageView: ImageView, selectedLanguage: Language) {
        StorageManager.language = selectedLanguage

        selectedLanguage.apply {
            textView.text = label
            imageView.element.src = imageSrc
            document.body?.style?.direction = direction.toHtmlDirection()
            document.body?.lang = toHtmlLanguage()
        }

        window.location.reload()
    }

    private fun View.menuItemStyle() {
        style {
            AppFontSizes.smallText
            width = matchParent
            padding = 8.px
            pointerCursor()
            textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
            hover {
                backgroundColor = gray(0.98)
            }
        }
    }

    private fun onServerConnectionUpdated(isConnected: Boolean) {
        val tippy = serverConnectionStatusIndicator?.element?.asDynamic()._tippy
        if (isConnected) {
            serverConnectionStatusIndicator?.element?.style?.backgroundColor = AppColors.greenLight.toCss()
            tippy?.setContent("Server status: Connected".localized())
        } else {
            serverConnectionStatusIndicator?.element?.style?.backgroundColor = AppColors.redLight.toCss()
            tippy?.setContent("Server status: Disconnected".localized())
        }
    }

    private fun LinearLayout.routeComponents() {

        redirect("/home")

        route("/home") {
            verticalLayout {
                textView {
                    style {
                        fontSize = 32.px
                        color = AppColors.narcoreColor
                        padding = 32.px
                    }
                    text = "Hello, world"
                }
                testRouting()
            }
        }
        routeComponent(AdminPageComponent.routeDetails.href, isExact = false) { adminPageComponent }
        routeComponent(UserProfileComponent.routeDetails.href, isExact = false) { userProfileComponent }
    }

    private fun testRouting(){
        console.log("Testing Routing")
        networkCall {
            val response = HelloWorldEndPoint.remoteProcess(HelloWorldEndPoint.Request(data = "Hi!"))
            console.log(response.data.data)
            //output: "Hi! :Hehe"
        }
    }

}

interface HomePageNavigator {
    fun onLogoutSelected()
}
