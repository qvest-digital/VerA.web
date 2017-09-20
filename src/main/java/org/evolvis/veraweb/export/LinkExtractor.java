package org.evolvis.veraweb.export;

/*-
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), export module is
 * Copyright © 2016, 2017
 * 	Атанас Александров <a.alexandrov@tarent.de>
 * Copyright © 2016
 * 	Lukas Degener <l.degener@tarent.de>
 * 	Max Weierstall <m.weierstall@tarent.de>
 * Copyright © 2017
 * 	mirabilos <t.glaser@tarent.de>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import de.tarent.extract.ResultSetValueExtractor;

public class LinkExtractor implements ResultSetValueExtractor{

    private final String prefix;

    public LinkExtractor(Properties properties) {
        final String prefixPropertyName = properties.getProperty("prefixPropertyName", "prefix");
        prefix = properties.getProperty(prefixPropertyName);
    }

    public Object extractValue(ResultSet rs, int col) throws SQLException {
        final String value = rs.getString(col+1);
        if(value==null){
            return null;
        }
        if(prefix.endsWith("/")){
            return prefix+value;
        }
        return prefix+"/"+value;
    }

}
