package de.tarent.aa.veraweb.cucumber.env.hook;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.tarent.aa.veraweb.cucumber.env.GlobalConfig;
import de.tarent.aa.veraweb.cucumber.env.StartAndShutdownHook;
import de.tarent.aa.veraweb.cucumber.env.Utils;
import de.tarent.aa.veraweb.cucumber.env.event.HandlerStop;

/**
 * This class represents a hook. They zip all test results to a separate zip-file.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public class ResultZipper implements HandlerStop {

    /**
     * Log.
     */
    private static final Log LOG = LogFactory.getLog(ResultZipper.class);

    /**
     * Injected {@link GlobalConfig}.
     */
    @Autowired
    private GlobalConfig config;

    /**
     * Constructor.
     */
    public ResultZipper() {
        // register me
        StartAndShutdownHook.addOnStopHandler(this);
    }

    /**
     * {@inheritDoc}<br/>
     * Zip all test results to a separate zip-file.
     */
    public void handleStop() {
        if (config.getResultContentPath() != null && !"".equals(config.getResultContentPath())
                && config.getResultContentZipPath() != null && !"".equals(config.getResultContentZipPath())) {

            LOG.error("=============== Zip result-files ===============");
            try {
                Utils.zipDirectory(config.getResultContentPath(), config.getResultContentZipPath());
            } catch (IOException e) {
                LOG.error("Error on creating zip-file", e);
            }
        }
    }

}
