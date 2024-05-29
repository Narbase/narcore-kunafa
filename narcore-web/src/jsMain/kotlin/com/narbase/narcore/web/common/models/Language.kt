/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

/*
 * Copyright 2017-2020 Narbase technologies and contributors. Use of this source code is governed by the MIT License.
 */

package com.narbase.narcore.web.common.models


enum class Direction(val key: String) {
    LTR("ltr"),
    RTL("rtl"), ;

    fun toHtmlDirection(): String {
        return this.key
    }

}

enum class Language(
    val locale: String,
    val label: String,
    val imageSrc: String,
    val direction: Direction,
    val appFontFamily: String
) {

    En("en-GB", "Change to English", "/public/img/english.png", Direction.LTR, "'Work Sans', sans-serif"),
    Ar("ar-EG", "التغير الى العربية", "/public/img/arabic.png", Direction.RTL, "'Cairo', 'Work Sans', sans-serif"),

    ;

    fun toHtmlLanguage(): String {
        return this.locale
    }


}

fun String.toLanguage(): Language {

    return Language.values().firstOrNull { it.locale == this }
        ?: throw NotImplementedError("Language key: {$this} is not implemented!")


}