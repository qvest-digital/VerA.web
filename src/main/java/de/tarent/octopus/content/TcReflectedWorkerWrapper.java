/* $Id: TcReflectedWorkerWrapper.java,v 1.3 2005/12/15 10:04:10 christoph Exp $
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
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.server.OctopusContext;
import java.util.ArrayList;

/**
 * Reflection Worker, der als Wrapper zu einer Generischen Java-Klasse fungiert.
 * 
 * @author Sebastian Mancke
 */
public class TcReflectedWorkerWrapper 
    
    extends AbstractWorkerWrapper {

    Logger logger = Logger.getLogger(getClass().getName());

    // Feldkonstanten f�r die Metadaten
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
			logger.log(Level.CONFIG, "F�r den Worker "+workerClass.getName()+" wurde keine Version angegeben.");
            return "1.0";
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Fehler beim Ermitteln der Version von "+workerClass.getName(), e);
        }    
    }


    /**
     * Holt die Metadaten zu der Action aus dem Cache, 
     * oder stellt sie �ber getActionData neu zusammen.
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
                throw new TcActionDeclarationException("Serverfehler: Keine passende Methode f�r die Action "+actionName+" im Worker "+workerClass.getName()+" gefunden.");
            
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
            // unterschieden. Deswegen m�ssen alle vorhandenen Fields �berpr�ft werden.
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
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_INPUT + actionName+" in der Klasse "+workerClass.getName()+" hat eine falsche L�nge.");

            if (action.mandatoryFlags != null && action.mandatoryFlags.length != action.genericArgsCount)
                throw new TcActionDeclarationException("Serverfehler: Das Feld "+FIELD_NAME_PREFIX_MANDORITY + actionName+" in der Klasse "+workerClass.getName()+" hat eine falsche L�nge.");
            
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
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new TcActionDeclarationException("Fehler beim Ermitteln der Selbstbeschreibung von "+workerClass.getName(), e);
        }
        
        return (String[])out.toArray(new String[]{});
    }



}