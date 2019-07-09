package de.tarent.aa.veraweb.beans;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class UserConfig extends AbstractBean {
    /**
     * ID
     */
    public Integer id;
    /**
     * FK auf User
     */
    public Integer user;
    /**
     * Key
     */
    public String key;
    /**
     * Value
     */
    public String value;

    /**
     * Hebt den Leseschutz für die User-Config auf.
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
    }

    /**
     * Hebt den Schreibschutz für die User-Config auf.
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
    }
}
