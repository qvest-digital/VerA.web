/* $Id: CVS.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * Created on 07.05.2003
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
 * by Michael Klink. 
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */ 
package de.tarent.octopus.util;

/**
 * Utility-Funktionen für den Umgang mit CVS
 * 
 * @author mikel
 */
public class CVS {
    public static String getContent(String keyPhrase) {
        String result = null;
        
        if (keyPhrase != null) {
            int index = keyPhrase.indexOf(':');
            if (index >= 0) {
                keyPhrase = keyPhrase.substring(index + 1);
                index = keyPhrase.indexOf('$');
                if (index >= 0)
                    result = keyPhrase.substring(0, index).trim();
            }
        }
        
        return result;
    }

    private CVS() {
    }
}
