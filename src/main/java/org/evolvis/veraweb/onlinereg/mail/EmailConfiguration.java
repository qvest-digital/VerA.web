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
    private VworPropertiesReader vworPropertiesReader;

    public EmailConfiguration() {
        if (vworPropertiesReader == null) {
            vworPropertiesReader = new VworPropertiesReader();
        }
        this.host = vworPropertiesReader.getProperty("mail.smtp.host");
        if (vworPropertiesReader.getProperty("mail.smtp.port") == null) {
            this.port = 25;
        } else {
            this.port = new Integer(vworPropertiesReader.getProperty("mail.smtp.port"));
        }
        this.security = vworPropertiesReader.getProperty("mail.smtp.security");
        this.username = vworPropertiesReader.getProperty("mail.smtp.user");
        this.password = vworPropertiesReader.getProperty("mail.smtp.password");
        this.from = vworPropertiesReader.getProperty("mail.smtp.from");
        this.subject = vworPropertiesReader.getProperty("mail.subject");
        this.content = vworPropertiesReader.getProperty("mail.content");
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public VworPropertiesReader getVworPropertiesReader() {
        return vworPropertiesReader;
    }

    public void setVworPropertiesReader(VworPropertiesReader vworPropertiesReader) {
        this.vworPropertiesReader = vworPropertiesReader;
    }
}
