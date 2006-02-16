/*
 * $Id: OctopusFactory.java,v 1.3 2006/02/16 10:31:27 asteban Exp $
 * 
 * Created on 14.06.2005
 */
package de.tarent.octopus.jndi;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import de.tarent.octopus.server.OctopusContext;

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
 * 
 * @author mikel
 */
public class OctopusFactory implements ObjectFactory, OctopusReferences, Serializable {

    /** Serialisierungs-ID */
    private static final long serialVersionUID = 3258132436037547832L;

    private static Logger logger = Logger.getLogger(OctopusFactory.class.getName());

    //
    // statische Methoden
    //
    public static void tryToBind() {
        Context initCtx = null;
        Context envCtx = null;
        try {
            initCtx = new InitialContext();
            envCtx = (Context) initCtx.lookup("java:comp/env");
            
            // Wenn schon eine OctopusFactory Instanz eingebunden wurde,
            // muss dies nicht noch einmal geschehen.
        	if ( envCtx.lookup("octopus/References") instanceof OctopusFactory )
        		return;
        	
        } catch (NamingException ne) {
        	logger.log(Level.INFO, "Keine OctopusFactory Instanz im JNDI-Baum vorgefunden.");
        	
        	// Exception wird erwartet, wenn JNDI Struktur OctopusFactory
        	// Instanz nicht enthält.
        }
        
        assert ( initCtx != null );
        assert ( envCtx != null );
        
        Object instanceToBind = new OctopusFactory();
        
        try {
//            logger.info("JNDI Environment: " + envCtx.getEnvironment());
//            envCtx.addToEnvironment(Context.OBJECT_FACTORIES, OctopusFactory.class.getName());
//            logger.info("JNDI Environment: " + envCtx.getEnvironment());
            envCtx.bind("octopus/References", instanceToBind);
        } catch (NamingException e) {
            logger.log(Level.INFO, "JNDI Kontext steht nur lesend zur Verfügung. Die OctopusReferences-Instanz konnte nicht gebunden werden.");
        }
    }
    
    //
    // Schnittstelle OctopusReferences
    //
    /**
     * Diese Methode liefert den aktuellen {@link OctopusContext}. Dieser wird
     * Thread-spezifisch in {@link #octopusContext} gehalten.
     * 
     * @see OctopusReferences#getContext()
     * @return aktueller Octopus-Kontext
     */
    public OctopusContext getContext() {
        return de.tarent.octopus.server.Context.getActive();
    }
    
    //
    // Schnittstelle ObjectFactory
    //
    /**
     * Diese Methode liefert diese Klasse selbst als {@link OctopusReferences}-Instanz.<br>
     * TODO: Die Implementierung ist relevant für octopus/References. Erweitern!
     * 
     * @see javax.naming.spi.ObjectFactory#getObjectInstance(java.lang.Object, javax.naming.Name, javax.naming.Context, java.util.Hashtable)
     */
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
        return this;
    }
}
