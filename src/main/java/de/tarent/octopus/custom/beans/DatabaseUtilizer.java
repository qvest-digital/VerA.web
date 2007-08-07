/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
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
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * Created on 22.08.2005
 */
package de.tarent.octopus.custom.beans;

/**
 * Diese Schnittstelle beschreibt allgemein Objekte, die eine
 * {@link Database}-Instanz ben�tigen.
 * 
 * @author mikel
 */
public interface DatabaseUtilizer {
    /**
     * Die zu nutzende Datenbank<br>
     * TODO: Umstellen auf Nutzung eines {@link ExecutionContext}s
     */
    void setDatabase(Database database);
    
    /**
     * Die zu nutzende Datenbank<br>
     * TODO: Umstellen auf Nutzung eines {@link ExecutionContext}s
     */
    Database getDatabase();
}
