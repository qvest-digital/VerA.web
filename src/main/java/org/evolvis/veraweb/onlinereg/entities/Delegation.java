package org.evolvis.veraweb.onlinereg.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.evolvis.veraweb.onlinereg.entities.pk.DelegationPK;

import lombok.Data;

/**
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_fields_delegation_content")
public class Delegation {
	
	@EmbeddedId
	private DelegationPK pk;
	private String value;
	
	
	public DelegationPK getPk() {
		return pk;
	}

	public void setPk(DelegationPK pk) {
		this.pk = pk;
	}

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
