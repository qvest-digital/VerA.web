package org.evolvis.veraweb.onlinereg.mail

import spock.lang.Specification

import javax.mail.Transport
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class MailDispatcherGroovyTest extends Specification {
    def dispatcher
    def transport = Mock(Transport)

    void setup() {
        def emailConfiguration = new EmailConfiguration("host", 465, "ssl", "username", "password", "from@tarent.de")
        dispatcher = new MailDispatcher(emailConfiguration)
        dispatcher.setTransport(transport)
    }

    void testSendVerificationEmail() {
        given:
            def from = "from@tarent.de"
            def to = "to@tarent.de"
            def subject = "subjectForVerificationEmail"
            def text = "mail contentForVerificationEmail"
            def link = "http://tarent.de/activate"

        when:
            dispatcher.sendVerificationEmail(from, to, subject, text, link)

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
    }

    void testSendEmailWithoutAttachments() {
        given:
            def from = "from@tarent.de"
            def to = "to@tarent.de"
            def subject = "subjectForVerificationEmail"
            def text = "mail contentForVerificationEmail"

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
            def subject = "subjectForVerificationEmail"
            def text = "mail contentForVerificationEmail"
            def attachments = new HashMap<String, File>()
            attachments.put("1", Files.createTempFile(Paths.get("/tmp"), "testfile", ".tmp").toFile())

        when:
            dispatcher.sendEmailWithAttachments(from, to, subject, text, attachments)

        then:
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
    }
}
