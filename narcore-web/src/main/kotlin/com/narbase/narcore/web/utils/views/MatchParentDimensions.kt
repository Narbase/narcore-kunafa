package com.narbase.narcore.web.utils.views

import com.narbase.kunafa.core.css.RuleSet
import com.narbase.kunafa.core.css.height
import com.narbase.kunafa.core.css.width
import com.narbase.kunafa.core.dimensions.dependent.matchParent

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

val RuleSet.matchParentDimensions: Unit
    get() {
        width = matchParent
        height = matchParent
    }
