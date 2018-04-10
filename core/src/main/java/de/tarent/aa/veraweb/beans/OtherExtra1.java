package de.tarent.aa.veraweb.beans;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;

/**
 * Diese Klasse liefert eine Facade für die Zusatz-Adresse im Zusatzzeichensatz 1.
 */
public class OtherExtra1 implements PersonAddressFacade {
    private Person person;

    public OtherExtra1(Person person) {
        this.person = person;
    }

    public String getFunction() {
        return person.function_c_e2;
    }

    public String getCompany() {
        return person.company_c_e2;
    }

    public String getStreet() {
        return person.street_c_e2;
    }

    public String getZipCode() {
        return person.zipcode_c_e2;
    }

    public String getState() {
        return person.state_c_e2;
    }

    public String getCity() {
        return person.city_c_e2;
    }

    public String getCountry() {
        return person.country_c_e2;
    }

    public String getPOBox() {
        return person.pobox_c_e2;
    }

    public String getPOBoxZipCode() {
        return person.poboxzipcode_c_e2;
    }

    public String getSuffix1() {
        return person.suffix1_c_e2;
    }

    public String getSuffix2() {
        return person.suffix2_c_e2;
    }

    public String getPhone() {
        return person.fon_c_e2;
    }

    public String getFax() {
        return person.fax_c_e2;
    }

    public String getMobile() {
        return person.mobil_c_e2;
    }

    public String getEMail() {
        return person.mail_c_e2;
    }

    public String getUrl() {
        return person.url_c_e2;
    }

    public void setFunction(String value) {
        person.function_c_e2 = value;
    }

    public void setCompany(String value) {
        person.company_c_e2 = value;
    }

    public void setStreet(String value) {
        person.street_c_e2 = value;
    }

    public void setZipCode(String value) {
        person.zipcode_c_e2 = value;
    }

    public void setState(String state) {
        person.state_c_e2 = state;
    }

    public void setCity(String value) {
        person.city_c_e2 = value;
    }

    public void setCountry(String value) {
        person.country_c_e2 = value;
    }

    public void setPOBox(String value) {
        person.pobox_c_e2 = value;
    }

    public void setPOBoxZipCode(String value) {
        person.poboxzipcode_c_e2 = value;
    }

    public void setSuffix1(String value) {
        person.suffix1_c_e2 = value;
    }

    public void setSuffix2(String value) {
        person.suffix2_c_e2 = value;
    }

    public void setPhone(String value) {
        person.fon_c_e2 = value;
    }

    public void setFax(String value) {
        person.fax_c_e2 = value;
    }

    public void setMobile(String value) {
        person.mobil_c_e2 = value;
    }

    public void setEMail(String value) {
        person.mail_c_e2 = value;
    }

    public void setUrl(String value) {
        person.url_c_e2 = value;
    }
}
