package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.Dimension
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.dimensions.vw
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.common.AppConfig
import com.narbase.narcore.web.events.EscapeClickedEvent
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.PopupZIndex
import com.narbase.narcore.web.utils.eventbus.LifecycleSubscriber
import com.narbase.narcore.web.utils.views.customViews.showPopupMessage
import kotlinx.browser.window

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class PageSlide private constructor(
    _backgroundWidth: Dimension,
    isDismissible: Boolean = true,
    private val onDismissed: (() -> Unit)? = null,
    private val pageSlideId: String? = null,
    private val shouldShowWarningBeforeClosing: (() -> Boolean)? = null
) : Component() {
    private val backgroundWidth: Dimension = if (AppConfig.isMobile) 95.vw else _backgroundWidth

    private val transitionTime = 0.4
    private var transparentBackground: View? = null
    private var solidBackground: View? = null
    private var componentRootView: View? = null

    var isPageSlideDismissible: Boolean = isDismissible

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        dismissDialog()
        if (isPageSlideDismissible) {
            lifecycleOwner.bind(LifecycleSubscriber<EscapeClickedEvent> {
                dismissDialog()
            })
        }
    }

    override fun View?.getView() = verticalLayout {
        id = pageSlideId ?: "pageSlideRootView"
        hide()
        transparentBackground = verticalLayout {
            id = "PageSlide background"
            style {
                position = "absolute"
                top = 0.px
                bottom = 0.px
                right = 0.px
                left = 0.px
                zIndex = PopupZIndex.getTopIndex()
                transition = "background-color ${transitionTime}s ease-out"
                alignItems = Alignment.End
            }
            element.style.backgroundColor = Color(0, 0, 0, 0.0).toString()

            onClick = {
                if (isPageSlideDismissible) dismissDialog()
            }

            horizontalLayout {
                style {
                    matchParentDimensions
                    justifyContent = JustifyContent.End
                }
                horizontalLayout {
                    pageSlideId?.let {
                        id = "dismissPageSlideButton-$it"
                    }
                    addClass("dismissPageSlideButton")
                    style {
                        marginEnd = 4.px
                        width = 32.px
                        height = 32.px
                        alignContent = Alignment.Center
                        alignItems = Alignment.Center
                        justifyContent = JustifyContent.Center
                        borderRadius = 50.px
                        backgroundColor = AppColors.white
                        border = "1px solid ${AppColors.borderColorHex}"
                        pointerCursor()
                        hover {
                            backgroundColor = AppColors.hoverBackground
                        }
                    }
                    onClick = {
                        if (shouldShowWarningBeforeClosing?.invoke() == true) {
                            showPopupMessage(
                                popupTitle = "",
                                warning = "Are you sure you want to continue without saving?".localized(),
                                positiveText = "Return to dialog".localized(),
                                image = "/public/img/warning.png",
                                onPositiveClicked = {
                                },
                                addCancelButton = true,
                                cancelText = "Dismiss anyway".localized(),
                                onCancelClicked = {
                                    dismissDialog()
                                }
                            )

                        } else {
                            dismissDialog()
                        }
                    }
                    materialIcon("clear") {
                        style {
                            color = Color.red
                            fontSize = 16.px
                        }
                    }
                }

                solidBackground = verticalLayout {
                    id = "PageSlide inner views"
                    style {
                        width = backgroundWidth
                        height = matchParent
                        backgroundColor = Color.white
                        transition = "max-width ${transitionTime}s ease-out"
                        overflow = "hidden"
                    }
                    element.style.maxWidth = "0px"
                    onClick = { it.stopPropagation() }
                }
            }

        }
        shrinkSoldBackground()

    }.also { componentRootView = it }

    fun expandSoldBackground() {
        solidBackground?.element?.style?.maxWidth = backgroundWidth.toString()
    }

    fun shrinkSoldBackground() {
        solidBackground?.element?.style?.maxWidth = "0px"
    }

    private fun View.hide() {
        element.style.visibility = "hidden"
    }

    private fun View.show() {
        element.style.visibility = "visible"
    }

    fun show(block: View.() -> Unit) {
        transparentBackground?.element?.style?.zIndex = PopupZIndex.getTopIndex().toString()
        rootView?.show()
        solidBackground?.clearAllChildren()
        transparentBackground?.element?.style?.backgroundColor = Color(0, 0, 0, 0.7).toString()
        expandSoldBackground()
        solidBackground?.view {
            style {
                width = backgroundWidth
                height = matchParent
            }
            block()
        }
    }

    fun dismissDialog() {
        transparentBackground?.apply { element.style.backgroundColor = Color(0, 0, 0, 0.0).toString() }
        shrinkSoldBackground()
        window.setTimeout({
            rootView?.hide()
            solidBackground?.clearAllChildren()
            onDismissed?.invoke()
            PopupZIndex.restoreTopIndex()
        }, (transitionTime * 1000).toInt())
    }

    companion object {

        private val pageSlides = mutableMapOf<String, PageSlide>()

        lateinit var pageSlidesRootView: View

        /**
         * Get page slide or create it if it does not exist.
         * Do NOT mount it. Use it directly.
         * Do NOT call it lazily.
         */
        fun get(
            id: String,
            backgroundWidth: Dimension,
            isDismissible: Boolean = true,
            showWarningBeforeClosing: (() -> Boolean)? = null
        ): PageSlide {

            return pageSlides.getOrPut(id) {
                val pageSlide = PageSlide(
                    backgroundWidth,
                    isDismissible,
                    pageSlideId = id,
                    shouldShowWarningBeforeClosing = showWarningBeforeClosing
                )
                pageSlidesRootView.mount(pageSlide)
                pageSlide
            }
        }
    }
}

