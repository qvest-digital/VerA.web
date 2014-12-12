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
    @NamedQuery(name = "OptionalField.findByEventId", query = "select o from OptionalField o where fk_event=:eventId"),
    @NamedQuery(name = "OptionalField.findByEventIdAndLabel", query = "select f.pk From OptionalField f where f.fk_event=:eventId and f.label=:label ")
})
public class OptionalField {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	private int fk_event;
	private String label;
	
	public int getPk() {
		return pk;
	}
	
	public void setPk(int pk) {
		this.pk = pk;
	}
	
	public int getFk_event() {
		return fk_event;
	}
	
	public void setFk_event(int fk_event) {
		this.fk_event = fk_event;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
