package de.tarent.octopus.content;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Eine MessageDefinition ist eine Auflistung
 * Ein- oder Ausgabeparameter von Operationen.
 *
 * Jeder Parameter wird durch eine TcMessageDefinitionPart Objekt dar gestellt.
 *
 * Eine Ausführlichere Beschreibung des Contextes ist in TcPortDefinition
 *
 * @see TcPortDefinition
 */
public class TcMessageDefinition {
    /**
     * Constante für den Datentyp Scalar.
     * Ein Scalar ist ein Parameter, der beliebige einteilige Daten aufnehmen kann (z.B. integer, string, float, ...)
     */
    public static final String TYPE_SCALAR = "xsd:string";

    /**
     * Constante für den Datentyp Array mit beliebigen Elementen.
     * Ein Array ist ein Parameter, der eine Liste von anderen Parametern aufnehmen kann.
     */
    public static final String TYPE_ARRAY = "xsd:array";

    /**
     * Constante für den Datentyp Struct.
     * Ein Struct ist ein Parameter, der eine Map (z.B. wie eine Java Map) aufnehmen kann.
     * Darin können also andere Parameter mit ihren Namen als Keys abgelegt werden.
     */
    public static final String TYPE_STRUCT = "tc:struct";

    private List parts;

    /**
     * Initialisierung mit einer leeren Menge von Parametern
     */
    public TcMessageDefinition() {
        parts = new ArrayList();
    }

    /**
     * Erzeugt einen neuen Parameter und fügt diesen an.
     *
     * @param newPart Der neue Parameter
     */
    public TcMessageDefinition addPart(TcMessageDefinitionPart newPart) {
        int size = parts.size();
        for (int i = 0; i < size; i++) {
            TcMessageDefinitionPart curr = (TcMessageDefinitionPart) parts.get(i);
            if (curr.getName().equals(newPart.getName())) {
                parts.remove(i);
                break;
            }
        }
        parts.add(newPart);
        return this;
    }

    /**
     * Erzeugt einen neuen Parameter und fügt diesen an.
     *
     * @param name         Name des Parameters
     * @param partDataType Datentyp des Parameters als XML-Schema-Type. Die Konstanten der Klassen können und sollen benutzt
     *                     werden.
     * @param description  Beschreibung des Parameters
     * @param optional     Flag, ob der Parameter optional sein soll
     */
    public TcMessageDefinition addPart(String name, String partDataType, String description, boolean optional) {
        addPart(new TcMessageDefinitionPart(name, partDataType, description, optional));
        return this;
    }

    /**
     * Erzeugt einen neuen Parameter und fügt diesen an.
     * Optional wird auf false gesetzt.
     *
     * @param name         Name des Parameters
     * @param partDataType Datentyp des Parameters als XML-Schema-Type. Die Konstanten der Klassen können und sollen benutzt
     *                     werden.
     * @param description  Beschreibung des Parameters
     */
    public TcMessageDefinition addPart(String name, String partDataType, String description) {
        addPart(name, partDataType, description, false);
        return this;
    }

    /**
     * Fügt eine Liste von neuen Parametern an.
     *
     * @param addParts Vector mit Elementen vom Type TcMessageDefinitionPart
     */
    public TcMessageDefinition addParts(List addParts) {
        int size = addParts.size();
        for (int i = 0; i < size; i++) {
            addPart((TcMessageDefinitionPart) addParts.get(i));
        }
        return this;
    }

    /**
     * Liefert die TcMessageDefinitionParts. Also die Parameter dieser Message
     *
     * @return Vector mit TcMessageDefinitionPart Objekten
     */
    public List getParts() {
        return parts;
    }

    /**
     * Liefert die den TcMessageDefinitionPart mit dem entsprechenden Namen.
     *
     * @return TcMessageDefinitionPart mit dem Namen oder NULL,
     * wenn es keinen solchen gibt.
     */
    public TcMessageDefinitionPart getPartByName(String partName) {
        for (int i = 0; i < parts.size(); i++) {
            TcMessageDefinitionPart curr = (TcMessageDefinitionPart) parts.get(i);
            if (curr.getName().equals(partName)) {
                return curr;
            }
        }
        return null;
    }

    public String toString() {
        return parts.toString();
    }
}
