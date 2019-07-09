package org.evolvis.veraweb.onlinereg.mail

import spock.lang.Specification

import javax.mail.event.TransportEvent
import javax.mail.internet.InternetAddress

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
class MailDispatchMonitorTest extends Specification {
    private monitor

    void setup() {
        monitor = new MailDispatchMonitor()
    }

    void testMessageDelivered() {
        given:
            def transportEvent = Mock(TransportEvent)
            transportEvent.getType() >> TransportEvent.MESSAGE_DELIVERED

        when:
            def result = monitor.message(transportEvent)

        then:
            assert result == "MESSAGE_DELIVERED:\n\n"
    }

    void testMessageNotDelivered() {
        given:
            def transportEvent = Mock(TransportEvent)
            transportEvent.getType() >> TransportEvent.MESSAGE_NOT_DELIVERED

        when:
            def result = monitor.message(transportEvent)

        then:
            assert result == "MESSAGE_NOT_DELIVIERED:\n\n"
    }

    void testMessagePartialyDelivered() {
        given:
            def transportEvent = Mock(TransportEvent)
            transportEvent.getType() >> TransportEvent.MESSAGE_PARTIALLY_DELIVERED
            transportEvent.getInvalidAddresses() >> [new InternetAddress(address: "max@mustermann.de")]
            transportEvent.getValidSentAddresses() >> [new InternetAddress(address: "todo@tarent.de")]
            transportEvent.getValidUnsentAddresses() >> [new InternetAddress(address: "pseudouser@tarent.de")]
            def expectedResult = "MESSAGE_PARTIALLY_DELIVERED:\ninvalid: max@mustermann.de\nvalid/sent: " +
                    "todo@tarent.de\nvalid/unsent: pseudouser@tarent.de\n\n"

        when:
            def result = monitor.message(transportEvent)

        then:
            assert result == expectedResult
    }
}
