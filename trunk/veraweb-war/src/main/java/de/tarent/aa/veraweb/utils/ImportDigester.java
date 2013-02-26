/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.List;

import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonExtra;
import de.tarent.octopus.beans.BeanException;

/**
 * Diese Schnittstelle stellt bei einem Import die Instanz dar, der ein
 * {@link Importer} neue Personendatens�tze anliefert.  
 * 
 * @author mikel
 */
public interface ImportDigester {
    /**
     * Diese Methode wird zu Beginn eines Imports aufgerufen.
     * @throws BeanException 
     */
    public void startImport() throws BeanException;
    
    /**
     * Diese Methode wird zum Ende eines Imports aufgerufen.
     * @throws BeanException 
     */
    public void endImport() throws BeanException;
    
    /**
     * Diese Methode wird von einem {@link Importer} zu jeder zu importierenden
     * Person aufgerufen, �bergeben wird die Person und eine Liste mit Beans,
     * die Zus�tze zur Person darstellen.<br>
     * Falls Abh�ngigkeiten unter diesen Beans bestehen, stehen in der
     * Liste die Beans, von der eine bestimmte Bean abh�ngt, vor dieser. 
     * 
     * @param person eine {@link ImportPerson}-Instanz
     * @param extras eine Liste mit Beans, die Zus�tze zur Person darstellen; es
     *  werden nur solche akzeptiert, die {@link ImportPersonExtra} implementieren.
     * @throws BeanException 
     * @throws IOException 
     */
    public void importPerson(ImportPerson person, List extras) throws BeanException, IOException;
}
