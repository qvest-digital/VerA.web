package de.tarent.aa.veraweb.cucumber.env.event;

/**
 * Handler for processing hooks before cucumber starts.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * @version 1.0
 * 
 */
public interface HandlerStart {

    /**
     * This will be called before cucumber starts.
     */
    public void handleStart();
}
