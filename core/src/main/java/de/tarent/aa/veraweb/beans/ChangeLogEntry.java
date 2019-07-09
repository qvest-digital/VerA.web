package de.tarent.aa.veraweb.beans;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.util.Date;

/**
 * The bean class ChangeLogEntry represents a single entry
 * in the newly introduced change logging feature.
 *
 * Each changelog entry stores information on the change
 * action, which is one of insert, delete or update and
 * the attributes of the object that were changed by the
 * user who committed the action.
 *
 * For now, changelogging is enabled for the following
 * entities {see Person}, {see Guest}, and {see Event}.
 *
 * @author cklein
 * @see de.tarent.octopus.beans.veraweb.BeanChangeLogger
 * @since 1.2.0
 */
public class ChangeLogEntry extends AbstractBean {
    public Integer id;
    public String username;
    public String objectname;
    public String objecttype;
    public Integer objectid;
    public String op;
    public String attributes;
    public Date created;

    /**
     * Creates a new instance of this.
     */
    public ChangeLogEntry() {
        super();
    }

    /**
     * Only admins may read the entity beans from the table.
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_ADMIN);
    }

    /**
     * Anonymous user group requires write access to the
     * entity bean, since a background service will be responsible
     * for purging old entries from change log. The service runs
     * with the priviledge of the anonymous user group.
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        try {
            checkGroup(octopusContext, PersonalConfig.GROUP_ANONYMOUS);
        } catch (BeanException e) {
            checkGroup(octopusContext, PersonalConfig.GROUP_USER);
        }
    }
}
