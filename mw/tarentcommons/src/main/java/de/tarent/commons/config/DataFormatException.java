/* $Id: DataFormatException.java,v 1.1 2007/08/17 11:20:24 fkoester Exp $
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
 * by Sebastian Mancke, Michael Klink. 
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.commons.config;

/**
 * This exception class is catched in some places
 * where plain XML data is accessed. Since direct
 * access to XML elements is discouraged this class
 * is, too.
 * 
 * A better approach is to add the neccessary accessor
 * methods in the {@link ConfigManager} class. It should
 * parse the XML and provide it as Map or other suitable
 * data structures to the user.
 *  
 * @author Robert Schuster
 * @deprecated Direct access to XML data is discouraged. See class
 * documentation for details.
 *
 */
public class DataFormatException extends Exception {

    public DataFormatException( String message ) {
	super( message );
    }

}
