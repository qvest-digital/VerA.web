package de.tarent.octopus.request.directcall;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.octopus.request.TcResponse;
import de.tarent.octopus.soap.TcSOAPEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Stellt Funktionen zur direkten Übergabe
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
    Map header = null;
    TcSOAPEngine soapEngine = null;
    String taskName = null;
    String moduleName = null;

    List responseObjects;
    Map responseObjectsMap;

    boolean errorWhileProcessing = false;
    String errorMsg = null;
    Exception errorException = null;

    public void addResponseObject(String responseObjectName, Object o) {
        if (responseObjects == null) {
            responseObjects = new LinkedList();
            responseObjectsMap = new LinkedHashMap();
        }
        responseObjects.add(o);
        responseObjectsMap.put(responseObjectName, o);
    }

    public Iterator getResponseObjectKeys() {
        if (responseObjectsMap != null) {
            return responseObjectsMap.keySet().iterator();
        }
        return new EmptyIterator();
    }

    public Object getResponseObject(String key) {
        if (responseObjectsMap != null) {
            return responseObjectsMap.get(key);
        }
        return null;
    }

    public Object readNextResponseObject() {
        return responseObjects.remove(0);
    }

    public boolean hasMoreResponseObjects() {
        return responseObjects.size() > 0;
    }

    /**
     * Gibt einen Writer für die Ausgabe zurück.
     * <br><br>
     * Bevor etwas ausgegeben werden kann, muss der ContentType gesetzt werden.
     */
    public PrintWriter getWriter() {
        assertOutputStreamExistance();
        return printWriter;
    }

    /**
     * Returns the ByteArrayOutputStream.
     *
     * @return OutputStream implemented as an ByteArrayOutputStream;
     */
    public OutputStream getOutputStream() {
        assertOutputStreamExistance();
        return outputStream;
    }

    /**
     * Sets the outputStream.
     *
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
      throws IOException {
        if (printWriter != null) {
            printWriter.flush();
        }
        if (outputStream != null) {
            outputStream.flush();
        }
    }

    /**
     * Diese Methode schließt die Ausgaben ab.
     */
    public void close()
      throws IOException {
        if (printWriter != null) {
            printWriter.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void assertOutputStreamExistance() {
        if (outputStream == null) {
            setOutputStream(new ByteArrayOutputStream());
            printWriter = new PrintWriter(outputStream);
        }
    }

    /**
     * Setzt den Mime-Type für die Ausgabe.
     * Das muss passiert sein, bevor etwas ausgegeben wurde.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    /**
     * Setzt den Status für die Ausgabe.
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
        if (header == null) {
            header = new LinkedHashMap();
        }
        header.put(key, value);
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
        if (errorMsg != null) {
            return errorMsg;
        } else if (errorException != null) {
            return errorException.getMessage();
        }
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

    public void setErrorLevel(String errorLevel) {
    }
}
