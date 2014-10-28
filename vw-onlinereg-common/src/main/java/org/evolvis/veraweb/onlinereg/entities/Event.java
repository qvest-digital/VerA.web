package org.evolvis.veraweb.onlinereg.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by mley on 03.08.14.
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tevent")
@NamedQueries({
        @NamedQuery(name = "Event.list", query =
            "SELECT e FROM Event e " +
                "where (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
                "AND e.eventtype LIKE 'Offene Veranstaltung'"),
        @NamedQuery(name = "AllEvents.list", query = "SELECT e FROM Event e"),
        @NamedQuery(name = "Event.getEvent", query = "SELECT e FROM Event e where e.pk = :pk")
})
@NamedNativeQueries({
        @NamedNativeQuery(name="Event.list.userevents", query = "SELECT e.* FROM tevent e " +
                "JOIN tguest g on g.fk_event = e.pk " +
                "JOIN tuser tu on tu.pk = g.fk_person " +
                    "where (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
                    "AND e.eventtype LIKE 'Offene Veranstaltung' " +
                    "AND tu.username LIKE :username", resultClass=Event.class)})

public class Event {
	
    @Id
    private int pk;
    private String shortname;
    private Date datebegin;
    private Date dateend;
    private String eventtype;

    @ManyToOne
    @JoinColumn(name="fk_location")
    private Location location;
}
