package de.tarent.commons.logging;

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
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.tarent.commons.utils.Tools;

/**
 * Used for saving name and parameters of a method call in {@link ThreadLogger}.
 *
 * @author Tim Steffens
 */
public class MethodCall {

    private static String LINE_SEPARATOR = de.tarent.commons.utils.StringTools.LINE_SEPARATOR;

    /**
     * The time this Object was instanciatet in millseconds.
     */
    private final long invokeTime = System.currentTimeMillis();
    /**
     * Class name
     */
    private String classname;
    /**
     * Method name.
     */
    private String methodname;
    /**
     * Free text note.
     */
    private String note;
    /**
     * Parameter list
     */
    protected List parameters = new LinkedList();
    /**
     * Variable list.
     */
    protected List variables = new LinkedList();

    /**
     * Creates a new method call object.
     */
    public MethodCall() {
        init(+2);
        this.note = null;
    }

    /**
     * Creates a new method call object.
     */
    public MethodCall(int logpos) {
        init(logpos + 2);
        this.note = null;
    }

    /**
     * creates a new method call object with a method note.
     */
    public MethodCall(String note) {
        init(+2);
        this.note = note;
    }

    /**
     * creates a new method call object with a method note.
     */
    public MethodCall(int logpos, String note) {
        init(logpos + 2);
        this.note = note;
    }

    /**
     * Init the current methodcall instance and set the classname and the
     * method name of the given <code>logpos</code>. Logpos define the
     * position of an entry in the current stack.
     *
     * @param logpos
     */
    protected void init(int logpos) {
        StackTraceElement ste[];
        try {
            throw new RuntimeException();
        } catch (RuntimeException e) {
            ste = e.getStackTrace();
        }
        if (ste.length >= logpos) {
            classname = ste[logpos].getClassName();
            methodname = ste[logpos].getMethodName();
            return;
        }
        throw new RuntimeException("Illegal stacktrace depth. Want get logpos "
          + logpos + " from this stack: " + Arrays.asList(ste));
    }

    /**
     * Adds a new parameter to the end of the list of parameters
     *
     * @param name  name of the parameter
     * @param value value of the parameter
     */
    public void addParameter(String name, Object value) {
        parameters.add(new NamedObject(name, value));
    }

    public void addVariable(String name, Object value) {
        variables.add(new NamedObject(name, value));
    }

    public String getClassName() {
        return classname;
    }

    public String getMethodName() {
        return methodname;
    }

    public String getNote() {
        return note;
    }

    public Date getInvokeTime() {
        return new Date(invokeTime);
    }

    public List getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public List getVariables() {
        return Collections.unmodifiableList(variables);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(500);
        buffer.append(classname).append("#").append(methodname);
        buffer.append(" [at ").append(getInvokeTime()).append("]:").append(LINE_SEPARATOR);
        if (!parameters.isEmpty()) {
            buffer.append("      With parameters:").append(LINE_SEPARATOR);
            buffer.append(Tools.iteratorToString(parameters.iterator(), "        ", true, LINE_SEPARATOR, true));
        }
        if (!variables.isEmpty()) {
            buffer.append("      Local variables:").append(LINE_SEPARATOR);
            buffer.append(Tools.iteratorToString(variables.iterator(), "        ", true, LINE_SEPARATOR, true));
        }
        return buffer.toString();
    }
}
