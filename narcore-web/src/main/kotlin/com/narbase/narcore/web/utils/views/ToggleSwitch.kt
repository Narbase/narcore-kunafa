package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.Component
import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.layout.LinearLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.percent
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.kunafa.core.lifecycle.LifecycleOwner
import com.narbase.kunafa.core.lifecycle.Observable

class ToggleSwitch(val idString: String? = null) : Component() {
    var switch: LinearLayout? = null
    var outerCircle: LinearLayout? = null
    var innerCircle: LinearLayout? = null
    val isSelected = Observable<Boolean>().apply { value = false }

    fun resetIsSelected() {
        isSelected.clearObservers()
        isSelected.value = false
        isSelected.observe { onIsClickedUpdated(it) }
    }

    override fun onViewMounted(lifecycleOwner: LifecycleOwner) {
        isSelected.observe {
            onIsClickedUpdated(it)
        }
    }

    private fun onIsClickedUpdated(it: Boolean?) {
        it ?: return
        if (it == true) {
            switch?.removeRuleSet(unselectedBacground)
            switch?.addRuleSet(selectedBackground)
            outerCircle?.removeRuleSet(circleUnselectedStyle)
            outerCircle?.removeRuleSet(unselectedBacground)
            outerCircle?.addRuleSet(circleSelectedStyle)
            outerCircle?.addRuleSet(selectedBackground)
            innerCircle?.removeRuleSet(unselectedBacground)
            innerCircle?.addRuleSet(selectedBackground)
        } else {
            switch?.removeRuleSet(selectedBackground)
            switch?.addRuleSet(unselectedBacground)
            outerCircle?.removeRuleSet(circleSelectedStyle)
            outerCircle?.removeRuleSet(selectedBackground)
            outerCircle?.addRuleSet(circleUnselectedStyle)
            outerCircle?.addRuleSet(unselectedBacground)
            innerCircle?.removeRuleSet(selectedBackground)
            innerCircle?.addRuleSet(unselectedBacground)
        }
    }

    override fun onViewRemoved(lifecycleOwner: LifecycleOwner) {
        isSelected.clearObservers()
    }

    override fun View?.getView(): View = horizontalLayout {

        this.id = idString
        style {
            position = "relative"
            alignItems = Alignment.Center
            alignSelf = Alignment.Center
        }

        switch = horizontalLayout {
            style {
                opacity = 0.2
                display = "inline-block"
                width = 40.px
                height = 10.px
                borderRadius = 20.px
                transition = ".3s"
            }
        }
        outerCircle = horizontalLayout {
            style {
                content = ""
                position = "absolute"
                opacity = 1.0
                width = 14.px
                height = 14.px
                borderRadius = 50.percent
                transition = ".4s"
            }

            innerCircle = horizontalLayout {
                style {
                    content = ""
                    position = "absolute"
                    zIndex = 2
                    left = (-2).px
                    top = (-2).px
                    width = 18.px
                    height = 18.px
                    opacity = 0.2
                    borderRadius = 50.percent
                    transition = ".4s"
                }
            }

        }
        onClick = {
            isSelected.value = isSelected.value?.not()
            1
        }

    }

    val circleUnselectedStyle = classRuleSet {
        left = 5.px
    }

    val circleSelectedStyle = classRuleSet {
        left = 20.px
    }

    val selectedBackground = classRuleSet {
        backgroundColor = Color("31c1c1")
    }
    val unselectedBacground = classRuleSet {
        backgroundColor = Color("b5b5b5")
    }
}
