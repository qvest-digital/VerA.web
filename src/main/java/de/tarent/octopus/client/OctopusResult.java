/* $Id: OctopusResult.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
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

package de.tarent.octopus.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;

/** 
 * Aufruf eines Task des Octopus.
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface OctopusResult {

    public boolean hasStreamContent();
    public InputStream getContent();
    public String getContentType();
    public void writeContent(OutputStream to)
        throws IOException;


    public boolean hasMoreData();
    public Iterator getDataKeys();
    public Object getData(String key);
    public Object nextData();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public Object nextDataAs(Class type);

    /**
     * @throws java.lang.ClassCastException
     */
    public String nextDataAsString();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public int nextDataAsInt();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public float nextDataAsFloat();

//     /**
//      * @throws java.lang.ClassCastException
//      */
//     public byte[] nextDataAsByteArray();
}
