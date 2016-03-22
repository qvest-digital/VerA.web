package org.evolvis.veraweb.onlinereg.mail

import spock.lang.Specification

import javax.mail.Address
import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.MimeMessage
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

    void testSend() {
        given:
            def from = "from@tarent.de"
            def to = "to@tarent.de"
            def subject = "subject"
            def text = "mail content"
            def link = "http://tarent.de/activate"
            def contentType = "plaintext"

        when:
            dispatcher.send(from, to, subject, text, link, contentType)

        then:
//            1 * transport.sendMessage(any(), ['to@tarent.de'])
            1 * transport.connect('host', 'username', 'password')
            1 * transport.close()
    }
}
