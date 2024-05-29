package com.narbase.narcore.web.utils.views


import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.dto.models.roles.Privilege
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppFontSizes
import com.narbase.narcore.web.storage.bidirectional
import com.narbase.narcore.web.utils.colors.gray
import com.narbase.narcore.web.utils.eventbus.LifecycleSubscriber
import com.narbase.narcore.web.utils.scrollable.scrollable
import com.narbase.narcore.web.utils.session.authorized
import com.narbase.narcore.web.utils.views.SideBar.Styles.mainTabTextStyle
import com.narbase.narcore.web.utils.views.SideBar.Styles.selectedTabRuleSet

class SideBar<T>(
    private val sections: Map<MainTab, List<SubTab<T>>>,
    private val onTabSelected: (T) -> Unit
) : Component() {
    private val tabsIndicators: MutableMap<String, View?> = mutableMapOf()
    private var sideBarTabsLayout: LinearLayout? = null


    fun setTabSelected(tab: SubTab<T>) {
        updateSelectedTab(tab)
    }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.bind(LifecycleSubscriber<HideSideBarEvent> {
            rootView?.isVisible = it.hide.not()
        })

    }


    override fun View?.getView(): View = verticalLayout {
        style {
            width = 250.px
            height = matchParent
            backgroundColor = AppColors.extraLightBackground
            borderEnd = "1px solid ${gray(0.9)}"
        }
        scrollable {
            style {
                matchParentDimensions
            }
            sideBarTabsLayout = verticalLayout {
                style {
                    width = matchParent
                    paddingEnd = 16.px
                }
                tabsLayout()
            }
        }
    }

    private fun LinearLayout.tabsLayout() {
        sections.forEach { section ->
            authorized(section.key.privileges) {
                verticalLayout {
                    style {
                        width = matchParent
                    }
                    textView {
                        addRuleSet(mainTabTextStyle)
                        text = section.key.title
                    }

                    section.value.forEach {
                        tabsIndicators[it.title] = addMenuItem(it)
                    }
                }
            }
        }
    }

    private fun LinearLayout.addMenuItem(tab: SubTab<T>): View {
        return horizontalLayout {
            this.id = tab.title
            style {
                width = matchParent
                height = TAB_HEIGHT.px
                position = "relative"
                alignItems = Alignment.Center
                padding = 16.px
                color = AppColors.text
                borderStart = "8px solid ${Color.transparent}"
                bidirectional({
                    borderTopRightRadius = 32.px
                    borderBottomRightRadius = 32.px
                }, {
                    borderTopLeftRadius = 32.px
                    borderBottomLeftRadius = 32.px
                })
                pointerCursor()
                hover {
                    color = AppColors.textNarcoreDarkColor
                }
            }

            onClick = {
                onTabSelected(tab.tab)
                updateSelectedTab(tab)
            }

            tab.icon?.let {
                imageView {
                    style {
                        width = 18.px
                        height = 18.px
                        opacity = 0.8
                        marginEnd = 8.px
                        alignSelf = Alignment.Center
                    }

                    element.src = it
                }
            }

            textView {
                text = tab.title
                style {
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

    private fun updateSelectedTab(selectedTab: SubTab<T>) {
        tabsIndicators.forEach { pair ->
            if (pair.key == selectedTab.title) {
                pair.value?.addRuleSet(selectedTabRuleSet)
            } else {
                pair.value?.removeRuleSet(selectedTabRuleSet)
            }
        }
    }


    object Styles {
        val mainTabTextStyle = classRuleSet {
            color = AppColors.textInactiveTitle
            textAlign = bidirectional(TextAlign.Left, TextAlign.Right)
            padding = 16.px
            fontSize = AppFontSizes.smallText
            marginTop = 8.px
//            fontWeight = "bold"
            mediumScreen {
                fontSize = AppFontSizes.smallText
            }
        }

        val selectedTabRuleSet by lazy {
            classRuleSet {
                backgroundColor = Color("#D8E7E8")
                color = AppColors.textNarcoreDarkColor
                borderStart = "8px solid ${AppColors.textNarcoreDarkColor}"
            }
        }
    }

    class MainTab(val title: String, val privileges: List<Privilege>)
    class SubTab<T>(val tab: T, val title: String, val icon: String? = null)
    companion object {
        const val TAB_HEIGHT = 40
    }

    data class HideSideBarEvent(val hide: Boolean)
}
