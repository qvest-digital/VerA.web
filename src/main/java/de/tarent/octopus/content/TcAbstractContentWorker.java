/* $Id: TcAbstractContentWorker.java,v 1.1.1.1 2005/11/21 13:33:37 asteban Exp $
 * 
 * tarent-octopus, Webservice Data Integrator and Applicationserver
 * Copyright (C) 2002 tarent GmbH
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'tarent-octopus'
 * (which makes passes at compilers) written
 * by Hendrik Helwich and Michael Klink.
 * signature of Elmar Geese, 1 June 2002
 * Elmar Geese, CEO tarent GmbH
 */

package de.tarent.octopus.content;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.request.TcRequest;

/** 
 * Worker zur bequemeren Bedienung aller Content-Worker. Ruft automatisch über Reflection
 * die mit der Action gleichnamige Mathode auf.
 *
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public abstract class TcAbstractContentWorker implements TcContentWorker {
    private static Logger logger = Logger.getLogger(TcAbstractContentWorker.class.getName());
    private TcCommonConfig commonConfig;

    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
        throws TcContentProzessException {

        if (commonConfig == null)
            commonConfig = tcConfig.getCommonConfig();

        String result = RESULT_error;
        Class workerClass = this.getClass();
        Class[] parameterTypes = new Class[] { TcConfig.class, TcRequest.class, TcContent.class };
        Method actionMethod;
        Object[] arguments = new Object[] { tcConfig, tcRequest, tcContent };
        try {
            actionMethod = workerClass.getMethod(actionName, parameterTypes);
            if (logger.isLoggable(Level.INFO))
                logger.finest("Starte Methode \"" + workerClass.getName() + "." + actionName + "(...)\"");
//            result = (String) actionMethod.invoke(this, arguments);
            actionMethod.invoke(this, arguments);
            result = RESULT_ok;
        } catch (NoSuchMethodException e) {
            throw new TcContentProzessException(
                "Nicht unterstützte Action im Worker '" + workerClass.getName() + "': " + actionName);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Fehler im Worker '" + workerClass.getName() + "'", e);
            throw new TcContentProzessException(e);
        } catch (InvocationTargetException e) {
            logger.log(Level.SEVERE, "Fehler im Worker '" + workerClass.getName() + "'", e);
            throw new TcContentProzessException(e);
        }
        return result;
    }
}
