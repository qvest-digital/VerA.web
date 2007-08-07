/*
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

public class Event extends AbstractHistoryBean implements OrgUnitDependent {
	/** PK */
	public Integer id;
	/** Orgunit */
	public Integer orgunit;
	/** Einladungsart, siehe TYPE_* */
	public Integer invitationtype;
	/** FK auf veraweb.tperson.pk */
	public Integer host;
	public String shortname;
	public String eventname;
	public Timestamp begin;
	public Timestamp end;
	public Boolean invitepartner;
	public String hostname;
	public Integer maxguest;
	public String location;
	public String note;
	public String createdby;
	public String changedby;
	public Timestamp created;
	public Timestamp changed;

	public void verify() throws BeanException {
		if (begin != null && end != null && begin.after(end)) {
			Timestamp timestamp = begin;
			begin = end;
			end = timestamp;
		}
		
		if (shortname == null || shortname.length() == 0)
			addError("Die Veranstaltung kann nicht gespeichert werden. Vergeben Sie bitte eine Kurzbezeichnung.");
		if (begin == null)
			addError("Sie m&uuml;ssen den Beginn der Veranstaltung im Format TT.MM.JJJJ sowie SS.MM angeben.");
	}

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Writer ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Hier sind es die Bemerkungsfelder, wenn der Benutzer nicht in der Gruppe
     * {@link PersonalConfigAA#GROUP_READ_REMARKS} der hierzu freigeschalteten ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#clearRestrictedFields(de.tarent.octopus.server.OctopusContext)
     */
    public void clearRestrictedFields(OctopusContext cntx) throws BeanException {
        PersonalConfig personalConfig = cntx != null ? cntx.configImpl() : null;
        if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
            note = null;
        }
        super.clearRestrictedFields(cntx);
    }
}
