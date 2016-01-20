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
import java.util.UUID;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class MailDispatcher {
    private MailDateFormat dateFormat = new MailDateFormat();

    private String host;
    private Integer port;
    private String security;
    private String username;
    private String password;

    public MailDispatcher() {
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        this.host = emailConfiguration.getHost();
        this.port = emailConfiguration.getPort();
        this.security = emailConfiguration.getSecurity();
        if (this.security == null) {
            this.security = "none";
        }
        this.username = emailConfiguration.getUsername();
        this.password = emailConfiguration.getPassword();
    }

    public void send(String to, String subject, String text, String link) throws MessagingException {
        final String emailContent = text.replace("${link}", link);
        final Session session = getSession();
        final Message message = getMessage(session, to, subject, emailContent);
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
        properties.put("mail.smtp.port", port);
        setSecurityProperties(properties);

        return Session.getInstance(properties);
    }

    private void setSecurityProperties(Properties properties) {
        if (security.equals("starttls")) {
            properties.put("mail.smtp.starttls.enable", true);
        } else if (security.equals("ssl")) {
            properties.put("mail.smtp.ssl.enable", true);
        }
    }
}
