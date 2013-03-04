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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.Color;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * <p>
 * Octopus-Worker der Aktionen zum {@link #showList(OctopusContext) laden}
 * und {@link #saveList(OctopusContext) speichern} von {@link Color Farben}
 * bereitstellt.
 * </p>
 * <p>
 * Es werden nur vier Farben gespeichert, die entsprechend des Flags
 * Inland und Geschlecht der jeweiligen Person gebildet und zugeordent werden.
 * Die Datenbank IDs sind wie folgt zugeordent:
 * </p>
 * <ul>
 * <li>ID 1 = Inland: Ja - Geschlecht: Weiblich</li>
 * <li>ID 2 = Inland: Ja - Geschlecht: M�nnlich</li>
 * <li>ID 3 = Inland: Nein - Geschlecht: Weiblich</li>
 * <li>ID 4 = Inland: Nein - Geschlecht: M�nnlich</li>
 * </ul>
 * 
 * @author Christoph
 */
public class ColorWorker {
	/** Octopus-Eingabeparameter f�r die Aktion {@link #showList(OctopusContext)} */
	public static final String INPUT_showList[] = {};
	/**
	 * Holt eine Liste von Farben und stellt diese in den Content.
	 * 
	 * @param cntx Octopus-Context
	 * @throws IOException 
	 * @throws BeanException 
	 */
	public void showList(OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		cntx.setContent("color1",
				database.getBean("Color",
				database.getSelect("Color").
				where(Expr.equal("pk", new Integer(1)))));
		cntx.setContent("color2",
				database.getBean("Color",
				database.getSelect("Color").
				where(Expr.equal("pk", new Integer(2)))));
		cntx.setContent("color3",
				database.getBean("Color",
				database.getSelect("Color").
				where(Expr.equal("pk", new Integer(3)))));
		cntx.setContent("color4",
				database.getBean("Color",
				database.getSelect("Color").
				where(Expr.equal("pk", new Integer(4)))));
	}

	/** Octopus-Eingabeparameter f�r die Aktion {@link #saveList(OctopusContext)} */
	public static final String INPUT_saveList[] = {};
	/**
	 * Speichert eine Liste von Farben.
	 * 
	 * @param cntx Octopus-Context
	 * @throws IOException 
	 * @throws BeanException 
	 */
	public void saveList(OctopusContext cntx) throws BeanException, IOException {
		if (!cntx.personalConfig().isUserInGroup(PersonalConfigAA.GROUP_ADMIN))
			return;
		
		Request request = new RequestVeraWeb(cntx);
		Database database = new DatabaseVeraWeb(cntx);
		List errors = new ArrayList();
		
		Color color1 = (Color)request.getBean("Color", "color1");
		Color color2 = (Color)request.getBean("Color", "color2");
		Color color3 = (Color)request.getBean("Color", "color3");
		Color color4 = (Color)request.getBean("Color", "color4");
		
		removeColor(database);
		saveColor(database, new Integer(1), color1, errors);
		saveColor(database, new Integer(2), color2, errors);
		saveColor(database, new Integer(3), color3, errors);
		saveColor(database, new Integer(4), color4, errors);
		
		if (!errors.isEmpty()) {
			cntx.setContent("errors", errors);
		}
	}

	protected void removeColor(Database database) throws BeanException {
		Delete delete = SQL.Delete( database ).from("veraweb.tcolor");
		delete.where(new RawClause("pk NOT IN (1, 2, 3, 4)"));
		database.execute(delete);
	}

	protected void saveColor(Database database, Integer id, Color color, List errors) throws BeanException, IOException {
		if (color.id == null) {
			if (color.isCorrect()) {
				database.execute(database.getInsert(color).insert("pk", id));
			}
		} else {
			if (color.isCorrect()) {
				database.saveBean(color);
			} else {
				database.execute(database.getDelete(color));
				errors.addAll(color.getErrors());
			}
		}
	}

	/**
	 * Gibt die ID des entsprechenden Farbwertes zur�ck.
	 * 
	 * @param domestic (default ja)
	 * @param sex (default m�nnlich)
	 * @return 1 - 4
	 */
	public static Integer getColor(String domestic, String sex) {
		if (PersonConstants.DOMESTIC_AUSLAND.equals(domestic))
			return new Integer(PersonConstants.SEX_FEMALE.equals(sex) ? 3 : 4);
		else
			return new Integer(PersonConstants.SEX_FEMALE.equals(sex) ? 1 : 2);
	}
}
