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
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_fields")
@NamedQueries({
    @NamedQuery(name = OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID, query = "select o from OptionalField o where fk_event=:eventId order by o.pk"),
    @NamedQuery(name = OptionalField.OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL, query = "select f.pk From OptionalField f where f.fk_event=:eventId and f.label=:label ")
})
public class OptionalField {

	public static final String OPTIONAL_FIELD_FIND_BY_EVENT_ID = "OptionalField.findByEventId";
	public static final String OPTIONAL_FIELD_FIND_BY_EVENT_ID_AND_LABEL = "OptionalField.findByEventIdAndLabel";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer pk;
	private Integer fk_event;
	private String label;
	private Integer fk_type;
	
	public Integer getPk() {
		return pk;
	}
	
	public void setPk(Integer pk) {
		this.pk = pk;
	}
	
	public Integer getFk_event() {
		return fk_event;
	}
	
	public void setFk_event(Integer fk_event) {
		this.fk_event = fk_event;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Integer getFk_type() {
		return fk_type;
	}
	
	public void setFk_type(Integer fk_type) {
		this.fk_type = fk_type;
	}
	
	
}
