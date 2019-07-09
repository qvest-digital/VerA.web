package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Bohne stellt Benutzer mit ihren Berechtigungen dar.
 *
 * @author mikel
 */
public class User extends AbstractBean implements OrgUnitDependent {
    /**
     * tuser.pk: serial NOT NULL
     */
    public Integer id;
    /**
     * tuser.fk_orgunit: int4 DEFAULT 0
     */
    public Integer orgunit;
    /**
     * tuser.username: varchar(100) NOT NULL
     */
    public String name;
    /**
     * tuser.role: int4
     */
    public Integer role;

    /**
     * Der Benutzername (genauer: die AA-Rolle) muss angegeben sein.
     *
     * @param octopusContext The {@link OctopusContext}
     */
    public void verify(final OctopusContext octopusContext) {
        if (name == null || name.trim().length() == 0) {

            final VerawebMessages messages = new VerawebMessages(octopusContext);
            addError(messages.getMessageUserMissingRole());
        }
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist leer.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Mandanten-Administrator oder der SystemUser ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroups(octopusContext, PersonalConfigAA.GROUP_PARTIAL_ADMIN, PersonalConfigAA.GROUP_SYSTEM_USER);
    }

    //
    // Konstanten
    //
    /**
     * Rolle: Restriktiv lesen und exportieren, d.h. gewisse Bemerkungsfelder werden nicht angezeigt
     */
    public final static int ROLE_READ_RESTRICTED = 1;
    /**
     * Rolle: Lesen und exportieren
     */
    public final static int ROLE_READ_FULL = 2;
    /**
     * Rolle: Restriktiv lesen, schreiben und exportieren, d.h. gewisse Bemerkungsfelder werden nicht angezeigt
     */
    public final static int ROLE_READ_WRITE_RESTRICTED = 6;
    /**
     * Rolle: Lesen, schreiben und exportieren
     */
    public final static int ROLE_READ_WRITE_FULL = 3;
    /**
     * Rolle: Administrator innerhalb eines Mandanten
     */
    public final static int ROLE_PARTIAL_ADMIN = 4;
    /**
     * Rolle: globaler Veraweb-Administrator
     */
    public final static int ROLE_GLOBAL_ADMIN = 5;
}
