package org.evolvis.veraweb.onlinereg.entities;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
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
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017 mirabilos <t.glaser@tarent.de>
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
import java.util.Date;

/**
 * Created by mley on 01.09.14.
 * @author Stefan Weiz, tarent solutions GmbH
 */
@Data
@XmlRootElement
@Entity
@Table(name = "tperson")
@NamedQueries(value = {
        @NamedQuery(name = "Person.findByUsername", query = "SELECT p FROM Person p where p.username like :username"),
		@NamedQuery(name = "Person.findByMail", query = "SELECT p FROM Person p where p.mail_a_e1=:email"),
        @NamedQuery(name = "Person.findPersonIdByUsername", query = "SELECT p.pk FROM Person p where p.username like :username"),
        @NamedQuery(name = "Person.findByPersonId", query = "SELECT p FROM Person p where p.pk=:personId"),
        @NamedQuery(name = "Person.getPeopleByEventId", query = "SELECT p FROM Person p where p.pk IN (SELECT g.fk_person from Guest g where g.fk_event=:eventid)")
})
@NamedNativeQueries(value={
		 @NamedNativeQuery(name = "Person.getDelegatesByUUID", query = "SELECT tperson.* FROM tperson LEFT JOIN tguest g on tperson.pk=g.fk_person WHERE g.delegation=:uuid AND tperson.iscompany='f'", resultClass=Person.class),
		 @NamedNativeQuery(name = "Person.getCompanyByUUID", query = "SELECT tperson.* FROM tperson LEFT JOIN tguest g on tperson.pk=g.fk_person WHERE g.delegation=:uuid AND tperson.iscompany='t'", resultClass=Person.class),
		 @NamedNativeQuery(name = "Person.getPersonNamesByUsername", query = "SELECT CASE WHEN iscompany='t' THEN company_a_e1 " +
		 																				 "WHEN iscompany='f' THEN firstname_a_e1 || ' ' || lastname_a_e1 END " +
		 																				 "from tperson where username like :username")
})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;

    private Date created = new Date();
    private String createdby = "onlinereg";
    private Date changed;
    private String changedby;

    private int fk_orgunit = 1;
    private char isCompany = 'f';
    private char deleted = 'f';
    private int fk_workarea = 0;

    private String note_a_e1;
    private String notehost_a_e1;
    private String firstname_a_e1;
    private String firstname_a_e2;
    private String firstname_a_e3;
    private String lastname_a_e1;
    private String lastname_a_e2;
    private String lastname_a_e3;

    private String function_a_e1;
    private String street_a_e1;
    private String zipcode_a_e1;
    private String city_a_e1;
    private String country_a_e1;
	private String sex_a_e1;
	private String salutation_a_e1;
	private Integer fk_salutation_a_e1;
	private String title_a_e1;
	private Date birthday_a_e1;
	private String languages_a_e1;
	private String nationality_a_e1;

    private String company_a_e1;
    
    private String poboxzipcode_a_e1;
    private String  pobox_a_e1;
    private String suffix1_a_e1;
    private String suffix2_a_e1; 
    private String fon_a_e1;
    private String fax_a_e1; 
    private String mobil_a_e1; 
    private String mail_a_e1; 
    private String url_a_e1;
    /* Username for the Onlinereg service */
    private String username;

    public void setFirstName(String firstName) {
        firstname_a_e1 = firstname_a_e2 = firstname_a_e3 = firstName;
    }

    public void setLastName(String lastName) {
        lastname_a_e1 = lastname_a_e2 = lastname_a_e3 = lastName;
    }

    public String getCompany_a_e1() {
		return company_a_e1;
	}

    public void setCompany_a_e1(String company_a_e1) {
		this.company_a_e1 = company_a_e1;
	}

    public void setNote_a_e1(String note_a_e1) {
        this.note_a_e1 = note_a_e1;
    }

    public void setFk_orgunit(Integer fk_orgunit) {
        this.fk_orgunit = fk_orgunit;
    }

    public int getFk_orgunit() {
        return this.fk_orgunit;
    }

    public int getPk() {
        return this.pk;
    }

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public char getIsCompany() {
		return isCompany;
	}

	public void setIsCompany(char isCompany) {
		this.isCompany = isCompany;
	}

	public char getDeleted() {
		return deleted;
	}

	public void setDeleted(char deleted) {
		this.deleted = deleted;
	}

	public String getFirstname_a_e1() {
		return firstname_a_e1;
	}

	public void setFirstname_a_e1(String firstname_a_e1) {
		this.firstname_a_e1 = firstname_a_e1;
	}

	public String getFirstname_a_e2() {
		return firstname_a_e2;
	}

	public void setFirstname_a_e2(String firstname_a_e2) {
		this.firstname_a_e2 = firstname_a_e2;
	}

	public String getFirstname_a_e3() {
		return firstname_a_e3;
	}

	public void setFirstname_a_e3(String firstname_a_e3) {
		this.firstname_a_e3 = firstname_a_e3;
	}

	public String getLastname_a_e1() {
		return lastname_a_e1;
	}

	public void setLastname_a_e1(String lastname_a_e1) {
		this.lastname_a_e1 = lastname_a_e1;
	}

	public String getLastname_a_e2() {
		return lastname_a_e2;
	}

	public void setLastname_a_e2(String lastname_a_e2) {
		this.lastname_a_e2 = lastname_a_e2;
	}

	public String getLastname_a_e3() {
		return lastname_a_e3;
	}

	public void setLastname_a_e3(String lastname_a_e3) {
		this.lastname_a_e3 = lastname_a_e3;
	}

	public String getFunction_a_e1() {
		return function_a_e1;
	}

	public void setFunction_a_e1(String function_a_e1) {
		this.function_a_e1 = function_a_e1;
	}

	public String getMail_a_e1() {
		return mail_a_e1;
	}

	public void setMail_a_e1(String mail_a_e1) {
		this.mail_a_e1 = mail_a_e1;
	}

	public String getStreet_a_e1() {
		return street_a_e1;
	}

	public void setStreet_a_e1(String street_a_e1) {
		this.street_a_e1 = street_a_e1;
	}

	public String getZipcode_a_e1() {
		return zipcode_a_e1;
	}

	public void setZipcode_a_e1(String zipcode_a_e1) {
		this.zipcode_a_e1 = zipcode_a_e1;
	}

	public String getCity_a_e1() {
		return city_a_e1;
	}

	public void setCity_a_e1(String city_a_e1) {
		this.city_a_e1 = city_a_e1;
	}

	public String getCountry_a_e1() {
		return country_a_e1;
	}

	public void setCountry_a_e1(String country_a_e1) {
		this.country_a_e1 = country_a_e1;
	}

	public String getSex_a_e1() {
		return sex_a_e1;
	}

	public void setSex_a_e1(String sex_a_e1) {
		this.sex_a_e1 = sex_a_e1;
	}

	public String getNote_a_e1() {
		return note_a_e1;
	}

	public void setPk(int pk) {
		this.pk = pk;
	}

	public Date getChanged() {
		return changed;
	}

	public void setChanged(Date changed) {
		this.changed = changed;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

    public String getNotehost_a_e1() {
		return notehost_a_e1;
	}

    public void setNotehost_a_e1(String notehost_a_e1) {
		this.notehost_a_e1 = notehost_a_e1;
	}

    public String getChangedby() {
		return changedby;
	}

    public void setChangedby(String changedby) {
		this.changedby = changedby;
	}

    public int getFk_workarea() {
 		return fk_workarea;
 	}

 	public void setFk_workarea(int fk_workarea) {
 		this.fk_workarea = fk_workarea;
 	}

	public String getSalutation_a_e1() {
		return salutation_a_e1;
	}

	public void setSalutation_a_e1(String salutation_a_e1) {
		this.salutation_a_e1 = salutation_a_e1;
	}

	public Integer getFk_salutation_a_e1() {
		return fk_salutation_a_e1;
	}

	public void setFk_salutation_a_e1(Integer fk_salutation_a_e1) {
		this.fk_salutation_a_e1 = fk_salutation_a_e1;
	}

	public String getTitle_a_e1() {
		return title_a_e1;
	}

	public void setTitle_a_e1(String title_a_e1) {
		this.title_a_e1 = title_a_e1;
	}

	public Date getBirthday_a_e1() {
		return birthday_a_e1;
	}

	public void setBirthday_a_e1(Date birthday_a_e1) {
		this.birthday_a_e1 = birthday_a_e1;
	}

	public void setLanguages_a_e1(String languages_a_e1) {
		this.languages_a_e1 = languages_a_e1;
	}

	public String getLanguages_a_e1() {
		return languages_a_e1;
	}

	public void setNationality_a_e1(String nationality_a_e1) {
		this.nationality_a_e1 = nationality_a_e1;
	}

	public String getNationality_a_e1() {
		return nationality_a_e1;
	}
}
