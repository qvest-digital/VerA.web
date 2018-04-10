/*
 * $Id: BeanStatement.java,v 1.3 2007/06/11 13:24:36 christoph Exp $
 *
 * Created on 20.01.2006
 */
package de.tarent.octopus.beans;

/**
 * Diese Schnittstelle beschreibt gekapselte PreparedStatements, die auf
 * Beans arbeiten.
 *
 * @author mikel
 */
public interface BeanStatement {
    /**
     * Diese Methode führt das Statement auf der übergebenen Bean aus.
     *
     * @param bean Bean, auf der das Statement ausgeführt wird.
     * @return Rückgabe von Updates etc.
     */
    int execute(Bean bean) throws BeanException;
}
