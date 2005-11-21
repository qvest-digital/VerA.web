/*
 * $Id: Threads.java,v 1.1.1.1 2005/11/21 13:33:38 asteban Exp $
 * 
 * Created on 04.07.2005
 */
package de.tarent.octopus.util;

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
