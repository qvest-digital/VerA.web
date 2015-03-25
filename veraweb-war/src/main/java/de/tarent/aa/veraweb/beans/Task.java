/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;
import java.util.Date;

import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

/**
 * Bean 'Task'.
 *
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 */
public class Task extends AbstractHistoryBean {

    /**
     * PK.
     */
    public Integer id;

    /**
     * fk_event.
     */
    public Integer eventId;

    /**
     * Title.
     */
    public String title;

    /**
     * Description.
     */
    public String description;

    /**
     * Start date.
     */
    public Timestamp startdate;

    /**
     * End date.
     */
    public Timestamp enddate;

    /**
     * Degree of completion.
     */
    public Integer degreeofcompletion;

    /**
     * Person
     */
    public Integer personId;

    /**
     * Name of person
     */
    public String personName;

    /**
     * Priority.
     */
    public Integer priority;

    /**
     * Created by.
     */
    public String createdby;

    /**
     * Changed by.
     */
    public String changedby;

    /**
     * Created date.
     */
    public Timestamp created;

    /**
     * Changed date.
     */
    public Timestamp changed;

    /**
     *  Begin time. This bean properties is not mapped to database field.
     */
    public String starttime;

    /**
     *  End time. This bean properties is not mapped to database field.
     */
    public String endtime;

    /**
     * Get id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set id.
     *
     * @param id
     *            the id to set
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Get fk_event
     *
     * @param eventId
     *
     */
    public Integer getEventId() {
        return this.eventId;
    }

    /**
     * Set fk_event
     *
     * @param fk_event
     *
     */
    public void setEventId(final Integer eventId) {
        this.eventId = eventId;
    }

    /**
     * Get title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title.
     *
     * @param title
     *            the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Get description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description.
     *
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Get start date.
     *
     * @return the startdate
     */
    public Timestamp getStartdate() {
        return startdate;
    }

    /**
     * Set start date.
     *
     * @param startdate
     *            the startdate to set
     */
    public void setStartdate(final Timestamp startdate) {
        this.startdate = startdate;
    }

    /**
     * Get end date.
     *
     * @return the enddate
     */
    public Timestamp getEnddate() {
        return enddate;
    }

    /**
     * Set end date.
     *
     * @param enddate
     *            the enddate to set
     */
    public void setEnddate(final Timestamp enddate) {
        this.enddate = enddate;
    }

    /**
     * Get degree if completion.
     *
     * @return the degreeofcompletion
     */
    public Integer getDegreeofcompletion() {
        return degreeofcompletion;
    }

    /**
     * Set degree of completion.
     *
     * @param degreeofcompletion
     *            the degreeofcompletion to set
     */
    public void setDegreeofcompletion(final Integer degreeofcompletion) {
        this.degreeofcompletion = degreeofcompletion;
    }

    /**
     * Get person responsible.
     *
     * @return
     */
    public Integer getPersonId() {
        return this.personId;
    }

    /**
     * Set person id
     *
     * @param personId
     */
    public void setPersonId(final Integer personId) {
        this.personId = personId;
    }

    /**
     * Get person name
     *
     * @return personName
     */
    public String getPersonName() {
        return this.personName;
    }

    /**
     * Set person name
     *
     * @param personName
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * Get priority.
     *
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Set priority.
     *
     * @param priority
     *            the priority to set
     */
    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    /**
     * Set created by.
     *
     * @return the createdby
     */
    public String getCreatedby() {
        return createdby;
    }

    /**
     * Set created by.
     *
     * @param createdby
     *            the createdby to set
     */
    public void setCreatedby(final String createdby) {
        this.createdby = createdby;
    }

    /**
     * Get changed by.
     *
     * @return the changedby
     */
    public String getChangedby() {
        return changedby;
    }

    /**
     * Set changed by.
     *
     * @param changedby
     *            the changedby to set
     */
    public void setChangedby(final String changedby) {
        this.changedby = changedby;
    }

    /**
     * Get created.
     *
     * @return the created
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Set created.
     *
     * @param created
     *            the created to set
     */
    public void setCreated(final Timestamp created) {
        this.created = created;
    }

    /**
     * Get changed.
     *
     * @return the changed
     */
    public Timestamp getChanged() {
        return changed;
    }

    /**
     * Set changed.
     *
     * @param changed
     *            the changed to set
     */
    public void setChanged(final Timestamp changed) {
        this.changed = changed;
    }

    /**
     * Get start time.
     *
     * @return the starttime
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     * Set start time.
     *
     * @param starttime
     *            the start time to set
     */
    public void setStarttime(final String starttime) {
        this.starttime = starttime;
    }

    /**
     * Get end time.
     *
     * @return the endtime
     */
    public String getEndtime() {
        return endtime;
    }

    /**
     * Set end time.
     *
     * @param endtime
     *            the end time to set
     */
    public void setEndtime(final String endtime) {
        this.endtime = endtime;
    }

    @Override
    public void verify() throws BeanException {
        if ((starttime != null && starttime.length() > 0 && startdate == null)
                || (endtime != null && endtime.length() > 0 && enddate == null)) {
            addError("Die Eingabe einer Uhrzeit ohne ein zugeh\u00f6riges Datum ist nicht zul\u00e4ssig.");
        }

        if (enddate != null) {
            if (enddate.before(new Date())) {
                addError("Die Aufgabe kann nicht gespeichert werden. Das Enddatum muss in der Zukunft liegen.");
            } else if (startdate == null) {
                addError("Die Aufgabe kann nicht gespeichert werden. Bitte geben Sie neben dem Enddatum auch ein "
                        + "Startdatum an.");
            } else if (startdate.after(enddate)) {
                addError("Die Aufgabe kann nicht gespeichert werden. Das Startdatum muss vor dem Enddatum liegen.");
            }
        }
        if (title == null || title.trim().length() == 0) {
            addError("Die Aufgabe kann nicht gespeichert werden. Vergeben Sie bitte eine Kurzbezeichnung.");
        }
        if (description != null && description.length() > 1000) {
            addError("Die Aufgabe kann nicht gespeichert werden. Das Feld Beschreibung darf nicht mehr als 1000 Zeichen besitzen.");
        }
        DateHelper.temporary_fix_translateErrormessageEN2DE( this.getErrors() );
    }

    /**
     * Checks whether logged in user in current context is allowed to read this bean.<br>
     * Group {@link PersonalConfigAA#GROUP_READ_STANDARD} is required.
     *
     * @param cntx
     *            Octopus context
     * @throws BeanException
     *             thrown exception if user is prohibited from reading this bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(final OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Checks whether logged in user in current context is allowed to read this bean.<br>
     * Group {@link PersonalConfigAA#GROUP_WRITE} is required.
     *
     * @param cntx
     *            Octopus context
     * @throws BeanException
     *             thrown exception if user is prohibited from writing this bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(final OctopusContext cntx) throws BeanException {
        checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
    }

}
