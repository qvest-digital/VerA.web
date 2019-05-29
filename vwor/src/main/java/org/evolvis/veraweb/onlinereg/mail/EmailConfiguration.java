package org.evolvis.veraweb.onlinereg.mail;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import org.evolvis.veraweb.onlinereg.utils.VworPropertiesReader;

/**
 * @author Atanas Alexandrov, tarent solutions GmbH
 */
public class EmailConfiguration {
    private String host;
    private Integer port;
    private String security;
    private String username;
    private String password;
    private String from;
    private String subject;
    private String content;
    private String subjectResetPassword;
    private String contentResetPassword;
    private String subjectResendLogin;
    private String contentResendLogin;
    private String contentType;
    private VworPropertiesReader vworPropertiesReader;

    public EmailConfiguration(String currentLanguageKey) {
        readProperties(currentLanguageKey);
    }

    public EmailConfiguration(String host,
      Integer port,
      String security,
      String username,
      String password,
      String from,
      String subject,
      String content,
      String contentType,
      String subjectResetPassword,
      String contentResetPassword,
      String subjectResendLogin,
      String contentResendLogin) {

        this.host = host;
        this.port = port;
        this.security = security;
        this.username = username;
        this.password = password;
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.subjectResetPassword = subjectResetPassword;
        this.contentResetPassword = contentResetPassword;
        this.subjectResendLogin = subjectResendLogin;
        this.contentResendLogin = contentResendLogin;
        this.contentType = contentType;
    }

    public EmailConfiguration(String currentLanguageKey, VworPropertiesReader propertiesReader) {
        vworPropertiesReader = propertiesReader;
        readProperties(currentLanguageKey);
    }

    private void readProperties(String currentLanguageKey) {
        final VworPropertiesReader propertiesReader = getVworPropertiesReader();
        if (this.host == null) {
            this.host = propertiesReader.getProperty("mail.smtp.host");
            final String smtpPort = propertiesReader.getProperty("mail.smtp.port");
            if (smtpPort == null) {
                this.port = 25;
            } else {
                this.port = new Integer(smtpPort);
            }
            this.security = propertiesReader.getProperty("mail.smtp.security");
            this.username = propertiesReader.getProperty("mail.smtp.user");
            this.password = propertiesReader.getProperty("mail.smtp.password");
            this.from = propertiesReader.getProperty("mail.smtp.from");
            this.contentType = propertiesReader.getProperty("mail.content.type");
            this.subject = propertiesReader.getProperty("mail.subject." + currentLanguageKey);
            this.subjectResetPassword = propertiesReader.getProperty("mail.subject_reset_password." + currentLanguageKey);
            this.subjectResendLogin = propertiesReader.getProperty("mail.subject_resend_login." + currentLanguageKey);
            this.content = propertiesReader.getProperty("mail.content." + currentLanguageKey);
            this.contentResetPassword = propertiesReader.getProperty("mail.content_reset_password." + currentLanguageKey);
            this.contentResendLogin = propertiesReader.getProperty("mail.content_resend_login." + currentLanguageKey);
        }
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getSecurity() {
        return security;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getSubjectResetPassword() {
        return subjectResetPassword;
    }

    public String getContentResetPassword() {
        return contentResetPassword;
    }

    public String getSubjectResendLogin() {
        return subjectResendLogin;
    }

    public String getContentResendLogin() {
        return contentResendLogin;
    }

    public VworPropertiesReader getVworPropertiesReader() {
        if (vworPropertiesReader == null) {
            vworPropertiesReader = new VworPropertiesReader();
        }
        return vworPropertiesReader;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFrom(int fk_orgunit) {
        final String fromProperty = getVworPropertiesReader().getProperty("mail.smtp.from." + fk_orgunit);
        if (fromProperty != null) {
            return fromProperty;
        }
        return getFrom();
    }
}
