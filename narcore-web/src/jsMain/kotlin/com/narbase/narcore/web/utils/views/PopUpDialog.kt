package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.Component
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.detached
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.events.EscapeClickedEvent
import com.narbase.narcore.web.utils.PopupZIndex
import com.narbase.narcore.web.utils.eventbus.EventBus
import com.narbase.narcore.web.utils.eventbus.Unsubscriber

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
class PopUpDialog(private val onDismissed: (() -> Unit)? = null) {

    var onDialogDismissEvent: (() -> Unit)? = null

    private val background by lazy {

        detached.verticalLayout {
            style {
                zIndex = 200
            }
            isVisible = false

        }
    }


    fun setup(parent: View) {
        parent.mount(background)
    }

    fun showDialog(
        isDismissible: Boolean = true,
        shouldDismiss: (() -> Boolean)? = null,
        onDialogDismissEvent: (() -> Unit)? = null,
        block: LinearLayout.() -> Unit
    ) {
        this.onDialogDismissEvent = onDialogDismissEvent
        val dismissCondition = (shouldDismiss == null && isDismissible) || (shouldDismiss?.invoke() == true)
        background.isVisible = true
        background.clearAllChildren()
        background.mount(object : Component() {
            override fun View?.getView() =
                verticalLayout {
                    id = "PopUp background"
                    style {
                        position = "absolute"
                        top = 0.px
                        bottom = 0.px
                        right = 0.px
                        left = 0.px
                        backgroundColor = Color(0, 0, 0, 0.7)
//                            this["backdrop-filter"] = "blur(2px)"
                        alignItems = Alignment.Center
                        justifyContent = JustifyContent.Center
                        zIndex = PopupZIndex.getTopIndex()
                    }
                    onClick = {
                        onDialogDismissEvent?.invoke()
                        if (dismissCondition) {
                            dismissDialog()
                        }
                    }

                    verticalLayout {
                        id = "PopUp Dialog inner views"
                        style {
                            width = wrapContent
                            height = wrapContent
                        }
                        onClick = { it.stopPropagation() }
                        block()
                    }
                }
        })
        if (dismissCondition) {
            unSubscriber = EventBus.subscribe<EscapeClickedEvent> {
                onDialogDismissEvent?.invoke()
                dismissDialog()
            }
        }
    }

    private var unSubscriber: Unsubscriber? = null
    fun dismissDialog() {
        unSubscriber?.unsubscribe()
        background.isVisible = false
        background.clearAllChildren()
        onDismissed?.invoke()
        onDialogDismissEvent?.invoke()
        PopupZIndex.restoreTopIndex()
    }

    companion object {

        lateinit var popUpRootView: View
        val textPrompt by lazy { popUpDialog { } }
        val logoutLoading by lazy { popUpDialog { } }
        val editSavedProceduresPopup by lazy { popUpDialog { } }
        val editInsurancePlansPopup by lazy { popUpDialog { } }
        val confirmationPopUpDialog by lazy { popUpDialog { } }

    }
}


fun popUpDialog(onDismissed: (() -> Unit)? = null): PopUpDialog {
    return PopUpDialog(onDismissed).apply {
        setup(PopUpDialog.popUpRootView)
    }
}
