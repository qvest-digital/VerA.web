/**
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2015 tarent solutions GmbH and its contributors
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
package de.tarent.octopus.client.remote;

import de.tarent.octopus.client.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;

/** 
 * Kapselung der Antwort auf eine Octopus Anfrage
 *
 * @author <a href="mailto:sebastian@tarent.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class OctopusRemoteResult implements OctopusResult {

    InputStream streamContent = null;
    String contentType;
    HashMap dataMap = new HashMap();
    LinkedList dataKeys = new LinkedList();

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String newContentType) {
        this.contentType = newContentType;
    }
   

    public void setStreamContent(InputStream newStreamContent) {
        this.streamContent = newStreamContent;
    }
   
    public boolean hasStreamContent() {
        return streamContent != null;
    }

    public InputStream getContent() {
        return streamContent;
    }

    public void writeContent(OutputStream to) 
        throws IOException {
        byte[] buff = new byte[1024];
        int len = -1;
        while(-1 != (len = streamContent.read(buff))) {
            to.write(buff, 0, len);
        }
        to.flush();
    }


    public void addData(String key, Object dataObject) {
                                                
        //System.out.println("ADD: "+ key +"=>" +dataObject);
        
        dataMap.put(key, dataObject);
        dataKeys.add(key);
    }

    public boolean hasMoreData() {
        return dataKeys.size() > 0;
    }

    public Object nextData() {
        if (!hasMoreData())
            return null;
        Object key = dataKeys.removeFirst();       
        return dataMap.get(key);
    }

    /**
     * TODO: Implement
     */
    public Iterator getDataKeys() {
        return dataKeys.iterator();
    }

    /**
     * TODO: Implement
     */
    public Object getData(String key) {
        return dataMap.get(key);
    }
   
//     public Object nextDataAs(Class type) {
//         if (!hasMoreData())
//             return null;
//         Object o = data.removeFirst();
//         if (!type.isInstance(o))
//             throw new ClassCastException("Can not cast <"+o.getClass().getName()+"> to <"+type.getClass().getName()+">.");
//         return o;
//    }
    
    public String nextDataAsString() {
        return ""+nextData();
    }

//     public int nextDataAsInt() {
//         Object o = data.removeFirst();
//         if (o instanceof Integer)
//             return ((Integer)o).intValue();
        
//         try {
//             return Integer.parseInt(""+o);
//         } catch (NumberFormatException e) {
//             throw new ClassCastException("Can not parse int from <"+o+">.");
//         }
//     }

//     xpublic float nextDataAsFloat() {
//         Object o = data.removeFirst();
//         if (o instanceof Float)
//             return ((Float)o).floatValue();
        
//         try {
//             return Float.parseFloat(""+o);
//         } catch (NumberFormatException e) {
//             throw new ClassCastException("Can not parse float from <"+o+">.");
//         }
//     }
    
//     public byte[] nextDataAsByteArray() {
//        return (byte[])nextData();
//     }

    public String toString() {
        return "OctopusRemoteResult: "+dataMap.toString();
    }
}