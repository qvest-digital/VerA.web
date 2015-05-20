package org.evolvis.veraweb.onlinereg.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

/**
 * Class which represents the table toptional_field_type
 * 
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_field_type")
public class OptionalFieldType {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pk;
	
	private String description;
	
	public int getPk() {
		return pk;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
