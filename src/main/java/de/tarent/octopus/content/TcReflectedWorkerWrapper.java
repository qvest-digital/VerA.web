package de.tarent.octopus.content;

/*-
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.server.OctopusContext;

/**
 * Reflection Worker, der als Wrapper zu einer Generischen Java-Klasse fungiert.
 * 
 * @author Sebastian Mancke
 */
public class TcReflectedWorkerWrapper 
    
    extends AbstractWorkerWrapper {

    Log logger = LogFactory.getLog(getClass());

    // Feldkonstanten für die Metadaten
    public static final String FIELD_NAME_PREFIX_INPUT = "INPUT_";
    public static final String FIELD_NAME_PREFIX_OUTPUT = "OUTPUT_";
    public static final String FIELD_NAME_PREFIX_MANDORITY = "MANDATORY_";

    public static final String FIELD_NAME_VERSION = "VERSION";

    public static final Class TYPE_STRING = String.class;
    public static final Class TYPE_STRING_ARRAY = String[].class;
    public static final Class TYPE_BOOLEAN_ARRAY = boolean[].class;

        
    
    public TcReflectedWorkerWrapper(Object workerDelegate) {
        super(workerDelegate);
    }


    public String getVersion() {
        try {
            return (String)workerClass.getField(FIELD_NAME_VERSION).get(workerDelegate);
		} catch (NoSuchFieldException nf) {
			logger.debug("Für den Worker "+workerClass.getName()+" wurde keine Version angegeben.");
            return "1.0";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
            throw new RuntimeException("Fehler beim Ermitteln der Version von "+workerClass.getName(), e);
        }    
    }


    /**
     * Holt die Metadaten zu der Action aus dem Cache, 
     * oder stellt sie über getActionData neu zusammen.
     *
     * @param actionName Name der Action
     * @return Metadaten die beschreiben, wie die Action-Methode aufgerufen werden soll.
     */
    public ActionData getActionData(String actionName) 
        throws TcActionDeclarationException {
        if (!actionDataLookup.containsKey(actionName)) {
            ActionData action = new ActionData();
            Method[] methods = workerClass.getMethods();
            
            for (int i = 0; i < methods.length; i++)
                if (actionName.equals(methods[i].getName())) {
                    action.method = methods[i];
                    break;
                }
            if (action.method == null)
                throw new TcActionDeclarationException("Serverfehler: Keine passende Methode für die Action "+actionName+" im Worker "+workerClass.getName()+" gefunden.");
            
            action.args = action.method.getParameterTypes();
            action.passOctopusContext = action.args.length > 0 
                && OctopusContext.class.equals(action.args[0]);


            action.genericArgsCount = action.args.length;
            if (action.passOctopusContext)
                action.genericArgsCount--;

            Field[] fields = workerClass.getFields();
            String inpNameLower = (FIELD_NAME_PREFIX_INPUT + actionName).toLowerCase();
            String outNameLower = (FIELD_NAME_PREFIX_OUTPUT + actionName).toLowerCase();
            String manNameLower = (FIELD_NAME_PREFIX_MANDORITY + actionName).toLowerCase();
           
            // Beachte: Bei den Feldnamen wird nicht zwischen Gross- und Kleinschreibung
            // unterschieden. Deswegen müssen alle vorhandenen Fields überprüft werden.
            try {
                for (int i = 0; i < fields.length; i++) {
                    String fNameLower = fields[i].getName().toLowerCase();
                    if (inpNameLower.equals(fNameLower)
                        && TYPE_STRING_ARRAY.equals(fields[i].getType()))
                        action.inputParams = (String[])fields[i].get(workerDelegate);
                    
                    else if (outNameLower.equals(fNameLower)
                             && TYPE_STRING.equals(fields[i].getType()))
                        action.outputParam = (String)fields[i].get(workerDelegate);
                    
                    else if (manNameLower.equals(fNameLower)
                             && TYPE_BOOLEAN_ARRAY.equals(fields[i].getType()))
                        
                        action.mandatoryFlags = (boolean[])fields[i].get(workerDelegate); 
                }
            } catch (IllegalAccessException ie) {
                throw new TcActionDeclarationException(ie);
            }
            if (action.inputParams == null)
                throw new TcActionDeclarationException("Serverfehler: Kein Feld "+FIELD_NAME_PREFIX_INPUT + actionName+" vom Typ String[] im Worker "+workerClass.getName()+" gefunden.");
            if (action.inputParams.length != action.genericArgsCount)
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_INPUT + actionName+" in der Klasse "+workerClass.getName()+" hat eine falsche Länge.");

            if (action.mandatoryFlags != null && action.mandatoryFlags.length != action.genericArgsCount)
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_MANDORITY + actionName+" in der Klasse "+workerClass.getName()+" hat eine falsche Länge.");
            
            if (action.mandatoryFlags == null) {
                action.mandatoryFlags = new boolean[action.genericArgsCount];
                for (int i = 0; i < action.mandatoryFlags.length; i++)
                    action.mandatoryFlags[i] = true;
            }
            
            if (action.outputParam == null 
                && ! Void.TYPE.equals(action.method.getReturnType()))
                throw new TcActionDeclarationException("Serverfehler: Kein Feld "+FIELD_NAME_PREFIX_OUTPUT + actionName+" vom Typ String im "+workerClass.getName()+" gefunden. Der Returnwert der entsprechenden Methode ist aber != void.");

            if (action.outputParam != null 
                && Void.TYPE.equals(action.method.getReturnType()))
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_OUTPUT + actionName+" ist in "+workerClass.getName()+" definiert. Der Returnwert der entsprechenden Methode ist aber void.");
            
            
            actionDataLookup.put(actionName, action);
        }
        return (ActionData)actionDataLookup.get(actionName);
    }

    public String[] getActionNames() 
        throws TcActionDeclarationException {

        List out = new ArrayList();
        try {
            
            Field[] fields = workerClass.getFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().toLowerCase().startsWith(FIELD_NAME_PREFIX_INPUT.toLowerCase())) {
                    String operationName = fields[i].getName().substring(FIELD_NAME_PREFIX_INPUT.length());
                    
                    Method[] methods = workerClass.getMethods();            
                    for (int k = 0; k < methods.length; k++) {
                        if (operationName.equalsIgnoreCase(methods[k].getName())) {
                            operationName = methods[k].getName();
                            break;
                        }
                    }
                    out.add( operationName );
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TcActionDeclarationException("Fehler beim Ermitteln der Selbstbeschreibung von "+workerClass.getName(), e);
        }
        
        return (String[])out.toArray(new String[]{});
    }


    public EnrichedInOutParam wrapWithInOutParam(Object value) {
        return new EnrichedParamImplementation(value);
    }


    /**
     * Implementierung eines InOutParam, mit dem Ein-Ausgabeparameter bei Actions realisiert werden können
     */
    class EnrichedParamImplementation
        implements EnrichedInOutParam {

        Object data;
        String contextFieldName;

        protected EnrichedParamImplementation(Object data) {
            this.data = data;
        }

        public String getContextFieldName() {
            return contextFieldName;
        }

        public void setContextFieldName(String newContextFieldName) {
            this.contextFieldName = newContextFieldName;
        }

        public Object get() {
            return this.data;
        }
        public void set(Object newData) {
            this.data = newData;
        }	
    }

}