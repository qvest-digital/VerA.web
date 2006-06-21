/*
 * Copyright (c) tarent GmbH
 * Bahnhofstrasse 13 . 53123 Bonn
 * www.tarent.de . info@tarent.de
 *
 * Created on 21.06.2006
 */

package de.tarent.octopus.extensions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple extension loader that relieves the calling code of extensive
 * error handling on instantiation of the selected extension.
 * TODO: this should be extended to include a simple configurable extension
 * description and automatic loading and running of extensions from
 * a configuration.
 *
 * @author Michael Kleinhenz (m.kleinhenz@tarent.de)
 */
public class OctopusExtensionLoader
{
    private static Logger logger = Logger.getLogger(OctopusExtensionLoader.class.getName());
    
    /**
     * Loads, initializes and starts the extension given by classname.
     * On initialization, the given parameter is used.
     * 
     * @param classname Extension class to be initialized and started.
     * @param param Parameter passed to the extension's initialize method.
     * @return Extension instance or null if the extension loading failed.
     */
    public static OctopusExtension load(String classname, Object param)
    {
        logger.info("Enabling octopus extension: " + classname);

        OctopusExtension extension = null;
        try
        {
            extension = (OctopusExtension)Class.forName(classname).newInstance();
        }
        catch (InstantiationException e)
        {
            logger.log(Level.SEVERE, "Error getting extension instance: " + classname, e);
        }
        catch (IllegalAccessException e)
        {
            logger.log(Level.SEVERE, "Illegal Access getting extension instance: " + classname, e);
        }
        catch (ClassNotFoundException e)
        {
            logger.log(Level.SEVERE, "Extension class not found: " + classname, e);
        }

        if (extension!=null)
        {
            extension.initialize(param);
            extension.start();
        }

        return extension;
    }
}
