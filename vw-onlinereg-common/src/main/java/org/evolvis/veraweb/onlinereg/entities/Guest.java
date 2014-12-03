package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by mley on 03.08.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tguest")
@NamedQueries({
    @NamedQuery(name = "Guest.findByEventAndUser", query = "SELECT g FROM Guest g where fk_event = :eventId and fk_person = :userId")
})
@NamedNativeQueries({
    @NamedNativeQuery(name="Event.list.userevents", query = "SELECT e.* FROM tevent e " +
            "JOIN tguest g on g.fk_event = e.pk " +
            "JOIN tperson tp on g.fk_person = tp.pk " +
            "WHERE (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
            "AND tp.pk = :fk_person", resultClass=Event.class),
    @NamedNativeQuery(name="Guest.guestByUUID", query = "SELECT count(g.*) FROM tguest g " +
    		"LEFT JOIN tperson on tperson.pk=g.fk_person " +
    		"WHERE delegation=:uuid AND tperson.iscompany='t'"),
	@NamedNativeQuery(name = "Guest.findEventIdByDelegationUUID", query ="SELECT g.* FROM tguest g  " +
    		"LEFT JOIN tperson on tperson.pk=g.fk_person " +
    		"WHERE delegation=:uuid AND tperson.iscompany='t'", resultClass=Guest.class)

})
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    private int fk_event;
    private int fk_person;
    private String gender;
    private String gender_p;
    private int invitationstatus;
    private int invitationtype;
    private String notehost;
    private String delegation;

    public int getFk_event() {
        return fk_event;
    }

    public void setFk_event(int fk_event) {
        this.fk_event = fk_event;
    }

    public int getFk_person() {
        return fk_person;
    }

    public void setFk_person(int fk_person) {
        this.fk_person = fk_person;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender_p() {
        return gender_p;
    }

    public void setGender_p(String gender_p) {
        this.gender_p = gender_p;
    }

    public int getInvitationstatus() {
        return invitationstatus;
    }

    public void setInvitationstatus(int invitationstatus) {
        this.invitationstatus = invitationstatus;
    }

    public String getNotehost() {
        return notehost;
    }

    public void setNotehost(String notehost) {
        this.notehost = notehost;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public int getInvitationtype() {
        return invitationtype;
    }

    public void setInvitationtype(int invitationtype) {
        this.invitationtype = invitationtype;
    }
}

