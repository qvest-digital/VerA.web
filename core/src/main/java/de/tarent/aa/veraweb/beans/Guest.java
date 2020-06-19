package de.tarent.aa.veraweb.beans;

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

import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.beans.facade.GuestMemberFacade;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

import java.sql.Timestamp;

/**
 * Dieses Bean stellt einen Eintrag der Tabelle <em>veraweb.tguest</em> dar.
 *
 * @author Christoph
 * @author Mikel
 */
public class Guest extends AbstractHistoryBean implements EventConstants {
    /**
     * ID
     */
    public Integer id;
    /**
     * Erstellt am
     */
    public Timestamp created;
    /**
     * Erstellt von
     */
    public String createdby;
    /**
     * Geändert am
     */
    public Timestamp changed;
    /**
     * Geändert von
     */
    public String changedby;
    /**
     * ID der Person
     */
    public Integer person;
    /**
     * ID der Veranstaltung
     */
    public Integer event;
    /**
     * ID der Kategory
     */
    public Integer category;
    /**
     * Einladungsart
     */
    public Integer invitationtype;
    /**
     * Ist Gastgeber
     */
    public Integer ishost;
    /**
     * Reserve
     */
    public Boolean reserve;
    /**
     * Arbeitsbereich
     */
    public String workarea_name;
    /**
     * Delegation
     */
    public String delegation;

    public String keywords;

    // Hauptperson
    public Integer invitationstatus_a;
    public Integer tableno_a;
    public Integer seatno_a;
    public Integer orderno_a;
    public String notehost_a;
    public String noteorga_a;
    public String language_a;
    public String sex_a;
    public String nationality_a;
    public String domestic_a;
    public String image_uuid;

    // Partner
    public Integer invitationstatus_b;
    public Integer tableno_b;
    public Integer seatno_b;
    public Integer orderno_b;
    public String notehost_b;
    public String noteorga_b;
    public String language_b;
    public String sex_b;
    public String nationality_b;
    public String domestic_b;
    public String image_uuid_p;

    @Override
    public void verify() {
        if (ishost == null) {
            ishost = 0;
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
     * Test ist, ob der Benutzer Writer ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
     * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void checkWrite(OctopusContext octopusContext) throws BeanException {
        checkGroup(octopusContext, PersonalConfigAA.GROUP_WRITE);
    }

    /**
     * Diese Methode leert beschränkte Felder.<br>
     * Hier sind es die Bemerkungsfelder, wenn der Benutzer nicht in der Gruppe
     * {@link PersonalConfigAA#GROUP_READ_REMARKS} der hierzu freigeschalteten ist.
     *
     * @param octopusContext Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#clearRestrictedFields(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public void clearRestrictedFields(OctopusContext octopusContext) throws BeanException {
        PersonalConfig personalConfig = octopusContext != null ? octopusContext.personalConfig() : null;
        if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
            notehost_a = null;
            notehost_b = null;
            noteorga_a = null;
            noteorga_b = null;
        }
        super.clearRestrictedFields(octopusContext);
    }

    /**
     * Diese Methode gibt an, ob ein Partner für diesen Gast mit auf der Gästeliste steht.
     *
     * @return boolean true falls ein existierender Partner mit eingeladen wurde
     */
    // added as per change request for version 1.2.0
    public boolean getIsPartnerInvited() {
        GuestMemberFacade g = this.getMain();
        int invitationType = g.getInvitationType();
        return ((invitationType == TYPE_MITPARTNER) || (invitationType == TYPE_NURPARTNER));
    }

    /**
     * Diese Methode liefert eine Facade für die Hauptperson dieses Gastes zurück.
     *
     * @return GuestMemberFacade
     */
    public GuestMemberFacade getMain() {
        return new Main();
    }

    /**
     * Diese Methode liefert eine Facade für den Partner dieses Gastes zurück.
     *
     * @return GuestMemberFacade
     */
    public GuestMemberFacade getPartner() {
        return new Partner();
    }

    /**
     * Diese Klasse stellt eine Facade für die Hauptperson dieses Gastes dar.
     */
    private class Main implements GuestMemberFacade {
        public Integer getInvitationType() {
            return invitationtype;
        }

        public Integer getInvitationStatus() {
            return invitationstatus_a;
        }

        public Integer getTableNo() {
            return tableno_a;
        }

        public Integer getSeatNo() {
            return seatno_a;
        }

        public Integer getOrderNo() {
            return orderno_a;
        }

        public String getNoteOrga() {
            return noteorga_a;
        }

        public String getNoteHost() {
            return notehost_a;
        }

        public String getLanguages() {
            return language_a;
        }

        public String getSex() {
            return sex_a;
        }

        public String getNationality() {
            return nationality_a;
        }

        public String getDomestic() {
            return domestic_a;
        }

        public String getImageUuid() {
            return image_uuid;
        }

        public void setInvitationType(Integer value) {
            invitationtype = value;
        }

        public void setInvitationStatus(Integer value) {
            invitationstatus_a = value;
        }

        public void setTableNo(Integer value) {
            tableno_a = value;
        }

        public void setSeatNo(Integer value) {
            seatno_a = value;
        }

        public void setOrderNo(Integer value) {
            orderno_a = value;
        }

        public void setNoteOrga(String value) {
            noteorga_a = value;
        }

        public void setNoteHost(String value) {
            notehost_a = value;
        }

        public void setLanguages(String value) {
            language_a = value;
        }

        public void setSex(String value) {
            sex_a = value;
        }

        public void setNationality(String value) {
            nationality_a = value;
        }

        public void setDomestic(String value) {
            domestic_a = value;
        }

        public void setImageUuid(String value) {
            image_uuid = value;
        }
    }

    /**
     * Diese Klasse stellt eine Facade für den Partner dieses Gastes dar.
     */
    private class Partner implements GuestMemberFacade {
        public Integer getInvitationType() {
            return invitationtype;
        }

        public Integer getInvitationStatus() {
            return invitationstatus_b;
        }

        public Integer getTableNo() {
            return tableno_b;
        }

        public Integer getSeatNo() {
            return seatno_b;
        }

        public Integer getOrderNo() {
            return orderno_b;
        }

        public String getNoteOrga() {
            return noteorga_b;
        }

        public String getNoteHost() {
            return notehost_b;
        }

        public String getLanguages() {
            return language_b;
        }

        public String getSex() {
            return sex_b;
        }

        public String getNationality() {
            return nationality_b;
        }

        public String getDomestic() {
            return domestic_b;
        }

        public void setInvitationType(Integer value) {
            invitationtype = value;
        }

        public void setInvitationStatus(Integer value) {
            invitationstatus_b = value;
        }

        public void setTableNo(Integer value) {
            tableno_b = value;
        }

        public void setSeatNo(Integer value) {
            seatno_b = value;
        }

        public void setOrderNo(Integer value) {
            orderno_b = value;
        }

        public void setNoteOrga(String value) {
            noteorga_b = value;
        }

        public void setNoteHost(String value) {
            notehost_b = value;
        }

        public void setLanguages(String value) {
            language_b = value;
        }

        public void setSex(String value) {
            sex_b = value;
        }

        public void setNationality(String value) {
            nationality_b = value;
        }

        public void setDomestic(String value) {
            domestic_b = value;
        }

        public String getImageUuid() {
            return image_uuid_p;
        }

        public void setImageUuid(String value) {
            image_uuid_p = value;
        }
    }
}
