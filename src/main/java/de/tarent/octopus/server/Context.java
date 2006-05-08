/* $Id: Context.java,v 1.2 2006/05/08 15:47:38 asteban Exp $
 * 
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
 * by Sebastian Mancke
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.server;

/**
 * This class gives a static access to the OctopusContext Object associated with the 
 * request of the current Thread. At the begin of the octopus request processing 
 * the context is set by an ThreadLocal Variable. So it can be obtained at later time.
 * Without passing a reference to it.
 * 
 * During the processing of an Octopus request the active OctpusContext may change 
 * depending on the current executed scope.
 *
 * TODO: Maybe it would be nice to have a stack for each thread. Then we would be
 * able to push the contexts for a new sub-scope and pop it afterwards.
 * 
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public class Context {
    
    private static ThreadLocal currentContext = new ThreadLocal();

    /**
     * Returns the current active OctopusContext for this thread
     */
    public static OctopusContext getActive() {
        return (OctopusContext)currentContext.get();
    }

    /**
     * Sets the current active OctopusContext for this thread
     */
    public static void setActive(OctopusContext oc) {
        currentContext.set(oc);
    }

    /**
     * Removes all Information for hold for the current thread
     */
    public static void clear() {
        currentContext.set(null);
    }
    
}