package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.BasicUiState

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class NetworkAwareWrapper(
    private val errorTextViewStyle: RuleSet = Styles.errorTextViewStyle,
    private val loadingIndicatorStyle: RuleSet = Styles.loadingIndicatorStyle,
    private val retryOnError: Boolean = true,
    private val hideViewOnError: Boolean = true,
    var getErrorMsg: (() -> String?)? = null
) {

    var onRetryClicked: () -> Unit = { console.log("No retry callback is setup") }
    var view: View? = null
    var parent: View? = null
    var component: Component? = null

    open val loadingIndicator by lazy {
        detached.imageView {
            id = "loading"
            isVisible = false
            element.src = "/public/img/loading.gif"
            addRuleSet(loadingIndicatorStyle)
        }
    }

    open val errorTextView by lazy {
        detached.textView {
            isVisible = false
            addRuleSet(errorTextViewStyle)
            onClick = {
                showLoading()
                if (retryOnError) onRetryClicked()
            }
        }
    }

    fun initialize(view: View, startWithLoading: Boolean = false) {
        this.view = view
        val parent = view.parent ?: return
        this.parent = parent
        parent.mountBefore(errorTextView, view)
        parent.mountAfter(loadingIndicator, view)
        view.isVisible = startWithLoading.not()
        loadingIndicator.isVisible = startWithLoading
    }

    fun initialize(component: Component, startWithLoading: Boolean = false) {
        val view = component.rootView
            ?: return console.log("NetworkAwareComponent is called but component is not mounted. Make sure component is mounted first")
        initialize(view, startWithLoading)
    }

    fun showView() {
        hideAll()
        makeVisible(view)
        component?.let { parent?.mountAfter(it, loadingIndicator) }
    }

    fun showLoading() {
        hideAll()
        makeVisible(loadingIndicator)
    }


    fun showError(text: String = "Network error") {
        hideAll()
        if (hideViewOnError.not()) {
            makeVisible(view)
        }
        if (retryOnError) errorTextView.text = text else errorTextView.text = text.localized()
        makeVisible(errorTextView)
    }

    private fun hideAll() {
        makeNotVisible(view, loadingIndicator, errorTextView)
        component?.let { parent?.unMount(it) }
    }

    fun bind(uiStateObservable: Observable<BasicUiState>, onLoaded: () -> Unit) {
        uiStateObservable.observe { onUiStateUpdated(it, onLoaded) }
    }

    fun onUiStateUpdated(uiState: BasicUiState?, onLoaded: () -> Unit) {
        when (uiState) {
            BasicUiState.Loading -> showLoading()
            BasicUiState.Loaded -> {
                showView()
                try {
                    onLoaded()
                } catch (e: Exception) {
                    console.log(e)
                }
            }

            BasicUiState.Error -> {
                val msg: String? = getErrorMsg?.invoke()
                if (msg != null)
                    showError(msg)
                else
                    showError()
            }
            null -> {}
        }
    }


    object Styles {
        val errorTextViewStyle = classRuleSet {
            alignSelf = Alignment.Center
            color = AppColors.redLight
            margin = 18.px
            pointerCursor()
        }

        val loadingIndicatorStyle = classRuleSet {
            width = 40.px
            height = 40.px
            alignSelf = Alignment.Center
            margin = 18.px
        }
        val smallLoadingStyle = classRuleSet {
            width = 20.px
            height = 20.px
            alignSelf = Alignment.Center
        }
    }
}

fun View.withLoadingAndError(
    uiState: Observable<BasicUiState>,
    startWithLoading: Boolean = false,
    errorTextViewStyle: RuleSet = NetworkAwareWrapper.Styles.errorTextViewStyle,
    loadingIndicatorStyle: RuleSet = NetworkAwareWrapper.Styles.loadingIndicatorStyle,
    onRetryClicked: () -> Unit,
    onLoaded: () -> Unit,
    retryOnError: Boolean = true,
    hideViewOnError: Boolean = true,
    getErrorMsg: (() -> String?)? = null

) {
    val wrapper =
        NetworkAwareWrapper(errorTextViewStyle, loadingIndicatorStyle, retryOnError, hideViewOnError, getErrorMsg)
    wrapper.onRetryClicked = onRetryClicked
    wrapper.initialize(this, startWithLoading)
    wrapper.bind(uiState, onLoaded)
}

