/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2008 tarent GmbH
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
 * $Id: ChangeLogEntry.java,v 1.2 2008/02/12 12:00:00 cklein Exp $
 * 
 * Created on 12.02.2008
 */
package de.tarent.octopus.custom.beans;

import java.sql.Date;

import de.tarent.aa.veraweb.beans.AbstractBean;

/**
 * The bean class ChangeLogEntry represents an EntityBean
 * used for storage and retrieval of change log entries
 * from and to a database, respectively.
 * 
 * @author cklein
 * @since 1.2
 */
public class ChangeLogEntry extends AbstractBean
{
	public Integer	id;
	public String	username;
	public String	otype;
	public Integer	oid;
	public String	action;
	public String	attributes;
	public Date		date;

	/**
	 * Creates a new instance of this.
	 */
	public ChangeLogEntry()
	{
		super();
	}
}
