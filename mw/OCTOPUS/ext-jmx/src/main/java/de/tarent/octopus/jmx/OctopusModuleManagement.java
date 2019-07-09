package de.tarent.octopus.jmx;
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
