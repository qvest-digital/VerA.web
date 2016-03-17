package org.evolvis.veraweb.onlinereg.mail;

import org.evolvis.veraweb.onlinereg.utils.VworConstants;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeMessage;

import java.util.Date;
import java.util.Properties;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class MailDispatcher {
    private static final String PROPERTY_MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
	private static final String PROPERTY_MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	private static final String PROPERTY_MAIL_SMTP_PORT = "mail.smtp.port";
	private static final String PROPERTY_MAIL_SMTP_AUTH = "mail.smtp.auth";

	private static final String TYPE_SSL = "ssl";
	private static final String TYPE_STARTTLS = "starttls";
	private static final String TYPE_HTML = "html";

	private MailDateFormat dateFormat = new MailDateFormat();

    private String host;
    private Integer port;
    private String security;
    private String username;
    private String password;

    public MailDispatcher(final EmailConfiguration emailConfiguration) {
        this.host = emailConfiguration.getHost();
        this.port = emailConfiguration.getPort();
        this.security = emailConfiguration.getSecurity();
        if (this.security == null) {
            this.security = "none";
        }
        this.username = emailConfiguration.getUsername();
        this.password = emailConfiguration.getPassword();
    }

    public void send(String from, String to, String subject, String text, String link, String contentType) throws MessagingException {
        final String emailContent = text.replace("${link}", link);
        final Session session = getSession();
        final Message message = getMessage(session, from, to, subject, emailContent, contentType);
        final Transport transport = session.getTransport("smtp");
        transport.connect(host, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    private Message getMessage(Session session, String from, String to, String subject, String text, String contentType) throws MessagingException {
        final MimeMessage message = initMessage(text, contentType, session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setHeader("Date", dateFormat.format(new Date(System.currentTimeMillis())));
        message.saveChanges();
        return message;
    }

    private MimeMessage initMessage(String text, String contentType, Session session) throws MessagingException {
        final MimeMessage message = new MimeMessage(session);
        if (TYPE_HTML.equalsIgnoreCase(contentType)) {
            message.setContent(text, VworConstants.HTML_CONTENT_TYPE);
        } else {
            message.setContent(text, VworConstants.PLAINTEXT_CONTENT_TYPE);
        }
        return message;
    }

    private Session getSession() {
        final Properties properties = System.getProperties();
        if (username != null && password != null) {
            properties.put(PROPERTY_MAIL_SMTP_AUTH, "true");
        }
        properties.put(PROPERTY_MAIL_SMTP_PORT, port);
        setSecurityProperties(properties);

        return Session.getInstance(properties);
    }

    private void setSecurityProperties(Properties properties) {
        if (TYPE_STARTTLS.equals(security)) {
            properties.put(PROPERTY_MAIL_SMTP_STARTTLS_ENABLE, true);
        } else if (TYPE_SSL.equals(security)) {
            properties.put(PROPERTY_MAIL_SMTP_SSL_ENABLE, true);
        }
    }
}
