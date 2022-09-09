@file:Suppress("unused")

package com.narbase.narcore.web.utils.draggable

import com.narbase.kunafa.core.components.*
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.w3c.dom.events.MouseEvent
import kotlin.math.max
import kotlin.math.min

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

class HorizontalDraggable(
    firstComponent: Component,
    secondComponent: Component,
    firstViewInitialPercentage: Int = 50,
    viewMinWidth: Double = 40.0
) : DraggableComponent(
    firstComponent,
    secondComponent,
    firstViewInitialPercentage,
    viewMinWidth,
    HORIZONTAL_MOUSE_DRAG_STYLE
) {


    override fun updateViewsDimension(firstViewDimension: String, secondViewDimension: String) {
        firstView?.element?.style?.width = firstViewDimension
        secondView?.element?.style?.width = secondViewDimension
    }

    override fun View?.getView() = horizontalLayout {
        style {
            width = matchParent
            height = matchParent
        }
        firstView = view {
            style {
                height = matchParent
                overflow = "hidden"
            }
            mount(firstComponent)
        }
        view {
            style {
                width = 1.px
                height = matchParent
                position = "relative"
            }
            handlerView = verticalLayout {
                style {
                    left = (-4).px
                    position = "absolute"
                    width = 8.px
                    height = matchParent
                    cursor = "ew-resize"
                    zIndex = 100
                }
            }
        }

        secondView = view {
            style {
                height = matchParent
                overflow = "hidden"
            }
            mount(secondComponent)
        }
    }

    override fun getDiff(e: MouseEvent) = mouseDownXPos - e.clientX

    override val View.computedDimension: Double
        get() {
            val width = window.getComputedStyle(element, "").width.removeSuffix("px")
            return width.toDoubleOrNull() ?: 0.0
        }
}

class VerticalDraggable(
    firstComponent: Component,
    secondComponent: Component,
    firstViewInitialPercentage: Int = 50,
    viewMinWidth: Double = 40.0
) : DraggableComponent(
    firstComponent,
    secondComponent,
    firstViewInitialPercentage,
    viewMinWidth,
    VERTICAL_MOUSE_DRAG_STYLE
) {

    override fun updateViewsDimension(firstViewDimension: String, secondViewDimension: String) {
        firstView?.element?.style?.height = firstViewDimension
        secondView?.element?.style?.height = secondViewDimension
    }

    override fun View?.getView() = verticalLayout {
        style {
            width = matchParent
            height = matchParent
        }
        firstView = view {
            style {
                width = matchParent
                overflow = "hidden"
            }
            mount(firstComponent)
        }
        view {
            style {
                height = 1.px
                width = matchParent
                position = "relative"
                opacity = 0.0
            }
            handlerView = verticalLayout {
                style {
                    position = "absolute"
                    top = (-4).px
                    height = 8.px
                    width = matchParent
                    cursor = "ns-resize"
                    zIndex = 100
                }
            }
        }

        secondView = view {
            mount(secondComponent)
            style {
                width = matchParent
                overflow = "hidden"
            }
        }
    }

    override fun getDiff(e: MouseEvent) = mouseDownYPos - e.clientY

    override val View.computedDimension: Double
        get() {
            val height = window.getComputedStyle(element, "").height.removeSuffix("px")
            return height.toDoubleOrNull() ?: 0.0
        }
}

