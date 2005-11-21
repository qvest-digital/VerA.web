/* $Id: TcReflectedWorkerWrapper.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * 
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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.InOutParam;
import de.tarent.octopus.server.OctopusContext;

/**
 * Refection Worker, der als Wrapper zu einer Generischen Java-Klasse fungiert.
 * 
 * @author Sebastian Mancke
 */
public class TcReflectedWorkerWrapper 
    implements TcContentWorker {

    Logger logger = Logger.getLogger(getClass().getName());


    public static final String FIELD_NAME_PREFIX_INPUT = "INPUT_";
    public static final String FIELD_NAME_PREFIX_OUTPUT = "OUTPUT_";
    public static final String FIELD_NAME_PREFIX_MANDORITY = "MANDATORY_";

    public static final String FIELD_NAME_VERSION = "VERSION";

    public static final Class TYPE_STRING = String.class;
    public static final Class TYPE_STRING_ARRAY = String[].class;
    public static final Class TYPE_BOOLEAN_ARRAY = boolean[].class;

    Object workerDelegate;

    Class c;
    HashMap actionDataLookup = new HashMap();
        
    
    public TcReflectedWorkerWrapper(Object workerDelegate) {
        this.workerDelegate = workerDelegate;
        c = workerDelegate.getClass();
    }


    public void init(TcModuleConfig config) {
        try {                    
            Method m = c.getMethod("init", new Class[]{TcModuleConfig.class});
            m.invoke(workerDelegate, new Object[]{config} );
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (NoSuchMethodException e) {
            //DO NOTHING
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Fehler bei Ausführung der init() Methode von "+c.getName(), e);
        }
    }

    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
        throws TcContentProzessException {
        
        try {

            ActionData actionData = getActionData(actionName);
            
            Object[] args = new Object[actionData.args.length];
            int argsPos = 0;
            List inOutParams = new LinkedList();
            OctopusContext octopusContext = new TcAll(tcRequest, tcContent, tcConfig);
            
            if (actionData.passOctopusContext)
                args[argsPos++] = octopusContext;
            
            for (int i = 0; i < actionData.genericArgsCount; i++) {
                Object paramValue = octopusContext.getContextField(actionData.inputParams[i]);
                
                //InOutParameter?
                boolean isInOutParameter = actionData.args[argsPos].equals(InOutParam.class);
                
                // Bei InOutParametern kann keine Typkonvertierung geschehen.
                // TODO: Mit JDK 1.5 - Generics wird das aber gehen.
                if (! isInOutParameter) {               
                    if (! actionData.args[argsPos].isInstance(paramValue)) {
                        paramValue = tryToConvert(paramValue, actionData.args[argsPos]);
                    }
                }
                
                if (paramValue == null && actionData.mandorityFlags[i])
                    throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + actionData.inputParams[i] + "' muss vorhanden sein. (" + c.getName() + "#" + actionName + ")");
                
                if (isInOutParameter) {
                    paramValue = new EnrichedInOutParam(actionData.inputParams[i], paramValue);
                    inOutParams.add(paramValue);                
                }
                
                args[argsPos++] = paramValue;
            }   
            Object result;
            
            result = actionData.method.invoke(workerDelegate, args);
            if (actionData.outputParam != null)
                octopusContext.setContextField(actionData.outputParam, result);
            
            for (Iterator iter = inOutParams.iterator(); iter.hasNext();) {
                EnrichedInOutParam ioParam = (EnrichedInOutParam)iter.next();
                octopusContext.setContextField(ioParam.getContextFieldName(), ioParam.get());
            }
            
            return (octopusContext != null) 
                ? octopusContext.getStatus()
                : TcContentWorker.RESULT_ok;
            
        } catch (IllegalArgumentException e) {
            throw new TcActionInvocationException("Anfragefehler: Fehler beim Aufruf einer Worker-Action: (" + c.getName() + "#" + actionName + ")", e);
		} catch (IllegalAccessException e) {
            throw new TcActionInvocationException("Anfragefehler: Fehler beim Aufruf einer Worker-Action: (" + c.getName() + "#" + actionName + ")", e);
		} catch (InvocationTargetException e) {
			Throwable t = e.getTargetException();
			if (t instanceof TcContentProzessException)
				throw (TcContentProzessException)t;
			else
	            throw new TcActionInvocationException("Anfragefehler: Fehler beim Aufruf einer Worker-Action: (" + c.getName() + "#" + actionName + ")", t);
		}
    }


    
    /** Convertiert ein Object.
     *  Falls dies fehl schlägt oder der Parameter==null ist wird <code>null</code> zurück gegeben.
     */
    protected Object tryToConvert(Object param, Class targetType) {
        if (param == null)
            return null;
        try {

            if (targetType.equals(Boolean.class) || targetType.equals(Boolean.TYPE)) {
                return Boolean.valueOf(param.toString());
            }

            else if (targetType.equals(Integer.class) || targetType.equals(Integer.TYPE)) {
                return Integer.valueOf(param.toString());
            }

            else if (targetType.equals(Long.class) || targetType.equals(Long.TYPE)) {
                return Long.valueOf(param.toString());
            }

            else if (targetType.equals(Double.class) || targetType.equals(Double.TYPE)) {
                return Double.valueOf(param.toString());                
            }
                
            else if (targetType.equals(List.class) && param instanceof Object[]) {
                return Arrays.asList((Object[])param);
            }

            else if (targetType.equals(List.class)) {
                return Collections.singletonList(param);
            }
            
            else if (targetType.equals(String.class)){
            	return param.toString();
            }

            
        } catch (NumberFormatException e) {
            return null;
        }
        return null;        
    }

    public ActionData getActionData(String actionName) 
        throws TcActionDeclarationException {
        if (!actionDataLookup.containsKey(actionName)) {
            ActionData action = new ActionData();
            Method[] methods = c.getMethods();
            
            for (int i = 0; i < methods.length; i++)
                if (actionName.equals(methods[i].getName())) {
                    action.method = methods[i];
                    break;
                }
            if (action.method == null)
                throw new TcActionDeclarationException("Serverfehler: Keine passende Methode für die Action "+actionName+" im Worker "+c.getName()+" gefunden.");
            
            action.args = action.method.getParameterTypes();
            action.passOctopusContext = action.args.length > 0 
                && OctopusContext.class.equals(action.args[0]);


            action.genericArgsCount = action.args.length;
            if (action.passOctopusContext)
                action.genericArgsCount--;

            Field[] fields = c.getFields();
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
                        
                        action.mandorityFlags = (boolean[])fields[i].get(workerDelegate); 
                }
            } catch (IllegalAccessException ie) {
                throw new TcActionDeclarationException(ie);
            }
            if (action.inputParams == null)
                throw new TcActionDeclarationException("Serverfehler: Kein Feld "+FIELD_NAME_PREFIX_INPUT + actionName+" vom Typ String[] im Worker "+c.getName()+" gefunden.");
            if (action.inputParams.length != action.genericArgsCount)
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_INPUT + actionName+" in der Klasse "+c.getName()+" hat eine falsche Länge.");

            if (action.mandorityFlags != null && action.mandorityFlags.length != action.genericArgsCount)
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_MANDORITY + actionName+" in der Klasse "+c.getName()+" hat eine falsche Länge.");
            
            if (action.mandorityFlags == null) {
                action.mandorityFlags = new boolean[action.genericArgsCount];
                for (int i = 0; i < action.mandorityFlags.length; i++)
                    action.mandorityFlags[i] = true;
            }
            
            if (action.outputParam == null 
                && ! Void.TYPE.equals(action.method.getReturnType()))
                throw new TcActionDeclarationException("Serverfehler: Kein Feld "+FIELD_NAME_PREFIX_OUTPUT + actionName+" vom Typ String im "+c.getName()+" gefunden. Der Returnwert der entsprechenden Methode ist aber != void.");

            if (action.outputParam != null 
                && Void.TYPE.equals(action.method.getReturnType()))
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_OUTPUT + actionName+" ist in "+c.getName()+" definiert. Der Returnwert der entsprechenden Methode ist aber void.");
            
            
            actionDataLookup.put(actionName, action);
        }
        return (ActionData)actionDataLookup.get(actionName);
    }

	public Object getWorkerDelegate() {
		return workerDelegate;
	}
    
   /**
     * TODO: Berücksichtigen von:
     *         - Datentypen der Signatur
     *         - InOutParams
     *         - Descriptions
     *         - Mögliche Exceptions
     */
    public TcPortDefinition getWorkerDefinition() {
        try { 
            TcPortDefinition port = new TcPortDefinition(c.getName(), "n/a");
            

            Field[] fields = c.getFields();
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getName().toLowerCase().startsWith(FIELD_NAME_PREFIX_INPUT.toLowerCase())) {

                    String operationName = fields[i].getName().substring(FIELD_NAME_PREFIX_INPUT.length());
                   
                    Method[] methods = c.getMethods();            
                    for (int k = 0; k < methods.length; k++)
                        if (operationName.equalsIgnoreCase(methods[k].getName())) {
                            operationName = methods[k].getName();
                            break;
                        }

                    ActionData actionData = getActionData(operationName);
                    TcOperationDefinition operation = new TcOperationDefinition(operationName, "n/a");
                    TcMessageDefinition in = new TcMessageDefinition();
                    TcMessageDefinition out = new TcMessageDefinition();
                    for (int j = 0; j < actionData.inputParams.length; j++) {

                        int argsPos = actionData.passOctopusContext ? j+1 : j;
                        boolean isInOutParameter = actionData.args[argsPos].equals(InOutParam.class);
                        String type = "xsd:anyType";

                        if (! isInOutParameter)
                            type = actionData.args[argsPos].getName();

                        in.addPart(actionData.inputParams[j], type, "n/a", !actionData.mandorityFlags[j]);
                        if (isInOutParameter)
                            out.addPart(actionData.inputParams[j], type, "n/a");
                    }
                    if (actionData.outputParam != null)
                        out.addPart(actionData.outputParam, actionData.method.getReturnType().getName(), "n/a");
                    operation.setInputMessage(in);
                    operation.setOutputMessage(out);
                    port.addOperation(operation);
                }
            }
            return port;                    
        } catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Fehler beim Ermitteln der Selbstbeschreibung von "+c.getName(), e);
        }
	}

    public String getVersion() {
        try {
            return (String)c.getField(FIELD_NAME_VERSION).get(workerDelegate);
		} catch (NoSuchFieldException nf) {
			logger.log(Level.CONFIG, "Für den Worker "+c.getName()+" wurde keine Version angegeben.");
            return "1.0";
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Fehler beim Ermitteln der Version von "+c.getName(), e);
        }    
    }


    
    class ActionData {            
        Method method;
        Class[] args;

        // Länge von args, ohne einen OctopusContext Parameter zu berücksichtigen
        int genericArgsCount;
        boolean passOctopusContext;
        String[] inputParams;
        String outputParam;
        boolean[] mandorityFlags;
    }


    class EnrichedInOutParam 
        implements InOutParam {

        Object data;
        String contextFieldName;

        protected EnrichedInOutParam(String contextFieldName, Object data) {
            this.data = data;
            this.contextFieldName = contextFieldName;
        }

        protected String getContextFieldName() {
            return contextFieldName;
        }

        protected void setContextFieldName(String newContextFieldName) {
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