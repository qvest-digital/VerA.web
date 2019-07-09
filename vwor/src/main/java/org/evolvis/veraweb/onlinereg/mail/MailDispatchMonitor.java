package org.evolvis.veraweb.onlinereg.mail;
import lombok.extern.log4j.Log4j2;

import javax.mail.Address;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

@Log4j2
public class MailDispatchMonitor implements TransportListener, ConnectionListener {
    private StringBuilder sb = new StringBuilder();

    private static String message(TransportEvent e) {
        final StringBuilder sb = new StringBuilder();
        switch (e.getType()) {
        case TransportEvent.MESSAGE_DELIVERED:
            sb.append("MESSAGE_DELIVERED");
            break;
        case TransportEvent.MESSAGE_NOT_DELIVERED:
            sb.append("MESSAGE_NOT_DELIVIERED");
            break;
        case TransportEvent.MESSAGE_PARTIALLY_DELIVERED:
            sb.append("MESSAGE_PARTIALLY_DELIVERED");
            break;
        default:
            sb.append("MESSAGE_NOT_DELIVIERED");
            break;
        }
        sb.append(":\n");
        appendAddresses(sb, "invalid", e.getInvalidAddresses());
        appendAddresses(sb, "valid/sent", e.getValidSentAddresses());
        appendAddresses(sb, "valid/unsent", e.getValidUnsentAddresses());
        sb.append("\n");
        return sb.toString();
    }

    private static void appendAddresses(StringBuilder sb, String label, Address[] addresses) {
        if (addresses != null && addresses.length > 0) {
            sb.append(label);
            sb.append(": ");
            boolean first = true;
            for (Address address : addresses) {
                sb.append(address.toString());
                if (!first) {
                    sb.append(", ");
                }
                first = false;
            }
            sb.append("\n");
        }
    }

    private void debug(ConnectionEvent e) {
        logger.debug(e);
    }

    private void info(TransportEvent e) {
        sb.append(message(e));
    }

    private void warn(TransportEvent e) {
        logger.warn(message(e));
        sb.append(message(e));
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void opened(ConnectionEvent e) {
        debug(e);
    }

    @Override
    public void disconnected(ConnectionEvent e) {
        debug(e);
    }

    @Override
    public void closed(ConnectionEvent e) {
        debug(e);
    }

    @Override
    public void messageDelivered(TransportEvent e) {
        info(e);
    }

    @Override
    public void messageNotDelivered(TransportEvent e) {
        warn(e);
    }

    @Override
    public void messagePartiallyDelivered(TransportEvent e) {
        warn(e);
    }
}
