/* $Id: TarDBConnection.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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

package de.tarent.octopus.data;

import java.util.Map;

/** 
 * Enthält die Verbindungsinformationen zu einer Datenquelle
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TarDBConnection {
    public static final String ACCESS_WRAPPER_CLASS_NAME = "accessWrapperClassName";
    public static final String DEFAULT_ACCESS_WRAPPER_CLASS_NAME = "de.tarent.octopus.data.TcGenericDataAccessWrapper";
    Map params;
    private String schema = "";

    public TarDBConnection(Map params) {
        this.params = params;
        if(params.get("schema") != null){
            schema = params.get("schema").toString();
        }
    }

    public String get(String key) {
        return (String) params.get(key);
    }

    public String getAccessWrapperClassName() {
        String out = (String) params.get(ACCESS_WRAPPER_CLASS_NAME);
        if (out != null)
            return out;
        return DEFAULT_ACCESS_WRAPPER_CLASS_NAME;
    }
    /**
     * @return String
     */
    public String getSchema() {
        if(schema.equals(""))return "";
        else
      return schema+".";
    }
}
