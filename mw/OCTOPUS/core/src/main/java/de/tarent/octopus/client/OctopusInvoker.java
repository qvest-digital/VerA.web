package de.tarent.octopus.client;

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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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

import de.tarent.octopus.resource.Resources;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
