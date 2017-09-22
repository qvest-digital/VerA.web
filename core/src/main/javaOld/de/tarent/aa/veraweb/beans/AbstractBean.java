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
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.MapBean;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Diese Klasse stellt eine abstrakte Basis für Beans auf Basis der
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
	 * @param octopusContext Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 */
	public void checkRead(OctopusContext octopusContext) throws BeanException {
		checkGroup(octopusContext, PersonalConfigAA.GROUP_ADMIN);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
	 *
	 * @param octopusContext Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 */
	public void checkWrite(OctopusContext octopusContext) throws BeanException {
		checkGroup(octopusContext, PersonalConfigAA.GROUP_ADMIN);
	}

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Achtung: Bei Benutzern, die diese Bean auch schreiben dürfen
     * (siehe {@link #checkWrite(OctopusContext)}), sollte die Bean hier nicht
     * verändert werden.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     */
    public void clearRestrictedFields(OctopusContext octopusContext) throws BeanException {
    }

	/**
	 * Diese Methode testet, ob im aktuellen Kontext der User der übergebenen
	 * Gruppe zugeordenet ist.
	 *
	 * @param octopusContext FIXME
	 * @param group FIXME
	 * @throws BeanException FIXME
	 */
	protected void checkGroup(OctopusContext octopusContext, String group) throws BeanException {
		PersonalConfig personalConfig = octopusContext != null ? octopusContext.personalConfig() : null;
		if (personalConfig == null)
			throw new BeanException("No personal config");
		if (!personalConfig.isUserInGroup(group))
			throw new BeanException("Only group " + group + " may write " + getClass().getName());
	}

	/**
	 * Checks whether the user is a member of either of the specified groups.
	 *
	 * @param octopusContext FIXME
	 * @param groups FIXME
	 *
	 * @throws BeanException FIXME
	 */
	protected void checkGroups(OctopusContext octopusContext, String... groups) throws BeanException
	{
		PersonalConfig personalConfig = ( octopusContext != null ) ? octopusContext.personalConfig() : null;
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
