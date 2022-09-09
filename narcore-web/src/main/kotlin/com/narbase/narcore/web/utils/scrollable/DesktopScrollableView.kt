package com.narbase.narcore.web.utils.scrollable

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.components.view
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.narcore.web.utils.onHover
import com.narbase.narcore.web.utils.views.DelayBouncer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.events.MouseEvent
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */
@Suppress("NOTHING_TO_INLINE")
class DesktopScrollableView(
    val parentBlock: (View.() -> Unit)?,
    val block: View.() -> Unit
) : ScrollableView() {

    override var childView: View? = null
    private var scrollbarView: View? = null
    private var scrollbarHandler: View? = null
    private val handlerUpdaterDelayBouncer = DelayBouncer<Unit>(100) {
        refreshScrollHandler()
    }

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        scrollbarHandler?.element?.onmousedown = { e ->
            onHandlerMouseDown(e)
        }
        scrollbarHandler?.onClick = { it.preventDefault() }
        scrollbarView?.element?.onmousedown = this::onBarClicked
        window.addEventListener("resize", {
            refreshScrollHandler()
        })
        scrollbarView?.element?.onmouseenter = {
            refreshScrollHandler()
        }
        childView?.element?.onmouseenter = {
            refreshScrollHandler()
        }
        val observer = MutationObserver { _, _ ->
            handlerUpdaterDelayBouncer.onInputChanged(Unit)
        }
        childView?.let {
            observer.observe(
                it.element, MutationObserverInit(
                    childList = true, attributes = true, subtree = true
                )
            )
        }

    }

    var calculatedScrollBarWidth = 0

    private fun hideScrollBar() {
        val element = childView?.element ?: return
        var padding = element.offsetWidth - element.clientWidth
        if (padding > 0) {
            if (calculatedScrollBarWidth == 0 || (calculatedScrollBarWidth - padding).absoluteValue > 1) {
                calculatedScrollBarWidth = padding
            } else if (calculatedScrollBarWidth > 0) {
                padding = calculatedScrollBarWidth
            }
        }
        element.style.width = "calc(100% + ${padding}px)"
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        refreshScrollHandler()
    }

    @Suppress("USELESS_ELVIS")
    private fun onBarClicked(e: MouseEvent) {
        val scrollbarViewTop = scrollbarView?.element?.getBoundingClientRect()?.top ?: return
        val scrollableViewTopOffset = scrollbarViewTop + (window.pageYOffset ?: document.documentElement?.scrollTop
        ?: 0.0)
        val yPosition = e.pageY - (scrollableViewTopOffset)
        val handlerTop = scrollbarHandler?.element?.offsetTop ?: 0
        val handlerHeight = scrollbarHandler?.element?.offsetHeight ?: 0
        val handlerEnd = handlerHeight + handlerTop
        if (yPosition > handlerTop && yPosition < handlerEnd) {
            return
        }

        val factor = yPosition / (scrollbarView?.element?.offsetHeight?.toDouble() ?: 0.0)
        scrollHandlerByFactor(factor)
        scrollContentByFactor(factor)
    }

    var mouseDownPos = 0
    private fun onHandlerMouseDown(event: MouseEvent) {
        event.preventDefault()
        mouseDownPos = event.clientY
        document.onmouseup = {
            document.onmouseup = null
            document.onmousemove = null
            asDynamic()
        }
        document.onmousemove = { e ->
            val diff = mouseDownPos - e.clientY
            mouseDownPos = e.clientY
            val elementTop = scrollbarHandler?.element?.offsetTop ?: 0
            val newVal = elementTop - diff
            val maxHandlerScroll = getMaxHandlerScroll()
            val newElementTop = when {
                newVal < 0 -> 0
                newVal > maxHandlerScroll -> maxHandlerScroll
                else -> newVal
            }
            scrollbarHandler?.element?.style?.top = "${newElementTop}px"

            scrollContentByFactor(newElementTop.toDouble() / maxHandlerScroll.toDouble())
            asDynamic()
        }

    }

    override fun View?.getView() = parent {
        style {
            width = matchParent
            height = matchParent
            overflow = "hidden"
            position = "relative"
        }

        parentBlock?.invoke(this)
        childView = child {
            style {
                width = 100.percent
                height = 100.percent
                overflowY = "scroll"
                boxSizing = "content-box"
            }
            element.onscroll = {
                readjustScrollHandlerPosition()
            }
            block()
        }

        scrollbarView = scrollbar {
            val scrollBarStyle = style {
                position = "absolute"
                top = 0.px
                bottom = 0.px
                right = 0.px
                width = 14.px
                backgroundColor = Color(0, 0, 0, 0.0)
                padding = 1.px
                transition = "0.3s"
                hover {
                    backgroundColor = Color(0, 0, 0, 0.1)
                }
            }
            element.style.opacity = "0"
            scrollbarHandler = view {
                style {
                    position = "absolute"
                    backgroundColor = Color(0, 0, 0, 0.2)
                    left = 6.px
                    right = 2.px
                    borderRadius = 10.px
                    transitionDuration = "0.3s"
                    transitionProperty = "left,background-color"
                    onHover(scrollBarStyle) {
                        left = 2.px
                        backgroundColor = Color(0, 0, 0, 0.4)
                    }
                }
                element.style.minHeight = 30.px.toString()

            }
        }
    }

    private fun readjustScrollHandlerPosition() {
        val view = childView ?: return
        var factor = view.element.scrollTop / (view.element.scrollHeight - view.element.offsetHeight).toDouble()
        if (factor.isNaN()) factor = 0.0
        scrollHandlerByFactor(factor)
    }

    private fun scrollHandlerByFactor(factor: Double) {
        val maxScroll = getMaxHandlerScroll()
        val currentScroll = (maxScroll.toDouble() * factor).roundToInt()
        scrollbarHandler?.element?.style?.top = "${currentScroll}px"

    }

    private fun scrollContentByFactor(factor: Double) {
        val maxScroll = (childView?.element?.scrollHeight ?: 0) - (childView?.element?.offsetHeight ?: 0)
        val currentScroll = (maxScroll.toDouble() * factor)
        childView?.element?.scrollTop = currentScroll

    }

    private fun getMaxHandlerScroll(): Int {
        val totalLength = scrollbarView?.element?.offsetHeight ?: 0
        val handlerLength = scrollbarHandler?.element?.offsetHeight ?: 0
        return totalLength - handlerLength
    }

    var minHandlerHeight = 30

    override fun refreshScrollHandler() {
        hideScrollBar()
        val scrollHeight = childView?.element?.scrollHeight ?: 1
        val scrollBarHeight = scrollbarView?.element?.offsetHeight ?: 1
        val factor = when {
            scrollBarHeight == 0 || scrollHeight == 0 -> 1.0
            else -> scrollBarHeight.toDouble() / scrollHeight.toDouble()
        }
        val handlerHeight = factor * scrollBarHeight.toDouble()
        val adjustedHandlerHeight = if (handlerHeight < minHandlerHeight) minHandlerHeight else handlerHeight.toInt()
        scrollbarHandler?.element?.style?.height = adjustedHandlerHeight.px.toString()

        if (factor >= 1.toDouble()) {
            scrollbarView?.element?.style?.opacity = "0"
        } else {
            scrollbarView?.element?.style?.opacity = "1"
        }
        readjustScrollHandlerPosition()
    }

    private inline fun View?.parent(noinline block: LinearLayout.() -> Unit) = verticalLayout(null, block)
    private inline fun View?.child(noinline block: LinearLayout.() -> Unit) = verticalLayout(null, block)
    private inline fun View?.scrollbar(noinline block: LinearLayout.() -> Unit) = verticalLayout(null, block)
}


@Suppress("ConstantConditionIf")
fun View?.scrollable(parentBlock: (View.() -> Unit)? = null, block: View.() -> Unit): ScrollableView {
//    return if (AppConfig.isMobile) mobileScrollable(parentBlock, block) else desktopScrollable(parentBlock, block)
    return desktopScrollable(parentBlock, block)
}


fun View?.desktopScrollable(parentBlock: (View.() -> Unit)? = null, block: View.() -> Unit): ScrollableView {
    return DesktopScrollableView(parentBlock, block).apply {
        this@desktopScrollable?.mount(this)
    }
}

fun View?.mobileScrollable(parentBlock: (View.() -> Unit)? = null, block: View.() -> Unit): MobileScrollableView {
    return MobileScrollableView(parentBlock, block).apply {
        this@mobileScrollable?.mount(this)
    }
}
