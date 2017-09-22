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

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFrom(int fk_orgunit) {
        final String fromProperty = getVworPropertiesReader().getProperty("mail.smtp.from." + fk_orgunit);
        if (fromProperty != null) {
            return fromProperty;
        }
        return getFrom();
    }
}
