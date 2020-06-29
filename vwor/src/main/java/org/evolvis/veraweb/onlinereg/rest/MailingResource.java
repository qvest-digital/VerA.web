package org.evolvis.veraweb.onlinereg.rest;

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

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.evolvis.veraweb.common.RestPaths;
import org.evolvis.veraweb.onlinereg.entities.Person;
import org.evolvis.veraweb.onlinereg.entities.PersonMailinglist;
import org.evolvis.veraweb.onlinereg.mail.EmailConfiguration;
import org.evolvis.veraweb.onlinereg.mail.MailDispatchMonitor;
import org.evolvis.veraweb.onlinereg.mail.MailDispatcher;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.Session;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path(RestPaths.REST_MAILING)
@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Log4j2
public class MailingResource extends FormDataResource {
    public static final String PARAM_MAILINGLIST_ID = "mailinglist-id";
    public static final String PARAM_MAIL_TEXT = "mailtext";
    public static final String PARAM_MAIL_SUBJECT = "mail-subject";

    private MailDispatcher mailDispatcher;
    private EmailConfiguration emailConfiguration;

    @AllArgsConstructor
    private static final class SendResult {
        public final boolean success;
        public final String message;
    }

    @POST
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response uploadFile(final FormDataMultiPart formData) {
        final String subject = formData.getField(PARAM_MAIL_SUBJECT).getEntityAs(String.class);
        final String text = formData.getField(PARAM_MAIL_TEXT).getEntityAs(String.class);
        final int mailinglistId = Integer.parseInt(formData.getField(PARAM_MAILINGLIST_ID).getEntityAs(String.class));

        final List<PersonMailinglist> recipients = getRecipients(mailinglistId);
        logger.info("sending mailing list #" + mailinglistId + " to " + recipients.size() + " recipients");

        final Map<String, File> files = getFiles(formData.getFields("files"));
        final SendResult r;

        try {
            r = sendEmails(recipients, subject, text, files);
        } catch (AddressException e) {
            logger.error("Email-Adress is not valid", e);
            return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (final MessagingException e) {
            logger.error("Sending email failed", e);
            return Response.status(Status.BAD_GATEWAY).build();
        } finally {
            removeAttachmentsFromFilesystem(files);
        }

        return Response.status(r.success ? Status.OK : Status.BAD_REQUEST).entity(r.message).build();
    }

    @SuppressWarnings("unchecked")
    private List<PersonMailinglist> getRecipients(final int listId) {
        try (Session session = openSession()) {
            return session.getNamedQuery("PersonMailinglist.findByMailinglist").setParameter("listId", listId).list();
        }
    }

    public static boolean toTrim(final char c) {
        return Character.isWhitespace(c) ||
          c == 0x200B || c == 0x200C || c == 0x200D || c == 0x2060 || c == 0xFEFF;
    }

    public static String trim(final String str) {
        final StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && toTrim(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        while (sb.length() > 0 && toTrim(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String tt(final String text) {
        return String.format("<tt>%s</tt>", text.replace("&", "&amp;").
          replace("<", "&lt;").replace(">", "&gt;"));
    }

    private SendResult sendEmails(final List<PersonMailinglist> recipients, final String subject, final String text,
      final Map<String, File> files) throws MessagingException {
        final StringBuilder sb = new StringBuilder();
        if (emailConfiguration == null) {
            emailConfiguration = new EmailConfiguration();
        }
        if (mailDispatcher == null) {
            mailDispatcher = new MailDispatcher(emailConfiguration);
        }

        final HashMap<String, String> alreadySeen = new HashMap<>();
        int members = 0;
        int mailboxen = 0;
        int duplicates = 0;
        int badAddresses = 0;
        int badRecipients = 0;

        for (final PersonMailinglist recipient : recipients) {
            ++members;
            /*
             * This is rather ineffective. We shoul be able to send just one
             * eMail for a recipient, instead of one per addr-spec (their
             * field can contain an addr-list (list of address) which each
             * can contain an addr-spec or a group-list (list of addr-spec)),
             * merely skipping the invalid ones in the transport.sendMessage
             * call, but have the header show the full information, including
             * display-name for both mailbox and group… but, eh, old code/API,
             * hard to change on a deadline and I’ll celebrate if this at all
             * (correctly validating addresses and sending to valid ones) works.
             */
            final String from = getFrom(recipient);
            final String orgAddresses = trim(recipient.getAddress());
            val recipientAddresses = org.evolvis.tartools.rfc822.Path.of(orgAddresses);
            if (recipientAddresses == null) {
                logger.error("email address parser cannot be instantiated: " + orgAddresses);
                sb.append("ADDRESS_SYNTAX_NOT_CORRECT:").append(tt(orgAddresses)).append("\n\n");
                ++badRecipients;
                continue;
            }
            val addresses = recipientAddresses.asAddressList();
            if (addresses == null) {
                logger.error("email address cannot be parsed: " + orgAddresses);
                sb.append("ADDRESS_SYNTAX_NOT_CORRECT:").append(tt(orgAddresses)).append("\n\n");
                ++badRecipients;
                continue;
            }
            val addrlist = addresses.getAddresses().stream().flatMap(a ->
              (a.isGroup() ? a.getMailboxen() : Collections.singletonList(a)).stream().
                map(org.evolvis.tartools.rfc822.Path.Address::getMailbox)).
              collect(Collectors.toList());
            if (addrlist.isEmpty()) {
                logger.error("no email address found: " + orgAddresses);
                sb.append("ADDRESS_SYNTAX_NOT_CORRECT:").append(tt(orgAddresses)).append("\n\n");
                ++badRecipients;
                continue;
            }
            for (val addrspec : addrlist) {
                ++mailboxen;
                final String address = addrspec.toString();
                if (alreadySeen.put(address, address) != null) {
                    ++duplicates;
                    logger.info("skipping duplicate address: " + address);
                    continue;
                }
                if (!addrspec.isValid()) {
                    logger.error("email address " + address + " is invalid: " + orgAddresses);
                    sb.append("ADDRESS_SYNTAX_NOT_CORRECT:").append(tt(address)).
                      append(" of ").append(tt(orgAddresses)).append("\n\n");
                    ++badAddresses;
                }
                try {
                    final MailDispatchMonitor monitor = mailDispatcher.sendEmailWithAttachmentsKeepalive(from,
                      address, subject, substitutePlaceholders(text, recipient.getPerson()), files);
                    sb.append(monitor.toString());
                } catch (AddressException e) {
                    logger.error("email address " + address + " was not accepted: " + orgAddresses, e);
                    // #VERA-382: der String mit "ADDRESS_SYNTAX_NOT_CORRECT:" wird in veraweb-core/mailinglistWrite.vm
                    // zum parsen der Fehlerhaften E-Mail Adressen verwendet. Bei Änderungen also auch anpassen.
                    sb.append("ADDRESS_SYNTAX_NOT_CORRECT:").append(tt(address)).
                      append(" of ").append(tt(orgAddresses)).append("\n\n");
                    ++badAddresses;
                }
            }
        }

        mailDispatcher.getTransport().close();

        logger.info(String.format("mailing list sent to %d recipients (%d bad skipped), " +
            "%d addresses (%d duplicates skipped, not sent to %d bad addresses)",
          members, badRecipients, mailboxen, duplicates, badAddresses));

        return new SendResult(badAddresses == 0, sb.toString());
    }

    private String getFrom(PersonMailinglist recipient) {
        return emailConfiguration.getFrom(recipient.getPerson().getFk_orgunit());
    }

    private String substitutePlaceholders(final String text, final Person person) {
        return new PlaceholderSubstitution(person).apply(text);
    }

    private void removeAttachmentsFromFilesystem(final Map<String, File> files) {
        for (final File file : files.values()) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (final IOException e) {
                logger.error(new StringBuilder("The file ").append(file.toPath()).append("could not be deleted!"), e);
            }
        }
    }

    private Map<String, File> getFiles(final List<FormDataBodyPart> fields) {
        final Map<String, File> files = new HashMap<>();
        if (fields != null) {
            for (final FormDataBodyPart part : fields) {
                if (part.getFormDataContentDisposition().getFileName() == null) {
                    continue;
                }
                try {
                    final File destinationFile = saveTempFile(part);
                    files.put(part.getFormDataContentDisposition().getFileName(), destinationFile);
                } catch (final IOException e) {
                    logger.error("Could not write data to temp file!", e);
                    break;
                }
            }
        }
        return files;
    }

    public void setTmpPath(final String tmpPath) {
        this.tmpPath = tmpPath;
    }

    public void setMailDispatcher(MailDispatcher mailDispatcher) {
        this.mailDispatcher = mailDispatcher;
    }

    public void setEmailConfiguration(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }
}
