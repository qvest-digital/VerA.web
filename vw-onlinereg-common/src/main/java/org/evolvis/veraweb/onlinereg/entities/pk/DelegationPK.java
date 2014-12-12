package org.evolvis.veraweb.onlinereg.entities.pk;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * Composed PK for Delegation
 * @author jnunez
 */
@Embeddable()
public class DelegationPK implements Serializable{
	
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 1L;
	private int fk_guest;
	private int fk_delegation_field;
	
	public DelegationPK() {
	}
	
	public DelegationPK(int fk_guest, int fk_delegation_field) {
		super();
		this.fk_guest = fk_guest;
		this.fk_delegation_field = fk_delegation_field;
	}
	
	public int getFk_guest() {
		return fk_guest;
	}
	public void setFk_guest(int fk_guest) {
		this.fk_guest = fk_guest;
	}
	public int getFk_delegation_field() {
		return fk_delegation_field;
	}
	public void setFk_delegation_field(int fk_delegation_field) {
		this.fk_delegation_field = fk_delegation_field;
	}
	
	
}
