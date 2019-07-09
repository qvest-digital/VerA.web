package de.tarent.octopus.jndi;
import lombok.extern.log4j.Log4j2;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

/**
 * Diese Klasse stellt eine JNDI-{@link ObjectFactory} zum Zugriff auf den aktuellen
 * Octopus-Kontext dar.<br>
 * Sie ist angelehnt an das Beispiel für das erzeugen spezialisierter Resource Factories
 * <a href="http://jakarta.apache.org/tomcat/tomcat-5.0-doc/jndi-resources-howto.html#Adding Custom Resource Factories">hier</a>
 * in der Tomcat-Dokumentation.<br>
 * Zur Nutzung muss diese Factory der JNDI-Machinerie bekannt gemacht werden. Analog
 * obigem Beispiel ist dies im Projekt octopus/webapp getan worden, entsprechende
 * Einträge stehen dort in den Dateien <code>octopus.xml</code> und <code>web.xml</code>
 * im Verzeichnis <code>webapp/WEB-INF</code>.
 */
@Log4j2
public abstract class AbstractJndiFactory implements ObjectFactory {
    /**
     * Currently only support the apache tomcat (and maby more servlet container?)
     * as JNDI context provider. See
     * <code>http://tomcat.apache.org/tomcat-5.5-doc/jndi-resources-howto.html</code>
     * for more informations about this configuration.
     *
     * @return naming context
     */
    protected Context getContext() {
        try {
            return (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            logger.info("No JNDI context available. Can not bind context '" + getLookupPath() + "'.", e);
            return null;
        }
    }

    public boolean bind() {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        try {
            context.addToEnvironment(Context.OBJECT_FACTORIES, getClass().getName());
        } catch (NamingException e) {
            logger.warn(
              "Can not add current class '" + getClass().getName() + "' " +
                "to the object factory list.");
        }

        try {
            Object object = context.lookup(getLookupPath());
            if (object != null && object.getClass().getName().equals(getClass().getName())) {
                logger.info(
                  "JNDI context available. " +
                    "Context '" + getLookupPath() + "' already binded.");
                return true;
            } else if (object != null) {
                logger.info(
                  "JNDI context available. " +
                    "Wrong class '" + object.getClass().getName() + "' " +
                    "for context '" + getLookupPath() + "' binded, will rebind it now.");
            } else {
                logger.info(
                  "JNDI context available. " +
                    "Context '" + getLookupPath() + "' not binded yet, will do it now.");
            }
        } catch (NamingException e) {
            logger.info(
              "JNDI context available. " +
                "Exception '" + e.getLocalizedMessage() + "' while lookup " +
                "context '" + getLookupPath() + "' catched, will rebind it now.");
        }

        try {
            context.rebind(getLookupPath(), this);
            logger.info("JNDI context '" + getLookupPath() + "' successful binded.");
            return true;
        } catch (NamingException e) {
            logger.info("JNDI context available, but can not bind context '" + getLookupPath() + "'.");
            return false;
        }
    }

    public boolean unbind() {
        Context context = getContext();
        if (context == null) {
            return false;
        }

        try {
            context.unbind(getLookupPath());
            logger.info("JNDI context '" + getLookupPath() + "' successful unbinded.");
            return true;
        } catch (NamingException e) {
            logger.error("JNDI context available, but can not unbind context '" + getLookupPath() + "'.", e);
            return false;
        }
    }

    abstract protected String getLookupPath();

    public Object getObjectInstance(Object object, Name name, Context context, Hashtable environment) throws Exception {
        return this;
    }
}
