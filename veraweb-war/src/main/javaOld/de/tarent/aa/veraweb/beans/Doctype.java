/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Repr�sentiert ein Datenbank-Tupel aus der Tabelle <code>tdoctype</code>,
 * ein Dokumenttyp.
 * 
 * @author Christoph, Mikel
 */
public class Doctype extends AbstractBean {
    /** {@link #flags}-Attributwert: kein Freitext */
	public static final int FLAG_NO_FREITEXT = 50;
    /** {@link #flags}-Attributwert: Standardtyp */
	public static final int FLAG_IS_STANDARD = 99;

    /** pk serial NOT NULL: Prim�rschl�ssel */
	public Integer id;
    /** docname varchar(200) NOT NULL: Name des Dokumenttyps */
	public String name;
    /** addresstype int4 NOT NULL DEFAULT 1: anzuwendender Adresstyp */
	public Integer addresstype;
    /** locale int4 NOT NULL DEFAULT 0: anzuwendende Locale (Zeichensatz) */
	public Integer locale;
    /** partner int4 NOT NULL DEFAULT 0: Hauptperson und Partner getrennt oder zusammen */
	public Boolean partner;
    /** sortorder int4: Sortierung */
	public Integer sortorder;
    /** flags int4: Flags, vergleiche <code>FLAG_*</code> */
	public Integer flags;
    /** host int4 NOT NULL DEFAULT 1: Dokument auch f�r Gastgeber erzeugen */
	public Boolean host;
    /** isdefault int4 NOT NULL DEFAULT 0: Standard-Dokumenttyp */
	public Boolean isdefault;
	/**
     * format varchar(20) NOT NULL:
	 * Gibt das Export-Format dieses Dokumententypes an.
	 * Vgl. de.tarent.aa.veraweb.export.SpreadSheetHelper#getSpreadSheet(String)
	 */
	public String format;

    /**
     * �berpr�ft das Bean auf innere Vollst�ndigkeit.
     * Hier wird getestet, ob die Kategorie einen nicht-leeren Namen hat.
     * 
     * @throws BeanException bei Unvollst�ndigkeit
     */
	@Override
    public void verify() throws BeanException {
		if (name == null || name.length() == 0)
			addError("Sie müssen einen Namen eingeben.");
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
     * Test ist, ob der Benutzer Teil-Admin ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_PARTIAL_ADMIN);
    }
}
