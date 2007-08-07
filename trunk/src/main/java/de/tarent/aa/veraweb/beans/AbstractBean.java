package de.tarent.aa.veraweb.beans;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.MapBean;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Diese Klasse stellt eine abstrakte Basis für Beans auf Basis der
 * {@link de.tarent.octopus.custom.beans.MapBean} dar.
 * 
 * @author christoph
 */
public abstract class AbstractBean extends MapBean {
	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 */
	public void checkRead(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_ADMIN);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
     * Default-Test ist, ob der Benutzer Administrator ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 */
	public void checkWrite(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_ADMIN);
	}

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Achtung: Bei Benutzern, die diese Bean auch schreiben dürfen
     * (siehe {@link #checkWrite(OctopusContext)}), sollte die Bean hier nicht
     * verändert werden. 
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     */
    public void clearRestrictedFields(OctopusContext cntx) throws BeanException {
    }
    
	/**
	 * Diese Methode testet, ob im aktuellen Kontext der User der übergebenen
	 * Gruppe zugeordenet ist.
	 * 
	 * @param cntx
	 * @param group
	 * @throws BeanException
	 */
	protected void checkGroup(OctopusContext cntx, String group) throws BeanException {
		PersonalConfig personalConfig = cntx != null ? cntx.configImpl() : null;
		if (personalConfig == null)
			throw new BeanException("No personal config");
		if (!personalConfig.isUserInGroup(group))
			throw new BeanException("Only group " + group + " may write " + getClass().getName());
	}
}
