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
 * <p>
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.torgunit dar,
 * also einen Mandanten.
 * </p>
 * 
 * @author christoph
 * @author mikel
 */
public class OrgUnit extends AbstractBean {
    /** Prim�rschl�ssel */
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
    @Override
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
    @Override
    public void checkWrite(OctopusContext cntx) throws BeanException
    {
    	checkGroups( cntx, new String[] { PersonalConfigAA.GROUP_ADMIN, PersonalConfigAA.GROUP_SYSTEM_USER } );
    }
}
