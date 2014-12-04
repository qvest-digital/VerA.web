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
        @NamedQuery(name = "Event.getEvent", query = "SELECT e FROM Event e where e.pk = :pk"),
        @NamedQuery(name = "Event.getEventByUUID", query = "SELECT e.pk FROM Event e where e.mediarepresentatives=:uuid ")
})
@NamedNativeQueries({
	@NamedNativeQuery(name="Event.guestByUUID",query="SELECT count(e.*) FROM tevent e WHERE mediarepresentatives=:uuid ")
})

public class Event {
	
    @Id
    private int pk;
    private String shortname;
    private Date datebegin;
    private Date dateend;
    private String eventtype;
    private String mediarepresentatives;

    @ManyToOne
    @JoinColumn(name="fk_location")
    private Location location;
}
