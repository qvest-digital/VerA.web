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
package org.evolvis.veraweb.onlinereg.entities;

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
 * Created by Max Marche <m.marche@tarent.de> on 22.12.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tperson_doctype")
@NamedQueries(value = {
        @NamedQuery(
        		name = "PersonDoctype.findByDoctypeIdAndPersonId", 
        		query = "SELECT pd FROM PersonDoctype pd WHERE fk_person = :fk_person AND fk_doctype = :fk_doctype")
})
public class PersonDoctype {

	/**
	 * Empty constructor MUST
	 */
	public PersonDoctype() {
	}

	/**
	 * Constructor with all required fields
	 * 
	 * @param fk_person
	 * @param fk_doctype
	 * @param addresstype
	 * @param locale
	 */
	public PersonDoctype(int fk_person, int fk_doctype, int addresstype,
			int locale) {
		this.fk_person = fk_person;
		this.fk_doctype = fk_doctype;
		this.addresstype = addresstype;
		this.locale = locale;

		this.textfield = "";
		this.textfield_p = "";
		this.textjoin = "";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	private int fk_person;
	private int fk_doctype;
	private int addresstype;
	private int locale;

	private String textfield;
	private String textfield_p;
	private String textjoin;

	public int getFk_person() {
		return fk_person;
	}

	public int getPk() {
		return pk;
	}
	
	public int getFk_doctype() {
		return fk_doctype;
	}

	public int getAddresstype() {
		return addresstype;
	}

	public int getLocale() {
		return locale;
	}

	public void setTextfield(String textfield) {
		this.textfield = textfield;
	}
}
