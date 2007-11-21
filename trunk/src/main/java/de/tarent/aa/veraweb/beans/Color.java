/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: Color.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tcolor, eine
 * Farbe, dar.
 * 
 * @author christoph
 * @author mikel
 */
public class Color extends AbstractBean {
    /** pk serial NOT NULL: Prim�rschl�ssel */
	public Integer id;
    /** color varchar(100) NOT NULL: Name der Farbe */
	public String name;
    /** rgb int4 NOT NULL: RGB-Wert der Farbe */
	public Integer rgb;
    /** addresstype int4 NOT NULL DEFAULT 0: Adresstyp, f�r den die Farbe genutzt werden soll */
	public Integer addresstype;
    /** locale int4 NOT NULL DEFAULT 0: Locale, f�r die die Farbe genutzt werden soll */
	public Integer locale;

    /**
     * Diese Methode setzt das {@link #rgb}-Attribut aus einer String-Darstellung
     * einer oder dreier Zahlen, was als kombinierter oder getrennter RGB-Wert
     * interpretiert wird. 
     * 
     * @param s String-Darstellung des RGB-Werts
     */
	public void setRgb(String s) {
		try {
			int r = s.indexOf(' ');
			int g = s.indexOf(' ', r + 1);
			int b = 0;
			if (s.length() == 0) {
				rgb = new Integer(0);
				return;
			} else if (r == -1 || g == -1) {
				rgb = new Integer(s);
				return;
			} else {
				b = Integer.parseInt(s.substring(g + 1));
				g = Integer.parseInt(s.substring(r + 1, g));
				r = Integer.parseInt(s.substring(0, r));
				if (r < 0x00) r = 0x00; else if (r > 0xff) r = 0xff;
				if (g < 0x00) g = 0x00; else if (g > 0xff) g = 0xff;
				if (b < 0x00) b = 0x00; else if (b > 0xff) b = 0xff;
				rgb = new Integer(r * 0x10000 + g * 0x100 + b);
				return;
			}
		} catch (NumberFormatException e) {
		} catch (NullPointerException e) {
		}
		rgb = new Integer(0);
	}

    /**
     * �berpr�ft das Bean auf innere Vollst�ndigkeit.
     * Hier wird getestet, ob die Kategorie einen nicht-leeren Namen hat.
     * 
     * @throws BeanException bei Unvollst�ndigkeit
     */
	public void verify() throws BeanException {
		if (name == null || name.length() == 0)
			addError("Sie m&uuml;ssen einen Namen eingeben.");
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
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }
}
