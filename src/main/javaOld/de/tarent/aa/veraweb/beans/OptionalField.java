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
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Dieses Bean bildet einen Eintrag der Tabelle <em>veraweb.toptional_fields</em> ab.
 *
 * @author Max Marche, m.marche@tarent.de
 */
public class OptionalField extends AbstractHistoryBean {
	private int id;
	private String label;
	private int fkEvent;
	/* references to OptionalFieldType */
	private Integer fkType;
	private String content;

	public OptionalField() {
		this.label = "";
		this.fkEvent = -1;
	}

	public OptionalField(ResultSet resultSet) throws SQLException {
		this.id = resultSet.getInt("pk");
		this.label = resultSet.getString("label");
		this.fkEvent = resultSet.getInt("fk_event");
		this.fkType = resultSet.getInt("fk_type");
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

	public Integer getFkType() {
		return fkType;
	}

	public void setFkType(Integer fkType) {
		this.fkType = fkType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
