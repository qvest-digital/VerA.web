/*
 * $Id: Import.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 15.09.2005
 */
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.timport dar,
 * einen Importvorgang.
 * 
 * @author mikel
 */
public class Import extends AbstractHistoryBean {
    /** ID: pk serial NOT NULL */
    public Integer id;
    /** ID der Mandanten-Einheit: fk_orgunit int4 */
    public Integer orgunit;
    /** Erstellt am: created timestamptz */
    public Timestamp created;
    /** Erstellt von: createdby varchar(100) */
    public String createdby;
    /** Geändert am: changed timestamptz */
    public Timestamp changed;
    /** Geändert von: changedby varchar(100) */
    public String changedby;
    /** Datenherkunft: importsource varchar(250) */
    public String importsource;
    /** Importformatbezeichner: importformat varchar(250) */
    public String importformat;

    //
    // Oberklasse AbstractBean
    //
    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer allgemeine Schreibrechte hat.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer allgemeine Schreibrechte hat.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }
}
