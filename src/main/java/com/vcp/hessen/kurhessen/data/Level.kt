package com.vcp.hessen.kurhessen.data

import com.vcp.hessen.kurhessen.core.i18n.TranslatableText
import java.awt.Color

enum class Level(val langKey : String, val maxAge : Int, val colorStr: String) {
    WOELFLING("WOELFLING", 10, "#ed3422"),
    JUNGPFADFINDER("JUNGPFADFINDER", 14, "#5cbd00"),
    PFADFINDER("PFADFINDER", 16, "#00834d"),
    ROVER("ROVER", 21, "#701a32"),
    ERWACHSEN("ERWACHSEN", 21, "#4f2778");

    fun getTitleTranslated() : String {
        return TranslatableText(this.langKey).translate()
    }
    fun getColor() : Color {
        return Color.getColor(colorStr);
    }
}