package de.tarent.octopus.extensions;

/*
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2017 tarent solutions GmbH and its contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.apache.commons.logging.Log;

import de.tarent.octopus.logging.LogFactory;

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
    private static Log logger = LogFactory.getLog(OctopusExtensionLoader.class);
    
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
            logger.error("Error getting extension instance: " + classname, e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("Illegal Access getting extension instance: " + classname, e);
        }
        catch (ClassNotFoundException e)
        {
            logger.info("Extension class not found: " + classname, e);
        }

        if (extension!=null)
        {
            extension.initialize(param);
            extension.start();
        }

        return extension;
    }
}
