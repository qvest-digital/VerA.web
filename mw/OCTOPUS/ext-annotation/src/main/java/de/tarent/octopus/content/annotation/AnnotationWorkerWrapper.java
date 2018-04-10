package de.tarent.octopus.content.annotation;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
 * @author Sebastian Mancke
 */
public class AnnotationWorkerWrapper extends AbstractWorkerWrapper {
    Logger logger = Logger.getLogger(getClass().getName());

    public AnnotationWorkerWrapper(Object workerDelegate) {
        super(workerDelegate);
    }

    public String getVersion() {
        Version version = (Version) getWorkerClass().getAnnotation(Version.class);
        if (null != version) {
            return version.value();
        }

        logger.log(Level.CONFIG, "Für den Worker " + getWorkerClass().getName() + " wurde keine Version angegeben.");
        return "1.0";
    }

    /**
     * Fetches the metadata of an action from the annotations.
     *
     * @param actionName Name der Action
     * @return Metadaten die beschreiben, wie die Action-Methode aufgerufen werden soll.
     */
    public ActionData getActionData(String actionName)
            throws TcActionDeclarationException {

        if (null == actionName) {
            throw new NullPointerException("Action Name darf nicht null sein");
        }

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

        if (action.method == null) {
            throw new TcActionDeclarationException(
                    "Serverfehler: Keine passende Methode für die Action " + actionName + " im Worker " +
                            getWorkerClass().getName() + " gefunden.");
        }

        if (action.method.getAnnotation(Description.class) != null) {
            action.description = action.method.getAnnotation(Description.class).value();
        }

        // Finding the parameters
        action.args = action.method.getParameterTypes();
        action.passOctopusContext = action.args.length > 0
                && OctopusContext.class.equals(action.args[0]);

        action.genericArgsCount = action.args.length;
        if (action.passOctopusContext) {
            action.genericArgsCount--;
        }

        action.inputParams = new String[action.genericArgsCount];
        action.mandatoryFlags = new boolean[action.genericArgsCount];
        action.descriptions = new String[action.genericArgsCount];
        //        int passCntxOffset = (action.passOctopusContext) ? 1 : 0;
        Annotation[][] parameterAnnotations = action.method.getParameterAnnotations();
        for (int i = 0; i < action.genericArgsCount; i++) {

            int paramPos = (action.passOctopusContext) ? i + 1 : i;
            Description pDescription = null;
            Optional pOptional = null;
            WebParam pWebParam = null;
            Name pName = null;

            if (logger.isLoggable(Level.FINEST)) {
                for (Object o : parameterAnnotations[paramPos]) {
                    logger.finest("Annotations on " + paramPos + ": " + o);
                }
            }
            for (int j = 0; j < parameterAnnotations[paramPos].length; j++) {
                logger.finest("IS WEB PARAM: " + parameterAnnotations[paramPos][j].annotationType().getName());
                if (parameterAnnotations[paramPos][j].annotationType().equals(Description.class)) {
                    pDescription = (Description) parameterAnnotations[paramPos][j];
                } else if (parameterAnnotations[paramPos][j].annotationType().equals(Optional.class)) {
                    pOptional = (Optional) parameterAnnotations[paramPos][j];
                } else if (parameterAnnotations[paramPos][j].annotationType().equals(WebParam.class)) {
                    pWebParam = (WebParam) parameterAnnotations[paramPos][j];
                } else if (parameterAnnotations[paramPos][j].annotationType().equals(Name.class)) {
                    pName = (Name) parameterAnnotations[paramPos][j];
                }
            }

            // If WebParam is present, its name will be used,
            // otherwise the Name Annotation or null.
            action.inputParams[i] = (pWebParam != null)
                    ? pWebParam.name()
                    : ((pName != null) ? pName.value() : null);
            action.mandatoryFlags[i] = (pOptional == null) ? true : (!pOptional.value());
            action.descriptions[i] = (pDescription == null) ? null : pDescription.value();
        }

        // Getting the Key for the return-Value
        if (!Void.class.equals(action.method.getReturnType())) {
            if (null != action.method.getAnnotation(WebResult.class)) {
                action.outputParam = action.method.getAnnotation(WebResult.class).name();
            } else if (null != action.method.getAnnotation(Result.class)) {
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
        return (String[]) out.toArray(new String[] {});
    }

    class AnnotationActionData extends ActionData {
        private Type[] genericParameterTypes = null;

        /**
         * Returns the type for the parameter to use.
         */
        public Class getArgTargetType(int pos) {
            if (args[pos] == null) {
                return Void.class;
            }

            if (de.tarent.octopus.server.InOutParam.class.isAssignableFrom(args[pos])) {
                return Object.class;
            }

            return args[pos];
        }

        public boolean isInOutParam(int pos) {
            return de.tarent.octopus.server.InOutParam.class.isAssignableFrom(args[pos]);
        }
    }
}
