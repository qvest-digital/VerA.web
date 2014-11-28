package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
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
        @NamedQuery(name = "Guest.findByEventAndUser", query = "SELECT g FROM Guest g where fk_event = :eventId and fk_person = :userId"),
        @NamedQuery(name = "Guest.findEventIdByDelegation", query ="SELECT g FROM Guest g WHERE  delegation=:uuid ")
})
@NamedNativeQueries({
        @NamedNativeQuery(name="Event.list.userevents", query = "SELECT e.* FROM tevent e " +
                "JOIN tguest g on g.fk_event = e.pk " +
                "JOIN tperson tp on g.fk_person = tp.pk " +
                "WHERE (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
                "AND tp.pk = :fk_person", resultClass=Event.class)})



/**
 * select fk_event from tguest where delegation like '0746a0c5-17c7-4b02-9f85-3570a71eb430';

 * @author jnunez
 *
 */

public class Guest {

    @Id
    private int pk;
    private int fk_event;
    private int fk_person;
    private String gender;
    private String gender_p;
    private int invitationstatus;
    private String notehost;
    private String delegation;

}

