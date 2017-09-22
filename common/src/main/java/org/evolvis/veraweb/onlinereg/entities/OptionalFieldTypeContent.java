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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Class which represents the table toptional_field_type_content
 * 
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_field_type_content")
@NamedQueries({
    @NamedQuery(name = "OptionalFieldTypeContent.findTypeContentsByOptionalField", 
    			query = "SELECT oftc from OptionalFieldTypeContent oftc WHERE oftc.fk_optional_field=:optionalFieldId "
    					+ "AND ((oftc.content not like '') AND oftc.content is not null) order by oftc.fk_optional_field")
})
public class OptionalFieldTypeContent {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	
	private Integer fk_optional_field;
	
	private String content;
	
	public void setPk(int pk) {
		this.pk = pk;
	}
	
	public int getPk() {
		return pk;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getFk_optional_field() {
		return fk_optional_field;
	}
	
	public void setFk_optional_field(Integer fk_optional_field) {
		this.fk_optional_field = fk_optional_field;
	}

}
