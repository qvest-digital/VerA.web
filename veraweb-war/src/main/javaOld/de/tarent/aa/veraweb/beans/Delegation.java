package de.tarent.aa.veraweb.beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Delegation {
	private String value;
	private int fkGuest;
	private int fkDelegationField;
    private String label;


    public Delegation() {
		this.value = "";
	}

	public Delegation(ResultSet resultSet) throws SQLException {
		this.value = resultSet.getString("value");
		this.fkGuest = resultSet.getInt("fk_guest");
		this.fkDelegationField = resultSet.getInt("fk_delegation_field");
        this.label = resultSet.getString("label");
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
