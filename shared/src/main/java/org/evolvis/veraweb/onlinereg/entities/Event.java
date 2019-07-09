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
 * @author mley on 03.08.14.
 * @author jnunez
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tevent")
@NamedQueries({
  @NamedQuery(name = "AllEvents.list", query = "SELECT e FROM Event e"),
  @NamedQuery(name = "Event.getEvent", query = "SELECT e FROM Event e where e.pk = :pk"),
  @NamedQuery(name = "Event.getEventByHash", query = "SELECT e FROM Event e where e.hash = :hash")
})
@NamedNativeQueries({
  @NamedNativeQuery(name = "Event.list",
    query = "SELECT DISTINCT tevent.* FROM tevent " +
      "LEFT JOIN tguest g ON tevent.pk=g.fk_event " +
      "WHERE (CURRENT_TIMESTAMP < tevent.datebegin OR CURRENT_TIMESTAMP < tevent.dateend) " +
      "AND (tevent.maxguest > (SELECT count(pk) FROM tguest WHERE fk_event=tevent.pk AND reserve=0 " +
      "AND tguest.invitationstatus != 2 ) " +
      "OR (tevent.maxreserve > 0 " +
      "AND tevent.maxreserve > (SELECT count(pk) FROM tguest WHERE fk_event=tevent.pk AND reserve=1 " +
      "AND tguest.invitationstatus != 2))) " +
      "OR tevent.maxguest IS NULL " +
      "AND tevent.eventtype LIKE 'Offene Veranstaltung' ",
    resultClass = Event.class),
  @NamedNativeQuery(name = "Event.guestByUUID",
    query = "SELECT count(e.*) FROM tevent e WHERE mediarepresentatives=:uuid "),
  @NamedNativeQuery(name = "Event.getEventByUUID",
    query = "SELECT e.pk FROM tevent e where e.mediarepresentatives=:uuid "),
  @NamedNativeQuery(name = "Event.isOpen",
    query = "SELECT count(e.*) FROM tevent e " +
      "WHERE (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
      "AND e.eventtype LIKE 'Offene Veranstaltung' AND pk=:eventId"),
  @NamedNativeQuery(name = "Event.checkMaxGuestLimit",
    query = "SELECT DISTINCT count(*) " +
      "FROM veraweb.tevent " +
      "where tevent.pk=:eventId " +
      "AND (tevent.maxguest IS NOT NULL " +
      "AND (tevent.maxguest <= " +
      "(SELECT count(*) FROM tguest WHERE fk_event=:eventId " +
      "AND reserve = 0 " +
      "AND tguest.invitationstatus != 2) " +
      "AND tevent.maxguest > 0 ))"),
  @NamedNativeQuery(name = "Event.checkMaxReserveLimit",
    query = "SELECT DISTINCT count(*) " +
      "FROM veraweb.tevent  " +
      "WHERE tevent.pk=:eventId " +
      "AND ((tevent.maxreserve IS NULL OR tevent.maxreserve=0) " +
      "OR (tevent.maxreserve <= " +
      "(SELECT count(*) FROM tguest WHERE fk_event=:eventId " +
      "AND reserve = 1 " +
      "AND tguest.invitationstatus != 2) " +
      "AND tevent.maxreserve > 0 ))")
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
    private Integer maxguest;
    private Integer maxreserve;

    @ManyToOne
    @JoinColumn(name = "fk_location")
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

    public void setDatebegin(Date datebegin) {
        this.datebegin = datebegin;
    }

    public Date getDateend() {
        return dateend;
    }

    public String getEventtype() {
        return eventtype;
    }

    public Location getLocation() {
        return location;
    }

    public String getMediarepresentatives() {
        return mediarepresentatives;
    }

    public Integer getMaxguest() {
        return maxguest;
    }

    public void setMaxguest(Integer maxguest) {
        this.maxguest = maxguest;
    }

    public Integer getMaxreserve() {
        return maxreserve;
    }

    public void setMaxreserve(Integer maxreserve) {
        this.maxreserve = maxreserve;
    }
}
