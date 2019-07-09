package de.tarent.octopus.jmx;

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

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.content.TcMessageDefinition;
import de.tarent.octopus.content.TcMessageDefinitionPart;
import de.tarent.octopus.request.Octopus;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.request.TcTask;
import de.tarent.octopus.request.TcTaskList;
import de.tarent.octopus.request.directcall.TcDirectCallResponse;
import de.tarent.octopus.request.directcall.TcDirectCallSession;
import de.tarent.octopus.response.ResponseProcessingException;
import lombok.extern.log4j.Log4j2;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Log4j2
public class OctopusModuleManagement implements DynamicMBean {
    private ObjectName jmxName = null;
    private MBeanInfo octopusMBeanInfo = null;
    private MBeanServer mbs = null;
    private HashMap octopusOperationMap = null;
    private TcCommonConfig octopusConfig = null;
    private Octopus octopus = null;
    private String module = null;

    public OctopusModuleManagement(Octopus octopus, TcCommonConfig commonconfig, String module)
      throws MalformedObjectNameException, NullPointerException {
        octopusOperationMap = new HashMap();

        this.octopusConfig = commonconfig;
        this.octopus = octopus;
        this.module = module;

        jmxName = new ObjectName("de.tarent.octopus.jmx:type=" + module);

        buildDynamicMBeanInfo(module);
    }

