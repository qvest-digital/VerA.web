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
package de.tarent.aa.veraweb.worker;

import java.util.List;

import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Worker der eine Autovervollst�ndigung verschiedener Stammdaten zur
 * Verf�gung stellt.
 *  
 * @author Christoph
 */
public class CompleteWorker {
	private static final Limit LIMIT = new Limit(new Integer(10), new Integer(0));
	private static final String COLUMN = "entry";
	private static final String QUERY = "query";
	private static final String RESULT = "list";

	/**
	 * @param cntx
	 * @param table
	 * @param column
	 * @param query
	 * @return Liste mit den 10 ersten Eintr�gen.
	 * @throws BeanException
	 */
	private List getList(OctopusContext cntx, String table, String column, String query) throws BeanException {
		cntx.setContent(QUERY, query);
		Database database = new DatabaseVeraWeb(cntx);
		return database.getList(SQL.Select( database ).
				from(table).
				selectAs(column, COLUMN).
				where(Expr.like(column, query + '%')).
				orderBy(Order.asc(column)).
				Limit(LIMIT), database);
	}

	/** Octopus-Eingabeparameter der Aktion {@link #completeLocation(OctopusContext, String)} */
	public static final String INPUT_completeLocation[] = { QUERY };
	/** Octopus-Ausgabeparameter der Aktion {@link #completeLocation(OctopusContext, String)} */
	public static final String OUTPUT_completeLocation = RESULT;
	/**
	 * @param cntx Octopus-Context
	 * @param query Aktuelle Benutzereingabe
	 * @return Liste mit �hnlichen Locations.
	 * @throws BeanException 
	 */
	public List completeLocation(OctopusContext cntx, String query) throws BeanException {
		return getList(cntx, "veraweb.tlocation", "locationname", query);
	}
}
