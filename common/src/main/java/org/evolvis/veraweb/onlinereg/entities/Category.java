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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class of table 'tcategorie'
 * 
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tcategorie")
@NamedNativeQueries({
	@NamedNativeQuery(name = "Category.findIdByCatname",
			query = "SELECT c.pk " +
					"FROM tcategorie c " +
					"where catname=:catname " +
					"and fk_orgunit=(SELECT fk_orgunit from tevent where mediarepresentatives=:uuid)"),
	@NamedNativeQuery(name = "Category.getCategoryIdByCategoryName",
			query = "SELECT c.pk " +
					"FROM tcategorie c " +
					"where catname=:catname"),
	@NamedNativeQuery(name = "Category.findCatnameByUserAndDelegation",
			query = "SELECT c.catname " +
					"FROM tcategorie c " +
					"WHERE c.pk = (SELECT g.fk_category FROM tguest g WHERE g.fk_person=:personId AND g.delegation=:uuid)"
					)
})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    private String catname;

	public int getPk() {
		return pk;
	}
	public void setPk(int pk) {
		this.pk = pk;
	}
	public String getCatname() {
		return catname;
	}
	public void setCatname(String catname) {
		this.catname = catname;
	}

}
