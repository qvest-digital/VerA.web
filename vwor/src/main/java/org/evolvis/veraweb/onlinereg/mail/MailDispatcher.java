package org.evolvis.veraweb.onlinereg.mail;
import org.evolvis.veraweb.onlinereg.utils.VworConstants;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
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

    private final MailDateFormat dateFormat = new MailDateFormat();

    private final String host;
    private final Integer port;
    private String security;
    private final String username;
    private final String password;
    private Transport transport;
    private final Session session;

    public MailDispatcher(final EmailConfiguration emailConfiguration) throws NoSuchProviderException {
        host = emailConfiguration.getHost();
        port = emailConfiguration.getPort();
        security = emailConfiguration.getSecurity();
        if (security == null) {
            security = "none";
        }
        username = emailConfiguration.getUsername();
        password = emailConfiguration.getPassword();
        session = getSession();
        transport = session.getTransport("smtp");
    }

    public void sendVerificationEmail(final String from, final String to, final String subject, final String text,
      final String link) throws MessagingException {
        final String emailContent = text.replace("${link}", link);
        final Message message;
        message = getMessage(session, from, to, subject, emailContent, VworConstants.HTML_CONTENT_TYPE);
        transport.connect(host, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public MailDispatchMonitor sendEmailWithAttachments(final String from, final String to, final String subject,
      final String emailContent, final Map<String, File> attachments) throws MessagingException {
        final Multipart multipart = new MimeMultipart();
        final MimeBodyPart messageBodyPart = getMessageBody(emailContent);
        multipart.addBodyPart(messageBodyPart);
        if (attachments != null && !attachments.isEmpty()) {
            for (final Entry<String, File> entry : attachments.entrySet()) {
                final DataSource attachment = new FileDataSource(entry.getValue());
                final MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                attachmentBodyPart.setDataHandler(new DataHandler(attachment));
                attachmentBodyPart.setFileName(entry.getKey());
                multipart.addBodyPart(attachmentBodyPart);
            }
        }
        final Message message = getMessage(session, from, to, subject, multipart, multipart.getContentType());
        MailDispatchMonitor monitor = new MailDispatchMonitor();
        transport.addConnectionListener(monitor);
        transport.addTransportListener(monitor);
        transport.connect(host, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        return monitor;
    }

    private MimeBodyPart getMessageBody(String emailContent) throws MessagingException {
        final MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(emailContent, VworConstants.HTML_CONTENT_TYPE);
        return messageBodyPart;
    }

    private Message getMessage(final Session session, final String from, final String to, final String subject, final Object text,
      final String contentType) throws MessagingException {
        final MimeMessage message = initMessage(text, contentType, session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setHeader("Date", dateFormat.format(new Date(System.currentTimeMillis())));
        message.saveChanges();
        return message;
    }

    private MimeMessage initMessage(final Object text, final String contentType, final Session session)
      throws MessagingException {
        final MimeMessage message = new MimeMessage(session);
        message.setContent(text, contentType);
        return message;
    }

    private Session getSession() {
        final Properties properties = System.getProperties();
        if (username != null && password != null) {
            properties.put(PROPERTY_MAIL_SMTP_AUTH, "true");
        }
        if (port != null) {
            properties.put(PROPERTY_MAIL_SMTP_PORT, port);
        }
        setSecurityProperties(properties);

        return Session.getInstance(properties);
    }

    private void setSecurityProperties(final Properties properties) {
        if (TYPE_STARTTLS.equals(security)) {
            properties.put(PROPERTY_MAIL_SMTP_STARTTLS_ENABLE, true);
        } else if (TYPE_SSL.equals(security)) {
            properties.put(PROPERTY_MAIL_SMTP_SSL_ENABLE, true);
        }
    }

    public void setTransport(final Transport transport) {
        this.transport = transport;
    }
}
