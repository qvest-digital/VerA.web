/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Dieses Bean bildet einen Eintrag der Tabelle <em>veraweb.toptional_fields</em> ab.
 *
 * @author Max Marche <m.marche@tarent.de
 */
public class OptionalField {
	private int pk;
	private String label;
	private int fkEvent;

	public OptionalField() {
		this.label = "";
		this.fkEvent = -1;
	}

	public OptionalField(ResultSet resultSet) throws SQLException {
		this.pk = resultSet.getInt("pk");
		this.label = resultSet.getString("label");
		this.fkEvent = resultSet.getInt("fk_event");
	}


	public int getPk() {
		return pk;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getFkEvent() {
		return fkEvent;
	}

	public void setFkEvent(int fkEvent) {
		this.fkEvent = fkEvent;
	}
}
