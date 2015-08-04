package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.VerawebCoreMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

public class EventSubEventList extends AbstractBean implements OrgUnitDependent  {
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
    public String eventtype;
	public String hostname;
	public Integer maxguest;
	public Integer maxreserve;
	public Integer location;
	public String note;
	public String createdby;
	public String changedby;
	public Timestamp created;
	public Timestamp changed;
	public String mediarepresentatives;
	public String hash;
	public Boolean login_required;
	public Boolean apply_without_precondition;
	public Integer parenteventid;
	
	@Override
    public void verify() throws BeanException {
		if (begin != null && end != null && begin.after(end)) {
			addError(VerawebCoreMessages.MESSAGE_END_AND_START_DATUM);
		}

		if (shortname == null || shortname.trim().length() == 0)
			addError("Die Veranstaltung kann nicht gespeichert werden. Vergeben Sie bitte eine Kurzbezeichnung.");
		if (begin == null)
			addError("Sie m\u00fcssen den Beginn der Veranstaltung im Format TT.MM.JJJJ angeben.");

		/*
		 * 2009-05-17 cklein
		 * temporarily fixes issue #1529 until i gain access to the old octopus repository
		 */
		DateHelper.temporary_fix_translateErrormessageEN2DE( this.getErrors() );
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
    @Override
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    private void getEvent(OctopusContext cntx) {
    	
    	
//	    if (id == null) return null;
//		Database database = new DatabaseVeraWeb(cntx);
//		Event event = (Event)database.getBean("Event", id);
//	
//	    if (event != null) {
//	        cntx.setContent("event-beginhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.begin)));
//	        cntx.setContent("event-endhastime", Boolean.valueOf(DateHelper.isTimeInDate(event.end)));
//	    }
//		return event;
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
    @Override
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
    @Override
    public void clearRestrictedFields(OctopusContext cntx) throws BeanException {
        PersonalConfig personalConfig = cntx != null ? cntx.personalConfig() : null;
        if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
            note = null;
        }
        super.clearRestrictedFields(cntx);
    }

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
}
