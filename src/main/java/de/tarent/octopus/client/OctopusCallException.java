/* $Id: OctopusCallException.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * tarent-octopus, Webservice Data Integrator and Applicationserver
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
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.client;

/** 
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusCallException extends RuntimeException {
    /**
	 * serialVersionUID = -604458204528968362L
	 */
	private static final long serialVersionUID = -604458204528968362L;

	protected String errorCode;
    protected String causeMessage;

    public OctopusCallException(String msg, Exception e) {
        super(msg, e);
        if (e != null)
            causeMessage = e.getMessage();
    }

    public OctopusCallException(String errorCode, String msg, Exception e) {
        super(msg, e);
        this.errorCode = errorCode;
        if (e != null)
            causeMessage = e.getMessage();
    }

    public String getCauseMessage() {
        return causeMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
