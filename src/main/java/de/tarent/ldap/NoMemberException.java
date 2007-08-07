/* $Id: NoMemberException.java,v 1.2 2005/08/17 09:58:57 kirchner Exp $
 * 
 * Created on 09.12.2004 by kirchner
 * 
 * tarent-contact, Plattform-Independent Webservice-Based Contact Management
 * Copyright (C) 2004 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Philipp Kirchner.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.ldap;

/**
 * @author kirchner
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NoMemberException extends LDAPException {
	private static final long serialVersionUID = 6191825942458988785L;

	public NoMemberException(String message){
		super(message);
	}
}
