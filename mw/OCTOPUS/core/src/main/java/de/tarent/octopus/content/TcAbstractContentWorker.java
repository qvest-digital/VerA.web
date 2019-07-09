package de.tarent.octopus.content;
import de.tarent.octopus.config.TcCommonConfig;
import de.tarent.octopus.config.TcConfig;
import de.tarent.octopus.request.TcRequest;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Worker zur bequemeren Bedienung aller Content-Worker. Ruft automatisch über Reflection
 * die mit der Action gleichnamige Mathode auf.
 *
 * @author <a href="mailto:H.Helwich@tarent.de">Hendrik Helwich</a>, <b>tarent GmbH</b>
 */
@Log4j2
public abstract class TcAbstractContentWorker implements TcContentWorker {
    private TcCommonConfig commonConfig;

    public String doAction(TcConfig tcConfig, String actionName, TcRequest tcRequest, TcContent tcContent)
      throws TcContentProzessException {

        if (commonConfig == null) {
            commonConfig = tcConfig.getCommonConfig();
        }

        String result = RESULT_error;
        Class workerClass = this.getClass();
        Class[] parameterTypes = new Class[] { TcConfig.class, TcRequest.class, TcContent.class };
        Method actionMethod;
        Object[] arguments = new Object[] { tcConfig, tcRequest, tcContent };
        try {
            actionMethod = workerClass.getMethod(actionName, parameterTypes);
            if (logger.isTraceEnabled()) {
                logger.trace("Starte Methode \"" + workerClass.getName() + "." + actionName + "(...)\"");
            }
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
