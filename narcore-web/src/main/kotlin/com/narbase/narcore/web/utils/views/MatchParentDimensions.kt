package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.css.RuleSet
import com.narbase.kunafa.core.css.height
import com.narbase.kunafa.core.css.width
import com.narbase.kunafa.core.dimensions.dependent.matchParent

/**
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] -[2019] Narbase Technologies
 * All Rights Reserved.
 * Created by islam
 * On: 2020/01/27.
 */

val RuleSet.matchParentDimensions: Unit
    get() {
        width = matchParent
        height = matchParent
    }
