package de.tarent.octopus.content;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection Worker, der als Wrapper zu einer Generischen Java-Klasse fungiert.
 *
 * @author Sebastian Mancke
 */
@Log4j2
public class TcReflectedWorkerWrapper extends AbstractWorkerWrapper {
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
            return (String) workerClass.getField(FIELD_NAME_VERSION).get(workerDelegate);
        } catch (NoSuchFieldException nf) {
            logger.debug("Für den Worker " + workerClass.getName() + " wurde keine Version angegeben.");
            return "1.0";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Fehler beim Ermitteln der Version von " + workerClass.getName(), e);
        }
    }

    /**
     * Holt die Metadaten zu der Action aus dem Cache,
     * oder stellt sie über getActionData neu zusammen.
     *
     * @param actionName Name der Action
     * @return Metadaten die beschreiben, wie die Action-Methode aufgerufen werden soll.
     */
    public ActionData getActionData(String actionName) throws TcActionDeclarationException {
        if (!actionDataLookup.containsKey(actionName)) {
            ActionData action = new ActionData();
            Method[] methods = workerClass.getMethods();

            for (int i = 0; i < methods.length; i++) {
                if (actionName.equals(methods[i].getName())) {
                    action.method = methods[i];
                    break;
                }
            }
            if (action.method == null) {
                throw new TcActionDeclarationException(
                  "Serverfehler: Keine passende Methode für die Action " + actionName + " im Worker " +
                    workerClass.getName() + " gefunden.");
            }

            action.args = action.method.getParameterTypes();
            action.passOctopusContext = action.args.length > 0
              && OctopusContext.class.equals(action.args[0]);

            action.genericArgsCount = action.args.length;
            if (action.passOctopusContext) {
                action.genericArgsCount--;
            }

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
                      && TYPE_STRING_ARRAY.equals(fields[i].getType())) {
                        action.inputParams = (String[]) fields[i].get(workerDelegate);
                    } else if (outNameLower.equals(fNameLower)
                      && TYPE_STRING.equals(fields[i].getType())) {
                        action.outputParam = (String) fields[i].get(workerDelegate);
                    } else if (manNameLower.equals(fNameLower)
                      && TYPE_BOOLEAN_ARRAY.equals(fields[i].getType())) {
                        action.mandatoryFlags = (boolean[]) fields[i].get(workerDelegate);
                    }
                }
            } catch (IllegalAccessException ie) {
                throw new TcActionDeclarationException(ie);
            }
            if (action.inputParams == null) {
                throw new TcActionDeclarationException(
                  "Serverfehler: Kein Feld " + FIELD_NAME_PREFIX_INPUT + actionName + " vom Typ String[] im Worker " +
                    workerClass.getName() + " gefunden.");
            }
            if (action.inputParams.length != action.genericArgsCount) {
                throw new TcActionDeclarationException(
                  "Serverfehler: Das Feld " + FIELD_NAME_PREFIX_INPUT + actionName + " in der Klasse " +
                    workerClass.getName() + " hat eine falsche Länge.");
            }

            if (action.mandatoryFlags != null && action.mandatoryFlags.length != action.genericArgsCount) {
                throw new TcActionDeclarationException(
                  "Serverfehler: Das Feld " + FIELD_NAME_PREFIX_MANDORITY + actionName + " in der Klasse " +
                    workerClass.getName() + " hat eine falsche Länge.");
            }

            if (action.mandatoryFlags == null) {
                action.mandatoryFlags = new boolean[action.genericArgsCount];
                for (int i = 0; i < action.mandatoryFlags.length; i++) {
                    action.mandatoryFlags[i] = true;
                }
            }

            if (action.outputParam == null
              && !Void.TYPE.equals(action.method.getReturnType())) {
                throw new TcActionDeclarationException(
                  "Serverfehler: Kein Feld " + FIELD_NAME_PREFIX_OUTPUT + actionName + " vom Typ String im " +
                    workerClass.getName() + " gefunden. Der Returnwert der entsprechenden Methode ist aber != void.");
            }

            if (action.outputParam != null
              && Void.TYPE.equals(action.method.getReturnType())) {
                throw new TcActionDeclarationException(
                  "Serverfehler: Das Feld " + FIELD_NAME_PREFIX_OUTPUT + actionName + " ist in " + workerClass.getName() +
                    " definiert. Der Returnwert der entsprechenden Methode ist aber void.");
            }

            actionDataLookup.put(actionName, action);
        }
        return (ActionData) actionDataLookup.get(actionName);
    }

    public String[] getActionNames() throws TcActionDeclarationException {
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
                    out.add(operationName);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TcActionDeclarationException("Fehler beim Ermitteln der Selbstbeschreibung von " + workerClass.getName(),
              e);
        }
        return (String[]) out.toArray(new String[] {});
    }

    public EnrichedInOutParam wrapWithInOutParam(Object value) {
        return new EnrichedParamImplementation(value);
    }

    /**
     * Implementierung eines InOutParam, mit dem Ein-Ausgabeparameter bei Actions realisiert werden können
     */
    static class EnrichedParamImplementation implements EnrichedInOutParam {
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
