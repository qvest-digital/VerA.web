/* $Id: OctopusConstants.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

import javax.xml.namespace.QName;

/** 
 * Konstanten, die ein Octopus Client benötigen kann.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusConstants {


	public static final String OCTOPUS_NAMESPACE = "http://schemas.tarent.de/octopus";

	public static final String SERVER_ERROR_PREFIX = "Server";
	public static final String AUTHENTICATION_ERROR_PREFIX = "Client.authentication";

	public static final String AUTHENTICATION_FAILED = "Client.authentication.authenticationFailed";
	public static final String AUTHENTICATION_UNKNOWN_ERROR = "Client.authentication.unknownError";
	public static final String AUTHENTICATION_NEED_LOGIN = "Client.authentication.needLogin";
	public static final String AUTHENTICATION_NOT_ENOUGH_RIGHTS = "Client.authentication.notEnoughRights";
	public static final String AUTHENTICATION_CANCELED = "Client.authentication.canceled";

	public static final QName SOAPF_AUTHENTICATION_UNKNOWN_ERROR = new QName( OCTOPUS_NAMESPACE, AUTHENTICATION_UNKNOWN_ERROR );
	public static final QName SOAPF_AUTHENTICATION_NEED_LOGIN = new QName( OCTOPUS_NAMESPACE, AUTHENTICATION_NEED_LOGIN );
	public static final QName SOAPF_AUTHENTICATION_NOT_ENOUGH_RIGHTS = new QName( OCTOPUS_NAMESPACE, AUTHENTICATION_NOT_ENOUGH_RIGHTS );
    public static final QName SOAPF_AXIS_HTTP_ERROR = new QName("http://xml.apache.org/axis/", "HTTP");
    
}
