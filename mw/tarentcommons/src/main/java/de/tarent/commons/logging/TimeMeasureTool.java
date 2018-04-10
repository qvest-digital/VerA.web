package de.tarent.commons.logging;

import org.apache.commons.logging.Log;

/**
 * Simple Tool for time measurement to the logging system.
 * This tool logs to "info" if the SystemProperty <code>enableTimeMeasureTool</code< is set.
 * If the SystemProperty is not set, if does not log anyway.
 */
public class TimeMeasureTool {

    static boolean doLogging = (System.getProperty("enableTimeMeasureTool") != null && ! "false".equalsIgnoreCase(System.getProperty("enableTimeMeasureTool")));

    private static final Log defaultLogger = LogFactory.getLog(TimeMeasureTool.class);
    private static final TimeMeasureTool NOP = new TimeMeasureTool();
    Log log;
    String level;
    long start;
    long last;
    int steps = 0;

    /**
     * Construcs a TimeMeasureTool with the default logger
     */
    protected TimeMeasureTool() {
        this(defaultLogger);
    }

    /**
     * Construcs a TimeMeasureTool with the supplied logger
     */
    protected TimeMeasureTool(Log logger) {
        this.start = System.currentTimeMillis();
        this.last = start;
        this.log = logger;
    }

    /**
     * Returns a measurement tool for the supplied logger and level.
     * If the logger should not log on this level, this returns a nop object.
     */
    public static TimeMeasureTool getMeasureTool(Log logger) {
        if (doLogging)
            return new TimeMeasureTool(logger);
        return NOP;
    }

    /**
     * Logs the time since the last step or the beginning.
     */
    public void step(String name) {
        if (!doLogging)
            return;
        long time = System.currentTimeMillis()-last;
        last = System.currentTimeMillis();
        log.info(name + " took "+ time +"ms");
        steps++;
    }

    /**
     * Logs the time since the beginning.
     */
    public void total(String name) {
        if (!doLogging)
            return;
        steps++;
        long total = System.currentTimeMillis()-start;
        log.info(name + " took "+ (total) +"ms" + " average "+ (total/steps) +"ms per step");
    }
}
