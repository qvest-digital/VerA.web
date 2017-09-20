package de.tarent.octopus.request.servlet;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
