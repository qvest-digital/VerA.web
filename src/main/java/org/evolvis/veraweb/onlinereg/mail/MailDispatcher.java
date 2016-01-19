package org.evolvis.veraweb.onlinereg.mail;

import com.sun.mail.smtp.SMTPMessage;
import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;

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

    private String host;
    private  String username;
    private  String password;
    private Integer port;

    public MailDispatcher() {
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        this.host = emailConfiguration.getHost();
        this.port = emailConfiguration.getPort();
        this.username = emailConfiguration.getUsername();
        this.password = emailConfiguration.getPassword();
    }

    public void send(String to, String subject, String text) throws MessagingException {
        final Session session = getSession();
        final Message message = getMessage(session, to, subject, text);
        final Transport transport = session.getTransport("smtp");
        transport.connect(host, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    protected Message getMessage(Session session, String to, String subject, String text) throws MessagingException {
        final Message message = new SMTPMessage(session);
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
        if (port != null) {
            setPortProperties(properties);
        }

        return Session.getInstance(properties);
    }

    private void setPortProperties(Properties properties) {
        properties.put("mail.smtp.port", port);
        if (port.equals(578)) {
            properties.put("mail.smtp.starttls.enable", true);
        } else if (port.equals(465)) {
            properties.put("mail.smtp.ssl.enable", true);
        }
    }
}
