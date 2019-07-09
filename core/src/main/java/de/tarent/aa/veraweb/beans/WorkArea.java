package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * The bean class WorkArea represents an entity that
 * stores so-called standing data for association with
 * {@link Person}s.
 *
 * @author cklein
 * @see de.tarent.aa.veraweb.worker.WorkAreaWorker
 * @since 1.2.0
 */
public class WorkArea extends AbstractBean {
    public Integer id;
    public Integer orgunit;
    public String name;

    /**
     * Verifies that the name was correctly set.
     *
     * @param octopusContext FIXME
     * @throws BeanException FIXME
     */
    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);
        if (name == null || name.trim().length() == 0) {
            addError(messages.getMessageWorkAreaMissingName());
        }
    }

    /**
     * Tests whether the user may read the bean
     * from the database.
     *
     * @param octopusContext the current octopus context
     * @throws BeanException FIXME
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
        checkGroups(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD, PersonalConfigAA.GROUP_SYSTEM_USER);
    }

    /**
     * Tests whether the user may write the bean
     * to the database.
     *
     * @param octopusContext the current octopus context
     * @throws BeanException FIXME
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroups(octopusContext, PersonalConfigAA.GROUP_ADMIN, PersonalConfigAA.GROUP_SYSTEM_USER);
    }
}
