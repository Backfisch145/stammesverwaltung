package com.vcp.hessen.kurhessen.data

import com.vcp.hessen.kurhessen.core.i18n.TranslatableText

enum class Diet(val langKey : String) {
    UNKNOWN("Unknown"),
    OMNIVORE("Omnivore"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan");

    fun getTitleTranslated() : String {
        return TranslatableText(this.langKey).translate()
    }
    fun getShortTranslated() : String {
        return TranslatableText(this.langKey).translate()[0] + ""
    }
}
