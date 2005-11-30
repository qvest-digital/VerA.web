/* $Id: AnnotationWorkerWrapper.java,v 1.1 2005/11/30 15:53:01 asteban Exp $
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
 * by Sebastian Mancke
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content.annotation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.content.AbstractWorkerWrapper;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.content.TcActionDeclarationException;
import de.tarent.octopus.server.InOutParam;

/**
 * Wrapper für Octopus Worker auf Basis von Annotations
 * Angelehnt an den Standard "WebServices Metadata for the Java Platform", JSR-181
 *
 * 
 * @author Sebastian Mancke
 */
public class AnnotationWorkerWrapper    
    extends AbstractWorkerWrapper {

    Logger logger = Logger.getLogger(getClass().getName());
        
    
    public AnnotationWorkerWrapper(Object workerDelegate) {
        super(workerDelegate);
    }


    public String getVersion() {
        Version version = (Version)getWorkerClass().getAnnotation(Version.class);
        if (null != version)
            return version.value();
        
        logger.log(Level.CONFIG, "Für den Worker "+getWorkerClass().getName()+" wurde keine Version angegeben.");
        return "1.0";
    }


    /**
     * Fetches the metadata of an action from the annotations.
     * 
     *
     * @param actionName Name der Action
     * @return Metadaten die beschreiben, wie die Action-Methode aufgerufen werden soll.
     */
    public ActionData getActionData(String actionName) 
        throws TcActionDeclarationException {
        
        if (null == actionName)
            throw new NullPointerException("Action Name darf nicht null sein");

        // Finding the proper method
        ActionData action = new ActionData();
        Method[] methods = getWorkerClass().getMethods();
        
        for (int i = 0; i < methods.length; i++) {

            // Nur die Methoden sind Services, die eine WebMethod Annotation haben.
            WebMethod wm = methods[i].getAnnotation(WebMethod.class);
            if (null != wm) {
                // Wenn kein operationName angegeben wurde muss der Methodenname als Default verwendet werden.
                String webMethodName = ("".equals(wm.operationName()))
                    ? methods[i].getName()
                    : wm.operationName();

                if (actionName.equals(webMethodName)) {
                    action.method = methods[i];
                    break;
                }
            }
        }
        
        if (action.method == null)
            throw new TcActionDeclarationException("Serverfehler: Keine passende Methode für die Action "+actionName+" im Worker "+getWorkerClass().getName()+" gefunden.");

        if (action.method.getAnnotation(Description.class) != null)
            action.description = action.method.getAnnotation(Description.class).value();
        

        // Finding the parameters
        action.args = action.method.getParameterTypes();
        action.passOctopusContext = action.args.length > 0
            && OctopusContext.class.equals(action.args[0]);

        action.genericArgsCount = action.args.length;
        if (action.passOctopusContext)
            action.genericArgsCount--;

        action.inputParams = new String[action.genericArgsCount];
        action.mandatoryFlags = new boolean[action.genericArgsCount];
        action.descriptions = new String[action.genericArgsCount];
        Annotation[][] parameterAnnotations = action.method.getParameterAnnotations();
        for (int i= (action.passOctopusContext) ? 1 : 0;
             i < action.args.length;
             i++) {
            
            Description pDescription = null;
            Optional pOptional = null;
            WebParam pWebParam = null;
            
            for (int j=0; j<parameterAnnotations[i].length; j++) {
                if (parameterAnnotations[i][j].getClass().equals(Description.class))
                    pDescription = (Description)parameterAnnotations[i][j];                                

                else if (parameterAnnotations[i][j].getClass().equals(Optional.class))
                    pOptional = (Optional)parameterAnnotations[i][j];                

                else if (parameterAnnotations[i][j].getClass().equals(WebParam.class))
                    pWebParam = (WebParam)parameterAnnotations[i][j];
            }
         
            action.inputParams[i] = (pWebParam == null) ? null : pWebParam.name();
            action.mandatoryFlags[i] = (pOptional == null) ? true : (! pOptional.value());
            action.descriptions[i] = (pDescription == null) ? null : pDescription.value();
        }

        
        // Getting the Key for the return-Value
        if (! Void.class.equals(action.method.getReturnType())) {
            if (null != action.method.getAnnotation(WebResult.class)) {
                action.outputParam = action.method.getAnnotation(WebResult.class).name();
            }
            else if (null != action.method.getAnnotation(Result.class)) {
                action.outputParam = action.method.getAnnotation(Result.class).value();
            }
        }
           
        return action;
    }

    public String[] getActionNames() 
        throws TcActionDeclarationException {

        List out = new ArrayList();

        Method[] methods = getWorkerClass().getMethods();
        for (int i = 0; i < methods.length; i++) {

            // Nur die Methoden sind Services, die eine WebMethod Annotation haben.
            WebMethod wm = methods[i].getAnnotation(WebMethod.class);
            if (null != wm) {
                // Wenn kein operationName angegeben wurde muss der Methodenname als Default verwendet werden.
                String webMethodName = ("".equals(wm.operationName()))
                    ? methods[i].getName()
                    : wm.operationName();
                out.add(webMethodName);
            }
        }        
        return (String[])out.toArray(new String[]{});
    }


    
    class AnnotationActionData 
        extends ActionData {

        private Type[] genericParameterTypes = null;

        /** 
         * Returns the type for the parameter to use.
         * Normaly, this ist the paramtype. In the case of InOutParams 
         * this is the Generic-Type of the InOutParam.
         */
        public Class getArgTargetType(int pos) 
            throws TcActionDeclarationException {
            if (args[pos] == null)
                return Void.class;

            // Generic InOutParam Type
            if (de.tarent.octopus.content.annotation.InOutParam.class.isAssignableFrom(args[pos])) {
                if (genericParameterTypes == null)
                    genericParameterTypes = method.getGenericParameterTypes();                
                if (genericParameterTypes[pos] instanceof ParameterizedType) {
                    Type genericType = ((ParameterizedType)genericParameterTypes[pos]).getActualTypeArguments()[0];
                    if (genericType instanceof Class)
                        return (Class)genericType;
                }                 
                throw new TcActionDeclarationException("Fehler bei Bestimmung des Generic-Zieltypes für "+pos+". Parameter von "+method.getName());
            } 
            
            if (de.tarent.octopus.server.InOutParam.class.isAssignableFrom(args[pos]))
                return Object.class;
            
            return args[pos];
        }
    }
}