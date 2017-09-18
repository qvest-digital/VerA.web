package de.tarent.octopus.content;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
