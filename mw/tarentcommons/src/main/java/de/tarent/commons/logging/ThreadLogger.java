package de.tarent.commons.logging;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
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
import java.util.LinkedList;
import java.util.List;

import de.tarent.commons.utils.StringTools;
import de.tarent.commons.utils.Tools;

/**
 * Provides a mechanism for logging the method calls and their arguments
 * encountered by a thread.
 * <p>
 * This class is a <a href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a>
 * which is altered a bit: for each thread an own instance exists.
 *
 * @author Tim Steffens, Christoph Jerolimov; tarent GmbH
 */
public class ThreadLogger {
    private static ThreadLocal threadInformation = new ThreadLocal();

    /**
     * Return a new threadlogger instance with an pseudo unique
     * threadId which was generated with this simple code:
     * <code>Long.toHexString(System.currentTimeMillis())</code>
     *
     * @return new threadlogger instance, never null.
     */
    public static ThreadLogger createInstance() {
        return createInstance(Long.toHexString(System.currentTimeMillis()));
    }

    /**
     * Return a new threadlogger instance with the given <code>threadId</code>.
     * For example this id can be a thread-name, a request-id or just a simple
     * developer note.
     *
     * @param threadId
     * @return new threadlogger instance, never null.
     */
    public static ThreadLogger createInstance(String threadId) {
        ThreadLogger threadLogger = new ThreadLogger(threadId);
        threadInformation.set(threadLogger);
        return threadLogger;
    }

    /**
     * Return a threadlogger for the current thread based on the
     * {@link ThreadLocal} instance {@link #threadInformation}.
     * Is no threadlogger yet has been initialized or the current
     * is disposed (the {@link #clean()} are already called) it
     * will be call {@link #createInstance()}.
     *
     * @return new threadlogger instance, never null.
     */
    public static ThreadLogger getInstance() {
        ThreadLogger threadLogger = (ThreadLogger) threadInformation.get();
        if (threadLogger == null || threadLogger.disposed) {
            return createInstance();
        }
        return threadLogger;
    }

    /**
     * Return a threadlogger for the current thread based on the
     * {@link ThreadLocal} instance {@link #threadInformation}.
     * Is no threadlogger yet has been initialized or the current
     * is disposed (the {@link #clean()} are already called) it
     * will be call {@link #createInstance(String)}.
     *
     * @param threadId
     * @return new threadlogger instance, never null.
     */
    public static ThreadLogger getInstance(String threadId) {
        ThreadLogger threadLogger = (ThreadLogger) threadInformation.get();
        if (threadLogger == null || threadLogger.disposed) {
            return createInstance(threadId);
        }
        return threadLogger;
    }

    static boolean isInstanceAvailable() {
        return threadInformation.get() != null;
    }

    public static MethodCall logMethodCall() {
        MethodCall methodCall = new MethodCall(+1);
        getInstance().addMethodCall(methodCall);
        return methodCall;
    }

    public static MethodCall logMethodCall(String note) {
        MethodCall methodCall = new MethodCall(+1, note);
        getInstance().addMethodCall(methodCall);
        return methodCall;
    }

    /**
     * ID identifiying the currently traced thread
     */
    private final String threadId;
    /**
     * A list of method calls in the order of their call time.
     */
    private LinkedList methodCalls = new LinkedList();
    /**
     * True if this threadlogger instance is already cleaned.
     */
    private boolean disposed = false;

    private ThreadLogger(String threadId) {
        this.threadId = threadId;
    }

    public void addMethodCall(MethodCall methodCall) {
        if (methodCall != null) {
            methodCalls.addLast(methodCall);
            if (methodCalls.size() >= 1024) {
                throw new StackOverflowError();
            }
        }
    }

    /**
     * returns the id associated to the current thread by this logger.
     * Do not mess this up with the internal java-thread-id!
     */
    public String getThreadId() {
        return threadId;
    }

    /**
     * Clean this threadlogger instance.
     */
    public void clean() {
        disposed = true;
        methodCalls.clear();
    }

    protected void finalize() throws Throwable {
        clean();
        super.finalize();
    }

    public List getMethodCalls() {
        return methodCalls;
    }

    /**
     * Human readable string output. :o)
     */
    public String toString() {
        return "Log for thread \"" + getThreadId() + "\""
                + (disposed ? " (disposed):" : ":")
                + StringTools.LINE_SEPARATOR + StringTools.LINE_SEPARATOR
                + Tools.iteratorToString(methodCalls.iterator(), "", true, StringTools.LINE_SEPARATOR, true);
    }
}
