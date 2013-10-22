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
import de.tarent.octopus.beans.MapBean;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Diese Klasse stellt eine abstrakte Basis f�r Beans auf Basis der
 * {@link de.tarent.octopus.beans.MapBean} dar.
 * 
 * @author christoph
 */
public abstract class AbstractBean extends MapBean {
	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 */
	public void checkRead(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_ADMIN);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 */
	public void checkWrite(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_ADMIN);
	}

    /**
     * Diese Methode leert beschr�nkte Felder.<br>
     * Achtung: Bei Benutzern, die diese Bean auch schreiben d�rfen
     * (siehe {@link #checkWrite(OctopusContext)}), sollte die Bean hier nicht
     * ver�ndert werden. 
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     */
    public void clearRestrictedFields(OctopusContext cntx) throws BeanException {
    }
    
	/**
	 * Diese Methode testet, ob im aktuellen Kontext der User der �bergebenen
	 * Gruppe zugeordenet ist.
	 * 
	 * @param cntx
	 * @param group
	 * @throws BeanException
	 */
	protected void checkGroup(OctopusContext cntx, String group) throws BeanException {
		PersonalConfig personalConfig = cntx != null ? cntx.personalConfig() : null;
		if (personalConfig == null)
			throw new BeanException("No personal config");
		if (!personalConfig.isUserInGroup(group))
			throw new BeanException("Only group " + group + " may write " + getClass().getName());
	}

	/**
	 * Checks whether the user is a member of either of the specified groups.
	 * 
	 * @param cntx
	 * @param groups
	 * 
	 * @throws BeanException
	 */
	protected void checkGroups(OctopusContext cntx, String... groups) throws BeanException
	{
		PersonalConfig personalConfig = ( cntx != null ) ? cntx.personalConfig() : null;
		if ( personalConfig == null )
		{
			throw new BeanException( "No personal config" );
		}
		Boolean found = Boolean.FALSE;
		for ( String group : groups )
		{
			found = personalConfig.isUserInGroup( group );
			if ( found )
			{
				break;
			}
		}
		if ( ! found )
		{
			throw new BeanException( "Only groups " + groups.toString() + " may write " + getClass().getName() );
		}
	}
}
