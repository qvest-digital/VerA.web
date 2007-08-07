/*
 * $Id: OrgUnit.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 17.08.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * <p>
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.torgunit dar,
 * also einen Mandanten.
 * </p>
 * 
 * @author christoph
 * @author mikel
 */
public class OrgUnit extends AbstractBean {
    /** Primärschlüssel */
    public Integer id;
    /** Mandantenname */
    public String name;
    
    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist leer.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    public void checkRead(OctopusContext cntx) throws BeanException {
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Administrator ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_ADMIN);
    }
}
