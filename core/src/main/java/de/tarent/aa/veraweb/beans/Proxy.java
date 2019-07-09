package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.sql.Timestamp;

/**
 * Diese Bohne stellt Stellvertretungen dar.
 *
 * @author mikel
 */
public class Proxy extends AbstractHistoryBean {
    //
    // tproxy
    //
    /**
     * tproxy.pk serial NOT NULL
     */
    public Integer id;

    /**
     * tproxy.fk_user int4 NOT NULL
     */
    public Integer user;

    /**
     * tproxy.proxy varchar(100) NOT NULL
     */
    public String proxy;

    /**
     * tproxy.validfrom timestamptz
     */
    public Timestamp validFrom;

    /**
     * tproxy.validtill timestamptz
     */
    public Timestamp validTill;

    //
    // tuser
    //
    /**
     * tuser.username varchar(100) NOT NULL
     */
    public String userRole;

    /**
     * tuser.fk_orgunit: int4 DEFAULT 0
     */
    public Integer orgunit;

    //
    // Klasse AbstractBean
    //

    /**
     * Der Benutzer und Stellvertreterrolle müssen angegeben sein.
     *
     * @param octopusContext The {@link OctopusContext}
     */
    public void verify(OctopusContext octopusContext) {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

        if (proxy == null || proxy.trim().length() == 0) {
            addError(messages.getMessageProxyNoRepresentative());
        }

        if (user == null || user.intValue() == 0) {
            addError(messages.getMessageProxyNoRole());
        }

        if (validFrom != null && validTill != null && validFrom.after(validTill)) {
            addError(messages.getMessageProxyRepresentativeBeginBeforeEnd());
        }
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist leer.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden
     *                       darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer User ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfig.GROUP_USER);
    }
}
