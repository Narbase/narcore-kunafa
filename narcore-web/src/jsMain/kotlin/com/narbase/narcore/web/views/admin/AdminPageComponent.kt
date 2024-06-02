package com.narbase.narcore.web.views.admin

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.weightOf
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleObserver
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.routing.link
import com.narbase.kunafa.core.routing.matchFirst
import com.narbase.kunafa.core.routing.redirect
import com.narbase.kunafa.core.routing.routeComponent
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.scrollable.scrollable
import com.narbase.narcore.web.utils.views.mediumScreen
import com.narbase.narcore.web.views.admin.roles.RolesManagementComponent
import com.narbase.narcore.web.views.admin.staff.StaffManagementComponent
import com.narbase.narcore.web.views.basePage.BasePageViewModel

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class AdminPageComponent : Component() {

    private val tabsToViews = mutableMapOf<AdminTabs, View>()
    private val tabsToComponents = mapOf(
        AdminTabs.Staff to StaffManagementComponent(showCurrent = true),
        AdminTabs.InActiveStaff to StaffManagementComponent(showCurrent = false),
        AdminTabs.Roles to RolesManagementComponent(),
    )

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        tabsToComponents.forEach {
            it.value.rootView?.bind(object : LifecycleObserver {
                override fun onViewMounted(lifecycleOwner: LifecycleOwner) = setSelectedTab(it.key)
            })
        }
    }

    override fun View?.getView(): View = horizontalLayout {
        id = "adminRootView"
        style {
            height = matchParent
            width = matchParent
        }
        sideBar()
        verticalLayout {
            style {
                height = matchParent
                width = weightOf(5)
                alignItems = Alignment.Center
            }

            matchFirst {
                tabsToComponents.forEach { pair ->
                    routeComponent(pair.key.routeDetails.href) { pair.value }
                }
                redirect(to = AdminTabs.Staff.routeDetails.href)
            }
        }
    }

    private fun LinearLayout.sideBar() {
        verticalLayout {
            style {
                height = matchParent
                width = 240.px
                backgroundColor = AppColors.DarkTheme.darkBackground
            }

            scrollable {
                style {
                    width = matchParent
                    height = matchParent
                }
                verticalLayout {
                    style {
                        width = matchParent
                        height = wrapContent
                        paddingTop = 8.px
                    }

                    AdminTabs.subLists.forEach { subList ->

                        textView {
                            text = subList.title
                            style {
                                color = Color.white
                                textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
                                padding = "4px 16px 4px".dimen()
                                fontSize = AppFontSizes.normalText
                                fontWeight = "bold"
                                cursor = "default"
                                mediumScreen {
                                    fontSize = AppFontSizes.normalText
                                }
                            }
                        }
                        subList.tabs.forEach { tab ->
                            addMenuItem(tab).let { menuView ->
                                tabsToViews[tab] = menuView
                            }
                        }

                        view {
                            style {
                                width = matchParent
                                height = 1.px
                                backgroundColor = AppColors.lightDarkBackground
                                margin = "8px 0px".dimen()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun LinearLayout.addMenuItem(tab: AdminTabs): LinearLayout {
        val routeDetails = tab.routeDetails
        return verticalLayout {
            style {
                width = matchParent

            }
            id = routeDetails.title
            val linkPath = "${AdminPageComponent.routeDetails.href}${routeDetails.href}"
            link(linkPath) {
                style {
                    textDecoration = "none"
                    width = matchParent
                }
                verticalLayout {
                    style {
                        width = matchParent
                        position = "relative"
                        border = "0.1px solid ${Color.transparent}"
                        padding = "4px 8px 4px 16px".dimen()
                        hover {
                            backgroundColor = AppColors.DarkTheme.background
                        }
                    }

                    horizontalLayout {
                        style {
                            justifyContent = JustifyContent.Center
                            alignItems = Alignment.Center
                            marginStart = 8.px
                        }

                        horizontalLayout {

                            textView {
                                text = routeDetails.title
                                style {
                                    color = Color.white
                                    opacity = 0.7
                                    height = wrapContent
                                    alignSelf = Alignment.Center
                                    textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
                                    fontSize = AppFontSizes.normalText
                                    mediumScreen {
                                        fontSize = AppFontSizes.smallText
                                    }

                                }
                            }
                        }
                    }
                }
            }
            onClick = {
                setSelectedTab(tab)
            }
        }
    }

    private fun setSelectedTab(selectedTab: AdminTabs) {
        tabsToViews.forEach {
            if (it.key != selectedTab) {
                it.value.removeRuleSet(selectedTabRuleSet)
            }
        }
        tabsToViews[selectedTab]?.addRuleSet(selectedTabRuleSet)
    }

    companion object {
        val routeDetails = BasePageViewModel.RouteDetails("/admin", "Admin".localized())
        val selectedTabRuleSet by lazy {
            classRuleSet {
                backgroundColor = Color("494949")

            }
        }
    }
}
