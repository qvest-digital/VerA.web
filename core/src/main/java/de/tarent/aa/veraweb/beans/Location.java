package de.tarent.aa.veraweb.beans;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017, 2018 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
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

import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class Location extends AbstractBean implements OrgUnitDependent, Comparable<Location> {
    /**
     * PK.
     */
    public Integer id;

    /**
     * Organisation unit.
     */
    public Integer orgunit;

    /**
     * Name of location.
     */
    public String name;

    /**
     * Contact person.
     */
    public String contactperson;

    /**
     * address.
     */
    public String address;

    /**
     * zip code.
     */
    public String zip;

    /**
     * location.
     */
    public String location;

    /**
     * Call number.
     */
    public String callnumber;

    /**
     * Fax number.
     */
    public String faxnumber;

    /**
     * E-Mail address.
     */
    public String email;

    /**
     * Comment.
     */
    public String comment;

    /**
     * URL.
     */
    public String url;

    /**
     * GPS data.
     */
    public String gpsdata;

    /**
     * Room number.
     */
    public String roomnumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrgunit() {
        return orgunit;
    }

    public void setOrgunit(Integer orgunit) {
        this.orgunit = orgunit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactperson() {
        return contactperson;
    }

    public void setContactperson(String contactperson) {
        this.contactperson = contactperson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCallnumber() {
        return callnumber;
    }

    public void setCallnumber(String callnumber) {
        this.callnumber = callnumber;
    }

    public String getFaxnumber() {
        return faxnumber;
    }

    public void setFaxnumber(String faxnumber) {
        this.faxnumber = faxnumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGpsdata() {
        return gpsdata;
    }

    public void setGpsdata(String gpsdata) {
        this.gpsdata = gpsdata;
    }

    public String getRoomnumber() {
        return roomnumber;
    }

    public void setRoomnumber(String roomnumber) {
        this.roomnumber = roomnumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void verify(final OctopusContext octopusContext) throws BeanException {
        final VerawebMessages messages = new VerawebMessages(octopusContext);

        if (name == null || name.trim().length() == 0) {
            addError(messages.getMessageLocationTitleEmpty());
        }

        if (comment != null && comment.length() > 1000) {
            addError(messages.getMessageLocationMaxRemarkReached());
        }
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
     * darf.<br>
     * Test ist, ob der Benutzer Standard-Reader ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkRead(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_READ_STANDARD);
    }

    /**
     * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
     * werden darf.<br>
     * Test ist, ob der Benutzer Teil-Administrator ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_PARTIAL_ADMIN);
    }

    public int compareTo(Location location) {
        int result = 0;

        Collator collator = Collator.getInstance(java.util.Locale.GERMAN);
        collator.setStrength(Collator.SECONDARY);

        List<Integer> comparedList = new ArrayList<Integer>();

        comparedList.add(collator.compare(this.address == null ? "" : this.address,
          location.address == null ? "" : location.address));
        comparedList.add(collator.compare(this.callnumber == null ? "" : this.callnumber,
          location.callnumber == null ? "" : location.callnumber));
        comparedList.add(collator.compare(this.comment == null ? "" : this.comment,
          location.comment == null ? "" : location.comment));
        comparedList.add(collator.compare(this.contactperson == null ? "" : this.contactperson,
          location.contactperson == null ? "" : location.contactperson));
        comparedList.add(collator.compare(this.email == null ? "" : this.email,
          location.email == null ? "" : location.email));
        comparedList.add(collator.compare(this.faxnumber == null ? "" : this.faxnumber,
          location.faxnumber == null ? "" : location.faxnumber));
        comparedList.add(collator.compare(this.gpsdata == null ? "" : this.gpsdata,
          location.gpsdata == null ? "" : location.gpsdata));
        comparedList.add(collator.compare(this.location == null ? "" : this.location,
          location.location == null ? "" : location.location));
        comparedList.add(collator.compare(this.name == null ? "" : this.name,
          location.name == null ? "" : location.name));
        comparedList.add(collator.compare(this.roomnumber == null ? "" : this.roomnumber,
          location.roomnumber == null ? "" : location.roomnumber));
        //comparedList.add(collator.compare(this.title, location.title));
        comparedList.add(collator.compare(this.url == null ? "" : this.url,
          location.url == null ? "" : location.url));
        comparedList.add(collator.compare(this.zip == null ? "" : this.zip,
          location.zip == null ? "" : location.zip));

        if (comparedList.contains(1) || comparedList.contains(-1)) { //It contains 1 or -1 so it is not equal
            return 1; //For this implementation it does not matter what value returns. Can be changed for future reasons.
        }

        return result;
    }
}
