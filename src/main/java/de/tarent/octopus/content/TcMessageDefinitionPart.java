/* $Id: TcMessageDefinitionPart.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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
 * by Sebastian Mancke and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

/**
 * Ein TcMessageDefinitionPart ist ein Parameter einer TcMessageDefinition 
 *
 * @see TcMessageDefinition
 * @see TcPortDefinition
 */
public class TcMessageDefinitionPart {
    private String name;
    private String description;
    private String partDataType;
    private boolean optional;

    /**
     * Erzeugt einen neuen Parameter
     * @param name Name des Parameters
     * @param partDataType Datentyp des Parameters als XML-Schema-Type. Die Konstanten der Klassen können und sollen benutzt werden.
     * @param description Beschreibung des Parameters
     * @param optional Flag, ob der Parameter optional sein soll
     */
    public TcMessageDefinitionPart(String name, String partDataType, String description, boolean optional) {
        this.name = name;
        this.description = description;
        this.partDataType = partDataType;
        this.optional = optional;
    }

    /**
     * Liefert den Namen dieses Parts
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setzt den Namen dieses Parts
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * Liefert die Beschreibung dieses Parts
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Liefert den Datentyp dieses Parts
     */
    public String getPartDataType() {
        return this.partDataType;
    }

    /**
     * Liefert das Optional Flag dieses Parts
     */
    public boolean isOptional() {
        return this.optional;
    }

    public String toString() {
        return "( " + name + ", " + description + ", " + partDataType + ", " + optional + " )";
    }
}
