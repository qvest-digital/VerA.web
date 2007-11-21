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
 * Diese Schnittstelle beschreibt einen Kontext, der SQL-Statements ausf�hren kann. 
 * 
 * @author mikel
 */
public interface ExecutionContext {
    /**
     * Diese Methode f�hrt das �bergebene {@link Statement} aus.
     * 
     * @param statement auszuf�hrendes {@link Statement}
     * @throws BeanException 
     */
    public void execute(Statement statement) throws BeanException;
    
    /**
     * Diese Methode f�hrt das �bergebene {@link Select}-{@link Statement}
     * aus und erwartet als Resultat ein {@link ResultSet}, das dann
     * zur�ckgegeben wird. 
     * 
     * @param statement  auszuf�hrendes {@link Select}-{@link Statement}
     * @return resultierendes {@link ResultSet}
     * @throws BeanException
     */
    public ResultSet result(Select statement) throws BeanException;
    
    /**
     * Diese Methode bereitet das �bergebene {@link Statement} vor.
     * 
     * @param statement vorzubereitendes {@link Statement}
     * @return resultierendes {@link PreparedStatement}
     * @throws BeanException
     */
    public PreparedStatement prepare(Statement statement) throws BeanException;
    
    /**
     * Diese Methode liefert die {@link Database}, in der dieser Kontext arbeitet. 
     * 
     * @return zugeh�rige {@link Database}
     */
    public Database getDatabase(); 
}
