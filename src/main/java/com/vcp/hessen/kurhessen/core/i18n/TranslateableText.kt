package com.vcp.hessen.kurhessen.core.i18n

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

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
}