package de.tarent.octopus.content;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcRequest;

/**
 * Jeglicher Zugriff auf Daten erfolgt über diese TcContentWorker.
 *
 * @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 */
public interface TcContentWorker {
    /**
     * Diese Methode wird nach Erzeugung des Workers aufgerufen, so dass dieser
     * sich im Kontext seines Moduls konfigurieren kann.
     *
     * @param config Modulkonfiguration.
     */
    public void init(TcModuleConfig config);

    /**
     * Abarbeiten einer Action mit diesem ContentWorker
     * Die Ergebnisse werden in dem tcContent-Kontainer abgelegt.
     * Ein ContentWorker kann für mehrere Actions zuständig sein.
     *
     * @param tcConfig Konfiguration
     * @param actionName Name der Aktion, die von diesem Worker ausgeführt werden soll.
     * @param tcRequest Die Anfragedaten
     * @param tcContent Der Content-Kontainer, in dem die Daten abgelegt werden können.
     * @return String mit einem Statuscode z.B. ok oder error
     */
    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
        throws TcContentProzessException;

    /**
     * Liefert eine Beschreibgung der Actions und deren Eingabeparameter,
     * die von diesem Worker bereit gestellt werden.
     *
     * @return Eine Abstrakte Beschreibung der Methoden und Parameter
     */
    public TcPortDefinition getWorkerDefinition();

    /**
     * Diese Methode liefert einen Versionseintrag.
     *
     * @return Version des Workers.
     */
    public String getVersion();

    /**
     * Standard-Ergebnis für den Erfolgsfall.
     */
    public final static String RESULT_ok = "ok";

    /**
     * Standard-Ergebnis für den Fehlerfall.
     */
    public final static String RESULT_error = "error";
}
