/* $Id: TcServletSession.java,v 1.1 2007/01/10 11:07:05 christoph Exp $
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

import de.tarent.octopus.request.TcSession;
import javax.servlet.http.HttpSession;

/** 
 * Kapselt das Sessionobjekt des Servletkontainers
 * um eine eventuelle spätere Umstellung leichter zu machen und eine 
 * Unabhängigkeit von der Umgebung zu ereichen.
 * <br><br>
 * Zur Zeit werden keine weiteren Methoden als die der javax.servlet.http.HttpSession bereit gestellt.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcServletSession implements TcSession {
    protected HttpSession session;

    public HttpSession getHttpSession() {
        return session;
    }

    public TcServletSession(HttpSession session) {
        this.session = session;
    }

    public Object getAttribute(String name) {
        return session.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }

    public long getCreationTime() {
        return session.getCreationTime();
    }

    public String getId() {
        return session.getId();
    }

    public long getLastAccessedTime() {
        return session.getLastAccessedTime();
    }

    public int getMaxInactiveInterval() {
        return session.getMaxInactiveInterval();
    }

    public void invalidate() {
        session.invalidate();
    }

    public boolean isNew() {
        return session.isNew();
    }

    public void removeAttribute(java.lang.String name) {
        session.removeAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    public void setMaxInactiveInterval(int interval) {
        session.setMaxInactiveInterval(interval);
    }
}
