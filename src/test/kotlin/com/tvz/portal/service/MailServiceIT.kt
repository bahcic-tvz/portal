package com.tvz.portal.service

import com.tvz.portal.PortalApp
import com.tvz.portal.config.DEFAULT_LANGUAGE
import com.tvz.portal.domain.User
import io.github.jhipster.config.JHipsterProperties
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.spring5.SpringTemplateEngine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URI
import java.nio.charset.Charset
import java.util.Properties
import java.util.regex.Pattern
import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.test.assertNotNull

private val languages = arrayOf<String>(
    // jhipster-needle-i18n-language-constant - JHipster will add/remove languages in this array
)
private val PATTERN_LOCALE_3 = Pattern.compile("([a-z]{2})-([a-zA-Z]{4})-([a-z]{2})")
private val PATTERN_LOCALE_2 = Pattern.compile("([a-z]{2})-([a-z]{2})")

/**
 * Integration tests for [MailService].
 */
@SpringBootTest(classes = [PortalApp::class])
class MailServiceIT {

    @Autowired
    private lateinit var jHipsterProperties: JHipsterProperties

    @Autowired
    private lateinit var messageSource: MessageSource

    @Autowired
    private lateinit var templateEngine: SpringTemplateEngine

    @Spy
    private lateinit var javaMailSender: JavaMailSenderImpl

    @Captor
    private lateinit var messageCaptor: ArgumentCaptor<MimeMessage>

