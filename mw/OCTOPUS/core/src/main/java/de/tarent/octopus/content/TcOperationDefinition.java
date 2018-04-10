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
import java.util.HashMap;
import java.util.Map;

/**
 * Beschreibgung einer Operation eines Ports.
 * Diese OperationDefinition enthält Input-, Output- und FaultMessages
 *
 * Eine Ausführlichere Beschreibung ist in TcPortDefinition
 *
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
     *
     * @param name        Name der TcPortDefinition
     * @param description Sinnvolle Beschreibung des Ports
     */
    public TcOperationDefinition(String name, String description) {
        this.name = name;
        this.description = description;
        faultMessages = new HashMap();
        faultMessageDescriptions = new HashMap();
    }

    /**
     * Liefert den Namen, der im Constructor übergeben wurde.
     *
     * @return String mit dem Namen
     */
    public String getName() {
        return this.name;
    }

    /**
     * Liefert die Beschreibung, die im Constructor übergeben wurde.
     *
     * @return String mit der Beschreibung
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Liefert die InputMessage, das sind die Parameter, die diese Operation als Eingabe benötigt.
     *
     * @return TcMessageDefinition mit den Inputparametern als Inhalt
     */
    public TcMessageDefinition getInputMessage() {
        return inputMessage;
    }

    /**
     * Liefert die OutputMessage, das sind die Parameter, die diese Operation als Ausgabe liefert.
     *
     * @return TcMessageDefinition mit den Ausgabeparametern als Inhalt
     */
    public TcMessageDefinition getOutputMessage() {
        return outputMessage;
    }

    /**
     * Liefert die FaultMessages. Das ist eine Map, die für jede Fehlerrückgabe
     * der Operation einen MessageBeschreibung der Ausgabeparameter enthält.
     *
     * @return Map mit den Namen der Fehlerzustände als String Keys, und den TcMessageDefinitions dieser Fehler als Values.
     */
    public Map getFaultMessages() {
        return faultMessages;
    }

    /**
     * Liefert eine FaultMessage, zu ihrem zugehörigen Statuscode
     *
     * @param faultStatusCode Der Fehlerstatus, zu dem die Beschreibung zurück gegeben werden soll.
     * @return Die Beschreibung der Rückgabeparameter bei einem bestimmten FaultCode
     */
    public TcMessageDefinition getFaultMessage(String faultStatusCode) {
        return (TcMessageDefinition) faultMessages.get(faultStatusCode);
    }

    /**
     * Liefert die Beschreibung zu einem faultStatusCode.
     * Das soll eine Beschreibung sein, die den Fehler kurz Charakterisiert.
     *
     * @return Beschreibung der Fehlerrückgabe
     */
    public String getFaultMessageDescription(String faultStatusCode) {
        return (String) faultMessageDescriptions.get(faultStatusCode);
    }

    /**
     * Setzt die InputMessage mit einem bereits existierenden Objekt.
     * Eine Operation hat genau eine InputMessage.
     *
     * @param inputMessage Die Beschreibung der Parameter der Operation
     * @return Gibt die neu gesetzte InputMessage zurück.
     */
    public TcMessageDefinition setInputMessage(TcMessageDefinition inputMessage) {
        this.inputMessage = inputMessage;
        return inputMessage;
    }

    /**
     * Erzeugt ein neues InputMessage Objekt und fügt es dieser Operation hinzu.
     * Eine Operation hat genau eine InputMessage.
     *
     * @return Gibt die neu erzeugte InputMessage zurück.
     */
    public TcMessageDefinition setInputMessage() {
        return setInputMessage(new TcMessageDefinition());
    }

    /**
     * Setzt die OutputMessage mit einem bereits existierenden Objekt.
     * Eine Operation hat genau eine OutputMessage.
     *
     * @param outputMessage Die Beschreibung der Ausgabeparameter der Operation
     * @return Gibt die neu gesetzte OutputMessage zurück.
     */
    public TcMessageDefinition setOutputMessage(TcMessageDefinition outputMessage) {
        this.outputMessage = outputMessage;
        return outputMessage;
    }

    /**
     * Erzeugt ein neues OutputMessage Objekt und fügt es dieser Operation hinzu.
     * Eine Operation hat genau eine OutputMessage.
     *
     * @return Gibt die neu erzeugte OutputMessage zurück.
     */
    public TcMessageDefinition setOutputMessage() {
        return setOutputMessage(new TcMessageDefinition());
    }

    /**
     * Fügt eine MessageDefinition für einen bestimmten Statuscode an.
     *
     * @param faultStatusCode  Der Fehlercode zu der diese FaultMessage gehört.
     * @param faultDescription Liefert die Beschreibung zu einem faultStatusCode. Das soll eine Beschreibung sein, die den
     * Fehler kurz Charakterisiert.
     * @param faultMessage     Die Beschreibung der Ausgabeparameter der Operation bei einem Fehler mit dem faultStatusCode
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
     * Erstellt eine neue MessageDefinition und fügt sie für einen bestimmten Statuscode an.
     *
     * @param faultStatusCode  Der Fehlercode zu der diese FaultMessage gehört.
     * @param faultDescription Liefert die Beschreibung zu einem faultStatusCode. Das soll eine Beschreibung sein, die den
     * Fehler kurz Charakterisiert.
     * @return Die neu erzeugte FaultMessage
     */
    public TcMessageDefinition addFaultMessage(String faultStatusCode, String faultDescription) {
        return addFaultMessage(faultStatusCode, faultDescription, new TcMessageDefinition());
    }
}
