package com.vcp.hessen.kurhessen.data

import com.vcp.hessen.kurhessen.core.i18n.TranslatableText

enum class Level(val langKey : String, val maxAge : Int) {
    WOELFLING("WOELFLING", 10),
    JUNGPFADFINDER("JUNGPFADFINDER", 14),
    PFADFINDER("PFADFINDER", 16),
    ROVER("ROVER", 21),
    ERWACHSEN("ERWACHSEN", 21);

    fun getTitleTranslated() : String {
        return TranslatableText(this.langKey).translate()
    }
}