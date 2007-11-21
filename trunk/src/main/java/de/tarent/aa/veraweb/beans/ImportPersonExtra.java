/*
 * $Id: ImportPersonExtra.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 15.09.2005
 */
package de.tarent.aa.veraweb.beans;

/**
 * Diese Schnittstelle markiert Beans, die Erg�nzungen zu ImportPersons darstellen. 
 * 
 * @author mikel
 */
public interface ImportPersonExtra {
    /**
     * Diese Methode assoziiert diese Importpersonenerg�nzung mit ihrer Importperson;
     * dies bedeutet insbesondere die �bernahme deren ID als Fremdschl�ssel auf sie. 
     * 
     * @param person Importperson, mit der dieses Extra assoziiert werden soll.
     */
    void associateWith(ImportPerson person);
}
