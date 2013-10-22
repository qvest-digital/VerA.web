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

import java.sql.Date;
import java.util.Calendar;

/**
 * TODO refactor existing change log bean management to this class
 * 
 * The bean class ChangeLogReport represents a 
 * session bean that models the configuration
 * for a change log report.
 *
 * As of now, the change log reports can be
 * filtered by their begin and end date, with
 * their respective defaults being set to
 * begin ::= 1st January of the current year
 * and end ::= NOW, respectively.
 * 
 * @author cklein
 * @since 1.2.0
 * @see de.tarent.aa.veraweb.worker.ChangeLogReportsWorker
 */
public class ChangeLogReport extends AbstractBean
{
	public Date begin;
	public Date end;

	/**
	 * Constructs a new instance of this.
	 */
	public ChangeLogReport()
	{
		this.begin = Date.valueOf( "01.01." + Calendar.getInstance().get( Calendar.YEAR ) );
		this.end = new Date( System.currentTimeMillis() );
	}
}
