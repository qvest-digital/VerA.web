/* $Id: TcResponse.java,v 1.6 2006/12/04 13:05:44 jens Exp $
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

package de.tarent.octopus.request;

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
     * @param param Zusätzliche Caching Paramater. Null entspricht default verhalten.
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
     *  TODO: Eigentlich macht es keinen Sinn, dass
     *        die response irgend eine Engine kennt.
     *        Warum wurde das gemacht?
     */
    public void setSoapEngine(TcSOAPEngine soapEngine);

    public TcSOAPEngine getSoapEngine();

    public void setTaskName(String taskName);

    public String getTaskName();

    public void setModuleName(String moduleName);

    public String getModuleName();

    /**
     * Returns the outputStream.
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
     * @param requestID die ID der Anfrage
     * @param header eine Überschrift (nur für HTML benutzt)
     * @param e eine Exception
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
