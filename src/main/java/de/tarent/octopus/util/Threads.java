package de.tarent.octopus.util;

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
/**
 * Diese Klasse stellt Hilfsmethoden für das Multi-Threading zur Verfügung.
 * 
 * @author mikel
 */
public class Threads {
    /**
     * Diese statische Methode setzt den Kontext-{@link ClassLoader} des
     * aktuellen {@link Thread}s und gibt den bisherigen zurück.
     * 
     * @param newLoader der neue {@link ClassLoader}; darf nicht <code>null</code>
     *  sein.
     * @return der bisherige {@link ClassLoader}.
     */
    public static ClassLoader setContextClassLoader(ClassLoader newLoader) {
//        if (newLoader == null)
//            throw new NullPointerException("ClassLoader instance is required.");
        Thread currentThread = Thread.currentThread();
        ClassLoader currentLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(newLoader);
        return currentLoader;
    }
}
