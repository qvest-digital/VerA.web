package de.tarent.octopus.request;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import de.tarent.octopus.soap.TcSOAPEngine;

/**
 * Stellt Funktionen zur Ausgabe an den Client bereit.
 * Diese werden an die HttpServletResponse weiter geleitet.
 * <br><br>
 * Bevor etwas ausgegeben werden kann, muss der ContentType gesetzt werden.
 *
 * <br><br>
 * Es mag verwirren, daß die Klasse TcResponse im Package tcRequest ist und nicht im Package tcResponse
 * wo sie dem Namen nach hin gehört. Das macht aber so Sinn, da sie wie auch TcRequestProxy und TcRequest
 * die Schnittstelle zum Client kapselt und somit protokollspezifisches Verhalten hat, vondem in
 * allen anderen Packages völlig abstrahiert wird.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface TcResponse {

    /**
     * Gibt einen Writer für die Ausgabe zurück.
     * <br><br>
     * Bevor etwas ausgegeben werden kann, muss der ContentType gesetzt werden.
     */
    public PrintWriter getWriter();

    /**
     * Setzt den Mime-Type für die Ausgabe.
     * Das muss passiert sein, bevor etwas ausgegeben wurde.
     */
    public void setContentType(String contentType);

    /**
     * Setzt den Status für die Ausgabe.
     * Das muss passiert sein, bevor etwas ausgegeben wurde.
     */
    public void setStatus(int code);

    /**
     * Setzt das Ausgabe-Level für geworfene Fehlermeldungen.
     */
    public void setErrorLevel(String errorLevel);

    /**
     * Setzt einen Header-Parameter.
     * Das muss passiert sein, bevor etwas ausgegeben wurde.
     */
    public void setHeader(String key, String value);

    /**
     * Diese Methode setzt die maximale Verweildauer der Antwort in einem Cache.
     *
     * @param millis maximale Caching-Dauer in Millisekunden. Negative Werte verbieten ein Caching.
     */
    public void setCachingTime(int millis);

    /**
     * Diese Methode setzt die maximale Verweildauer der Antwort in einem Cache.
     *
     * @param millis maximale Caching-Dauer in Millisekunden. Negative Werte verbieten ein Caching.
     * @param param  Zusätzliche Caching Paramater. Null entspricht default verhalten.
     */
    public void setCachingTime(int millis, String param);

    /**
     * Diese Methode liefert die maximale Verweildauer der Antwort in einem Cache
     *
     * @return maximale Caching-Dauer in Millisekunden. Negative Werte verbieten ein Caching.
     */
    public int getCachingTime();

    /**
     * Adds a cookie to the response.
     * Default cookie setting can be set in the config.
     * See {@link de.tarent.octopus.content.CookieMap} for detailed settings.
     *
     * @param name
     * @param value
     * @param settings
     */
    public void addCookie(String name, String value, Map settings);

    /**
     * Adds a cookie to the response.
     *
     * Because the dispatched classes in the octopus-core
     * does not know the Servlet-API and the Cookie-Object
     * this method accepts an Object as parameter and
     * adds this to cookies in case it is a Cookie-Object.
     *
     * @param cookie
     */
    public void addCookie(Object cookie);

    /**
     * TODO: Eigentlich macht es keinen Sinn, dass
     * die response irgend eine Engine kennt.
     * Warum wurde das gemacht?
     */
    public void setSoapEngine(TcSOAPEngine soapEngine);

    public TcSOAPEngine getSoapEngine();

    public void setTaskName(String taskName);

    public String getTaskName();

    public void setModuleName(String moduleName);

    public String getModuleName();

    /**
     * Returns the outputStream.
     *
     * @return OutputStream
     */
    public OutputStream getOutputStream();

    // REMOVED: der OutputStream sollte nur auf konkrete art in einer
    //          Implementierung gesetzt werden dürfen.
    //     /**
    //      * Sets the outputStream.
    //      * @param outputStream The outputStream to set
    //      */
    //     public void setOutputStream(OutputStream outputStream);

    /**
     * Gibt einen String auf den Weiter aus.
     */
    public void print(String responseString);

    /**
     * Gibt einen String + "\n" auf den Weiter aus.
     */
    public void println(String responseString);

    /**
     * Diese Methode sendet gepufferte Ausgaben.
     */
    public void flush()
      throws IOException;

    /**
     * Diese Methode schließt die Ausgabe ab.
     */
    public void close()
      throws IOException;

    /**
     * Diese Methode gibt einen Fehlerstatus aus.
     * Je nach übergebenen Typ und aufrufer geschieht dies in
     * HTML-, SOAP- oder sonstiger form.
     *
     * @param responseType Antwortart, vergleiche HttpHelper im OctopusWebapp-Projekt.
     * @param requestID    die ID der Anfrage
     * @param header       eine Überschrift (nur für HTML benutzt)
     * @param e            eine Exception
     */
    public void sendError(int responseType, String requestID, String header, Exception e);

    /**
     * Teilt der Response mit, dass ein Login oder eine andere Authorisierung für
     * die gewünschte Aktion erforderlich wäre.
     *
     * @param authorisationAction Vorschlag, wie die Autentifizierung erfolgen soll.
     */
    public void setAuthorisationRequired(String authorisationAction);
}
