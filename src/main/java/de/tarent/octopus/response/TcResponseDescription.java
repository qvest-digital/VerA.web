package de.tarent.octopus.response;

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

/** Objekt zur Beschreibung der Ausgabeseite für verscheidenen Ausgabeformate.
 *  @author <a href="mailto:mancke@mancke-software.de">Sebastian Mancke</a>, <b>tarent GmbH</b>
 *  @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public class TcResponseDescription {
    private String descName;
    private String responseType;

    /** Inititalisiert die Antwortbeschreibung
     *  @param descName Name der Response
     *  @param responseType Typ, von der die Response sein soll (z.B. velocityPage oder simplePage)
     */
    public TcResponseDescription(String descName, String responseType) {
        this.descName = descName;
        this.responseType = responseType;
    }

    /** Gibt den Typ zurück, von dem die Ausgabeseite sein soll.
     * Damit kann das Modul für die Ausgabegenerierung bestimmt werden.
     */
    public String getDescName() {
        return descName;
    }

    /** Gibt den Namen zurück, den das Modul für die Ausgabegenerierung benutzt.
     */
    public String getResponseType() {
        return responseType;
    }
}
