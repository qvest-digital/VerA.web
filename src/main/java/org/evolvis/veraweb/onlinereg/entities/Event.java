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
        @NamedQuery(name = "Event.getEventByHash", query = "SELECT e FROM Event e where e.hash = :hash")
})
@NamedNativeQueries({
	@NamedNativeQuery(name="Event.guestByUUID",query="SELECT count(e.*) FROM tevent e WHERE mediarepresentatives=:uuid "),
    @NamedNativeQuery(name="Event.getEventByUUID", query="SELECT e.pk FROM tevent e where e.mediarepresentatives=:uuid "),
    @NamedNativeQuery(name="Event.isOpen", query="SELECT count(e.*) FROM tevent e " +
											     "WHERE (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
											     "AND e.eventtype LIKE 'Offene Veranstaltung' AND pk=:eventId")
})

public class Event {

    @Id
    private int pk;
    private String shortname;
    private Date datebegin;
    private Date dateend;
    private String eventtype;
    private String mediarepresentatives;
    private int fk_orgunit = 1;
    private String hash;

    @ManyToOne
    @JoinColumn(name="fk_location")
    private Location location;

    public int getFk_orgunit() {
		return fk_orgunit;
	}
    
    public int getPk() {
		return pk;
	}
    
    public String getHash() {
		return hash;
    }
    
    public String getShortname() {
		return shortname;
	}
    
    public Date getDatebegin() {
		return datebegin;
	}
}
