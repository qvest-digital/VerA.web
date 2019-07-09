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
    private VworPropertiesReader vworPropertiesReader;

    public EmailConfiguration() {
        readProperties();
    }

    public EmailConfiguration(String host, Integer port, String security,
      String username, String password, String from) {
        this.host = host;
        this.port = port;
        this.security = security;
        this.username = username;
        this.password = password;
        this.from = from;
    }

    public EmailConfiguration(VworPropertiesReader propertiesReader) {
        vworPropertiesReader = propertiesReader;
        readProperties();
    }

    private void readProperties() {
        final VworPropertiesReader propertiesReader = getVworPropertiesReader();
        if (this.host == null) {
            this.host = propertiesReader.getProperty("mail.smtp.host");
            final String smtpPort = propertiesReader.getProperty("mail.smtp.port");
            if (smtpPort == null) {
                this.port = 25;
            } else {
                this.port = Integer.valueOf(smtpPort);
            }
            this.security = propertiesReader.getProperty("mail.smtp.security");
            this.username = propertiesReader.getProperty("mail.smtp.user");
            this.password = propertiesReader.getProperty("mail.smtp.password");
            this.from = propertiesReader.getProperty("mail.smtp.from");
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

    public VworPropertiesReader getVworPropertiesReader() {
        if (vworPropertiesReader == null) {
            vworPropertiesReader = new VworPropertiesReader();
        }
        return vworPropertiesReader;
    }

    public String getFrom(int fk_orgunit) {
        final String fromProperty = getVworPropertiesReader().getProperty("mail.smtp.from." + fk_orgunit);
        if (fromProperty != null) {
            return fromProperty;
        }
        return getFrom();
    }
}
