package org.evolvis.veraweb.onlinereg.mail;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

    public MailDispatchMonitor sendEmailWithAttachmentsKeepalive(final String from, final String to, final String subject,
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
        if (!transport.isConnected()) {
            transport.connect(host, username, password);
        }
        transport.sendMessage(message, message.getAllRecipients());
        return monitor;
    }

    public MailDispatchMonitor sendEmailWithAttachments(final String from, final String to, final String subject,
      final String emailContent, final Map<String, File> attachments) throws MessagingException {
        final MailDispatchMonitor monitor = sendEmailWithAttachmentsKeepalive(from, to, subject, emailContent, attachments);
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

    public Transport getTransport() {
        return transport;
    }
}
