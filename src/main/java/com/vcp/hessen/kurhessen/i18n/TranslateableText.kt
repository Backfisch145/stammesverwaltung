package com.vcp.hessen.kurhessen.i18n

import com.vaadin.flow.component.UI
import com.vaadin.flow.i18n.I18NProvider
import com.vaadin.flow.server.VaadinService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

val log = KotlinLogging.logger("TranslatableText")
open class TranslatableText(val id: String, vararg val params: Any) {

    companion object {
        private val i18NProvider : TranslationProvider = TranslationProvider()
        fun getCurrentLocale() : Locale{
            val locale = UI.getCurrent()?.locale ?: VaadinService.getCurrent().instantiator.i18NProvider.providedLocales?.firstOrNull() ?: Locale.getDefault()
            return locale
        }
    }

    fun translate(): String {
        return i18NProvider.getTranslation(
            id,
            getCurrentLocale(),
            *params
        )
    }
}