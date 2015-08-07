package de.tarent.aa.veraweb.beans;

import de.tarent.dblayer.sql.SQL;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Diese Bean stellt einen Eintrag der Tabelle veraweb.tevent_precondition, eine
 * Vorbedingung, dar.
 *
 * @author sweiz - tarent solutions GmbH on 30.07.15.
 */
public class EventPrecondition extends AbstractBean {
    public Integer event_main;
    public Integer event_precondition;
    public Integer invitationstatus;
    public Timestamp max_begin;
    //Needed to show shortname of event in velocity
    public String shortName;

    /**
     * Diese Methode überprüft die eingegangenen Werte von event_main, event_precondition und invitationstatus
     * auf null und 0.
     *
     * @throws de.tarent.octopus.beans.BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     */
    @Override
    public void verify() throws BeanException {
        if (event_main == null || event_main.intValue() == 0) {
            addError("Die Zuordnung einer Vorbedingung zu einer Veranstaltung ist fehlerhaft.");
        }
        if (event_precondition == null || event_precondition.intValue() == 0) {
            addError("Die Zuordnung einer Vorbedingung zu einer Veranstaltung ist fehlerhaft.");
        }
        if (invitationstatus == null || invitationstatus.intValue() == 0) {
            addError("Die Zuordnung einer Vorbedingung zu einer Veranstaltung ist fehlerhaft.");
        }
        if (max_begin == null) {
            addError("Die Zuordnung einer Vorbedingung zu einer Veranstaltung ist fehlerhaft.");
        }
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     *
     * @param cntx Octopus-Kontext
     * @throws de.tarent.octopus.beans.BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Writer ist.
     *
     * @param cntx Octopus-Kontext
     * @throws de.tarent.octopus.beans.BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }
}
