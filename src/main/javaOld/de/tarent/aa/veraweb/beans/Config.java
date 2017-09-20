package de.tarent.aa.veraweb.beans;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
import de.tarent.octopus.beans.BeanException;
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
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
	@Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
	}
}
