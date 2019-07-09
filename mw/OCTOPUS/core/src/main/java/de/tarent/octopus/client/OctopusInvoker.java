package de.tarent.octopus.client;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
