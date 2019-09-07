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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
 *
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
  @NamedNativeQuery(name = "Event.list.userevents",
    query = "SELECT e.* " +
      "FROM tevent e JOIN tguest g on g.fk_event = e.pk JOIN tperson tp on g.fk_person = tp.pk " +
      "WHERE (CURRENT_TIMESTAMP < e.datebegin OR CURRENT_TIMESTAMP < e.dateend) " +
      "AND tp.pk = :fk_person ORDER BY e.datebegin ASC", resultClass = Event.class),
  @NamedNativeQuery(name = "Event.count.userevents",
    query = "SELECT count(e.*) " +
      "FROM tevent e JOIN tguest g on g.fk_event = e.pk JOIN tperson tp on g.fk_person = tp.pk " +
      "WHERE tp.pk = :fk_person "),
  @NamedNativeQuery(name = "Guest.guestByUUID",
    query = "SELECT count(g.*) FROM tguest g LEFT JOIN tperson on tperson.pk=g.fk_person " +
      "WHERE delegation=:uuid AND tperson.iscompany='t'"),
  @NamedNativeQuery(name = "Guest.findEventIdByDelegationUUID",
    query = "SELECT g.* FROM tguest g  LEFT JOIN tperson on tperson.pk=g.fk_person " +
      "WHERE delegation=:uuid AND tperson.iscompany='t'", resultClass = Guest.class),
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
      "AND p.isCompany='t'", resultClass = Guest.class),
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
    private String keywords;

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

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

    public String getImage_uuid() {
        return image_uuid;
    }

    public void setImage_uuid(String image_uuid) {
        this.image_uuid = image_uuid;
    }

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
