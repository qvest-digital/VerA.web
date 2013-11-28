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
package de.tarent.aa.veraweb.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.tarent.dblayer.sql.Format;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;

/**
 * Datenbank-Hilfsklasse, erstellt u.a. Where-Bedingungen.
 * 
 * @author Christoph Jerolimov
 */
public class DatabaseHelper {
	/** Datenbankseitiges GROSS schreiben: MethodenName( */
	public static final String UPPER_PRE = "veraweb.upper_fix(";
	/** Datenbankseitiges GROSS schreiben: ) */
	public static final String UPPER_POST = ")";
	/** Datenbankseitiges klein schreiben: MethodenName( */
	public static final String LOWER_PRE = "veraweb.upper_fix(";
	/** Datenbankseitiges klein schreiben: ) */
	public static final String LOWER_POST = ")";

	/**
	 * Gibt eine Where-Clause zurück, die den übergebenen Suchbegriff
	 * (<code>search</code>) in allen übergebenen Spalten (<code>column</code>)
	 * sucht. Wenn im übergebenem Suchbegriff ein * oder ? vorkommt wird
	 * ein entsprechendes SQL LIKE mit % und _ verwendet, sofern die Zeichen nicht
	 * mit einem \ escaped wurden. Mehere Spalten werden mit ORs verknüpft.
	 * 
	 * @param search Suchbegriff
	 * @param column Liste mit Spaltennamen
	 * @return Where-Clause
	 */
	public static Clause getWhere(String search, String column[]) {
		WhereList list = new WhereList();
		if (search.indexOf('*') == -1 && search.indexOf('?') == -1) {
			for (int i = 0; i < column.length; i++)
				list.addOr(Expr.equal(
						UPPER_PRE + column[i] + UPPER_POST, new RawClause(
						UPPER_PRE + Format.format(search) + UPPER_POST)));
		} else {
			search = search.replaceAll("[*]", "%").replaceAll("[?]", "_");
			search = search.replaceAll("\\\\%", "*").replaceAll("\\\\_", "?");
			for (int i = 0; i < column.length; i++)
				list.addOr(Expr.like(
						UPPER_PRE + column[i] + UPPER_POST, new RawClause(
						UPPER_PRE + Format.format(search) + UPPER_POST)));
		}
		return list;
	}

	/**
	 * Gibt eine Order-Clause zur�ck, schaut jeweils nach der Spalte ob
	 * der Wert ASC oder DESC ist und wendet dieses Attribut entsprechend an.
	 * 
	 * @param list
	 * @return Order-Clause
	 */
	public static Order getOrder(List list) {
		if (list != null && list.size() > 0) {
			int pos = 0;
			Order order = null;
			if (list.size() > 1) {
				if ("DESC".equalsIgnoreCase((String)list.get(1))) {
					order = Order.desc((String)list.get(0));
					pos += 2;
				} else if ("ASC".equalsIgnoreCase((String)list.get(1))) {
					order = Order.asc((String)list.get(0));
					pos += 2;
				} else {
					order = Order.asc((String)list.get(0));
					pos += 1;
				}
			} else {
				order = Order.asc((String)list.get(0));
				pos++;
			}
			while (pos < list.size()) {
				if (pos < list.size() - 1) {
					if ("DESC".equalsIgnoreCase((String)list.get(pos + 1))) {
						order = order.andDesc((String)list.get(pos));
						pos += 2;
					} else if ("ASC".equalsIgnoreCase((String)list.get(pos + 1))) {
						order = order.andAsc((String)list.get(pos));
						pos += 2;
					} else {
						order = order.andAsc((String)list.get(pos));
						pos += 1;
					}
				} else {
					order = order.andAsc((String)list.get(pos));
					pos++;
				}
			}
			return order;
		} else {
			return null;
		}
	}

	public static String listsToIdListString(List[] lists)
	{
		StringBuffer result = new StringBuffer();
		Set<Integer> coalesced = new HashSet<Integer>();

		/* coalesce all of main, partner and reserve into a single identity set
		   this fixes an issue where the user can select either partner or reserve
		   but not the main contact, which would result in the person not being
		   invited.
		 */
		for ( int i = 0; i < lists.length; i++ )
		{
			coalesced.addAll(lists[i]);
		}

		Iterator< Integer > i = coalesced.iterator();
		while ( i.hasNext() )
		{
			result.append( i.next() );
			result.append( ',' );
		}

		if ( result.length() > 0 )
		{
			result.setLength( result.length() - 1 );
			return result.toString();
		}

		return "NULL";
	}
}