    private lateinit var mailService: MailService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doNothing().`when`(javaMailSender).send(any(MimeMessage::class.java))
        mailService = MailService(jHipsterProperties, javaMailSender, messageSource, templateEngine)
    }

    @Test
    fun testSendEmail() {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", isMultipart = false, isHtml = false)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        assertThat(message.subject).isEqualTo("testSubject")
        assertThat(message.allRecipients[0].toString()).isEqualTo("john.doe@example.com")
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content).isInstanceOf(String::class.java)
        assertThat(message.content.toString()).isEqualTo("testContent")
        assertThat(message.dataHandler.contentType).isEqualTo("text/plain; charset=UTF-8")
    }

    @Test
    fun testSendHtmlEmail() {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", isMultipart = false, isHtml = true)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        assertThat(message.subject).isEqualTo("testSubject")
        assertThat(message.allRecipients[0].toString()).isEqualTo("john.doe@example.com")
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content).isInstanceOf(String::class.java)
        assertThat(message.content.toString()).isEqualTo("testContent")
        assertThat(message.dataHandler.contentType).isEqualTo("text/html;charset=UTF-8")
    }

    @Test
    fun testSendMultipartEmail() {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", isMultipart = true, isHtml = false)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        val mp = message.content as MimeMultipart
        val part = (mp.getBodyPart(0).content as MimeMultipart).getBodyPart(0) as MimeBodyPart
        val aos = ByteArrayOutputStream()
        part.writeTo(aos)
        assertThat(message.subject).isEqualTo("testSubject")
        assertThat(message.allRecipients[0].toString()).isEqualTo("john.doe@example.com")
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content).isInstanceOf(Multipart::class.java)
        assertThat(aos.toString()).isEqualTo("\r\ntestContent")
        assertThat(part.dataHandler.contentType).isEqualTo("text/plain; charset=UTF-8")
    }

    @Test
    fun testSendMultipartHtmlEmail() {
        mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", isMultipart = true, isHtml = true)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        val mp = message.content as MimeMultipart
        val part = (mp.getBodyPart(0).content as MimeMultipart).getBodyPart(0) as MimeBodyPart
        val aos = ByteArrayOutputStream()
        part.writeTo(aos)
        assertThat(message.subject).isEqualTo("testSubject")
        assertThat(message.allRecipients[0].toString()).isEqualTo("john.doe@example.com")
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content).isInstanceOf(Multipart::class.java)
        assertThat(aos.toString()).isEqualTo("\r\ntestContent")
        assertThat(part.dataHandler.contentType).isEqualTo("text/html;charset=UTF-8")
    }

    @Test
    fun testSendEmailFromTemplate() {
        val user = User(
            login = "john",
            email = "john.doe@example.com",
            langKey = "en"
        )
        mailService.sendEmailFromTemplate(user, "mail/testEmail", "email.test.title")
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        assertThat(message.subject).isEqualTo("test title")
        assertThat(message.allRecipients[0].toString()).isEqualTo(user.email)
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content.toString()).isEqualToNormalizingNewlines("<html>test title, http://127.0.0.1:8080, john</html>\n")
        assertThat(message.dataHandler.contentType).isEqualTo("text/html;charset=UTF-8")
    }

    @Test
    fun testSendActivationEmail() {
        val user = User(
            langKey = DEFAULT_LANGUAGE,
            login = "john",
            email = "john.doe@example.com"
        )
        mailService.sendActivationEmail(user)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        assertThat(message.allRecipients[0].toString()).isEqualTo(user.email)
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content.toString()).isNotEmpty()
        assertThat(message.dataHandler.contentType).isEqualTo("text/html;charset=UTF-8")
    }

    @Test
    fun testCreationEmail() {
        val user = User(
            langKey = DEFAULT_LANGUAGE,
            login = "john",
            email = "john.doe@example.com"
        )
        mailService.sendCreationEmail(user)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        assertThat(message.allRecipients[0].toString()).isEqualTo(user.email)
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content.toString()).isNotEmpty()
        assertThat(message.dataHandler.contentType).isEqualTo("text/html;charset=UTF-8")
    }

    @Test
    fun testSendPasswordResetMail() {
        val user = User(
            langKey = DEFAULT_LANGUAGE,
            login = "john",
            email = "john.doe@example.com"
        )
        mailService.sendPasswordResetMail(user)
        verify(javaMailSender).send(messageCaptor.capture())
        val message = messageCaptor.value
        assertThat(message.allRecipients[0].toString()).isEqualTo(user.email)
        assertThat(message.from[0].toString()).isEqualTo(jHipsterProperties.mail.from)
        assertThat(message.content.toString()).isNotEmpty()
        assertThat(message.dataHandler.contentType).isEqualTo("text/html;charset=UTF-8")
    }

    @Test
    fun testSendEmailWithException() {
        doThrow(MailSendException::class.java)
            .`when`(javaMailSender)
            .send(any(MimeMessage::class.java))
        try {
            mailService.sendEmail("john.doe@example.com", "testSubject", "testContent", isMultipart = false, isHtml = false)
        } catch (e: Exception) {
            fail<String>("Exception shouldn't have been thrown")
        }
    }

    @Test
    fun testSendLocalizedEmailForAllSupportedLanguages() {
        val user = User(
            login = "john",
            email = "john.doe@example.com"
        )
        for (langKey in languages) {
            user.langKey = langKey
            mailService.sendEmailFromTemplate(user, "mail/testEmail", "email.test.title")
            verify(javaMailSender, atLeastOnce()).send(messageCaptor.capture())
            val message = messageCaptor.value

            val propertyFilePath = "i18n/messages_${getJavaLocale(langKey)}.properties"
            val resource = this::class.java.classLoader.getResource(propertyFilePath)
            assertNotNull(resource)
            val file = File(URI(resource.file).path)
            val properties = Properties()
            properties.load(InputStreamReader(FileInputStream(file), Charset.forName("UTF-8")))

            val emailTitle = properties["email.test.title"] as String
            assertThat(message.subject).isEqualTo(emailTitle)
            assertThat(message.content.toString())
                .isEqualToNormalizingNewlines("<html>$emailTitle, http://127.0.0.1:8080, john</html>\n")
        }
    }

    /**
     * Convert a lang key to the Java locale.
     */
    private fun getJavaLocale(langKey: String): String {
        var javaLangKey = langKey
        val matcher2 = PATTERN_LOCALE_2.matcher(langKey)
        if (matcher2.matches()) {
            javaLangKey = "${matcher2.group(1)}_${matcher2.group(2).toUpperCase()}"
        }
        val matcher3 = PATTERN_LOCALE_3.matcher(langKey)
        if (matcher3.matches()) {
            javaLangKey = "${matcher3.group(1)}_${matcher3.group(2)}_${matcher3.group(3).toUpperCase()}"
        }
        return javaLangKey
    }
}
