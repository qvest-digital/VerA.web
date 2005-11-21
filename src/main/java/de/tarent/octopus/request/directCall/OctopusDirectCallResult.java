/* $Id: OctopusDirectCallResult.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import de.tarent.octopus.client.OctopusResult;

/** 
 * Kapselung der Antwort auf eine Octopus Anfrage
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusDirectCallResult implements OctopusResult {

    TcDirectCallResponse response;
    public OctopusDirectCallResult(TcDirectCallResponse response) {
        this.response = response;
    }


    public boolean errorWhileProcessing() {
        return response.errorWhileProcessing();
    }

    public String getErrorMessage() {
        return response.getErrorMessage();
    }

    public Exception getErrorException() {
        return response.getErrorException();
    }


    public String getContentType() {
        return response.getContentType();
    }
    
    public boolean hasStreamContent() {
        return ((ByteArrayOutputStream)response.getOutputStream()).size() > 0;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(((ByteArrayOutputStream)response.getOutputStream()).toByteArray());
    }

    public void writeContent(OutputStream to) 
        throws IOException {
        ((ByteArrayOutputStream)response.getOutputStream()).writeTo(to);
        to.flush();
    }


    

    public boolean hasMoreData() {
        return response.hasMoreResponseObjects();
    }

    public Object nextData() {
        if (!hasMoreData())
            return null;
        return response.readNextResponseObject();
    }

    public Iterator getDataKeys() {
        return response.getResponseObjectKeys();
    }

    public Object getData(String key) {
        return response.getResponseObject(key);
    }
    
    public Object nextDataAs(Class type) {
        if (!hasMoreData())
            return null;
        Object o = response.readNextResponseObject();
        if (!type.isInstance(o))
            throw new ClassCastException("Can not cast <"+o.getClass().getName()+"> to <"+type.getClass().getName()+">.");
        return o;
    }
    
    public String nextDataAsString() {
        return (String)nextData();
    }

    public int nextDataAsInt() {
        if (!hasMoreData())
            return -1;
        Object o = response.readNextResponseObject();
        if (o instanceof Integer)
            return ((Integer)o).intValue();
        
        try {
            return Integer.parseInt(""+o);
        } catch (NumberFormatException e) {
            throw new ClassCastException("Can not parse int from <"+o+">.");
        }
    }

    public float nextDataAsFloat() {
        if (!hasMoreData())
            return -1f;
        Object o = response.readNextResponseObject();
        if (o instanceof Float)
            return ((Float)o).floatValue();
        
        try {
            return Float.parseFloat(""+o);
        } catch (NumberFormatException e) {
            throw new ClassCastException("Can not parse float from <"+o+">.");
        }
    }
    
    public byte[] nextDataAsByteArray() {
        return (byte[])nextData();
    }
}