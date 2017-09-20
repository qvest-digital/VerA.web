package org.evolvis.veraweb.onlinereg.entities;

/*-
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
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jnunez
 * @author Sven Schumann, s.schumann@tarent.de
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_fields_delegation_content")
@NamedQueries({
    @NamedQuery(name = Delegation.QUERY_FIND_BY_GUEST,
		query = "select d from Delegation d where fk_guest=:" + Delegation.PARAM_GUEST_ID + " and fk_delegation_field=:" + Delegation.PARAM_FIELD_ID),
	@NamedQuery(name = Delegation.DELETE_OPTIONAL_FIELDS,
		query = "delete from Delegation d where fk_guest=:" + Delegation.PARAM_GUEST_ID + " and fk_delegation_field=:" + Delegation.PARAM_FIELD_ID)

})
public class Delegation {
	
	public static final String QUERY_FIND_BY_GUEST = "Delegation.findByGuestId";
	public static final String DELETE_OPTIONAL_FIELDS = "Delegation.deleteOptionalFieldsByGuestId";
	public static final String PARAM_GUEST_ID = "guestId";
	public static final String PARAM_FIELD_ID = "fieldId";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	private Integer fk_guest;
	private Integer fk_delegation_field;
	private String value;

	public Delegation() {}
	
	public Delegation(Integer fk_guest, Integer fk_delegation_field, String value) {
	    super();
	    this.fk_guest = fk_guest;
	    this.fk_delegation_field = fk_delegation_field;
	    this.value = value;
    }

	public int getPk() {
		return pk;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}

	public Integer getFk_guest() {
		return fk_guest;
	}

	public void setFk_guest(Integer fk_guest) {
		this.fk_guest = fk_guest;
	}

	public Integer getFk_delegation_field() {
		return fk_delegation_field;
	}

	public void setFk_delegation_field(Integer fk_delegation_field) {
		this.fk_delegation_field = fk_delegation_field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
