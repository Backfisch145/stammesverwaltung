package com.vcp.hessen.kurhessen.data

import com.vcp.hessen.kurhessen.core.i18n.TranslatableText

enum class Gender(val langKey : String) {
    MALE("MALE"),
    FEMALE("FEMALE"),
    UNKNOWN("Unknown"),
    OTHER("Other");


    fun getTitleTranslated() : String {
        return TranslatableText(this.langKey).translate()
    }
    fun getShortTranslated() : String {
        return TranslatableText(this.langKey).translate()[0] + ""
    }
}