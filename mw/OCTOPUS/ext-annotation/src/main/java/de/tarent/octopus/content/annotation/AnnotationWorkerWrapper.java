/*
 * tarent-octopus annotation extension,
 * an opensource webservice and webapplication framework (annotation extension)
 * Copyright (c) 2006-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus annotation extension'
 * Signature of Elmar Geese, 11 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

package de.tarent.octopus.content.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import de.tarent.octopus.content.AbstractWorkerWrapper;
import de.tarent.octopus.content.TcActionDeclarationException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Wrapper für Octopus Worker auf Basis von Annotations
 * Angelehnt an den Standard "WebServices Metadata for the Java Platform", JSR-181
 *
 *
 * @author Sebastian Mancke
 */
public class AnnotationWorkerWrapper extends AbstractWorkerWrapper {

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
	ActionData action = new AnnotationActionData();
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
//        int passCntxOffset = (action.passOctopusContext) ? 1 : 0;
	Annotation[][] parameterAnnotations = action.method.getParameterAnnotations();
	for (int i=0; i < action.genericArgsCount; i++) {

	    int paramPos = (action.passOctopusContext) ? i+1 : i;
	    Description pDescription = null;
	    Optional pOptional = null;
	    WebParam pWebParam = null;
	    Name pName = null;

	    if (logger.isLoggable(Level.FINEST)) {
		for (Object o : parameterAnnotations[paramPos])
		    logger.finest("Annotations on "+paramPos+": "+o );
	    }
	    for (int j=0; j<parameterAnnotations[paramPos].length; j++) {
		logger.finest("IS WEB PARAM: "+parameterAnnotations[paramPos][j].annotationType().getName());
		if (parameterAnnotations[paramPos][j].annotationType().equals(Description.class))
		    pDescription = (Description)parameterAnnotations[paramPos][j];

		else if (parameterAnnotations[paramPos][j].annotationType().equals(Optional.class))
		    pOptional = (Optional)parameterAnnotations[paramPos][j];

		else if (parameterAnnotations[paramPos][j].annotationType().equals(WebParam.class))
		    pWebParam = (WebParam)parameterAnnotations[paramPos][j];

		else if (parameterAnnotations[paramPos][j].annotationType().equals(Name.class))
		    pName = (Name)parameterAnnotations[paramPos][j];
	    }

	    // If WebParam is present, its name will be used,
	    // otherwise the Name Annotation or null.
	    action.inputParams[i] = (pWebParam != null)
		? pWebParam.name()
		: ((pName != null) ? pName.value() : null);
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

    class AnnotationActionData extends ActionData {
	private Type[] genericParameterTypes = null;

	/**
	 * Returns the type for the parameter to use.
	 * Normaly, this ist the paramtype. In the case of InOutParams
	 * this is the Generic-Type of the InOutParam.
	 */
	public Class getArgTargetType(int pos) {
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

		TcActionDeclarationException actionDeclarationException = new TcActionDeclarationException(
				"Fehler bei Bestimmung des Generic-Zieltypes für " + pos + ". " +
				"Parameter von " + method.getName());
		throw new Error(actionDeclarationException);
	    }

	    if (de.tarent.octopus.server.InOutParam.class.isAssignableFrom(args[pos]))
		return Object.class;

	    return args[pos];
	}

	public boolean isInOutParam(int pos) {
	    return de.tarent.octopus.content.annotation.InOutParam.class.isAssignableFrom(args[pos])
		|| de.tarent.octopus.server.InOutParam.class.isAssignableFrom(args[pos]);
	}
    }

    public EnrichedInOutParam wrapWithInOutParam(Object value) {
	return new EnrichedParamImplementation(value);
    }

    /**
     * Implementierung eines InOutParam, mit dem Ein-Ausgabeparameter bei Actions realisiert werden können
     */
    class EnrichedParamImplementation<T>
	implements de.tarent.octopus.content.annotation.InOutParam, EnrichedInOutParam {

	T data;
	String contextFieldName;

	protected EnrichedParamImplementation(T data) {
	    this.data = data;
	}

	public String getContextFieldName() {
	    return contextFieldName;
	}

	public void setContextFieldName(String newContextFieldName) {
	    this.contextFieldName = newContextFieldName;
	}

	public T get() {
	    return this.data;
	}

	public void set(Object newData) {
	    this.data = (T)newData;
	}
    }
}
