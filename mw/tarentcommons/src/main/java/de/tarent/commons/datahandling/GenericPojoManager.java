package de.tarent.commons.datahandling;
import java.lang.reflect.Method;

/**
 * Interface for a controler for pojos
 *
 * @author Sebastian Mancke, tarent GmbH
 */
public interface GenericPojoManager {

    /**
     * This method is called when arbitrary methods are invoked on the generic pojo.
     *
     * @param pojo           the called pojo
     * @param theGenericPojo the generic pojo implementation for the pojo
     * @param method         the called method
     * @param args           the suplied params the the call
     * @return the result for the call
     */
    public Object methodCalled(Object pojo, GenericPojo theGenericPojo, Method method, Object[] args);
}
