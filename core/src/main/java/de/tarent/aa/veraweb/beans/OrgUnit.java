package de.tarent.aa.veraweb.beans;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * <p>
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.torgunit dar,
 * also einen Mandanten.
 * </p>
 *
 * @author christoph
 * @author mikel
 */
public class OrgUnit extends AbstractBean {
    /**
     * Primärschlüssel
     */
    public Integer id;
    /**
     * Mandantenname
     */
    public String name;

    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

        if (name == null || name.trim().length() == 0) {
            addError(messages.getMessageOrgUnitNoName());
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
     * Test ist, ob der Benutzer Administrator ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroups(octopusContext, PersonalConfigAA.GROUP_ADMIN, PersonalConfigAA.GROUP_SYSTEM_USER);
    }
}
