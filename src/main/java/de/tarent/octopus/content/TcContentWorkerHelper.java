/* $Id: TcContentWorkerHelper.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * Created on 27.10.2003
 */
package de.tarent.octopus.content;

import de.tarent.octopus.request.TcRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Diese Klasse enth�lt Hilfsmethoden f�r ContentWorker.
 * 
 * @author mikel
 */
public class TcContentWorkerHelper {
    /**
     * Diese Methode liefert einen Parameter zur�ck, wobei zun�chst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schl�ssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static Object getParam(TcRequest tcRequest, TcContent tcContent, String key) {
        Object param = tcContent.getAsObject(key);
        return (param == null) ? tcRequest.getParam(key) : param;
    }

    /**
     * Diese Methode liefert einen Parameter als Liste, wobei zun�chst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schl�ssel des gesuchten Parameters
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
     * Diese Methode liefert einen Parameter als String, wobei zun�chst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schl�ssel des gesuchten Parameters
     * @return Wert des Parameters
     */
    public static String getParamAsString(TcRequest tcRequest, TcContent tcContent, String key) {
        String param = tcContent.getAsString(key);
        return (param == null) ? tcRequest.get(key) : param;
    }

    /**
     * Diese Methode liefert einen Parameter als int, wobei zun�chst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schl�ssel des gesuchten Parameters
     * @param defaultValue default-Wert f�r den Parameter
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
     * Diese Methode liefert einen Parameter als boolean, wobei zun�chst im
     * Content, dann im Request gesucht wird.
     * 
     * @param tcRequest Request-Objekt
     * @param tcContent Content-Objekt
     * @param key Schl�ssel des gesuchten Parameters
     * @param defaultValue default-Wert f�r den Parameter
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
