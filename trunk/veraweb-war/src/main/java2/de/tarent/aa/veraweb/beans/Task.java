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

    @Override
    public void verify() throws BeanException {
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
            addError("Die Aufgabe kann nicht gespeichert werden. Vergeben Sie bitte einen Titel.");
        }
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

    /**
     * Clear restricted fields.
     * 
     * @param cntx
     *            Octopus context
     * @throws BeanException
     *             thrown exception in case of occurred bean errors
     * @see de.tarent.aa.veraweb.beans.AbstractBean#clearRestrictedFields(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void clearRestrictedFields(final OctopusContext cntx) throws BeanException {
        // PersonalConfig personalConfig = cntx != null ? cntx.personalConfig() : null;
        // if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
        // note = null;
        // }
        super.clearRestrictedFields(cntx);
    }
}
