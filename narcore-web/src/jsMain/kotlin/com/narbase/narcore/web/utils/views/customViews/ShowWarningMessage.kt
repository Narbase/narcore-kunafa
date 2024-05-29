package com.narbase.narcore.web.utils.views.customViews

import com.narbase.kunafa.core.components.horizontalLayout
import com.narbase.kunafa.core.components.imageView
import com.narbase.kunafa.core.components.textView
import com.narbase.kunafa.core.components.verticalLayout
import com.narbase.kunafa.core.css.*
import com.narbase.kunafa.core.dimensions.dependent.matchParent
import com.narbase.kunafa.core.dimensions.dependent.wrapContent
import com.narbase.kunafa.core.dimensions.dimen
import com.narbase.kunafa.core.dimensions.px
import com.narbase.kunafa.core.drawable.Color
import com.narbase.narcore.web.common.AppColors
import com.narbase.narcore.web.translations.localized
import com.narbase.narcore.web.utils.colors.gray
import com.narbase.narcore.web.utils.views.PopUpDialog
import com.narbase.narcore.web.utils.views.popUpDialog

fun initWarningPopupDialog() {
    showWarningPopUpDialog = popUpDialog { }
}

private var showWarningPopUpDialog: PopUpDialog? = null
fun showPopupMessage(
    popupTitle: String,
    warning: String,
    image: String? = null,
    imageSize: Int = 200,
    positiveText: String,
    onPositiveClicked: () -> Unit,
    addCancelButton: Boolean = true,
    cancelText: String = "Dismiss",
    onCancelClicked: (() -> Unit)? = null
): PopUpDialog? {
    if (showWarningPopUpDialog == null) {
        console.log("Call initWarningPopupDialog() before using showText")
        return showWarningPopUpDialog
    }
    showWarningPopUpDialog?.showDialog {
        verticalLayout {
            style {
                backgroundColor = Color.white
                borderRadius = 12.px
                padding = 40.px
                width = 500.px
                alignItems = Alignment.Center
                justifyContent = JustifyContent.SpaceAround
            }

            imageView {
                style {
                    alignSelf = Alignment.Center
                    width = imageSize.px
                    objectFit = "cover"
                    marginBottom = 50.px
                }

                element.src = image ?: return@imageView
            }


            textView {
                style {
                    marginBottom = 12.px
                    fontWeight = "bold"
                    fontSize = 16.px
                    textAlign = TextAlign.Center
                }
                text = popupTitle
            }

            textView {
                style {
                    marginBottom = 40.px
                    fontSize = 14.px
                    textAlign = TextAlign.Center
                }
                text = warning
            }

            horizontalLayout {
                style {
                    width = matchParent
                    height = wrapContent
                    justifyContent = JustifyContent.End
                }

                if (addCancelButton) {
                    textView {
                        id = "dismissWarningButton"
                        text = cancelText.localized()
                        addRuleSet(Styles.buttonStyle)
                        onClick = {
                            onCancelClicked?.invoke()
                            showWarningPopUpDialog?.dismissDialog()
                        }
                    }
                }

                textView {
                    id = "confirmButton"
                    style {
                        marginStart = 20.px
                    }

                    text = positiveText
                    addRuleSet(Styles.confirmButtonStyle)
                    onClick = {
                        onPositiveClicked()
                        showWarningPopUpDialog?.dismissDialog()
                    }
                }
            }
        }
    }

    return showWarningPopUpDialog
}

object Styles {
    val buttonStyle = classRuleSet {
        padding = "4px 12px".dimen()
        border = "1px solid ${AppColors.borderColorHex}"
        borderRadius = 18.px
        backgroundColor = gray(0.97)
        cursor = "pointer"
        fontSize = 14.px
    }
    val confirmButtonStyle = classRuleSet {
        padding = "4px 12px".dimen()
        border = "1px solid ${AppColors.narcoreColor}"
        borderRadius = 18.px
        backgroundColor = AppColors.narcoreColor
        color = Color.white
        cursor = "pointer"
        fontSize = 14.px
    }
}
