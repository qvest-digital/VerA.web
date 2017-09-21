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
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class UserConfig extends AbstractBean {
	/** ID */
	public Integer id;
	/** FK auf User */
	public Integer user;
	/** Key */
	public String key;
	/** Value */
	public String value;

	/**
	 * Hebt den Leseschutz für die User-Config auf.
	 */
	@Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
	}

	/**
	 * Hebt den Schreibschutz für die User-Config auf.
	 */
	@Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
	}
}
