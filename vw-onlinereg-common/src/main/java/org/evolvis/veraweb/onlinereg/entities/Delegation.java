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
 * @author Sven Schumann <s.schumann@tarent.de>
 */
@Data
@XmlRootElement
@Entity
@Table(name = "toptional_fields_delegation_content")
@NamedQueries({
    @NamedQuery(name = Delegation.QUERY_FIND_BY_GUEST,
    			query = "select d from Delegation d where fk_guest=:" + Delegation.PARAM_GUEST_ID + " and fk_delegation_field=:" + Delegation.PARAM_FIELD_ID)
})
public class Delegation {
	public static final String QUERY_FIND_BY_GUEST = "Delegation.findByGuestId";
	public static final String PARAM_GUEST_ID = "guestId";
	public static final String PARAM_FIELD_ID = "fieldId";

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
