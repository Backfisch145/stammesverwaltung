package com.vcp.hessen.kurhessen.features.usermanagement

import com.vcp.hessen.kurhessen.core.mail.EmailService
import com.vcp.hessen.kurhessen.data.UserRepository
import com.vcp.hessen.kurhessen.features.usermanagement.domain.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Component
class ScheduledTasks(
    val userRepository: UserRepository,
    val userService: UserService,
    val emailService: EmailService
) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }


    @Value("\${external.address}")
    private lateinit var externalAddress: String

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    @Throws(
        IOException::class
    )
    fun sendMailForInformationUpdates() {
        log.info("sendMailForInformationUpdates: start")
        val f = ResourceUtils.getFile("classpath:features/usermanagement/CheckInformationEmail.html")
        val html = Files.readString(f.toPath(), StandardCharsets.UTF_8)

        log.warn("sendMailForInformationUpdates: DEAKTIVATED")
        return

        for (user in userRepository.findAll()) {
            if (user.email == null) {
                continue
            }

            if (user.infoUpdateMailSent != null && user.infoUpdateMailSent.isAfter(LocalDate.now().minusYears(1))){
                continue
            }

            if (user.id != 1L) {
                log.info("sendMailForInformationUpdates: skipped User ${user.username}, because id != 1")
                continue
            }

            log.info("sendMailForInformationUpdates: externalAddress = $externalAddress")
            var replacedHtml = html.replace("~Vorname~".toRegex(), user.firstName?:"")
            replacedHtml = replacedHtml.replace("~Nachname~".toRegex(), user.lastName?:"")
            replacedHtml = replacedHtml.replace("~Adresse~".toRegex(), user.address?:"")
            replacedHtml = replacedHtml.replace("~Email~".toRegex(), user.email?:"")
            replacedHtml = replacedHtml.replace("~Allergien~".toRegex(), user.intolerances?:"keine")
            replacedHtml = replacedHtml.replace("~UpdateLink~".toRegex(), externalAddress)

            try {
                emailService.sendSimpleMessage(
                    user.email,
                    "Bitte überprüfen Sie ihre hinterlegten Informationen",
                    replacedHtml,
                    true
                )
            } catch (e: Exception) {
                log.error("sendMailForInformationUpdates: could not send an Email to User ${user.username}", e)
            }
        }
    }
}
