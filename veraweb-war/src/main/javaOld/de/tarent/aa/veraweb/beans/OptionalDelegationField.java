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

import java.sql.ResultSet;
import java.sql.SQLException;

public class OptionalDelegationField {
	private String value;
	private int fkGuest;
	private int fkDelegationField;
    private String label;

	public OptionalDelegationField(ResultSet resultSet) throws SQLException {
		this.value = resultSet.getString("value");
		this.fkGuest = resultSet.getInt("fk_guest");
		this.fkDelegationField = resultSet.getInt("fk_delegation_field");
        this.label = resultSet.getString("label");
	}

	public OptionalDelegationField() {
		this.value = "";
		this.label = "";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getFkGuest() {
		return fkGuest;
	}

	public void setFk_guest(int fkGuest) {
		this.fkGuest = fkGuest;
	}

	public int getFkDelegationnField() {
		return fkDelegationField;
	}

	public void setFkDelegationField(int fkDelegationField) {
		this.fkDelegationField = fkDelegationField;
	}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
