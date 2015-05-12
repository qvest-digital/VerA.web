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
 * @author Atanas Alexandrov, tarent solutions GmbH
 * @author Stefan Weiz, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tperson_categorie")
@NamedQueries(value = {
        @NamedQuery(
        		name = "PersonCategorie.malGucken",
        		query = "SELECT pd FROM PersonCategorie pd WHERE fk_person = :fk_person AND fk_doctype = :fk_doctype")
})
public class PersonCategory {

	/**
	 * Empty constructor MUST
	 */
	public PersonCategory() {
	}

	/**
	 * Constructor with all required fields
	 *
	 * @param fk_person
	 * @param fk_categorie
	 * @param rank
	 */
	public PersonCategory(int fk_person, int fk_categorie, int rank) {
		this.fk_person = fk_person;
		this.fk_categorie = fk_categorie;
		this.rank = rank;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	private int fk_person;
	private int fk_categorie;
	private int rank;
}
