/* $Id: TcDirectCallResponse.java,v 1.5 2006/05/07 23:05:57 jens Exp $
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

import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.soap.TcSOAPEngine;

import java.io.*;
import java.util.*;

/** 
 * Stellt Funktionen zur direkten �bergabe 
 * der Ausgaben an einen Aufrufer bereit.
 * 
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class TcDirectCallResponse implements TcResponse {

    ByteArrayOutputStream outputStream = null;
    PrintWriter printWriter = null;
    String contentType = null;
    int statusCode = -1;
    int cachingTime = -1;
    HashMap header = null;
    TcSOAPEngine soapEngine = null;
    String taskName = null;
    String moduleName = null;

    List responseObjects;
    Map responseObjectsMap;

    boolean errorWhileProcessing = false;
    String errorMsg = null;
    Exception errorException = null;


    public void addResponseObject(String responseObjectName, Object o) {
        if (responseObjects==null) {
            responseObjects = new LinkedList();
            responseObjectsMap = new HashMap();
        }
        responseObjects.add(o);
        responseObjectsMap.put(responseObjectName, o);
    }

    public Iterator getResponseObjectKeys() {
        if (responseObjectsMap != null)
            return responseObjectsMap.keySet().iterator();
        return new EmptyIterator();
    }

    public Object getResponseObject(String key) {
        if (responseObjectsMap != null)
            return responseObjectsMap.get(key);
        return null;
    }

    public Object readNextResponseObject() {
        return responseObjects.remove(0);
    }

    public boolean hasMoreResponseObjects() {
        return responseObjects.size() > 0;
    }

    /**
     * Gibt einen Writer f�r die Ausgabe zur�ck.
     * <br><br>
     * Bevor etwas ausgegeben werden kann, muss der ContentType gesetzt werden.
     */
    public PrintWriter getWriter() {
        assertOutputStreamExistance();
        return printWriter;
    }

    /**
     * Returns the ByteArrayOutputStream.
     * @return OutputStream implemented as an ByteArrayOutputStream;
     */
    public OutputStream getOutputStream() {
        assertOutputStreamExistance();
        return outputStream;
    }

    /**
     * Sets the outputStream.
     * @param outputStream The outputStream to set
     */
    public void setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
        printWriter = new PrintWriter(outputStream, true);
    }

    /**
     * Gibt einen String auf den Weiter aus.
     */
    public void print(String responseString) {
        printWriter.print(responseString);
    }

    /**
     * Gibt einen String + "\n" auf den Weiter aus.
     */
    public void println(String responseString) {
        printWriter.println(responseString);
    }

    /**
     * Diese Methode sendet gepufferte Ausgaben. 
     */
    public void flush() 
        throws IOException{
        if (printWriter != null)
            printWriter.flush();
        if (outputStream != null)
            outputStream.flush();
    }

    /**
     * Diese Methode schlie�t die Ausgaben ab. 
     */
    public void close() 
        throws IOException{
        if (printWriter != null)
            printWriter.close();
        if (outputStream != null)
            outputStream.close();
    }

    private void assertOutputStreamExistance() {
        if (outputStream == null) {
            setOutputStream(new ByteArrayOutputStream());
            printWriter = new PrintWriter(outputStream);
        }
    }
   

    /**
     * Setzt den Mime-Type f�r die Ausgabe.
     * Das muss passiert sein, bevor etwas ausgegeben wurde.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

	/**
	 * Setzt den Status f�r die Ausgabe.
	 * Das muss passiert sein, bevor etwas ausgegeben wurde.
	 */
	public void setStatus(int code) {
        code = statusCode;
    }

    /**
     * Setzt einen Header-Parameter.
     * Das muss passiert sein, bevor etwas ausgegeben wurde.
     */
    public void setHeader(String key, String value) {
        if (header == null)
            header = new HashMap();
        header.put(key,value);
    }

    public void setSoapEngine(TcSOAPEngine soapEngine) {
        this.soapEngine = soapEngine;
    }

    public TcSOAPEngine getSoapEngine() {
        return soapEngine;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void sendError(int responseType, String requestID, String header, Exception e) {
        errorWhileProcessing = true;
        errorMsg = header;
        errorException = e;
    }

    public boolean errorWhileProcessing() {
        return errorWhileProcessing;
    }

    public String getErrorMessage() {
        if (errorMsg != null)
            return errorMsg;
        else if (errorException != null)
            return errorException.getMessage();
        return null;
    }

    public Exception getErrorException() {
        return errorException;
    }

    
    public void setAuthorisationRequired(String authorisationAction) {
        // TODO: Geeignete Umsetzung finden.
    }


    class EmptyIterator 
        implements Iterator {

        public boolean hasNext() {
            return false;
        }

        public Object next() 
            throws NoSuchElementException {
            throw new NoSuchElementException();
        }

        public void remove() 
            throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }


    /* (non-Javadoc)
     * @see de.tarent.octopus.request.TcResponse#setCachingTime(int)
     */
    public void setCachingTime(int millis) {
        cachingTime = millis;
    }

    public void setCachingTime(int millis, String param) {
    	setCachingTime(millis);
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.request.TcResponse#getCachingTime()
     */
    public int getCachingTime() {
        return cachingTime;
    }

	public void addCookie(String name, String value, Map settings) {
	}
	
	public void addCookie(Object cookie) {		
	}
}
