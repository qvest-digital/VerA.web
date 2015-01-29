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
