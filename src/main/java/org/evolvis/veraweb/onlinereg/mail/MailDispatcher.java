package org.evolvis.veraweb.onlinereg.mail;

import com.sun.mail.smtp.SMTPMessage;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class MailDispatcher {
    private MailDateFormat dateFormat = new MailDateFormat();

    protected String host;
    protected String username;
    protected String password;

    public void send(String from, String to, String subject, String text) throws MessagingException {
        final Session session = getSession();
        final Message message = getMessage(session, from, to, subject, text);
        final Transport transport = session.getTransport("smtp");
        transport.connect(host, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    protected Message getMessage(Session session, String from, String to, String subject, String text) throws MessagingException {
        final Message message = new SMTPMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        message.setHeader("Date", dateFormat.format(new Date(System.currentTimeMillis())));
        message.saveChanges();
        return message;
    }

    protected Session getSession() {
        final Properties properties = System.getProperties();
        if (username != null && password != null) {
            properties.put("mail.smtp.auth", "true");
        }
        return Session.getDefaultInstance(properties);
    }
}
