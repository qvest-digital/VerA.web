/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.timportperson_doctype dar,
 * eine Dokumenttypfreitextzuordnung zu einer importierten Person.
 * 
 * @author mikel
 */
public class ImportPersonDoctype extends AbstractBean implements ImportPersonExtra {
    /** ID: pk serial NOT NULL */
    public Integer id;
    /** Import-Person: fk_importperson int4 NOT NULL */
    public Integer importperson;
    /** Dokumenttypname: docname varchar(200) NOT NULL */
    public String name;
    /** Freitext Hauptperson: textfield text */
    public String textfield;
    /** Freitext Partner: textfield_p text */
    public String textfieldPartner;
    /** Freitext Verbinder */
    public String textfieldJoin;

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
    @Override
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
    @Override
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
