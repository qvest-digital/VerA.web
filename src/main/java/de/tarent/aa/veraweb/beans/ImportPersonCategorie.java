/*
 * $Id: ImportPersonCategorie.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 15.09.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.timportperson_categorie dar,
 * eine Kategoriezuordnung zu einer importierten Person.
 * 
 * @author mikel
 */
public class ImportPersonCategorie extends AbstractBean implements ImportPersonExtra {
    /** ID: pk serial NOT NULL */
    public Integer id;
    /** Import-Person: fk_importperson int4 NOT NULL */
    public Integer importperson;
    /** Kategoriename: catname varchar(200) NOT NULL */
    public String name;
    /** Rang: rank int4 DEFAULT 0 */
    public Integer rank;
    /** Flags: flags int4 DEFAULT 0 */
    public Integer flags;

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

    //
    // Schnittstelle ImportPersonExtra
    //
    /**
     * Diese Methode assoziiert diese Importpersonenerg�nzung mit ihrer Importperson;
     * dies bedeutet insbesondere die �bernahme deren ID als Fremdschl�ssel auf sie. 
     * 
     * @param person Importperson, mit der dieses Extra assoziiert werden soll.
     * @see de.tarent.aa.veraweb.beans.ImportPersonExtra#associateWith(de.tarent.aa.veraweb.beans.ImportPerson)
     */
    public void associateWith(ImportPerson person) {
        importperson = (person != null) ? person.id : null;
    }
}
