/* $Id: ActionDefinitionException.java,v 1.1 2007/08/17 11:20:23 fkoester Exp $
 *
 * tarent-contact, Plattform-Independent Webservice-Based Contactmanagement
 * Copyright (C) 2002 tarent GmbH
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
 * interest in the program 'tarent-contact'
 * (which makes passes at compilers) written
 * by Sebastian Mancke.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.commons.action;

public class ActionDefinitionException extends Exception {

	private static final long	serialVersionUID	= -5748100286171926561L;

	private String actionUniqueName;

	public ActionDefinitionException(String msg) {
        super(msg);
    }

    public ActionDefinitionException(Throwable t) {
        super(t);
    }

    public ActionDefinitionException(String msg, Throwable t) {
        super(msg, t);
    }

    public void setActionUniqueName(String actionUniqueName) {
        this.actionUniqueName = actionUniqueName;
    }

    public String getActionUniqueName() {
    	return actionUniqueName;
    }
}
