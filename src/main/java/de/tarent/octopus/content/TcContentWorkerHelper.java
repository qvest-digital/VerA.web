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

import de.tarent.octopus.request.TcRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Diese Klasse enthält Hilfsmethoden für ContentWorker.
 * 
 * @author mikel
 */
public class TcContentWorkerHelper {
    /**
     * Diese Methode liefert einen Parameter zurück, wobei zunächst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schlüssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static Object getParam(TcRequest tcRequest, TcContent tcContent, String key) {
        Object param = tcContent.getAsObject(key);
        return (param == null) ? tcRequest.getParam(key) : param;
    }

    /**
     * Diese Methode liefert einen Parameter als Liste, wobei zunächst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schlüssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static List getParamAsList(TcRequest tcRequest, TcContent tcContent, String key) {
        Object param = tcContent.get(key);
        if (param == null)
            param = tcRequest.getParam(key);
        List result = null;
        if (param instanceof List)
            result = (List) param;
        else if (param instanceof Object[])
            result = Arrays.asList((Object[]) param);
        else {
            result = new ArrayList();
            if (param != null)
                result.add(param);
        }
        return result;
    }

    /**
     * Diese Methode liefert einen Parameter als String, wobei zunächst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schlüssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static String getParamAsString(TcRequest tcRequest, TcContent tcContent, String key) {
        String param = tcContent.getAsString(key);
        return (param == null) ? tcRequest.get(key) : param;
    }

    /**
     * Diese Methode liefert einen Parameter als int, wobei zunächst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schlüssel des gesuchten Parameters
     * @param defaultValue default-Wert für den Parameter
     * @return Wert des Parameters
     */
    public static int getParamAsInt(TcRequest tcRequest, TcContent tcContent, String key, int defaultValue) {
        String stringVal = getParamAsString(tcRequest, tcContent, key);
        if (stringVal != null)
            try {
                return Integer.parseInt(stringVal);
            } catch (NumberFormatException e) {
            }
        return defaultValue;
    }

    /**
     * Diese Methode liefert einen Parameter als boolean, wobei zunächst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schlüssel des gesuchten Parameters
     * @param defaultValue default-Wert für den Parameter
     * @return Wert des Parameters
     */
    public static boolean getParamAsBoolean(
        TcRequest tcRequest,
        TcContent tcContent,
        String key,
        boolean defaultValue) {
        String stringVal = getParamAsString(tcRequest, tcContent, key);
        if (stringVal != null)
            return Boolean.valueOf(stringVal).booleanValue();
        return defaultValue;
    }

    /**
     * Dieser private Konstruktor verbietet das instanziieren dieser Klasse.
     *
     */
    private TcContentWorkerHelper() {
        super();
    }
}
