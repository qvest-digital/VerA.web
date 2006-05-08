/* $Id: Closeable.java,v 1.1 2006/05/08 15:47:38 asteban Exp $
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
 * A Closeable is a Resource, which may be closed if not needed any longer.
 * This interace ist semanticaly the same as {@link java.io.Closeable} introduced with JDK 1.5.
 * <br><br>
 * Within the octopus, this interface may be used to safely close resources after the processing of a request.
 * This is done by a List of Closeable-Object within the OctopusContext. After the generation of the 
 * octopus response, the close method is called for each object in this list. This will be done even if the request processing will throw an exception.
 * Use the {@link OctopusContext.addCleanupCode()} to add an Object to the list of closeable objects.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 * @version 1.0
 */
public interface Closeable {
    
    /**                                         
     * Closes the resource.
     * @throws Exception This method may throw any exception, but should not rely on an proper handling by the calling code.
     */
    public void close();
}