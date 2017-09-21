package org.evolvis.veraweb.onlinereg.entities;

/*-
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
 * @author jnunez
 * @author sweiz
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tguest")
@NamedQueries({
    @NamedQuery(name = "Guest.findByEventAndUser",
            query = "SELECT g FROM Guest g where fk_event = :eventId and fk_person = :userId"),
    @NamedQuery(name = "Guest.findByNoLoginUUID",
            query = "SELECT g FROM Guest g where login_required_uuid = :noLoginRequiredUUID"),
    @NamedQuery(name = "Guest.findByDelegationAndUser",
            query = "SELECT g FROM Guest g where delegation = :delegation and fk_person = :userId"),
    @NamedQuery(name = "Guest.findIdByEventAndUser",
            query = "SELECT g.pk FROM Guest g where fk_event = :eventId and fk_person = :userId"),
    @NamedQuery(name = "Guest.findImageByDelegationAndUser",
            query = "SELECT g.image_uuid FROM Guest g where delegation=:delegationUUID and fk_person = :userId"),
    @NamedQuery(name = "Guest.getGuestByNoLoginRequiredUUID",
            query = "SELECT g.pk FROM Guest g where login_required_uuid = :noLoginRequiredUUID"),
    @NamedQuery(name = "Guest.getGuestById",
            query = "SELECT g FROM Guest g where pk=:guestId"),
    @NamedQuery(name = "Guest.isReserve",
                query = "SELECT g.reserve FROM Guest g WHERE g.fk_event=:eventId AND g.osiam_login=:username")
})
@NamedNativeQueries({
    @NamedNativeQuery(name="Event.list.userevents",
            query = "SELECT e.* " +
                    "FROM tevent e JOIN tguest g on g.fk_event = e.pk JOIN tperson tp on g.fk_person = tp.pk " +
                    "WHERE (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
                    "AND tp.pk = :fk_person ORDER BY e.datebegin ASC", resultClass=Event.class),
    @NamedNativeQuery(name="Event.count.userevents",
            query = "SELECT count(e.*) " +
                    "FROM tevent e JOIN tguest g on g.fk_event = e.pk JOIN tperson tp on g.fk_person = tp.pk " +
                    "WHERE tp.pk = :fk_person "),
    @NamedNativeQuery(name="Guest.guestByUUID",
            query = "SELECT count(g.*) FROM tguest g LEFT JOIN tperson on tperson.pk=g.fk_person " +
                    "WHERE delegation=:uuid AND tperson.iscompany='t'"),
	@NamedNativeQuery(name = "Guest.findEventIdByDelegationUUID",
            query ="SELECT g.* FROM tguest g  LEFT JOIN tperson on tperson.pk=g.fk_person " +
                    "WHERE delegation=:uuid AND tperson.iscompany='t'", resultClass=Guest.class),
    @NamedNativeQuery(name = "Guest.checkUserRegistration",
            query = "SELECT COUNT(g.*) FROM tguest g WHERE g.fk_event=:eventId AND g.osiam_login LIKE :username "),
    @NamedNativeQuery(name = "Guest.checkUserRegistrationToAccept",
            query = "SELECT COUNT(g.*) FROM tguest g WHERE g.fk_event=:eventId AND g.osiam_login LIKE :username " +
                    "AND invitationstatus=1"),
    @NamedNativeQuery(name = "Guest.checkUserRegistrationWithoutLogin",
            query = "SELECT COUNT(g.*) FROM tguest g WHERE g.fk_event=:eventId AND g.login_required_uuid " +
                    "LIKE :noLoginRequiredUUID AND invitationstatus=1 "), // TODO invitation status as parameter?
	@NamedNativeQuery(name = "Guest.findByDelegationUUID",
            query = "SELECT g.* FROM tguest g LEFT JOIN tperson p ON g.fk_person=p.pk WHERE g.delegation=:delegation " +
                    "AND p.isCompany='t'", resultClass=Guest.class),
    @NamedNativeQuery(name = "Guest.isGuestForEvent",
            query = "SELECT COUNT(*) FROM tguest g WHERE g.osiam_login=:osiam_login AND g.delegation=:delegation")
})
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    private Integer fk_event;
    private int fk_person;
    private String gender;
    private String gender_p;
    private Integer invitationstatus;
    private Integer invitationtype;
    private String notehost;
    private String delegation;
    private Integer fk_category;
    private String osiam_login;
    private String login_required_uuid;
    private String image_uuid;
    //Standard 0 = not on reserve
    private Integer reserve = 0;

	public Integer getPk() {
		return pk;
	}

    public void setPk(Integer pk) { this.pk = pk; }

    public Integer getFk_event() {
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

    public Integer getInvitationstatus() {
        return invitationstatus;
    }
    public void setInvitationstatus(Integer invitationstatus) {
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
    public void setInvitationtype(Integer invitationtype) {
        this.invitationtype = invitationtype;
    }

	public Integer getFk_category() {
		return fk_category;
	}
	public void setFk_category(Integer fk_category) {
		this.fk_category = fk_category;
	}

	public String getOsiam_login() {
		return osiam_login;
	}
	public void setOsiam_login(String osiam_login) {
		this.osiam_login = osiam_login;
	}

    public String getLogin_required_uuid() {
        return login_required_uuid;
    }
    public void setLogin_required_uuid(String login_required_uuid) {
        this.login_required_uuid = login_required_uuid;
    }

    public String getImage_uuid() { return image_uuid; }
    public void setImage_uuid(String image_uuid) { this.image_uuid = image_uuid; }

    public Integer getReserve() { return reserve; }
    public void setReserve(Integer reserve) { this.reserve = reserve; }
}
