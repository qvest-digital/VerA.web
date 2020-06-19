package de.tarent.octopus.content;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
     *                         Fehler kurz Charakterisiert.
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
     *                         Fehler kurz Charakterisiert.
     * @return Die neu erzeugte FaultMessage
     */
    public TcMessageDefinition addFaultMessage(String faultStatusCode, String faultDescription) {
        return addFaultMessage(faultStatusCode, faultDescription, new TcMessageDefinition());
    }
}
