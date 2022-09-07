package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.components.View
import com.narbase.kunafa.core.components.visit
import com.narbase.kunafa.core.css.classRuleSet
import com.narbase.kunafa.core.css.display
import com.narbase.kunafa.core.lifecycle.LifecycleObserver

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by nezuma
 * On: 4/4/21.
 */
class GridLayout(parent: View? = null) : View(parent) {
    override fun configureElement() {
        super.configureElement()
        addRuleSet(gridClass)
    }

    companion object {
        val gridClass = classRuleSet {
            display = "grid"
        }
    }

}

fun View?.gridLayout(lifecycleObserver: LifecycleObserver? = null, block: GridLayout.() -> Unit): GridLayout {
    return GridLayout(this).visit(lifecycleObserver, block)
}
