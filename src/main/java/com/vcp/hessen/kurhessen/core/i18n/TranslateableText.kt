package com.vcp.hessen.kurhessen.core.i18n

import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger("TranslatableText")
open class TranslatableText(val id: String, vararg val params: Any) {

    companion object {

    }

    fun translate(): String {
        return TranslationHelper.getTranslation(
            id,
            *params
        )
    }

    override fun toString(): String {
        return translate()
    }


}