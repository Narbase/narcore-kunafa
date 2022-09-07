/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by Shalaga44
 * On: 2022/Jun/20.
 */

/*
 * NARBASE TECHNOLOGIES CONFIDENTIAL
 * ______________________________
 * [2017] - [2022] Narbase Technologies
 * All Rights Reserved.
 * Created by Shalaga44
 * On: 2022/Jun/20.
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