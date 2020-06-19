package org.evolvis.veraweb.onlinereg.entities;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