    /**
     * Starts the management thread by getting a connection to a running
     * JMX server (or creating a new server) and publishing the management
     * information to the server.
     *
     * @throws Exception if the registration fails.
     */
    public void start() throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        mbs.registerMBean(this, jmxName);
    }

    /**
     * Shuts down the JMX server by unregistering the MBean from the
     * running server.
     *
     * @throws Exception if the unregistering fails.
     */
    public void stop() throws Exception {
        mbs.unregisterMBean(jmxName);
    }

    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        // Check whether used attribute name is null
        if (attribute == null) {
            throw new RuntimeOperationsException(
              new IllegalArgumentException("Attribute name cannot be null"),
              "Cannot invoke a getter with null attribute name");
        }

        if (octopusAttributeAvailable(attribute)) {
            return getOctopusAttribute(attribute);
        } else
        // attribute has not been recognized: throw an AttributeNotFoundException
        {
            throw new AttributeNotFoundException("Cannot find " + attribute + " attribute.");
        }
    }

    public void setAttribute(Attribute attribute)
      throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        // Check whether used attribute name is null
        if (attribute == null) {
            throw new RuntimeOperationsException(
              new IllegalArgumentException("Attribute name cannot be null"),
              "Cannot invoke a setter with null attribute name");
        }

        if (octopusAttributeAvailable(attribute.getName())) {
            setOctopusAttribute(attribute.getName(), attribute.getValue());
        } else
        // attribute has not been recognized: throw an AttributeNotFoundException
        {
            throw new AttributeNotFoundException("Cannot find " + attribute + " attribute.");
        }
    }

    public AttributeList getAttributes(String[] attributeNames) {
        // Check whether used attribute name is null
        if (attributeNames == null) {
            throw new RuntimeOperationsException(
              new IllegalArgumentException("Attribute name cannot be null"),
              "Cannot invoke a setter with null attribute name");
        }

        AttributeList resultList = new AttributeList();

        // An empty list returns an empty result list
        if (attributeNames.length == 0) {
            return resultList;
        }

        // Build result list
        for (int i = 0; i < attributeNames.length; i++) {
            try {
                Object value = getAttribute(attributeNames[i]);
                resultList.add(new Attribute(attributeNames[i], value));
            } catch (Exception e) {
                throw new RuntimeOperationsException(
                  new IllegalArgumentException("Error getting value for " + attributeNames[i] + " attribute."));
            }
        }

        return resultList;
    }

    public AttributeList setAttributes(AttributeList attributeNames) {
        // Check whether used attribute name is null
        if (attributeNames == null) {
            throw new RuntimeOperationsException(
              new IllegalArgumentException("Attribute name cannot be null"),
              "Cannot invoke a setter with null attribute name");
        }

        AttributeList resultList = new AttributeList();

        // An empty list returns an empty result list
        if (attributeNames.size() == 0) {
            return resultList;
        }

        // Build result list
        Iterator i = attributeNames.iterator();
        while (i.hasNext()) {
            Attribute thisAttribute = (Attribute) i.next();
            try {
                setAttribute(thisAttribute);
                String name = thisAttribute.getName();
                Object value = getAttribute(name);
                resultList.add(new Attribute(name, value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }

    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        // check whether the operation name is null
        if (actionName == null) {
            throw new RuntimeOperationsException(
              new IllegalArgumentException("Operation name cannot be null"),
              "Cannot invoke a null operation.");
        }

        if (octopusTaskAvailable(actionName)) {
            return callOctopusTask(actionName, params);
        } else
        // Unknown operation
        {
            throw new ReflectionException(
              new NoSuchMethodException(actionName),
              "Cannot find the operation " + actionName);
        }
    }

    public MBeanInfo getMBeanInfo() {
        return octopusMBeanInfo;
    }

    /******************* Octopus specific bindings follow *********************/

    /**
     * Checks if a task with the given name is available in this Octopus.
     *
     * @param taskName Name of the task.
     */
    private boolean octopusTaskAvailable(String taskName) {
        for (int i = 0; i < octopusMBeanInfo.getOperations().length; i++) {
            if (octopusMBeanInfo.getOperations()[i].getName().equals(taskName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a specific attribute is available in this Octopus.
     *
     * @param attributeName Name of the attribute.
     * @return true if the attribute is available, false otherwise.
     */
    private boolean octopusAttributeAvailable(String attributeName) {
        for (int i = 0; i < octopusMBeanInfo.getAttributes().length; i++) {
            if (octopusMBeanInfo.getAttributes()[i].getName().equals(attributeName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the value of a given attribute.
     *
     * @param attributeName Name of the attribute.
     * @return Value of the attribute.
     * @throws AttributeNotFoundException if the attribute is not available.
     */
    private Object getOctopusAttribute(String attributeName) throws AttributeNotFoundException {
        if (!octopusAttributeAvailable(attributeName)) {
            throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute.");
        }

        return octopusConfig.getConfigData(attributeName);
    }

    /**
     * Sets the value of an attribute.
     *
     * @param attributeName  Name of the attribute.
     * @param attributeValue Value of the attribute.
     * @throws AttributeNotFoundException if the attribute is not available or the parameters are of the wrong type.
     */
    private void setOctopusAttribute(String attributeName, Object attributeValue) throws AttributeNotFoundException {
        if (!octopusAttributeAvailable(attributeName)) {
            throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute.");
        }

        // TODO: implement
    }

    /**
     * Calls the given Octopus task. The actionName attribute gives the module and
     * task to be called in the form "module.task". The result of the task operation
     * is returned.
     *
     * @param actionName Name of the task an module.
     * @param params     Parameters of the task.
     * @return Return value of the operation.
     * @throws ReflectionException if the module or task is not available or the parameters are of the wrong type.
     * @throws MBeanException      if the task was not processed properly.
     */
    private Object callOctopusTask(String task, Object[] params) throws ReflectionException, MBeanException {
        // allow only available tasks
        if (!octopusTaskAvailable(task)) {
            throw new ReflectionException(
              new NoSuchMethodException(task),
              "Cannot find the operation " + task);
        }

        // retrieve parameter names and other info
        MBeanParameterInfo[] parameterInfo = (MBeanParameterInfo[]) octopusOperationMap.get(task);

        // build the request
        TcRequest request = new TcRequest();
        request.setRequestParameters(new HashMap());
        request.setParam("module", module);
        request.setParam("task", task);
        request.setParam("context", "Hallo");
        for (int i = 0; i < params.length; i++) {
            String key = parameterInfo[i].getName();
            Object value = params[i];

            request.setParam(key, value);
        }

        // issue call
        TcDirectCallResponse response = new TcDirectCallResponse();
        try {
            octopus.dispatch(request, response, new TcDirectCallSession());
        } catch (ResponseProcessingException e) {
            logger.fatal("Error calling Octopus task " + task + " from JMX subsystem.", e);
        }

        // catch errors
        if (response.errorWhileProcessing()) {
            throw new MBeanException(response.getErrorException(), response.getErrorMessage());
        }

        // retrieve output values
        Map result = new HashMap();
        Iterator iter = response.getResponseObjectKeys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = response.getResponseObject(key);

            result.put(key, value);
        }

        return result;
    }

    private void buildDynamicMBeanInfo(String module) {
        // construct Octopus core attribute descriptions
        List temp = new ArrayList();
        Iterator iter = null;
        Object tempA[] = null;
        MBeanAttributeInfo[] octopusAttributes = null;
        if ("octopus".equals(module)) {
            iter = octopusConfig.getConfigKeys();
            while (iter.hasNext()) {
                temp.add(new MBeanAttributeInfo((String) iter.next(),
                  "java.lang.String",
                  "Generic Octopus configuration parameter.",
                  true,
                  false,
                  false));
            }

            tempA = temp.toArray();
            octopusAttributes = new MBeanAttributeInfo[tempA.length];
            System.arraycopy(tempA, 0, octopusAttributes, 0, tempA.length);
        }

        // construct Octopus task descriptions
        temp = new ArrayList();
        TcTaskList taskList = null;
        if (!"octopus".equals(module)) {
            taskList = octopusConfig.getTaskList(module);
        }
        MBeanOperationInfo[] octopusOperations = null;
        if (taskList != null) {
            iter = taskList.getTasksKeys();
            while (iter.hasNext()) {
                String thisTaskName = (String) iter.next();
                TcTask thisTask = octopusConfig.getTaskList(module).getTask(thisTaskName);
                MBeanParameterInfo[] thisParameters = parseParameters(thisTask.getInputMessage());

                MBeanOperationInfo thisOperation = new MBeanOperationInfo(
                  thisTaskName,
                  thisTask.getDescription(),
                  thisParameters,
                  "java.util.Map",
                  MBeanOperationInfo.ACTION);
                temp.add(thisOperation);
                octopusOperationMap.put(thisTaskName, thisParameters);
            }

            tempA = temp.toArray();
            octopusOperations = new MBeanOperationInfo[tempA.length];
            System.arraycopy(tempA, 0, octopusOperations, 0, tempA.length);
        }

        // construct Octopus notification descriptions
        MBeanNotificationInfo[] octopusNotifications = new MBeanNotificationInfo[] {};
        /*
                    new MBeanNotificationInfo(
            new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
            AttributeChangeNotification.class.getName(),
            "This notification is emitted when the reset() method is called.");

        */

        octopusMBeanInfo = new MBeanInfo(module + "OctopusModuleManagement",
          "tarent Octopus JMX MBean for module " + module,
          octopusAttributes,
          new MBeanConstructorInfo[] {},
          octopusOperations,
          octopusNotifications);
    }

    private MBeanParameterInfo[] parseParameters(TcMessageDefinition inputMessage) {
        List result = new ArrayList();

        Iterator iter = inputMessage.getParts().iterator();
        while (iter.hasNext()) {
            TcMessageDefinitionPart thisPart = (TcMessageDefinitionPart) iter.next();
            String thisPartName = thisPart.getName().replaceFirst(".*:", "");

            String thisPartType = thisPart.getPartDataType();
            String thisPartDescription = thisPart.getDescription();

            MBeanParameterInfo thisInfo = new MBeanParameterInfo(thisPartName, thisPartType, thisPartDescription);
            result.add(thisInfo);
        }

        Object temp[] = result.toArray();
        MBeanParameterInfo[] octopusParameters = new MBeanParameterInfo[temp.length];
        System.arraycopy(temp, 0, octopusParameters, 0, temp.length);

        return octopusParameters;
    }
}
