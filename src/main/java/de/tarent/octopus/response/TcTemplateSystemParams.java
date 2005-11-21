/* $Id: TcTemplateSystemParams.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.response;

import de.tarent.octopus.config.TcConfig;

/** 
 * Enthält Informationen und Parameter, die für alle Templates gleich sind.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcTemplateSystemParams {
    TcConfig config;
    String templateRootPath;

    /**
     * Initialisierung mit Config Objekkt, aus dem die Informationen kommen können
     */
    public TcTemplateSystemParams(TcConfig config) {
        this.config = config;
    }

    /**
     * Gibt das Verzeichniss zurück, indem nach Templates gesucht werden soll.
     *
     * @return Absolute Pfadangabe mit abschließendem Slash
     */
    public String getTemplateRootPath() {
        return templateRootPath;
    }

    /**
     * Setzt das Verzeichniss indem nach Templates gesucht werden soll.
     *
     * @param path Absolute Pfadangabe mit abschließendem Slash
     */
    public void setTemplateRootPath(String path) {
        templateRootPath = path;
    }
}
