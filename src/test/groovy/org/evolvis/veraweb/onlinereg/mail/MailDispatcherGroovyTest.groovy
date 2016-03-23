package org.evolvis.veraweb.onlinereg.mail

import spock.lang.Specification

import javax.mail.Address
import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.MimeMessage
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Array

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class MailDispatcherGroovyTest extends Specification {
    def dispatcher
    def transport = Mock(Transport)

    void setup() {
        def emailConfiguration = new EmailConfiguration("host", 465, "ssl", "username", "password", "from@tarent.de", "subject", "content", "plaintext")
        dispatcher = new MailDispatcher(emailConfiguration)
        dispatcher.setTransport(transport)
    }

    void testSendVerificationEmail() {
        given:
            def from = "from@tarent.de"
            def to = "to@tarent.de"
            def subject = "subject"
            def text = "mail content"
            def link = "http://tarent.de/activate"
            def contentType = "plaintext"

        when:
            dispatcher.sendVerificationEmail(from, to, subject, text, link, contentType)

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
    }

    void testSendEmailWithoutAttachments() {
        given:
            def from = "from@tarent.de"
            def to = "to@tarent.de"
            def subject = "subject"
            def text = "mail content"

        when:
            dispatcher.sendEmailWithAttachments(from, to, subject, text, null)

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
    }

    void testSendEmailWithAttachments() {
        given:
            def from = "from@tarent.de"
            def to = "to@tarent.de"
            def subject = "subject"
            def text = "mail content"
            def attachments = new HashMap<String, File>()
            attachments.put("1", Files.createTempFile(Paths.get("/tmp"), "testfile", ".tmp").toFile())

        when:
            dispatcher.sendEmailWithAttachments(from, to, subject, text, attachments)

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
    }
}
