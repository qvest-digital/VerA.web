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
import java.util.List;

public class OptionalDelegationField extends AbstractHistoryBean {
	private int fkGuest;
	private int fkDelegationField;
    private String label;
	private Integer fkType;
	private String content;
	private List<OptionalFieldTypeContent> optionalFieldTypeContents;

	public OptionalDelegationField(ResultSet resultSet) throws SQLException {
		this.fkGuest = resultSet.getInt("fk_guest");
		this.fkDelegationField = resultSet.getInt("fk_delegation_field");
        this.label = resultSet.getString("label");
		this.fkType = resultSet.getInt("fk_type");
		this.content = resultSet.getString("value");
	}

	public OptionalDelegationField() {

	}

	public int getFkGuest() {
		return fkGuest;
	}

	public void setFk_guest(int fkGuest) {
		this.fkGuest = fkGuest;
	}

	public int getFkDelegationField() {
		return fkDelegationField;
	}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

	public Integer getFkType() {
		return fkType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFkDelegationField(int fkDelegationField) {
		this.fkDelegationField = fkDelegationField;
	}

	public void setFkType(Integer fkType) {
		this.fkType = fkType;
	}

	public void setOptionalFieldTypeContents(List<OptionalFieldTypeContent> optionalFieldTypeContents) {
		this.optionalFieldTypeContents = optionalFieldTypeContents;
	}

	public List<OptionalFieldTypeContent> getOptionalFieldTypeContents() {
		return optionalFieldTypeContents;
	}

	public boolean equals(OptionalDelegationField optionalDelegationField) {
		return this.getFkDelegationField() == optionalDelegationField.getFkDelegationField() && this.getFkType().intValue() == 3 && optionalDelegationField.getFkType().intValue() == 3;

	}
}
