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
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tcategorie, eine
 * Kategorie, dar.
 * 
 * @author christoph
 * @author mikel
 */
public class Categorie extends AbstractBean implements OrgUnitDependent {
    /** {@link #flags}-Eintrag: normale Kategorie */
	static public final int FLAG_DEFAULT = 0;
    /** {@link #flags}-Eintrag: Ereignis */
	static public final int FLAG_EVENT = 1;
    /** {@link #flags}-Eintrag: diplomatisches Korp */
	static public final int FLAG_DIPLO_CORPS = 99;

    /** pk serial NOT NULL: Prim�rschl�ssel */
	public Integer id;
    /** fk_orgunit int4 DEFAULT 0: Mandant */
	public Integer orgunit;
    /** fk_event int4 DEFAULT 0: Veranstaltung */
	public Integer event;
    /** catname varchar(200) NOT NULL: Name der Kategorie */
	public String name;
    /** flags int4 DEFAULT 0: Flags, vergleiche <code>FLAG_*</code> */
	public Integer flags;
    /** ank int4 DEFAULT 0: Kategorienrang */
	public Integer rank;

    /**
     * �berpr�ft das Bean auf innere Vollst�ndigkeit.
     * Hier wird getestet, ob die Kategorie einen nicht-leeren Namen hat.
     * 
     * @throws BeanException bei Unvollst�ndigkeit
     */
	@Override
    public void verify() throws BeanException {
		if (name == null || name.length() == 0)
			addError("Sie müssen eine Bezeichnung eingeben.");
	}

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Writer ist. F�r das automatische Generieren von
     * Kategorien beim Import ist dies notwendig.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }
}
