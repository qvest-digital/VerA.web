package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
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

public class Guest {

    @Id
    private int pk;
    private int fk_event;
    private int fk_person;
    private int invitationstatus;
    private String notehost;

}

