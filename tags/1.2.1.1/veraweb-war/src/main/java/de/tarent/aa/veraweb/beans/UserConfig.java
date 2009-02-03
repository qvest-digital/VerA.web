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
 * $Id: UserConfig.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 */
package de.tarent.aa.veraweb.beans;

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
	 * Hebt den Leseschutz f�r die User-Config auf.
	 */
	@Override
    public void checkRead(OctopusContext cntx) throws BeanException {
	}

	/**
	 * Hebt den Schreibschutz f�r die User-Config auf. 
	 */
	@Override
    public void checkWrite(OctopusContext cntx) throws BeanException {
	}
}
