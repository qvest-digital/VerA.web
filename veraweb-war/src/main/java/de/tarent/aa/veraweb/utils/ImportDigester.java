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
 * $Id$
 * 
 * Created on 16.06.2005
 */
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.List;

import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonExtra;
import de.tarent.octopus.beans.BeanException;

/**
 * Diese Schnittstelle stellt bei einem Import die Instanz dar, der ein
 * {@link Importer} neue Personendatensätze anliefert.  
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
     * Person aufgerufen, übergeben wird die Person und eine Liste mit Beans,
     * die Zusätze zur Person darstellen.<br>
     * Falls Abhängigkeiten unter diesen Beans bestehen, stehen in der
     * Liste die Beans, von der eine bestimmte Bean abhängt, vor dieser. 
     * 
     * @param person eine {@link ImportPerson}-Instanz
     * @param extras eine Liste mit Beans, die Zusätze zur Person darstellen; es
     *  werden nur solche akzeptiert, die {@link ImportPersonExtra} implementieren.
     * @throws BeanException 
     * @throws IOException 
     */
    public void importPerson(ImportPerson person, List extras) throws BeanException, IOException;
}
