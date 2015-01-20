/* $Id: TcReflectedWorker.java,v 1.2 2006/11/23 14:33:29 schmitz Exp $
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
 * by Wolfgang Klein.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.config.TcModuleConfig;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;

/**
 * Refection Worker
 * 
 * Deprecated: Die Worker sollen seit Octopus Version 1.2.0 den TcReflectedWorkerWrapper verwenden,
 *            der normale Java-Klassen als Worker ansprechen kann.
 * @author Wolfgang Klein 
 */
abstract public class TcReflectedWorker implements TcContentWorker {
	// TODO Logging verbessern und evtl. mit Octopus auf Log4J umstellen
	// TODO Initalisieren der Aktions in die init-Funktion verschieben?
	
	// Konstanten
	private final static Class[] PREPOST_PARAMETER = { TcAll.class, String.class }; 
	
	/**
	 * Sammlung der zur Verfügung stehenden Aktions.
	 * 
	 * Key = Methodenname
	 * Value = WorkerAction bzw. TcActionDeclarationException
	 */
	private final Map _serviceActions = new HashMap();
	private Method pre = null;
	private Method post = null;
	
	/**
	 * Initalisiert die Aktionen des Workers.
	 */
	public TcReflectedWorker() {
		Log logger = LogFactory.getLog(getClass());
		
		try {
			pre = getClass().getMethod("pre", PREPOST_PARAMETER);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
		}
		
		try {
			post = getClass().getMethod("post", PREPOST_PARAMETER);
		} catch (SecurityException e) {
			logger.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
		}
		
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// Method.Modifier 17 = public final 
			// Method.Modifier 25 = public static final 
			if (method.getModifiers() == 25) {
				String methodname = method.getName().toUpperCase();
				Class[] paramTypes;
				String[] paramNames;
				boolean[] mandatory;
				String output = null;
				try {
					try {
						Field field = getClass().getField("INPUT_".concat(methodname));
						paramNames = (String[])field.get(null);
						paramTypes = method.getParameterTypes();
						if (paramNames.length + 1 != paramTypes.length || !paramTypes[0].equals(TcAll.class)) {
							throw new TcActionDeclarationException("Serverfehler: Für die Action '" + method.getName() + "' im Worker '" + getClass().getName() + "' stimmt die Anzahl der Argumente (der Methode, exkl. TcAll) nicht mit dennen des statischen String[] 'INPUT_" + methodname + "' überein.");
						}
					} catch (NoSuchFieldException e) {
						throw new TcActionDeclarationException("Serverfehler: Für die Action '" + method.getName() + "' im Worker '" + getClass().getName() + "' muss ein statisches String[] Feld 'INPUT_" + methodname + "' übergeben sein.");
					}
					try {
						Field field = getClass().getField("MANDATORY_".concat(methodname));
						mandatory = (boolean[])field.get(null);
					} catch (NoSuchFieldException e) {
						throw new TcActionDeclarationException("Serverfehler: Für die Action '" + method.getName() + "' im Worker '" + getClass().getName() + "' muss ein statisches boolean[] Feld 'MANDATORY_" + methodname + "' übergeben sein.");
					}
					try {
						Field field = getClass().getField("OUTPUT_".concat(methodname));
						output = (String)field.get(null);
					} catch (NoSuchFieldException e) {
						throw new TcActionDeclarationException("Serverfehler: Für die Action '" + method.getName() + "' im Worker '" + getClass().getName() + "' muss ein statisches String Feld 'OUTPUT_" + methodname + "' übergeben sein.");
					}
					_serviceActions.put(method.getName(), new WorkerAction(method, method.getName(), paramNames, paramTypes, mandatory, output));
				} catch (TcActionDeclarationException e) {
					logger.error(e.getMessage(), e);
					_serviceActions.put(method.getName(), e);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(), e);
					_serviceActions.put(method.getName(), new TcActionDeclarationException(e));
				} catch (SecurityException e) {
					logger.error(e.getMessage(), e);
					_serviceActions.put(method.getName(), new TcActionDeclarationException(e));
				}
			}
		}
	}
	
	public void init(TcModuleConfig config) {
	}
	
	/**
	 * Führt impl. die Aktion aus.
	 * 
	 * Fehler werden als <code>TcContentProzessException</code>
	 * an den Octopus weiter gereicht und NICHT geloggt.
	 * 
	 * @return Content-Status
	 * @throws TcContentProzessException
	 */
	public final String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent) throws TcContentProzessException {
		try {
			WorkerAction action = getAction(actionName);
			TcAll all = new TcAll(tcRequest, tcContent, tcConfig);
			
			// pre
			if (pre != null) {
				pre.invoke(null, new Object[] { all, action._actionName });
			}
			
			// action
			tcContent.setField(action.getOutputField(), action.invoke(all));
            
			// post
			if (post != null) {
				post.invoke(null, new Object[] { all, action._actionName });
			}
			
			// return Content-Status
			return all.getStatus();
            
		} catch (TcActionDeclarationException e) { // WorkerAction#getAction
			throw new TcContentProzessException(e);
		} catch (TcActionInvocationException e) { // WorkerAction#getAction
			TcContentProzessException cpe = new TcContentProzessException(e.getMessage());
			cpe.setStackTrace(e.getStackTrace());
			throw cpe;
		} catch (IllegalArgumentException e) { // Method#invoke
			TcContentProzessException cpe = new TcContentProzessException(e.getMessage());
			cpe.setStackTrace(e.getStackTrace());
			throw cpe;
		} catch (IllegalAccessException e) { // Method#invoke
			TcContentProzessException cpe = new TcContentProzessException(e.getMessage());
			cpe.setStackTrace(e.getStackTrace());
			throw cpe;
		} catch (InvocationTargetException e) { // Method#invoke
		    Throwable cause = e.getCause();
		    if (cause instanceof TcContentProzessException) {
		        throw (TcContentProzessException)cause;
		    } else {
		        throw new TcContentProzessException(cause);
		    }
		}
	}
	
	public final TcPortDefinition getWorkerDefinition() {
		TcPortDefinition definition = new TcPortDefinition(getClass().getName(), "n/a");
		TcOperationDefinition operation;
		TcMessageDefinition message;
		
		Iterator k = _serviceActions.keySet().iterator();
		Iterator v = _serviceActions.values().iterator();
		while (k.hasNext() && v.hasNext()) {
			String key = (String)k.next();
			Object value = v.next();
			if (value instanceof WorkerAction) {
				// Worker-Action-Definition
				WorkerAction action = (WorkerAction)value;
				operation = new TcOperationDefinition(key, "n/a");
				
				// Input-Definition
				if (action._paramNames.length != 0) {
					message = new TcMessageDefinition();
					for (int i = 0; i < action._paramNames.length; i++) {
						message.addPart(action._paramNames[i], action._paramTypes[i + 1].getName(), "n/a", action._mandatory[i]);
					}
					operation.setInputMessage(message);
				}
				
				// Output-Definition
				if (action._output != null) {
					message = new TcMessageDefinition();
					message.addPart(action._output, action._method.getReturnType().getName(), "n/a");
					operation.setOutputMessage(message);
				}
				
				definition.addOperation(operation);
			} else if (value instanceof TcActionDeclarationException) {
				operation = new TcOperationDefinition(key, ((TcActionDeclarationException)value).getMessage());
				definition.addOperation(operation);
			}
		}
		return definition;
	}
	
	public String getVersion() {
		return null;
	}
	
	private final WorkerAction getAction(String actionName) throws TcActionDeclarationException, TcActionInvocationException {
		Object action = _serviceActions.get(actionName);
		if (action instanceof WorkerAction) {
			return (WorkerAction)action;
		} else if (action instanceof TcActionDeclarationException) {
			throw (TcActionDeclarationException)action;
		} else {
			throw new TcActionInvocationException("Anfragefehler: Die angegebene Action '" + actionName + "' ist im Worker '" + getClass().getName() + "' nicht definiert.");
		}
	}
	
	static private final class WorkerAction {
		private final Method _method;
		private final String _actionName;
		private final String[] _paramNames;
		private final Class[] _paramTypes;
		private final boolean[] _mandatory;
		private final String _output;
		
		private WorkerAction(Method method, String actionName, String[] paramNames, Class[] paramTypes, boolean[] mandatory, String output) {
			_method = method;
			_actionName = actionName;
			_paramNames = paramNames;
			_paramTypes = paramTypes;
			_mandatory = mandatory;
			_output = output;
		}
		
		private Object invoke(TcAll all) throws TcActionInvocationException, TcContentProzessException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			int requestType = all.requestType();
			List params = new ArrayList(_paramTypes.length);
			params.add(all);
			Class type;
			for (int i = 0; i < _paramNames.length; i++) {
				type = _paramTypes[i + 1];
				// Daten aus Content und ggf. aus dem Request
				Object param = all.contentAsObject(_paramNames[i]);
				if (param == null) {
					param = all.requestAsObject(_paramNames[i]);
				}
				
				switch (requestType) {
					/**
					 * RequestType 'WEB'
					 * 
					 * - Boolean wird bei null explizit false.
					 * - Parameter muss wenn 'mandatory' wahr ist angegeben sein.
					 * - Wenn der Parameter null ist wird null übergeben.
					 * - Integer, Long und Double können konvertiert werden.
					 */
					case TcRequest.REQUEST_TYPE_WEB:
						if (type.equals(Boolean.class)) {
							params.add((param != null) ? Boolean.valueOf(param.toString()) : Boolean.FALSE);
						} else if (_mandatory[i] && param == null) {
							throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' muss übergeben werden. (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
						} else if (param == null) {
							params.add(null);
						} else if (type.isInstance(param)) {
							params.add(param);
						} else if (type.equals(Integer.class)) {
							try {
								params.add(Integer.valueOf(param.toString()));
							} catch (NumberFormatException e) {
								if (_mandatory[i]) {
									throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' muss vom Typ '" + type.getName() + "' sein (tatsächlicher Typ: '" + param.getClass().getName() + "', Wert: '" + param.toString() + "'). (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
								} else {
									params.add(null);
								}
							}
						} else if (type.equals(Long.class)) {
							try {
								params.add(Long.valueOf(param.toString()));
							} catch (NumberFormatException e) {
								if (_mandatory[i]) {
									throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' (Wert: '" + param.toString() + "') konnte nicht nach '" + type.getName() + "' gecastet werden. (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
								} else {
									params.add(null);
								}
							}
						} else if (type.equals(Double.class)) {
							try {
								params.add(Double.valueOf(param.toString()));
							} catch (NumberFormatException e) {
								if (_mandatory[i]) {
									throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' (Wert: '" + param.toString() + "') konnte nicht nach '" + type.getName() + "' gecastet werden. (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
								} else {
									params.add(null);
								}
							}
						} else if (type.equals(List.class) && param instanceof Object[]) {
							params.add(Arrays.asList((Object[])param));
						} else if (type.equals(List.class)) {
							params.add(Collections.singletonList(param));
						} else {
							throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' muss vom Typ '" + type.getName() + "' sein (tatsächlicher Typ: '" + param.getClass().getName() + "', Wert: '" + param.toString() + "'). (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
						}
						break;
					/**
					 * RequestType 'default'
					 * 
					 * - Datentyp und Parametertyp müssen übereinstimmen.
					 * - Object-Arrays werden, wenn das Ziel eine Liste ist
					 *   automatisch umgewandelt (z.B. bei Vectoren)
					 * - Wenn 'mandatory' nicht wahr ist, kann <code>null</code> übergeben werden.
					 */
					default:
						if (_mandatory[i] && param == null) {
							throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' muss übergeben werden. (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
						} else if (param == null) {
							params.add(null);
						} else if (type.isInstance(param)) {
							params.add(param);
						} else if (type.equals(List.class) && param instanceof Object[]) {
							params.add(Arrays.asList((Object[])param));
						} else {
							throw new TcActionInvocationException("Anfragefehler: Der Parameter '" + _paramNames[i] + "' muss vom Typ '" + type.getName() + "' sein. (tatsächlicher Typ: '" + param.getClass().getName() + "', Wert: '" + param.toString() + "'). (" + _method.getDeclaringClass().getName() + "#" + _actionName + ")");
						}
						break;
				}
			}
			
			return _method.invoke(null, params.toArray());
		}
		
		private String getOutputField() {
			return _output;
		}
	}


    // Diese Exceptions sollen eigentlich entfernt werden.
    // Sind aber aus kompatibilitätsgründen zu alten Workern noch drinn.
    
    /**
     * @depracated Die nicht eingebetteten Varianten dieser Exceptions sollen verwendet werden.
     */
    static public class TcActionDeclarationException extends Exception {
        /**
		 * serialVersionUID = 4871294463116415598L
		 */
		private static final long serialVersionUID = 4871294463116415598L;

		public TcActionDeclarationException(String msg) {
            super(msg);
        }
        
        public TcActionDeclarationException(Throwable t) {
            super(t);
        }
    }
    
    /**
     * @depracated Die nicht eingebetteten Varianten dieser Exceptions sollen verwendet werden.
     */
    static public class TcActionInvocationException extends TcContentProzessException {
        /**
		 * serialVersionUID = 493630509826405394L
		 */
		private static final long serialVersionUID = 493630509826405394L;

		public TcActionInvocationException(String msg) {
            super(msg);
        }
        
        public TcActionInvocationException(Throwable t) {
            super(t);
        }
    }
    
	
}
