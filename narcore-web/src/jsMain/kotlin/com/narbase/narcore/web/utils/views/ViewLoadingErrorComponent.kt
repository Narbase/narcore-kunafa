package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.lifecycle.Observable
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.network.makeNotVisible
import com.narbase.narcore.web.network.makeVisible
import com.narbase.narcore.web.utils.BasicUiState

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@Suppress("unused")
open class ViewLoadingErrorComponent(
    val view: View?,
    private val onRetryClicked: () -> Unit,
    private val errorTextViewStyle: RuleSet = Styles.errorTextViewStyle,
    private val loadingIndicatorStyle: RuleSet = Styles.loadingIndicatorStyle
) {

    private var component: Component? = null
    private var parent: View? = null

    constructor(
        component: Component?,
        onRetryClicked: () -> Unit,
        errorTextViewStyle: RuleSet = Styles.errorTextViewStyle,
        loadingIndicatorStyle: RuleSet = Styles.loadingIndicatorStyle
    ) : this(
        view = null,
        onRetryClicked = onRetryClicked,
        errorTextViewStyle = errorTextViewStyle,
        loadingIndicatorStyle = loadingIndicatorStyle
    ) {
        this.component = component
    }

    open var loadingIndicator: ImageView? = null
    open var errorTextView: TextView? = null

    fun mountIn(parent: View?, showLoading: Boolean = true, showView: Boolean = false, showError: Boolean = false) {
        this.parent = parent
        parent?.apply {
            component?.let { c -> mount(c) }
            view?.let { v -> mount(v) }
            if (showView) {
                makeVisible(view)
                component?.let { parent.mountAfter(it, loadingIndicator ?: return) }
            } else {
                makeNotVisible(view)
                component?.let { parent.unMount(it) }
            }
            loadingIndicator = imageView {
                isVisible = showLoading
                element.src = "/public/img/loading.gif"
                addRuleSet(loadingIndicatorStyle)
            }

            errorTextView = textView {
                isVisible = showError
                addRuleSet(errorTextViewStyle)
                onClick = {
                    showLoading()
                    onRetryClicked()
                }
            }
        }
    }

    fun showView() {
        hideAll()
        makeVisible(view)
        component?.let { parent?.mountAfter(it, loadingIndicator ?: return) }
    }

    fun showLoading() {
        hideAll()
        makeVisible(loadingIndicator)
    }

    fun showError(text: String = "Network error") {
        hideAll()
        errorTextView?.text = text
        makeVisible(errorTextView)
    }

    private fun hideAll() {
        makeNotVisible(view, loadingIndicator, errorTextView)
        component?.let { parent?.unMount(it) }
    }

    fun bind(uiStateObservable: Observable<BasicUiState>, onViewLoaded: () -> Unit) {
        uiStateObservable.observe { onUiStateUpdated(it, onViewLoaded) }
    }

    fun onUiStateUpdated(uiState: BasicUiState?, onViewLoaded: () -> Unit) {
        when (uiState) {
            BasicUiState.Loading -> showLoading()
            BasicUiState.Loaded -> {
                showView()
                onViewLoaded()
            }

            BasicUiState.Error -> showError()
            null -> {}
        }
    }


    companion object {
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
        }
    }
}
