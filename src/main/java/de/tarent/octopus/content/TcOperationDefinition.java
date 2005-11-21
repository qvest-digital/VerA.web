/* $Id: TcOperationDefinition.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
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

import java.util.HashMap;
import java.util.Map;

/**
 * Beschreibgung einer Operation eines Ports.
 * Diese OperationDefinition enth�lt Input-, Output- und FaultMessages
 * 
 * Eine Ausf�hrlichere Beschreibung ist in TcPortDefinition
 * @see TcPortDefinition
 */
public class TcOperationDefinition {
    private String name;
    private String description;

    private TcMessageDefinition inputMessage;
    private TcMessageDefinition outputMessage;
    private Map faultMessages;
    private Map faultMessageDescriptions;

    /**
     * Initialisiert eine OperationDefinition
     * @param name Name der TcPortDefinition
     * @param description Sinnvolle Beschreibung des Ports
     */
    public TcOperationDefinition(String name, String description) {
        this.name = name;
        this.description = description;
        faultMessages = new HashMap();
        faultMessageDescriptions = new HashMap();
    }

    /**
     * Liefert den Namen, der im Constructor �bergeben wurde.
     * @return String mit dem Namen
     */
    public String getName() {
        return this.name;
    }

    /**
     * Liefert die Beschreibung, die im Constructor �bergeben wurde.
     * @return String mit der Beschreibung
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Liefert die InputMessage, das sind die Parameter, die diese Operation als Eingabe ben�tigt.
     * @return TcMessageDefinition mit den Inputparametern als Inhalt
     */
    public TcMessageDefinition getInputMessage() {
        return inputMessage;
    }

    /**
     * Liefert die OutputMessage, das sind die Parameter, die diese Operation als Ausgabe liefert.
     * @return TcMessageDefinition mit den Ausgabeparametern als Inhalt
     */
    public TcMessageDefinition getOutputMessage() {
        return outputMessage;
    }

    /**
     * Liefert die FaultMessages. Das ist eine Map, die f�r jede Fehlerr�ckgabe
     * der Operation einen MessageBeschreibung der Ausgabeparameter enth�lt.
     * @return Map mit den Namen der Fehlerzust�nde als String Keys, und den TcMessageDefinitions dieser Fehler als Values.
     */
    public Map getFaultMessages() {
        return faultMessages;
    }

    /**
     * Liefert eine FaultMessage, zu ihrem zugeh�rigen Statuscode
     * @param faultStatusCode Der Fehlerstatus, zu dem die Beschreibung zur�ck gegeben werden soll.
     * @return Die Beschreibung der R�ckgabeparameter bei einem bestimmten FaultCode
     */
    public TcMessageDefinition getFaultMessage(String faultStatusCode) {
        return (TcMessageDefinition) faultMessages.get(faultStatusCode);
    }

    /**
     * Liefert die Beschreibung zu einem faultStatusCode.
     * Das soll eine Beschreibung sein, die den Fehler kurz Charakterisiert.
     * @return Beschreibung der Fehlerr�ckgabe
     */
    public String getFaultMessageDescription(String faultStatusCode) {
        return (String) faultMessageDescriptions.get(faultStatusCode);
    }

    /**
     * Setzt die InputMessage mit einem bereits existierenden Objekt.
     * Eine Operation hat genau eine InputMessage.
     * @param inputMessage Die Beschreibung der Parameter der Operation
     * @return Gibt die neu gesetzte InputMessage zur�ck.
     */
    public TcMessageDefinition setInputMessage(TcMessageDefinition inputMessage) {
        this.inputMessage = inputMessage;
        return inputMessage;
    }

    /**
     * Erzeugt ein neues InputMessage Objekt und f�gt es dieser Operation hinzu.
     * Eine Operation hat genau eine InputMessage.
     * @return Gibt die neu erzeugte InputMessage zur�ck.
     */
    public TcMessageDefinition setInputMessage() {
        return setInputMessage(new TcMessageDefinition());
    }

    /**
     * Setzt die OutputMessage mit einem bereits existierenden Objekt.
     * Eine Operation hat genau eine OutputMessage.
     * @param outputMessage Die Beschreibung der Ausgabeparameter der Operation
     * @return Gibt die neu gesetzte OutputMessage zur�ck.
     */
    public TcMessageDefinition setOutputMessage(TcMessageDefinition outputMessage) {
        this.outputMessage = outputMessage;
        return outputMessage;
    }

    /**
     * Erzeugt ein neues OutputMessage Objekt und f�gt es dieser Operation hinzu.
     * Eine Operation hat genau eine OutputMessage.
     * @return Gibt die neu erzeugte OutputMessage zur�ck.
     */
    public TcMessageDefinition setOutputMessage() {
        return setOutputMessage(new TcMessageDefinition());
    }

    /**
     * F�gt eine MessageDefinition f�r einen bestimmten Statuscode an.
     * @param faultStatusCode Der Fehlercode zu der diese FaultMessage geh�rt.
     * @param faultDescription Liefert die Beschreibung zu einem faultStatusCode. Das soll eine Beschreibung sein, die den Fehler kurz Charakterisiert.
     * @param faultMessage Die Beschreibung der Ausgabeparameter der Operation bei einem Fehler mit dem faultStatusCode
     * @return Die gesetzte FaultMessage
     */
    public TcMessageDefinition addFaultMessage(
        String faultStatusCode,
        String faultDescription,
        TcMessageDefinition faultMessage) {

        faultMessages.put(faultStatusCode, faultMessage);
        faultMessageDescriptions.put(faultStatusCode, faultDescription);
        return faultMessage;
    }

    /**
     * Erstellt eine neue MessageDefinition und f�gt sie f�r einen bestimmten Statuscode an.
     * @param faultStatusCode Der Fehlercode zu der diese FaultMessage geh�rt.
     * @param faultDescription Liefert die Beschreibung zu einem faultStatusCode. Das soll eine Beschreibung sein, die den Fehler kurz Charakterisiert.
     * @return Die neu erzeugte FaultMessage
     */
    public TcMessageDefinition addFaultMessage(String faultStatusCode, String faultDescription) {
        return addFaultMessage(faultStatusCode, faultDescription, new TcMessageDefinition());
    }
}
