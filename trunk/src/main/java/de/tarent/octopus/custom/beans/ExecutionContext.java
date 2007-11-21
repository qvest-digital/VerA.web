/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * $Id: ExecutionContext.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 24.11.2005
 */
package de.tarent.octopus.custom.beans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.statement.Select;

/**
 * Diese Schnittstelle beschreibt einen Kontext, der SQL-Statements ausführen kann. 
 * 
 * @author mikel
 */
public interface ExecutionContext {
    /**
     * Diese Methode führt das übergebene {@link Statement} aus.
     * 
     * @param statement auszuführendes {@link Statement}
     * @throws BeanException 
     */
    public void execute(Statement statement) throws BeanException;
    
    /**
     * Diese Methode führt das übergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zurückgegeben wird. 
     * 
     * @param statement  auszuführendes {@link Select}-{@link Statement}
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     */
    public ResultSet result(Select statement) throws BeanException;
    
    /**
     * Diese Methode bereitet das übergebene {@link Statement} vor.
     * 
     * @param statement vorzubereitendes {@link Statement}
     * @return resultierendes {@link PreparedStatement}
     * @throws BeanException
     */
    public PreparedStatement prepare(Statement statement) throws BeanException;
    
    /**
     * Diese Methode liefert die {@link Database}, in der dieser Kontext arbeitet. 
     * 
     * @return zugehörige {@link Database}
     */
    public Database getDatabase(); 
}
