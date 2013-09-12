package de.tarent.aa.veraweb.cucumber.env.event;

/**
 * Handler for processing hooks after cucumber ends.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public interface HandlerStop {

    /**
     * This will be called after the program will be exit.
     */
    public void handleStop();
}
