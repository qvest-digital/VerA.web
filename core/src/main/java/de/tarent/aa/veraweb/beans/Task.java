package de.tarent.aa.veraweb.beans;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

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
     * Begin time. This bean properties is not mapped to database field.
     */
    public String starttime;

    /**
     * End time. This bean properties is not mapped to database field.
     */
    public String endtime;

    private VerawebMessages messages;

    public Task() {
    }

    public Task(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("pk");
        this.eventId = resultSet.getInt("fk_event");
        this.title = resultSet.getString("title");
        this.description = resultSet.getString("description");
        this.startdate = resultSet.getTimestamp("startdate");
        this.enddate = resultSet.getTimestamp("enddate");
        this.degreeofcompletion = resultSet.getInt("degree_of_completion");
        this.personId = resultSet.getInt("fk_person");
        this.priority = resultSet.getInt("priority");
        this.createdby = resultSet.getString("createdby");
        this.changedby = resultSet.getString("changedby");
        this.created = resultSet.getTimestamp("created");
        this.changed = resultSet.getTimestamp("changed");
    }

    public void verify(OctopusContext octopusContext) throws BeanException {
        if (messages == null) {
            messages = new VerawebMessages(octopusContext);
        }

        if ((starttime != null && starttime.length() > 0 && startdate == null)
          || (endtime != null && endtime.length() > 0 && enddate == null)) {
            addError(messages.getMessageTaskTimeWithoutDate());
        }

        if (enddate != null) {
            if (enddate.before(new Date())) {
                addError(messages.getMessageTaskEndDateNotFuture());
            } else if (startdate == null) {
                addError(messages.getMessageTaskEndDateWithoutBeginDate());
            } else if (startdate.after(enddate)) {
                addError(messages.getMessageTaskBeginDateBeforeEndDate());
            }
        }
        if (title == null || title.trim().length() == 0) {
            addError(messages.getMessageTaskNoShortname());
        }
        if (description != null && description.length() > 1000) {
            addError(messages.getMessageTaskMaxRemarkRechaed());
        }
        DateHelper.temporary_fix_translateErrormessageEN2DE(this.getErrors(), octopusContext);
    }

    /**
     * Checks whether logged in user in current context is allowed to read this bean.<br>
     * Group {@link PersonalConfigAA#GROUP_READ_STANDARD} is required.
     *
     * @param octopusContext Octopus context
     * @throws BeanException thrown exception if user is prohibited from reading this bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(final OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Checks whether logged in user in current context is allowed to read this bean.<br>
     * Group {@link PersonalConfigAA#GROUP_WRITE} is required.
     *
     * @param octopusContext Octopus context
     * @throws BeanException thrown exception if user is prohibited from writing this bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(final OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_WRITE);
    }

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
     * @param id the id to set
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Get fk_event
     *
     * @return the id
     */
    public Integer getEventId() {
        return this.eventId;
    }

    /**
     * Set fk_event
     *
     * @param eventId fk_event
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
     * @param title the title to set
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
     * @param description the description to set
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
     * @param startdate the startdate to set
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
     * @param enddate the enddate to set
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
     * @param degreeofcompletion the degreeofcompletion to set
     */
    public void setDegreeofcompletion(final Integer degreeofcompletion) {
        this.degreeofcompletion = degreeofcompletion;
    }

    /**
     * Get person responsible.
     *
     * @return The person id
     */
    public Integer getPersonId() {
        return this.personId;
    }

    /**
     * Set person id
     *
     * @param personId The person id
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
     * @param personName The person name
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
     * @param priority the priority to set
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
     * @param createdby the createdby to set
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
     * @param changedby the changedby to set
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
     * @param created the created to set
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
     * @param changed the changed to set
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
     * @param starttime the start time to set
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
     * @param endtime the end time to set
     */
    public void setEndtime(final String endtime) {
        this.endtime = endtime;
    }

    public VerawebMessages getMessages() {
        return messages;
    }

    public void setMessages(VerawebMessages messages) {
        this.messages = messages;
    }
}
