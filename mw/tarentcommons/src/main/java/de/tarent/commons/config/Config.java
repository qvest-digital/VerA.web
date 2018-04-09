/* $Id: Config.java,v 1.1 2007/08/17 11:20:24 fkoester Exp $
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

import de.tarent.commons.utils.VersionInfo;
import de.tarent.commons.utils.VersionTool;

/**
 * This class was once the configuration system.
 * 
 * @deprecated Remaining functionality should be moved to more
 * appropriate places.
 * 
 * @author mikel
 */
public abstract class Config {
    /**
     * Einige Strings, die der Anzeige dienen.
     */
    public static String APPLICATION_CAPTION;
    public static String APPLICATION_VERSION;
    public static String APPLICATION_BUILD;
    
    static {
      
      VersionInfo vi = VersionTool.getInfoFromClass(Config.class);
      
      if(vi != null)
      {
    	  APPLICATION_CAPTION = vi.getName("contact-client");

    	  APPLICATION_VERSION = vi.getVersion("n/a");

    	  APPLICATION_BUILD = vi.getBuildID("development build");
      }
    }
    
  // RSCHUS_TODO: Put this in a ConfigHelper class
  public static String getApplicationCaption() {
	  
	  // if we are running a development-build show as many information as possible
	  if(APPLICATION_VERSION == null || APPLICATION_VERSION.equals("n/a") || APPLICATION_VERSION.toLowerCase().indexOf("snapshot") != -1)
		  return APPLICATION_CAPTION + " " + APPLICATION_VERSION + " (" + APPLICATION_BUILD + ")";
	  
	  // if we are running on a final build, do not show build ID
	  return APPLICATION_CAPTION + " " + APPLICATION_VERSION;
    }
    
}
