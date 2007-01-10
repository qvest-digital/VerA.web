/* $Id: TcServletDummySession.java,v 1.1 2007/01/10 11:07:05 christoph Exp $
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

package de.tarent.octopus.request.servlet;

import java.util.Enumeration;
import java.util.Hashtable;

import de.tarent.octopus.request.TcSession;

/** 
 * Implementierung einer Session als Dummy ohne Session-Funktionalität
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcServletDummySession implements TcSession {
    
    private Hashtable dummyData = new Hashtable();
                    
    public Object getAttribute(String name) {  
        return dummyData.get(name);
    }
    public Enumeration getAttributeNames() {
        return dummyData.keys();
    }
    public long getCreationTime() {
        return -1;
    }
    public String getId() {
        return "NO-DUMMYDATA";
    }
    public long getLastAccessedTime() {
        return -1;
    }                    
    public int getMaxInactiveInterval() {
        return -1;
    }
    public void invalidate() {}
    public boolean isNew() {
        return true;
    }
    public void removeAttribute(java.lang.String name) {
        dummyData.remove(name);
    }
    public void setAttribute(String name, Object value) {
        dummyData.put(name, value);
    }
    public void setMaxInactiveInterval(int interval) {}                    
}
