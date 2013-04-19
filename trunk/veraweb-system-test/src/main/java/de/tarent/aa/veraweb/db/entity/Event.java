package de.tarent.aa.veraweb.db.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.joda.time.DateMidnight;

/**
 * Entity 'Event'.
 * 
 * @author Valentin But (v.but@tarent.de), tarent solutions GmbH
 * 
 */
@Entity
@Table(name = "tevent")
public class Event extends AbstractEntity {

    /**
     * Generated Serial id.
     */
    private static final long serialVersionUID = -60871858130502952L;

    /**
     * Primary key (sequential number).
     */
    @Id
    @SequenceGenerator(name = "eventSeqGen", sequenceName = "tevent_pk_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventSeqGen")
    @Column(name = "pk")
    private Long id;

    /**
     * Invitation type.
     */
    @Column(name = "invitationtype")
    private Integer invitationType;

    /**
     * Short name.
     */
    @Column(name = "shortname")
    private String shortName;

    /**
     * Short name.
     */
    @Column(name = "eventname")
    private String eventName;

    /**
     * Date begin.
     */
    @Column(name = "datebegin")
    private Timestamp startDate;

    /**
     * Date end.
     */
    @Column(name = "dateend")
    private Timestamp endDate;

    /**
     * Indicator for inviting host partner.
     */
    @Column(name = "invitehostpartner")
    private Integer inviteHostPartner;

    /**
     * Host name.
     */
    @Column(name = "hostname")
    private String hostname;

    /**
     * Maximum count of guests.
     */
    @Column(name = "maxguest")
    private Integer maxGuest;

    /**
     * Location.
     */
    @Column(name = "location")
    private String location;

    /**
     * Location.
     */
    @Column(name = "note")
    private String note;

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
     * Event's assigned organization unit.
     */
    @ManyToOne()
    @JoinColumn(name = "fk_orgunit", referencedColumnName = "pk")
    private Orgunit orgunit;

    /**
     * Event's assigned host.
     */
    @ManyToOne()
    @JoinColumn(name = "fk_host", referencedColumnName = "pk")
    private Person host;
    
    /**
     * Event's assigned tasks.
     */
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private Set<Task> tasks;

    /**
     * Default constructor.
     */
    public Event() {
    }

    /**
     * Get id.
     * 
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id.
     * 
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get invitation type.
     * 
     * @return the invitationType
     */
    public Integer getInvitationType() {
        return invitationType;
    }

    /**
     * Set invitation type.
     * 
     * @param invitationType
     *            the invitationType to set
     */
    public void setInvitationType(Integer invitationType) {
        this.invitationType = invitationType;
    }

    /**
     * Get short name.
     * 
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Set short name.
     * 
     * @param shortName
     *            the shortName to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Get event name.
     * 
     * @return the eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Set event name.
     * 
     * @param eventName
     *            the eventName to set
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Get start date.
     * 
     * @return the startDate
     */
    public Timestamp getStartDate() {
        return startDate;
    }

    /**
     * Set start date.
     * 
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    /**
     * Get end date.
     * 
     * @return the endDate
     */
    public Timestamp getEndDate() {
        return endDate;
    }

    /**
     * Set end date.
     * 
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    /**
     * Set flagging for inviting host partner.
     * 
     * @return the inviteHostPartner
     */
    public Integer getInviteHostPartner() {
        return inviteHostPartner;
    }

    /**
     * Get flagging for inviting host partner.
     * 
     * @param inviteHostPartner
     *            the inviteHostPartner to set
     */
    public void setInviteHostPartner(Integer inviteHostPartner) {
        this.inviteHostPartner = inviteHostPartner;
    }

    /**
     * Get host name.
     * 
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Set host name.
     * 
     * @param hostname
     *            the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Get maximum count of guests.
     * 
     * @return the maxGuest
     */
    public Integer getMaxGuest() {
        return maxGuest;
    }

    /**
     * Set maximum count of guests.
     * 
     * @param maxGuest
     *            the maxGuest to set
     */
    public void setMaxGuest(Integer maxGuest) {
        this.maxGuest = maxGuest;
    }

    /**
     * Get location.
     * 
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set location.
     * 
     * @param location
     *            the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get note.
     * 
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Set note.
     * 
     * @param note
     *            the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get created by name.
     * 
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Set created by name.
     * 
     * @param createdBy
     *            the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Get changed by name.
     * 
     * @return the changedBy
     */
    public String getChangedBy() {
        return changedBy;
    }

    /**
     * Set changed by name.
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
     * Set update date
     * 
     * @param changed
     *            the changed to set
     */
    public void setChanged(Timestamp changed) {
        this.changed = changed;
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
     * 
     * Get assigned host.
     * 
     * @return the host
     */
    public Person getHost() {
        return host;
    }

    /**
     * Set assigned host.
     * 
     * @param host
     *            the host to set
     */
    public void setHost(Person host) {
        this.host = host;
    }

    /**
     * @return the tasks
     */
    public Set<Task> getTasks() {
        return tasks;
    }

    /**
     * @param tasks the tasks to set
     */
    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
    
    public static Event fillEmptyMandatoryFields(final Event event) {
        if (event.getStartDate() == null) {
            event.setStartDate(new Timestamp(DateMidnight.now().minus(1).getMillis()));
        }
        if (event.getEndDate() == null) {
            event.setEndDate(new Timestamp(DateMidnight.now().plus(1).getMillis()));
        }
        if (event.getInvitationType() == null) {
            event.setInvitationType(Integer.valueOf(1));
        }
        if (event.getLocation() == null) {
            event.setLocation(String.valueOf(""));
        }
        return event;
    }

}
