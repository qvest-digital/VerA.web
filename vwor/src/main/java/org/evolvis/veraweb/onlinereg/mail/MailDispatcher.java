package org.evolvis.veraweb.onlinereg.mail;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
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
import javax.swing.text.html.HTML;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static org.evolvis.veraweb.onlinereg.utils.VworConstants.HTML_CONTENT_TYPE;
import static org.evolvis.veraweb.onlinereg.utils.VworConstants.PLAINTEXT_CONTENT_TYPE;

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

    public void sendVerificationEmail(final String from, final String to, final String subject, final String text, final String link,
                                      final String contentType) throws MessagingException {
        final String emailContent = text.replace("${link}", link);
        final Message message;
        if (TYPE_HTML.equalsIgnoreCase(contentType)) {
            message = getMessage(session, from, to, subject, emailContent, HTML_CONTENT_TYPE);
        } else {
            message = getMessage(session, from, to, subject, emailContent, PLAINTEXT_CONTENT_TYPE);
        }
        transport.connect(host, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public MailDispatchMonitor sendEmailWithAttachments(final String from,
                                                        final String to,
                                                        final String subject,
                                                        final String emailContent,
                                                        final Map<String, File> attachments,
                                                        String contentType) throws MessagingException {
        final Multipart multipart = new MimeMultipart();
        final MimeBodyPart messageBodyPart = getMessageBody(emailContent, contentType);
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

    private MimeBodyPart getMessageBody(String emailContent, String contentType) throws MessagingException {
        final MimeBodyPart messageBodyPart = new MimeBodyPart();
        if (TYPE_HTML.equalsIgnoreCase(contentType)) {
            messageBodyPart.setContent(emailContent, HTML_CONTENT_TYPE);
        } else {
            messageBodyPart.setContent(emailContent, PLAINTEXT_CONTENT_TYPE);
        }
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

    private MimeMessage initMessage(final Object text, final String contentType, final Session session) throws MessagingException {
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
