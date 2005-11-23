/* $Id: ContentWorkerDeclaration.java,v 1.1 2005/11/23 08:32:40 asteban Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Sebastian Mancke
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */
package de.tarent.octopus.config;

/** 
 * Klasse zur Kapselung der Informaionen zu einem ContentWorker.
 * 
 * @see TcContentWorker
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public class ContentWorkerDeclaration {

    String workerName;
    String implementationSource;
    String factory;
    boolean singletonInstantiation = true;


    /**
     * Gibt an ob von dem Worker immer neue Instanzen erzeugt werden sollen,
     * oder immer die gleiche verwendet wird. Default ist singletonInstantiation = true
     */
    public final boolean isSingletonInstantiation() {
        return singletonInstantiation;
    }

    public final void setSingletonInstantiation(final boolean newSingletonInstantiation) {
        this.singletonInstantiation = newSingletonInstantiation;
    }


    /**
     * Liefert die Factory, von der dieser Worker erzeugt werden kann.
     * @return Voll qualifizierter Klassenname der Factory
     */
    public final String getFactory() {
        return factory;
    }

    public final void setFactory(final String newFactory) {
        this.factory = newFactory;
    }


    /** 
     * Liefert die Factoryspezifische Quelle des Workers.
     * Dies kann z.B. der Klassenname oder Scriptname des Workers sein.
     */
    public final String getImplementationSource() {
        return implementationSource;
    }

    public final void setImplementationSource(final String newImplementationSource) {
        this.implementationSource = newImplementationSource;
    }



    /**
     * Eindeutiger Bezeichner, unter dem den Worker in dem Modul verfügbar ist.
     */
    public final String getWorkerName() {
        return workerName;
    }

    public final void setWorkerName(final String newWorkerName) {
        this.workerName = newWorkerName;
    }
    
}
