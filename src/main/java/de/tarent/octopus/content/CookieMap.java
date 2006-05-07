/* $Id: CookieMap.java,v 1.1 2006/05/07 23:05:57 jens Exp $
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
 * by Jens Neumaier. 
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.content;

/**
 * @author Jens Neumaier, tarent GmbH
 *
 */
public interface CookieMap {
    
    /** Prefix for the Map in which cookies will be stored **/ 
    public static final String PREFIX_COOKIE_MAP = "cookies";
    
    /** New cookies will be stored in the octopus-content.
     * 	Each cookie is saved in another Map either as a Cookie-Object
     * 	in the field "cookie" that has to be assigned manually or
     *  in the field "value" which will automatically be used
     *  if a user preference is saved in the PersonalConfig.
     */
    public static final String COOKIE_MAP_FIELD_VALUE = "value";
    public static final String COOKIE_MAP_FIELD_COOKIE = "cookie";
    
    /** Configuration settings for default cookie creation 
     * 	
     * 	e.g. <param name="cookies.defaultMaxAge" value="5000000"/>
     */
    public static final String PREFIX_CONFIG_MAP = "cookies";
    public static final String CONFIG_MAXAGE = "defaultMaxAge";
}
