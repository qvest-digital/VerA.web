/*
 * $Id: TcSecurityException.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $ tarent-octopus, Webservice Data Integrator and
 * Applicationserver Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright interest in the program 'tarent-octopus' (which makes passes at
 * compilers) written by Sebastian Mancke and Michael Klink. signature of Elmar Geese, 1 June 2002 Elmar Geese, CEO
 * tarent GmbH
 */

package de.tarent.octopus.security;

import de.tarent.octopus.resource.Resources;
import javax.xml.namespace.QName;

/**
 * Kapselt Fehlermeldungen, die auftreten, wenn ein User etwas ausführt, wofür er nicht autorisiert ist. <br>
 * Wird auch benutzt, wenn ein User versucht sich mit falscher Benutzername/Password kombination ein zu loggen.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke </a>, <b>tarent GmbH </b>
 */
public class TcSecurityException extends Exception {
	/**
	 * serialVersionUID = 5948930966578170159L
	 */
	private static final long serialVersionUID = 5948930966578170159L;

	public static final int		ERROR_NO_VALID_SESSION		= 1;

	public static final int		ERROR_AUTH_ERROR			= 2;

	public static final int		ERROR_WRONG_GROUP_FOR_TASK	= 3;

	public static final int		ERROR_INCOMPLETE_USER_DATA	= 4;

	public static final int		ERROR_SERVER_AUTH_ERROR		= 5;
	
	public static final int		ERROR_SERVER_USERMANAGEMENT_ERROR = 6;

	public static final String	uri							= "http://schemas.tarent.de/octopus";

	public static final QName[]	soapFaultCodes				= { 
        new QName(uri, "Client.authentication.unknownError"),
        new QName(uri, "Client.authentication.needLogin"),
        new QName(uri, "Client.authentication.authenticationFailed"),
        new QName(uri, "Client.authentication.notEnoughRights"),
        new QName(uri, "Client.authentication.incompleteUserData"),
        new QName(uri, "Server.authentication.serviceFailed") 
    };
    
	int							errorCode					= 0;

	String						message;

	String						detailMessage;

	public TcSecurityException(int errorCode) {
		super();
		this.errorCode = errorCode;
		setMessage(getMessageByErrorCode(errorCode));
	}

	public TcSecurityException(String message) {
		setMessage(message);
	}

	protected String getMessageByErrorCode(int errorCode) {
		if (errorCode == ERROR_WRONG_GROUP_FOR_TASK)
			return Resources.getInstance().get("SECURITYEXCEPTION_WRONG_GROUP_FOR_TASK");
		if (errorCode == ERROR_INCOMPLETE_USER_DATA)
			return Resources.getInstance().get("SECURITYEXCEPTION_INCOMPLETE_USER_DATA");
		if (errorCode == ERROR_AUTH_ERROR)
			return Resources.getInstance().get("SECURITYEXCEPTION_AUTH_ERROR");
		if (errorCode == ERROR_NO_VALID_SESSION)
			return Resources.getInstance().get("SECURITYEXCEPTION_NO_VALID_SESSION");
		if (errorCode == ERROR_SERVER_AUTH_ERROR)
			return Resources.getInstance().get("SECURITYEXCEPTION_SERVER_AUTH_ERROR");
		if (errorCode == ERROR_SERVER_USERMANAGEMENT_ERROR)
			return Resources.getInstance().get("SECURITYEXCEPTION_USERMANAGEMENT_ERROR");
		return "Unknown Errorcode";
	}

	/**
	 * @param string
	 * @param e
	 */
	public TcSecurityException(String string, Throwable e) {
		super(string, e);
	}

	public TcSecurityException(int errorCode, Throwable e) {
		this(errorCode);
		initCause(e);
	}

	public TcSecurityException(int errorCode, String detailMessage) {
		this(errorCode);
		setDetailMessage(detailMessage);
	}

	public QName getSoapFaultCode() {
		if (errorCode < 0 || errorCode >= soapFaultCodes.length)
			return soapFaultCodes[0];
		return soapFaultCodes[errorCode];
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String newMessage) {
		this.message = newMessage;
	}

	/**
	 * @return Returns the detailMessage.
	 */
	public String getDetailMessage() {
		return detailMessage;
	}

	/**
	 * @param detailMessage
	 *            The detailMessage to set.
	 */
	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}
}