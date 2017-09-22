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
 * @author Atanas Alexandrov, tarent solutions GmbH
 * @author Stefan Weiz, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tperson_categorie")
@NamedQueries(value = {
        @NamedQuery(name = "PersonCategory.personCategoryExists", query = "SELECT pc FROM PersonCategory pc where fk_person=:personId and fk_categorie=:categoryId")
})
public class PersonCategory {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	private Integer fk_person;
	private Integer fk_categorie;
	private Integer rank;
	
	
	/**
	 * Empty constructor MUST
	 */
	public PersonCategory() {
	}

	/**
	 * Constructor with custom set of fields.
	 *
	 * @param fk_person Person id
	 * @param fk_categorie Category id
	 */
	public PersonCategory(Integer fk_person, Integer fk_categorie) {
		this.fk_person = fk_person;
		this.fk_categorie = fk_categorie;
	}

	/**
	 * Constructor with all fields.
	 *
	 * @param fk_person Person id
	 * @param fk_categorie Category id
	 * @param rank Rank
	 */
	public PersonCategory(Integer fk_person, Integer fk_categorie, Integer rank) {
		this.fk_person = fk_person;
		this.fk_categorie = fk_categorie;
		this.rank = rank;
	}

	

	public int getPk() {
		return pk;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}

	public Integer getFk_person() {
		return fk_person;
	}

	public void setFk_person(Integer fk_person) {
		this.fk_person = fk_person;
	}

	public Integer getFk_categorie() {
		return fk_categorie;
	}

	public void setFk_categorie(Integer fk_categorie) {
		this.fk_categorie = fk_categorie;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}
}