abstract class DraggableComponent(
    protected val firstComponent: Component,
    protected val secondComponent: Component,
    firstViewInitialPercentage: Int,
    private val viewMinDimension: Double,
    private val mouseDragStyle: String
) : Component() {
    init {
        if (firstViewInitialPercentage > 100 || firstViewInitialPercentage < 0) {
            throw RuntimeException("firstViewInitialPercentage in DraggableContent should be between 0 to 100")
        }
    }

    private var layoutInitialized = false
    protected var handlerView: View? = null
    protected var firstView: View? = null
    protected var secondView: View? = null

    protected var mouseDownXPos = 0
    protected var mouseDownYPos = 0

    override fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        super.onViewCreated(lifecycleOwner)
        handlerView?.element?.onmousedown = { e ->
            onHandlerMouseDown(e)
        }
        createStyles()
        window.addEventListener("resize", {
            //            handlerUpdaterDelayBouncer.onInputChanged(Unit)
            refreshLayoutsSizes()
        })

    }

    private fun createStyles() {
        stringRuleSet(".$HORIZONTAL_MOUSE_DRAG_STYLE") {
            cursor = "ew-resize"
        }
        stringRuleSet(".$VERTICAL_MOUSE_DRAG_STYLE") {
            cursor = "ns-resize"
        }

    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        super.onViewMounted(lifecycleOwner)
        if (layoutInitialized.not()) {
            refreshLayoutsSizes()
            if (50.0.percentOfParent != 0) {
                layoutInitialized = true
            }
        }
    }

    fun refreshLayoutsSizes() {
        val firstViewDimension =
            max(min(firstViewPercent.percentOfParent, viewMaxDimension.toInt()), viewMinDimension.toInt())
        val secondViewDimension =
            max(min(secondViewPercent.percentOfParent, viewMaxDimension.toInt()), viewMinDimension.toInt())

        updateViewsDimension("${firstViewDimension}px", "${secondViewDimension}px")
    }

    private fun saveMousePosition(x: Int, y: Int) {
        mouseDownXPos = x
        mouseDownYPos = y
    }

    private fun onHandlerMouseDown(event: MouseEvent) {
        event.preventDefault()
        saveMousePosition(event.clientX, event.clientY)

        document.body?.addClass(mouseDragStyle)

        document.onmouseup = {
            document.onmouseup = null
            document.onmousemove = null
            document.body?.removeClass(mouseDragStyle)
            asDynamic()
        }

        document.onmousemove = onmousemove@{ e ->
            onMouseMove(e)
        }

    }

    abstract fun getDiff(e: MouseEvent): Int

    private fun onMouseMove(e: MouseEvent) {
        val diff = getDiff(e)
        val firstView = firstView ?: return
        val secondView = secondView ?: return
        val firstViewDimension = max(min(firstView.computedDimension - diff, viewMaxDimension), viewMinDimension)
        val secondViewDimension = max(min(secondView.computedDimension + diff, viewMaxDimension), viewMinDimension)

        firstViewPercent = firstViewDimension.pxToPercentOfParent

        updateViewsDimension("${firstViewDimension}px", "${secondViewDimension}px")

        if (firstViewDimension > viewMinDimension && secondViewDimension > viewMinDimension) {
            saveMousePosition(e.clientX, e.clientY)
        }
    }

    abstract fun updateViewsDimension(firstViewDimension: String, secondViewDimension: String)

    private val viewMaxDimension
        get() = (rootView?.computedDimension ?: 0.0) /*- (handlerView?.computedDimension ?: 0.0)*/ - viewMinDimension

    private val Double.percentOfParent: Int
        get() {
            return (((rootView?.computedDimension
                ?: 0.0) /*- (handlerView?.computedDimension ?: 0.0)*/) * this / 100.0).toInt()
        }

    private val Double.pxToPercentOfParent: Double
        get() {
            val parentDim = rootView?.computedDimension ?: 0.0
            val availableSpace = parentDim/* - (handlerView?.computedDimension ?: 0.0)*/
            return (this / availableSpace * 100.0)
        }

    private var firstViewPercent = firstViewInitialPercentage.toDouble()
    private val secondViewPercent get() = 100 - firstViewPercent

    abstract val View.computedDimension: Double


    companion object {
        const val HORIZONTAL_MOUSE_DRAG_STYLE = "horizontal-mouse-drag"
        const val VERTICAL_MOUSE_DRAG_STYLE = "vertical-mouse-drag"
    }
}
