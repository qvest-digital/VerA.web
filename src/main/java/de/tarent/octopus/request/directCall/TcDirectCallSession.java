/* $Id: TcDirectCallSession.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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

package de.tarent.octopus.request.directCall;

import de.tarent.octopus.request.TcSession;
import java.util.Enumeration;
import java.util.Hashtable;

/** 
 * Stellt ein Sessionobjekt eines direkten Aufrufes dar.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcDirectCallSession implements TcSession {

    Hashtable attributes = new Hashtable();
    long creationTime;
    String id;
    
    public TcDirectCallSession(){        
        this.creationTime = System.currentTimeMillis();
        this.id = (new StringBuffer(Long.toHexString(System.currentTimeMillis())).reverse()).toString();        
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name,value);
    }


    public long getCreationTime() {
        return creationTime;
    }

    public String getId() {
        return id;
    }

    public long getLastAccessedTime() {
        return -1;
    }

    public int getMaxInactiveInterval() {
        return -1;
    }

    public void invalidate() {
        // do nothing
    }

    public boolean isNew() {
        return false;        
    }
    
    public void setMaxInactiveInterval(int interval) {
        // do nothing
    }
}
