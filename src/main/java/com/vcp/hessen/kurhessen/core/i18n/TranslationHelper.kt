package com.vcp.hessen.kurhessen.core.i18n

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinService
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Slf4j
public class TranslationHelper {
    companion object {
        private val i18NProvider : TranslationProvider = TranslationProvider()
        fun getCurrentLocale(): Locale {
            val l = VaadinService.getCurrent().instantiator.i18NProvider.providedLocales?.firstOrNull()
                ?: Locale.getDefault()
            return l
        }
        fun getTranslation(id: String, vararg params: Any) : String {
            return i18NProvider.getTranslation(
                id,
                getCurrentLocale(),
                *params
            )
        }
    }


}