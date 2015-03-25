/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2015 tarent solutions GmbH
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
package de.tarent.aa.veraweb.db.entity;

import java.lang.String;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity 'Person'.
 *
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 *
 */
@Entity
@Table(name = "tperson")
@NamedQueries({ @NamedQuery(name = Person.GET_PERSON_BY_FIRSTNAME, query = "SELECT p FROM Person p WHERE p.firstName = :firstname") })
public class Person extends AbstractEntity {

    public static final String GET_PERSON_BY_FIRSTNAME = "getPersonByFirstName";

    /**
     * Generated Serial id.
     */
    private static final long serialVersionUID = 8013025384753554359L;

    /**
     * Primary key (sequential number).
     */
    @Id
    @SequenceGenerator(name = "personSeqGen", sequenceName = "tperson_pk_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personSeqGen")
    @Column(name = "pk")
    private Long id;

    /**
     * Created by.
     */
    @Column(name = "createdby")
    private String createdBy;

    /**
     * Changed by.
     */
    @Column(name = "changedby")
    private String changedBy;

    /**
     * Creation date.
     */
    @Column(name = "created")
    private Timestamp created;

    /**
     * Update date.
     */
    @Column(name = "changed")
    private Timestamp changed;

    /**
     * First name.
     */
    @Column(name = "firstname_a_e1")
    private String firstName;

    /**
     * Last name.
     */
    @Column(name = "lastname_a_e1")
    private String lastName;

    /**
     * Person's assigned organization unit.
     */
    @ManyToOne()
    @JoinColumn(name = "fk_orgunit", referencedColumnName = "pk")
    private Orgunit orgunit;

    /**
     * Person's events as hoster.
     */
    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    private Set<Event> hostingEvents;

    /**
     * Persons's tasks.
     */
    @OneToMany(mappedBy = "responsiblePerson", fetch = FetchType.LAZY)
    private Set<Task> tasks;

    /**
     * Username.
     */
    @Column(name = "username")
    private String username;

    /*
     * TODO: add complete property list
     */

    /**
     * Default constructor.
     */
    public Person() {
        hostingEvents = new HashSet<Event>();
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get created by.
     *
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set created by.
     *
     * @param createdBy
     *            the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get changed by.
     *
     * @return the changedBy
     */
    public String getChangedBy() {
        return changedBy;
    }

    /**
     * Set changed by.
     *
     * @param changedBy
     *            the changedBy to set
     */
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    /**
     * Get creation date.
     *
     * @return the created
     */
    public Timestamp getCreated() {
        return created;
    }

    /**
     * Set creation date.
     *
     * @param created
     *            the created to set
     */
    public void setCreated(Timestamp created) {
        this.created = created;
    }

    /**
     * Get update date.
     *
     * @return the changed
     */
    public Timestamp getChanged() {
        return changed;
    }

    /**
     * Set update date.
     *
     * @param changed
     *            the changed to set
     */
    public void setChanged(Timestamp changed) {
        this.changed = changed;
    }

    /**
     * Get first name.
     *
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set first name.
     *
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get last name.
     *
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set last name.
     *
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get assigned organization unit.
     *
     * @return the orgunit
     */
    public Orgunit getOrgunit() {
        return orgunit;
    }

    /**
     * Set assigned organization unit.
     *
     * @param orgunit
     *            the orgunit to set
     */
    public void setOrgunit(Orgunit orgunit) {
        this.orgunit = orgunit;
    }

    /**
     * Get assigned hosting events.
     *
     * @return the hostingEvents
     */
    public Set<Event> getHostingEvents() {
        return hostingEvents;
    }

    /**
     * Set assigned hosting events.
     *
     * @param hostingEvents
     *            the hostingEvents to set
     */
    public void setHostingEvents(Set<Event> hostingEvents) {
        this.hostingEvents = hostingEvents;
    }

    /**
     * @return the tasks
     */
    public Set<Task> getTasks() {
        return tasks;
    }

    /**
     * @param tasks
     *            the tasks to set
     */
    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
