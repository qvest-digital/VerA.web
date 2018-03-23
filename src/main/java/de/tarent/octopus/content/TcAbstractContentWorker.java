package de.tarent.octopus.content;

/*-
 * tarent-octopus, Webservice Data Integrator and Application Server
 * Copyright © 2002–2018 tarent solutions GmbH and its contributors
 * Copyright © 2015, 2017, 2018 mirabilos (t.glaser@tarent.de)
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;

import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.logging.LogFactory;
import de.tarent.octopus.request.TcRequest;

/**
 * Worker zur bequemeren Bedienung aller Content-Worker. Ruft automatisch über Reflection
 * die mit der Action gleichnamige Mathode auf.
 *
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
public abstract class TcAbstractContentWorker implements TcContentWorker {
    private static Log logger = LogFactory.getLog(TcAbstractContentWorker.class);
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
            if (logger.isTraceEnabled())
                logger.trace("Starte Methode \"" + workerClass.getName() + "." + actionName + "(...)\"");
//            result = (String) actionMethod.invoke(this, arguments);
            actionMethod.invoke(this, arguments);
            result = RESULT_ok;
        } catch (NoSuchMethodException e) {
            throw new TcContentProzessException(
                "Nicht unterstützte Action im Worker '" + workerClass.getName() + "': " + actionName);
        } catch (IllegalAccessException e) {
            logger.error("Fehler im Worker '" + workerClass.getName() + "'", e);
            throw new TcContentProzessException(e);
        } catch (InvocationTargetException e) {
            logger.error("Fehler im Worker '" + workerClass.getName() + "'", e);
            throw new TcContentProzessException(e);
        }
        return result;
    }
}
