package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.Page.mount
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.detached
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.utils.PopupZIndex
import kotlinx.browser.window

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
object SnackBar {
    private var snackbars = mutableListOf<LinearLayout>()

    private val snackBarContainer by lazy {
        detached.verticalLayout {
            id = "Container snackbar"

            style {
                position = "fixed"
                zIndex = PopupZIndex.getTopIndex()
                bottom = 24.px
                start = 24.px
                alignItems = Alignment.Start
            }
        }
    }

    private fun snackbarView() = detached.verticalLayout {
        id = "snackbar"

        style {
            visibility = "hidden"
            alignItems = Alignment.Center
            marginTop = 12.px
            boxShadow = "0px 2px 6px 2px ${Color(0, 0, 0, 0.3)}"
            borderRadius = 4.px
            overflow = "hidden"
            borderStart = "8px solid ${AppColors.narcoreColor}"
        }
    }

    private fun basicTextMessage(messageText: String) = detached.textView {
        id = "basicMessageText"

        style {
            minWidth = 512.px
            color = AppColors.textDarkest
            padding = 18.px
            backgroundColor = AppColors.white
            fontSize = 16.px
            textAlign = TextAlign.Start
        }

        text = messageText
    }


    fun setup() {
        mount(snackBarContainer)
    }

    fun showText(text: String) {
        showText(text, LENGTH_SHORT)
    }

    fun showText(messageText: String, duration: Double) {
        val messageTextView = basicTextMessage(messageText)
        showSnackBarView(messageTextView, duration)
    }

    /*
        To use this function properly pass the main view of your content
     */
    fun dismiss(contentView: View) {
        hideSnackBarView(contentView)
    }

    fun showContent(contentView: View?.() -> View?) {
        showSnackBarView(contentView, LENGTH_SHORT)
    }

    fun showContent(duration: Double, contentView: View?.() -> View?) {
        showSnackBarView(contentView, duration)
    }

    fun showContent(contentView: View) {
        showSnackBarView(contentView, LENGTH_SHORT)
    }

    fun showContent(contentView: View, duration: Double) {
        showSnackBarView(contentView, duration)
    }

    private fun showSnackBarView(contentView: View, duration: Double) {
        val snackbarView = snackbarView().apply { mount(contentView) }
        showCreatedSnackBarView(snackbarView, duration)
    }

    private fun showSnackBarView(contentView: View?.() -> View?, duration: Double) {
        val snackbarView = snackbarView().apply { contentView() }
        showCreatedSnackBarView(snackbarView, duration)
    }

    private fun showCreatedSnackBarView(snackbarView: LinearLayout, duration: Double) {
        snackbars.add(snackbarView)
        snackbarView.addRuleSet(showStyle)
        snackbarView.element.style.animationDelay = "0s, ${duration}s"
        val timeOut = (DEFAULT_ANIMATION_DURATION + duration) * 1000
        window.setTimeout({ snackbarView.element.remove() }, timeOut.toInt())
        snackBarContainer.mount(snackbarView)
    }

    private fun hideSnackBarView(contentView: View) {
        snackbars.forEach { snackbarView ->
            snackbarView.children.firstOrNull { it == contentView }?.let { view ->
                snackbarView.element.remove()
            }
        }
    }

    private val showStyle by lazy {
        classRuleSet {
            visibility = "visible !important"
            animationName = "fadein, fadeout"
            animationDuration = "${DEFAULT_ANIMATION_DURATION}s, ${DEFAULT_ANIMATION_DURATION}s"
        }
    }
    private val hideStyle by lazy {
        classRuleSet {
            visibility = "hidden !important"
            animationName = "fadein, fadeout"
            animationDuration = "${DEFAULT_ANIMATION_DURATION}s, ${DEFAULT_ANIMATION_DURATION}s"
        }
    }

    private val fadein = keyframes("fadein") {
        from {
            bottom = 0.px
            opacity = 0.0
        }

        to {
            bottom = 30.px
            opacity = 1.0
        }
    }

    private val fadeout = keyframes("fadeout") {
        from {
            bottom = 30.px
            opacity = 1.0
        }

        to {
            bottom = 0.px
            opacity = 0.0
        }
    }

    private const val DEFAULT_ANIMATION_DURATION = 0.5
    const val LENGTH_SHORT = 2.5
    const val LENGTH_LONG = 4.5
}
