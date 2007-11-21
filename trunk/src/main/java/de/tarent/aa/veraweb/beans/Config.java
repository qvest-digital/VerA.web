/*
 * $Id: Config.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tconfig, einen
 * Konfigurationseintrag, dar.
 * 
 * @author christoph
 * @author mikel
 */
public class Config extends AbstractBean {
    /** pk serial NOT NULL: Primärschlüssel */
	public Integer id;
    /** cname varchar(100) NOT NULL: Schlüssel des Konfigurationseintrags */
	public String key;
    /** cvalue varchar(300) NOT NULL: Wert des Konfigurationseintrags */
	public String value;

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Der Test hier ist leer, jeder darf Konfigurationseinträge lesen.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
	public void checkRead(OctopusContext cntx) throws BeanException {
	}
}
