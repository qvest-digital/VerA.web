package org.evolvis.veraweb.onlinereg.mail;

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
                              String subectResetPassword,
                              String contentResetPassword) {

        this.host = host;
        this.port = port;
        this.security = security;
        this.username = username;
        this.password = password;
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.subjectResetPassword = subectResetPassword;
        this.contentResetPassword = contentResetPassword;
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
            this.content = propertiesReader.getProperty("mail.content." + currentLanguageKey);
            this.contentResetPassword = propertiesReader.getProperty("mail.content_reset_password." + currentLanguageKey);
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
