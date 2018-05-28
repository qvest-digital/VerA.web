package de.tarent.octopus.client;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2007 David Goemans (d.goemans@tarent.de)
 *  © 2006, 2007, 2010 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2005, 2006, 2007 Christoph Jerolimov (c.jerolimov@tarent.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2007, 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2007, 2008 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2006, 2008, 2009 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Depends on
//import de.tarent.commons.logging.MethodCall;
//import de.tarent.commons.logging.ThreadLogger;
import de.tarent.octopus.client.OctopusConnection;
import de.tarent.octopus.client.OctopusConnectionFactory;
import de.tarent.octopus.client.OctopusResult;
import de.tarent.octopus.client.OctopusTask;
import de.tarent.octopus.resource.Resources;

/**
 * This static helper class wraps octopus calling mechanismus.
 *
 * @author Christoph Jerolimov
 */
public class OctopusInvoker {
    /**
     * Private OctopusInvoker constructor.
     */
    private OctopusInvoker() {
        // no instance of this class allowed yet.
    }

    /**
     * This method invoke an octopus task in the local octopus
     * classpath installation. Use the given parameter for select
     * a module and task and deliver the parameters.
     *
     * The result of the method is null if the task will return
     * nothing. If only one result object is given it will be
     * returned as unpacked instance. As map otherwise.
     *
     * @param module
     * @param task
     * @param parameters
     * @return null or octopus result
     */
    public static Object invoke(String module, String task, Map parameters) {
        return invoke(module, task,
          (String[]) parameters.keySet().toArray(new String[parameters.size()]),
          parameters.values().toArray());
    }

    /**
     * This method invoke an octopus task in the local octopus
     * classpath installation. Use the given parameter for select
     * a module and task and deliver the parameters.
     *
     * The result of the method is null if the task will return
     * nothing. If only one result object is given it will be
     * returned as unpacked instance. As map otherwise.
     *
     * @param module
     * @param task
     * @param keys
     * @param values
     * @return null or octopus result
     */
    public static Object invoke(String module, String task, Collection keys, Collection values) {
        return invoke(module, task,
          (String[]) keys.toArray(new String[keys.size()]),
          values.toArray());
    }

    /**
     * This method invoke an octopus task in the local octopus
     * classpath installation. Use the given parameter for select
     * a module and task and deliver the parameters.
     *
     * The result of the method is null if the task will return
     * nothing. If only one result object is given it will be
     * returned as unpacked instance. As map otherwise.
     *
     * @param module
     * @param task
     * @param keys
     * @param values
     * @return null or octopus result
     */
    public static Object invoke(String module, String task, String keys[], Object values[]) {
        // MethodCall logging
        //MethodCall methodCall = ThreadLogger.logMethodCall();
        //methodCall.addParameter("module", module);
        //methodCall.addParameter("task", task);
        //methodCall.addParameter("parameterKeys", parameterKeys);
        //methodCall.addParameter("parameterValues", parameterValues);

        // Verify input parameters.
        List parameterKeys = keys != null ? Arrays.asList(keys) : Collections.EMPTY_LIST;
        List parameterValues = keys != null ? Arrays.asList(values) : Collections.EMPTY_LIST;
        if (parameterKeys.size() != parameterValues.size()) {
            throw new RuntimeException(Resources.getInstance().get("REQUESTPROXY_OUT_PROCESSING_EXCEPTION", module, task));
        }

        // Set up octopus connection to use internal calls.
        // TODO Make this only one times or use a property?
        OctopusConnectionFactory ocf = OctopusConnectionFactory.getInstance();
        Map configuration = Collections.singletonMap(
          OctopusConnectionFactory.CONNECTION_TYPE_KEY,
          OctopusConnectionFactory.CONNECTION_TYPE_INTERNAL);
        ocf.setConfiguration(module, configuration);

        // Get octopus connection for module.
        OctopusConnection oc = ocf.getConnection(module);
        if (oc == null) {
            throw new RuntimeException(Resources.getInstance().get("INTERNAL_CALL_NO_OCTOPUS_MODULE", module));
        }

        // Get octopus task for taskname.
        OctopusTask ot = oc.getTask(task);
        if (ot == null) {
            throw new RuntimeException(Resources.getInstance().get("INTERNAL_CALL_NO_OCTOPUS_TASK", module, task));
        }

        // Set the parameters.
        // FIXME This module parameter is imo stupid because is already known by ot?
        // Fix this in OctopusConnection/OctopusTask/... and remove it here.
        ot.add("module", module);

        Iterator itKeys = parameterKeys.iterator();
        Iterator itValues = parameterValues.iterator();

        while (itKeys.hasNext() && itValues.hasNext()) {
            Object key = itKeys.next();
            if (key != null) {
                ot.add(key.toString(), itValues.next());
            }
        }

        // Invoke octopus task.
        OctopusResult or = ot.invoke();
        if (or == null) {
            throw new RuntimeException(Resources.getInstance().get("INTERNAL_CALL_NO_OCTOPUS_RESULT", module, task));
        }

        // Transform data and make it more simple to use them.
        Map data = new LinkedHashMap();
        for (Iterator it = or.getDataKeys(); it.hasNext(); ) {
            String key = (String) it.next();
            data.put(key, or.getData(key));
        }

        //methodCall.addVariable("data", data);

        if (data.size() == 0) {
            return null;
        } else if (data.size() == 1) {
            return data.values().toArray()[0];
        } else {
            return data;
        }
    }
}
