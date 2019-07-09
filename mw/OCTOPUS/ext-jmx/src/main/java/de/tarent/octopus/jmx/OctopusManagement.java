package de.tarent.octopus.jmx;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.extensions.OctopusExtension;
import de.tarent.octopus.request.Octopus;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Log4j2
public class OctopusManagement implements OctopusExtension {
    private List<OctopusModuleManagement> modules = new LinkedList<OctopusModuleManagement>();

    public OctopusManagement() {
        super();
    }

    public void initialize(Object params) {
        if (!(params instanceof Map)) {
            logger.fatal("JMX extension parameter is not a map!");
        }

        if (!((Map) params).containsKey("octopus") || !((Map) params).containsKey("config")) {
            logger.fatal("JMX extension needs parameter 'octopus' and parameter 'config'");
        }

        Octopus octopus = (Octopus) ((Map) params).get("octopus");
        TcCommonConfig commonconfig = (TcCommonConfig) ((Map) params).get("config");

        // initialize octopus core MBean
        try {
            modules.add(new OctopusModuleManagement(octopus, commonconfig, "octopus"));
        } catch (Exception e) {
            logger.fatal("Error initializing JMX for octopus core.", e);
        }

        // initialize module specific MBeans
        Iterator iter = commonconfig.getExistingModuleNames();
        String module = null;
        while (iter.hasNext()) {
            try {
                module = (String) iter.next();
                modules.add(new OctopusModuleManagement(octopus, commonconfig, module));
            } catch (Exception e) {
                logger.fatal("Error initializing JMX for module " + module, e);
            }
        }
    }

    /**
     * Starts the management thread by getting a connection to a running
     * JMX server (or creating a new server) and publishing the management
     * information to the server.
     */
    public void start() {
        for (OctopusModuleManagement module : modules) {
            try {
                module.start();
            } catch (Exception e) {
                logger.fatal("Error starting JMX for module " + module.getMBeanInfo().getClassName(), e);
            }
        }
    }

    /**
     * Shuts down the JMX server by unregistering the MBean from the
     * running server.
     */
    public void stop() {
        for (OctopusModuleManagement module : modules) {
            try {
                module.stop();
            } catch (Exception e) {
                logger.fatal("Error stopping JMX for module " + module.getMBeanInfo().getClassName(), e);
            }
        }
    }
}
