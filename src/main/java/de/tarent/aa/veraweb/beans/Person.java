/*
 * VerA.web,
 * Veranstaltungsmanagment VerA.web
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'VerA.web'
 * Signature of Elmar Geese, 7 August 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/*
 * Created on 23.02.2005
 */
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.aa.veraweb.beans.facade.AbstractMember;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;

/**
 * Dieses Bean stellt einen Eintrag der Tabelle <code>veraweb.tperson</code> da.
 * 
 * @author Christoph
 * @author Mikel
 */
public class Person extends AbstractHistoryBean implements PersonConstants, OrgUnitDependent {
	/** ID */
	public Integer id;
	/** ID der Mandanten-Einheit */
	public Integer orgunit;
	/** Erstellt am */
	public Timestamp created;
	/** Erstellt von */
	public String createdby;
	/** Ge�ndert am */
	public Timestamp changed;
	/** Ge�nder von */
	public String changedby;
	/** Als gel�scht markiert */
	public String deleted;
	/** G�ltigkeit lauft ab am */
	public Timestamp expire;
	/** Flag ob diese Person eine Firma ist */
	public String iscompany;
	/** Datenherkunft */
	public String importsource;

	// Hauptperson, Latein
	public String salutation_a_e1;
	public Integer fk_salutation_a_e1;
	public String title_a_e1;
	public String firstname_a_e1;
	public String lastname_a_e1;
	public String domestic_a_e1;
	public String sex_a_e1;
	public Timestamp birthday_a_e1;
	public Timestamp diplodate_a_e1;
	public String languages_a_e1;
	public String nationality_a_e1;
	public String note_a_e1;
	public String noteorga_a_e1;
	public String notehost_a_e1;

	// Hauptperson, Zeichensatz 1
	public String salutation_a_e2;
	public Integer fk_salutation_a_e2;
	public String title_a_e2;
	public String firstname_a_e2;
	public String lastname_a_e2;

	// Hauptperson, Zeichensatz 2
	public String salutation_a_e3;
	public Integer fk_salutation_a_e3;
	public String title_a_e3;
	public String firstname_a_e3;
	public String lastname_a_e3;

	// Partner, Latein
	public String salutation_b_e1;
	public Integer fk_salutation_b_e1;
	public String title_b_e1;
	public String firstname_b_e1;
	public String lastname_b_e1;
	public String domestic_b_e1;
	public String sex_b_e1;
	public Timestamp birthday_b_e1;
	public Timestamp diplodate_b_e1;
	public String languages_b_e1;
	public String nationality_b_e1;
	public String note_b_e1;
	public String noteorga_b_e1;
	public String notehost_b_e1;

	// Partner, Zeichensatz 1
	public String salutation_b_e2;
	public Integer fk_salutation_b_e2;
	public String title_b_e2;
	public String firstname_b_e2;
	public String lastname_b_e2;

	// Partner, Zeichensatz 2
	public String salutation_b_e3;
	public Integer fk_salutation_b_e3;
	public String title_b_e3;
	public String firstname_b_e3;
	public String lastname_b_e3;

	// Adressdaten Gesch�ftlich, Latein
	public String function_a_e1;
	public String company_a_e1;
	public String street_a_e1;
	public String zipcode_a_e1;
	public String city_a_e1;
	public String country_a_e1;
	public String pobox_a_e1;
	public String poboxzipcode_a_e1;
	public String suffix1_a_e1;
	public String suffix2_a_e1;
	public String fon_a_e1;
	public String fax_a_e1;
	public String mobil_a_e1;
	public String mail_a_e1;
	public String url_a_e1;

	// Adressdaten Gesch�ftlich, Zeichensatz 1
	public String function_a_e2;
	public String company_a_e2;
	public String street_a_e2;
	public String zipcode_a_e2;
	public String city_a_e2;
	public String country_a_e2;
	public String pobox_a_e2;
	public String poboxzipcode_a_e2;
	public String suffix1_a_e2;
	public String suffix2_a_e2;
	public String fon_a_e2;
	public String fax_a_e2;
	public String mobil_a_e2;
	public String mail_a_e2;
	public String url_a_e2;

	// Adressdaten Gesch�ftlich, Zeichensatz 2
	public String function_a_e3;
	public String company_a_e3;
	public String street_a_e3;
	public String zipcode_a_e3;
	public String city_a_e3;
	public String country_a_e3;
	public String pobox_a_e3;
	public String poboxzipcode_a_e3;
	public String suffix1_a_e3;
	public String suffix2_a_e3;
	public String fon_a_e3;
	public String fax_a_e3;
	public String mobil_a_e3;
	public String mail_a_e3;
	public String url_a_e3;

	// Adressdaten Privat, Latein
	public String function_b_e1;
	public String company_b_e1;
	public String street_b_e1;
	public String zipcode_b_e1;
	public String city_b_e1;
	public String country_b_e1;
	public String pobox_b_e1;
	public String poboxzipcode_b_e1;
	public String suffix1_b_e1;
	public String suffix2_b_e1;
	public String fon_b_e1;
	public String fax_b_e1;
	public String mobil_b_e1;
	public String mail_b_e1;
	public String url_b_e1;

	// Adressdaten Privat, Zeichensatz 1
	public String function_b_e2;
	public String company_b_e2;
	public String street_b_e2;
	public String zipcode_b_e2;
	public String city_b_e2;
	public String country_b_e2;
	public String pobox_b_e2;
	public String poboxzipcode_b_e2;
	public String suffix1_b_e2;
	public String suffix2_b_e2;
	public String fon_b_e2;
	public String fax_b_e2;
	public String mobil_b_e2;
	public String mail_b_e2;
	public String url_b_e2;

	// Adressdaten Privat, Zeichensatz 2
	public String function_b_e3;
	public String company_b_e3;
	public String street_b_e3;
	public String zipcode_b_e3;
	public String city_b_e3;
	public String country_b_e3;
	public String pobox_b_e3;
	public String poboxzipcode_b_e3;
	public String suffix1_b_e3;
	public String suffix2_b_e3;
	public String fon_b_e3;
	public String fax_b_e3;
	public String mobil_b_e3;
	public String mail_b_e3;
	public String url_b_e3;

	// Adressdaten Weitere, Latein
	public String function_c_e1;
	public String company_c_e1;
	public String street_c_e1;
	public String zipcode_c_e1;
	public String city_c_e1;
	public String country_c_e1;
	public String pobox_c_e1;
	public String poboxzipcode_c_e1;
	public String suffix1_c_e1;
	public String suffix2_c_e1;
	public String fon_c_e1;
	public String fax_c_e1;
	public String mobil_c_e1;
	public String mail_c_e1;
	public String url_c_e1;

	// Adressdaten Weitere, Zeichensatz 1
	public String function_c_e2;
	public String company_c_e2;
	public String street_c_e2;
	public String zipcode_c_e2;
	public String city_c_e2;
	public String country_c_e2;
	public String pobox_c_e2;
	public String poboxzipcode_c_e2;
	public String suffix1_c_e2;
	public String suffix2_c_e2;
	public String fon_c_e2;
	public String fax_c_e2;
	public String mobil_c_e2;
	public String mail_c_e2;
	public String url_c_e2;

	// Adressdaten Weitere, Zeichensatz 2
	public String function_c_e3;
	public String company_c_e3;
	public String street_c_e3;
	public String zipcode_c_e3;
	public String city_c_e3;
	public String country_c_e3;
	public String pobox_c_e3;
	public String poboxzipcode_c_e3;
	public String suffix1_c_e3;
	public String suffix2_c_e3;
	public String fon_c_e3;
	public String fax_c_e3;
	public String mobil_c_e3;
	public String mail_c_e3;
	public String url_c_e3;

	public void verify() throws BeanException {
		AddressHelper.checkPerson(this);
		
		if (!(
				(firstname_a_e1 != null && firstname_a_e1.length() != 0) ||
				(lastname_a_e1 != null && lastname_a_e1.length() != 0) ||
				(company_a_e1 != null && company_a_e1.length() != 0))) {
			addError("Sie m�ssen einen Vornamen, einen Nachnamen oder eine Firma angeben.");
		}
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne gelesen werden
	 * darf.<br>
	 * Test ist, ob der Benutzer Standard-Reader ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht gelesen werden darf.
	 * @see de.tarent.aa.veraweb.beans.AbstractBean#checkRead(de.tarent.octopus.server.OctopusContext)
	 */
	public void checkRead(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_READ_STANDARD);
	}

	/**
	 * Diese Methode testet, ob im aktuellen Kontext diese Bohne geschrieben
	 * werden darf.<br>
	 * Test ist, ob der Benutzer Writer ist.
	 * 
	 * @param cntx Octopus-Kontext
	 * @throws BeanException Wenn im angegebenen Kontext diese Bohne nicht geschrieben werden darf.
	 * @see de.tarent.aa.veraweb.beans.AbstractBean#checkWrite(de.tarent.octopus.server.OctopusContext)
	 */
	public void checkWrite(OctopusContext cntx) throws BeanException {
		checkGroup(cntx, PersonalConfigAA.GROUP_WRITE);
	}

    /**
     * Diese Methode leert beschr�nkte Felder.<br>
     * Hier sind es die Bemerkungsfelder, wenn der Benutzer nicht in der Gruppe
     * {@link PersonalConfigAA#GROUP_READ_REMARKS} der hierzu freigeschalteten ist.
     * 
     * @param cntx Octopus-Kontext
     * @throws BeanException bei Problemen mit der Bean
     * @see de.tarent.aa.veraweb.beans.AbstractBean#clearRestrictedFields(de.tarent.octopus.server.OctopusContext)
     */
    public void clearRestrictedFields(OctopusContext cntx) throws BeanException {
        PersonalConfig personalConfig = cntx != null ? cntx.configImpl() : null;
        if (personalConfig == null || !personalConfig.isUserInGroup(PersonalConfigAA.GROUP_READ_REMARKS)) {
            note_a_e1 = null;
            note_b_e1 = null;
            notehost_a_e1 = null;
            notehost_b_e1 = null;
            noteorga_a_e1 = null;
            noteorga_b_e1 = null;
        }
        super.clearRestrictedFields(cntx);
    }

    public PersonMemberFacade getMemberFacade(Integer member, Integer locale) {
		return getMemberFacade(member == null || member.intValue() != MEMBER_PARTNER, locale);
	}

	public PersonMemberFacade getMemberFacade(boolean hauptperson, Integer locale) {
		int l = locale != null ? locale.intValue() : LOCALE_LATIN;
		
		if (hauptperson) {
			if (l == LOCALE_EXTRA1) {
				return getMainExtra1();
			} else if (l == LOCALE_EXTRA2) {
				return getMainExtra2();
			} else {
				return getMainLatin();
			}
		} else {
			if (l == LOCALE_EXTRA1) {
				return getPartnerExtra1();
			} else if (l == LOCALE_EXTRA2) {
				return getPartnerExtra2();
			} else {
				return getPartnerLatin();
			}
		}
	}

	public PersonAddressFacade getAddressFacade(Integer addresstype, Integer locale) {
		int a = addresstype != null ? addresstype.intValue() : ADDRESSTYPE_BUSINESS;
		int l = locale != null ? locale.intValue() : LOCALE_LATIN;
		
		switch (a * 3 + l) {
			case 4:
				return getBusinessLatin();
			case 5:
				return getBusinessExtra1();
			case 6:
				return getBusinessExtra2();
			case 7:
				return getPrivateLatin();
			case 8:
				return getPrivateExtra1();
			case 9:
				return getPrivateExtra2();
			case 10:
				return getOtherLatin();
			case 11:
				return getOtherExtra1();
			case 12:
				return getOtherExtra2();
		}
		return getBusinessLatin();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Hauptperson-Daten im Latin-Zeichensatz.
	 */
	public PersonMemberFacade getMainLatin() {
		return new MainLatin();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Hauptperson-Daten im Zusatzzeichensatz 1.
	 */
	public PersonMemberFacade getMainExtra1() {
		return new MainExtra1();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Hauptperson-Daten im Zusatzzeichensatz 2.
	 */
	public PersonMemberFacade getMainExtra2() {
		return new MainExtra2();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Partner-Daten im Latin-Zeichensatz.
	 */
	public PersonMemberFacade getPartnerLatin() {
		return new PartnerLatin();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Partner-Daten im Zusatzzeichensatz 1.
	 */
	public PersonMemberFacade getPartnerExtra1() {
		return new PartnerExtra1();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Partner-Daten im Zusatzzeichensatz 2.
	 */
	public PersonMemberFacade getPartnerExtra2() {
		return new PartnerExtra2();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Dienst-Adresse im Latin-Zeichensatz.
	 */
	public PersonAddressFacade getBusinessLatin() {
		return new BusinessLatin();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Dienst-Adresse im Zusatzzeichensatz 1.
	 */
	public PersonAddressFacade getBusinessExtra1() {
		return new BusinessExtra1();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Dienst-Adresse im Zusatzzeichensatz 2.
	 */
	public PersonAddressFacade getBusinessExtra2() {
		return new BusinessExtra2();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Privat-Adresse im Latin-Zeichensatz.
	 */
	public PersonAddressFacade getPrivateLatin() {
		return new PrivateLatin();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Privat-Adresse im Zusatzzeichensatz 1.
	 */
	public PersonAddressFacade getPrivateExtra1() {
		return new PrivateExtra1();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Privat-Adresse im Zusatzzeichensatz 2.
	 */
	public PersonAddressFacade getPrivateExtra2() {
		return new PrivateExtra2();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Zusatz-Adresse im Latin-Zeichensatz.
	 */
	public PersonAddressFacade getOtherLatin() {
		return new OtherLatin();
	}

	/**
	 * Diese Methode liefert eine Facade f�r die Zusatz-Adresse im Zusatzzeichensatz 1.
	 */
	public PersonAddressFacade getOtherExtra1() {
		return new OtherExtra1();
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Zusatz-Adresse im Zusatzzeichensatz 2.
	 */
	public PersonAddressFacade getOtherExtra2() {
		return new OtherExtra2();
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Hauptperson-Daten im Latin-Zeichensatz.
	 */
	private class MainLatin extends AbstractMember {
		public String getSalutation() {
			return salutation_a_e1;
		}

		public Integer getSalutationFK() {
			return fk_salutation_a_e1;
		}

		public String getTitle() {
			return title_a_e1;
		}

		public String getFirstname() {
			return firstname_a_e1;
		}

		public String getLastname() {
			return lastname_a_e1;
		}

		public String getDomestic() {
			return domestic_a_e1;
		}

		public String getSex() {
			return sex_a_e1;
		}

		public Timestamp getBirthday() {
			return birthday_a_e1;
		}

		public Timestamp getDiplodate() {
			return diplodate_a_e1;
		}

		public String getLanguages() {
			return languages_a_e1;
		}

		public String getNationality() {
			return nationality_a_e1;
		}

		public String getNote() {
			return note_a_e1;
		}

		public String getNoteOrga() {
			return noteorga_a_e1;
		}

		public String getNoteHost() {
			return notehost_a_e1;
		}

		public void setSalutation(String value) {
			salutation_a_e1 = value;
		}

		public void setSalutationFK(Integer value) {
			fk_salutation_a_e1 = value;
		}

		public void setTitle(String value) {
			title_a_e1 = value;
		}

		public void setFirstname(String value) {
			firstname_a_e1 = value;
		}

		public void setLastname(String value) {
			lastname_a_e1 = value;
		}

		public void setDomestic(String value) {
			domestic_a_e1 = value;
		}

		public void setSex(String value) {
			sex_a_e1 = value;
		}

		public void setBirthday(Timestamp value) {
			birthday_a_e1 = value;
		}

		public void setDiplodate(Timestamp value) {
			diplodate_a_e1 = value;
		}

		public void setLanguages(String value) {
			languages_a_e1 = value;
		}

		public void setNationality(String value) {
			nationality_a_e1 = value;
		}

		public void setNote(String value) {
			note_a_e1 = value;
		}

		public void setNoteOrga(String value) {
			noteorga_a_e1 = value;
		}

		public void setNoteHost(String value) {
			notehost_a_e1 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Hauptperson-Daten im Zusatzzeichensatz 1.
	 */
	private class MainExtra1 extends AbstractMember {
		public String getSalutation() {
			return salutation_a_e2;
		}

		public Integer getSalutationFK() {
			return fk_salutation_a_e2;
		}

		public String getTitle() {
			return title_a_e2;
		}

		public String getFirstname() {
			return firstname_a_e2;
		}

		public String getLastname() {
			return lastname_a_e2;
		}

		public void setSalutation(String value) {
			salutation_a_e2 = value;
		}

		public void setSalutationFK(Integer value) {
			fk_salutation_a_e2 = value;
		}

		public void setTitle(String value) {
			title_a_e2 = value;
		}

		public void setFirstname(String value) {
			firstname_a_e2 = value;
		}

		public void setLastname(String value) {
			lastname_a_e2 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Hauptperson-Daten im Zusatzzeichensatz 2.
	 */
	private class MainExtra2 extends AbstractMember {
		public String getSalutation() {
			return salutation_a_e3;
		}

		public Integer getSalutationFK() {
			return fk_salutation_a_e3;
		}

		public String getTitle() {
			return title_a_e3;
		}

		public String getFirstname() {
			return firstname_a_e3;
		}

		public String getLastname() {
			return lastname_a_e3;
		}

		public void setSalutation(String value) {
			salutation_a_e3 = value;
		}

		public void setSalutationFK(Integer value) {
			fk_salutation_a_e3 = value;
		}

		public void setTitle(String value) {
			title_a_e3 = value;
		}

		public void setFirstname(String value) {
			firstname_a_e3 = value;
		}

		public void setLastname(String value) {
			lastname_a_e3 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Partner-Daten im Latin-Zeichensatz.
	 */
	private class PartnerLatin extends AbstractMember {
		public String getSalutation() {
			return salutation_b_e1;
		}

		public Integer getSalutationFK() {
			return fk_salutation_b_e1;
		}

		public String getTitle() {
			return title_b_e1;
		}

		public String getFirstname() {
			return firstname_b_e1;
		}

		public String getLastname() {
			return lastname_b_e1;
		}

		public String getDomestic() {
			return domestic_b_e1;
		}

		public String getSex() {
			return sex_b_e1;
		}

		public Timestamp getBirthday() {
			return birthday_b_e1;
		}

		public Timestamp getDiplodate() {
			return diplodate_b_e1;
		}

		public String getLanguages() {
			return languages_b_e1;
		}

		public String getNationality() {
			return nationality_b_e1;
		}

		public String getNote() {
			return note_b_e1;
		}

		public String getNoteOrga() {
			return noteorga_b_e1;
		}

		public String getNoteHost() {
			return notehost_b_e1;
		}

		public void setSalutation(String value) {
			salutation_b_e1 = value;
		}

		public void setSalutationFK(Integer value) {
			fk_salutation_b_e1 = value;
		}

		public void setTitle(String value) {
			title_b_e1 = value;
		}

		public void setFirstname(String value) {
			firstname_b_e1 = value;
		}

		public void setLastname(String value) {
			lastname_b_e1 = value;
		}

		public void setDomestic(String value) {
			domestic_b_e1 = value;
		}

		public void setSex(String value) {
			sex_b_e1 = value;
		}

		public void setBirthday(Timestamp value) {
			birthday_b_e1 = value;
		}

		public void setDiplodate(Timestamp value) {
			diplodate_b_e1 = value;
		}

		public void setLanguages(String value) {
			languages_b_e1 = value;
		}

		public void setNationality(String value) {
			nationality_b_e1 = value;
		}

		public void setNote(String value) {
			note_b_e1 = value;
		}

		public void setNoteOrga(String value) {
			noteorga_b_e1 = value;
		}

		public void setNoteHost(String value) {
			notehost_b_e1 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Partner-Daten im Zusatzzeichensatz 1.
	 */
	private class PartnerExtra1 extends AbstractMember {
		public String getSalutation() {
			return salutation_b_e2;
		}

		public Integer getSalutationFK() {
			return fk_salutation_b_e2;
		}

		public String getTitle() {
			return title_b_e2;
		}

		public String getFirstname() {
			return firstname_b_e2;
		}

		public String getLastname() {
			return lastname_b_e2;
		}

		public void setSalutation(String value) {
			salutation_b_e2 = value;
		}

		public void setSalutationFK(Integer value) {
			fk_salutation_b_e2 = value;
		}

		public void setTitle(String value) {
			title_b_e2 = value;
		}

		public void setFirstname(String value) {
			firstname_b_e2 = value;
		}

		public void setLastname(String value) {
			lastname_b_e2 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Partner-Daten im Zusatzzeichensatz 2.
	 */
	private class PartnerExtra2 extends AbstractMember {
		public String getSalutation() {
			return salutation_b_e3;
		}

		public Integer getSalutationFK() {
			return fk_salutation_b_e3;
		}

		public String getTitle() {
			return title_b_e3;
		}

		public String getFirstname() {
			return firstname_b_e3;
		}

		public String getLastname() {
			return lastname_b_e3;
		}

		public void setSalutation(String value) {
			salutation_b_e3 = value;
		}

		public void setSalutationFK(Integer value) {
			fk_salutation_b_e3 = value;
		}

		public void setTitle(String value) {
			title_b_e3 = value;
		}

		public void setFirstname(String value) {
			firstname_b_e3 = value;
		}

		public void setLastname(String value) {
			lastname_b_e3 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Dienst-Adresse im Latin-Zeichensatz.
	 */
	private class BusinessLatin implements PersonAddressFacade {
		public String getFunction() {
			return function_a_e1;
		}

		public String getCompany() {
			return company_a_e1;
		}

		public String getStreet() {
			return street_a_e1;
		}

		public String getZipCode() {
			return zipcode_a_e1;
		}

		public String getCity() {
			return city_a_e1;
		}

		public String getCountry() {
			return country_a_e1;
		}

		public String getPOBox() {
			return pobox_a_e1;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_a_e1;
		}

		public String getSuffix1() {
			return suffix1_a_e1;
		}

		public String getSuffix2() {
			return suffix2_a_e1;
		}

		public String getPhone() {
			return fon_a_e1;
		}

		public String getFax() {
			return fax_a_e1;
		}

		public String getMobile() {
			return mobil_a_e1;
		}

		public String getEMail() {
			return mail_a_e1;
		}

		public String getUrl() {
			return url_a_e1;
		}

		public void setFunction(String value) {
			function_a_e1 = value;
		}

		public void setCompany(String value) {
			company_a_e1 = value;
		}

		public void setStreet(String value) {
			street_a_e1 = value;
		}

		public void setZipCode(String value) {
			zipcode_a_e1 = value;
		}

		public void setCity(String value) {
			city_a_e1 = value;
		}

		public void setCountry(String value) {
			country_a_e1 = value;
		}

		public void setPOBox(String value) {
			pobox_a_e1 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_a_e1 = value;
		}

		public void setSuffix1(String value) {
			suffix1_a_e1 = value;
		}

		public void setSuffix2(String value) {
			suffix2_a_e1 = value;
		}

		public void setPhone(String value) {
			fon_a_e1 = value;
		}

		public void setFax(String value) {
			fax_a_e1 = value;
		}

		public void setMobile(String value) {
			mobil_a_e1 = value;
		}

		public void setEMail(String value) {
			mail_a_e1 = value;
		}

		public void setUrl(String value) {
			url_a_e1 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Dienst-Adresse im Zusatzzeichensatz 1.
	 */
	private class BusinessExtra1 implements PersonAddressFacade {
		public String getFunction() {
			return function_a_e2;
		}

		public String getCompany() {
			return company_a_e2;
		}

		public String getStreet() {
			return street_a_e2;
		}

		public String getZipCode() {
			return zipcode_a_e2;
		}

		public String getCity() {
			return city_a_e2;
		}

		public String getCountry() {
			return country_a_e2;
		}

		public String getPOBox() {
			return pobox_a_e2;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_a_e2;
		}

		public String getSuffix1() {
			return suffix1_a_e2;
		}

		public String getSuffix2() {
			return suffix2_a_e2;
		}

		public String getPhone() {
			return fon_a_e2;
		}

		public String getFax() {
			return fax_a_e2;
		}

		public String getMobile() {
			return mobil_a_e2;
		}

		public String getEMail() {
			return mail_a_e2;
		}

		public String getUrl() {
			return url_a_e2;
		}

		public void setFunction(String value) {
			function_a_e2 = value;
		}

		public void setCompany(String value) {
			company_a_e2 = value;
		}

		public void setStreet(String value) {
			street_a_e2 = value;
		}

		public void setZipCode(String value) {
			zipcode_a_e2 = value;
		}

		public void setCity(String value) {
			city_a_e2 = value;
		}

		public void setCountry(String value) {
			country_a_e2 = value;
		}

		public void setPOBox(String value) {
			pobox_a_e2 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_a_e2 = value;
		}

		public void setSuffix1(String value) {
			suffix1_a_e2 = value;
		}

		public void setSuffix2(String value) {
			suffix2_a_e2 = value;
		}

		public void setPhone(String value) {
			fon_a_e2 = value;
		}

		public void setFax(String value) {
			fax_a_e2 = value;
		}

		public void setMobile(String value) {
			mobil_a_e2 = value;
		}

		public void setEMail(String value) {
			mail_a_e2 = value;
		}

		public void setUrl(String value) {
			url_a_e2 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Dienst-Adresse im Zusatzzeichensatz 2.
	 */
	private class BusinessExtra2 implements PersonAddressFacade {
		public String getFunction() {
			return function_a_e3;
		}

		public String getCompany() {
			return company_a_e3;
		}

		public String getStreet() {
			return street_a_e3;
		}

		public String getZipCode() {
			return zipcode_a_e3;
		}

		public String getCity() {
			return city_a_e3;
		}

		public String getCountry() {
			return country_a_e3;
		}

		public String getPOBox() {
			return pobox_a_e3;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_a_e3;
		}

		public String getSuffix1() {
			return suffix1_a_e3;
		}

		public String getSuffix2() {
			return suffix2_a_e3;
		}

		public String getPhone() {
			return fon_a_e3;
		}

		public String getFax() {
			return fax_a_e3;
		}

		public String getMobile() {
			return mobil_a_e3;
		}

		public String getEMail() {
			return mail_a_e3;
		}

		public String getUrl() {
			return url_a_e3;
		}

		public void setFunction(String value) {
			function_a_e3 = value;
		}

		public void setCompany(String value) {
			company_a_e3 = value;
		}

		public void setStreet(String value) {
			street_a_e3 = value;
		}

		public void setZipCode(String value) {
			zipcode_a_e3 = value;
		}

		public void setCity(String value) {
			city_a_e3 = value;
		}

		public void setCountry(String value) {
			country_a_e3 = value;
		}

		public void setPOBox(String value) {
			pobox_a_e3 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_a_e3 = value;
		}

		public void setSuffix1(String value) {
			suffix1_a_e3 = value;
		}

		public void setSuffix2(String value) {
			suffix2_a_e3 = value;
		}

		public void setPhone(String value) {
			fon_a_e3 = value;
		}

		public void setFax(String value) {
			fax_a_e3 = value;
		}

		public void setMobile(String value) {
			mobil_a_e3 = value;
		}

		public void setEMail(String value) {
			mail_a_e3 = value;
		}

		public void setUrl(String value) {
			url_a_e3 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Privat-Adresse im Latin-Zeichensatz.
	 */
	private class PrivateLatin implements PersonAddressFacade {
		public String getFunction() {
			return function_b_e1;
		}

		public String getCompany() {
			return company_b_e1;
		}

		public String getStreet() {
			return street_b_e1;
		}

		public String getZipCode() {
			return zipcode_b_e1;
		}

		public String getCity() {
			return city_b_e1;
		}

		public String getCountry() {
			return country_b_e1;
		}

		public String getPOBox() {
			return pobox_b_e1;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_b_e1;
		}

		public String getSuffix1() {
			return suffix1_b_e1;
		}

		public String getSuffix2() {
			return suffix2_b_e1;
		}

		public String getPhone() {
			return fon_b_e1;
		}

		public String getFax() {
			return fax_b_e1;
		}

		public String getMobile() {
			return mobil_b_e1;
		}

		public String getEMail() {
			return mail_b_e1;
		}

		public String getUrl() {
			return url_b_e1;
		}

		public void setFunction(String value) {
			function_b_e1 = value;
		}

		public void setCompany(String value) {
			company_b_e1 = value;
		}

		public void setStreet(String value) {
			street_b_e1 = value;
		}

		public void setZipCode(String value) {
			zipcode_b_e1 = value;
		}

		public void setCity(String value) {
			city_b_e1 = value;
		}

		public void setCountry(String value) {
			country_b_e1 = value;
		}

		public void setPOBox(String value) {
			pobox_b_e1 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_b_e1 = value;
		}

		public void setSuffix1(String value) {
			suffix1_b_e1 = value;
		}

		public void setSuffix2(String value) {
			suffix2_b_e1 = value;
		}

		public void setPhone(String value) {
			fon_b_e1 = value;
		}

		public void setFax(String value) {
			fax_b_e1 = value;
		}

		public void setMobile(String value) {
			mobil_b_e1 = value;
		}

		public void setEMail(String value) {
			mail_b_e1 = value;
		}

		public void setUrl(String value) {
			url_b_e1 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Privat-Adresse im Zusatzzeichensatz 2.
	 */
	private class PrivateExtra1 implements PersonAddressFacade {
		public String getFunction() {
			return function_b_e2;
		}

		public String getCompany() {
			return company_b_e2;
		}

		public String getStreet() {
			return street_b_e2;
		}

		public String getZipCode() {
			return zipcode_b_e2;
		}

		public String getCity() {
			return city_b_e2;
		}

		public String getCountry() {
			return country_b_e2;
		}

		public String getPOBox() {
			return pobox_b_e2;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_b_e2;
		}

		public String getSuffix1() {
			return suffix1_b_e2;
		}

		public String getSuffix2() {
			return suffix2_b_e2;
		}

		public String getPhone() {
			return fon_b_e2;
		}

		public String getFax() {
			return fax_b_e2;
		}

		public String getMobile() {
			return mobil_b_e2;
		}

		public String getEMail() {
			return mail_b_e2;
		}

		public String getUrl() {
			return url_b_e2;
		}

		public void setFunction(String value) {
			function_b_e2 = value;
		}

		public void setCompany(String value) {
			company_b_e2 = value;
		}

		public void setStreet(String value) {
			street_b_e2 = value;
		}

		public void setZipCode(String value) {
			zipcode_b_e2 = value;
		}

		public void setCity(String value) {
			city_b_e2 = value;
		}

		public void setCountry(String value) {
			country_b_e2 = value;
		}

		public void setPOBox(String value) {
			pobox_b_e2 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_b_e2 = value;
		}

		public void setSuffix1(String value) {
			suffix1_b_e2 = value;
		}

		public void setSuffix2(String value) {
			suffix2_b_e2 = value;
		}

		public void setPhone(String value) {
			fon_b_e2 = value;
		}

		public void setFax(String value) {
			fax_b_e2 = value;
		}

		public void setMobile(String value) {
			mobil_b_e2 = value;
		}

		public void setEMail(String value) {
			mail_b_e2 = value;
		}

		public void setUrl(String value) {
			url_b_e2 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Privat-Adresse im Zusatzzeichensatz 2.
	 */
	private class PrivateExtra2 implements PersonAddressFacade {
		public String getFunction() {
			return function_b_e3;
		}

		public String getCompany() {
			return company_b_e3;
		}

		public String getStreet() {
			return street_b_e3;
		}

		public String getZipCode() {
			return zipcode_b_e3;
		}

		public String getCity() {
			return city_b_e3;
		}

		public String getCountry() {
			return country_b_e3;
		}

		public String getPOBox() {
			return pobox_b_e3;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_b_e3;
		}

		public String getSuffix1() {
			return suffix1_b_e3;
		}

		public String getSuffix2() {
			return suffix2_b_e3;
		}

		public String getPhone() {
			return fon_b_e3;
		}

		public String getFax() {
			return fax_b_e3;
		}

		public String getMobile() {
			return mobil_b_e3;
		}

		public String getEMail() {
			return mail_b_e3;
		}

		public String getUrl() {
			return url_b_e3;
		}

		public void setFunction(String value) {
			function_b_e3 = value;
		}

		public void setCompany(String value) {
			company_b_e3 = value;
		}

		public void setStreet(String value) {
			street_b_e3 = value;
		}

		public void setZipCode(String value) {
			zipcode_b_e3 = value;
		}

		public void setCity(String value) {
			city_b_e3 = value;
		}

		public void setCountry(String value) {
			country_b_e3 = value;
		}

		public void setPOBox(String value) {
			pobox_b_e3 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_b_e3 = value;
		}

		public void setSuffix1(String value) {
			suffix1_b_e3 = value;
		}

		public void setSuffix2(String value) {
			suffix2_b_e3 = value;
		}

		public void setPhone(String value) {
			fon_b_e3 = value;
		}

		public void setFax(String value) {
			fax_b_e3 = value;
		}

		public void setMobile(String value) {
			mobil_b_e3 = value;
		}

		public void setEMail(String value) {
			mail_b_e3 = value;
		}

		public void setUrl(String value) {
			url_b_e3 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Zusatz-Adresse im Latin-Zeichensatz.
	 */
	private class OtherLatin implements PersonAddressFacade {
		public String getFunction() {
			return function_c_e1;
		}

		public String getCompany() {
			return company_c_e1;
		}

		public String getStreet() {
			return street_c_e1;
		}

		public String getZipCode() {
			return zipcode_c_e1;
		}

		public String getCity() {
			return city_c_e1;
		}

		public String getCountry() {
			return country_c_e1;
		}

		public String getPOBox() {
			return pobox_c_e1;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_c_e1;
		}

		public String getSuffix1() {
			return suffix1_c_e1;
		}

		public String getSuffix2() {
			return suffix2_c_e1;
		}

		public String getPhone() {
			return fon_c_e1;
		}

		public String getFax() {
			return fax_c_e1;
		}

		public String getMobile() {
			return mobil_c_e1;
		}

		public String getEMail() {
			return mail_c_e1;
		}

		public String getUrl() {
			return url_c_e1;
		}

		public void setFunction(String value) {
			function_c_e1 = value;
		}

		public void setCompany(String value) {
			company_c_e1 = value;
		}

		public void setStreet(String value) {
			street_c_e1 = value;
		}

		public void setZipCode(String value) {
			zipcode_c_e1 = value;
		}

		public void setCity(String value) {
			city_c_e1 = value;
		}

		public void setCountry(String value) {
			country_c_e1 = value;
		}

		public void setPOBox(String value) {
			pobox_c_e1 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_c_e1 = value;
		}

		public void setSuffix1(String value) {
			suffix1_c_e1 = value;
		}

		public void setSuffix2(String value) {
			suffix2_c_e1 = value;
		}

		public void setPhone(String value) {
			fon_c_e1 = value;
		}

		public void setFax(String value) {
			fax_c_e1 = value;
		}

		public void setMobile(String value) {
			mobil_c_e1 = value;
		}

		public void setEMail(String value) {
			mail_c_e1 = value;
		}

		public void setUrl(String value) {
			url_c_e1 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Zusatz-Adresse im Zusatzzeichensatz 1.
	 */
	private class OtherExtra1 implements PersonAddressFacade {
		public String getFunction() {
			return function_c_e2;
		}

		public String getCompany() {
			return company_c_e2;
		}

		public String getStreet() {
			return street_c_e2;
		}

		public String getZipCode() {
			return zipcode_c_e2;
		}

		public String getCity() {
			return city_c_e2;
		}

		public String getCountry() {
			return country_c_e2;
		}

		public String getPOBox() {
			return pobox_c_e2;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_c_e2;
		}

		public String getSuffix1() {
			return suffix1_c_e2;
		}

		public String getSuffix2() {
			return suffix2_c_e2;
		}

		public String getPhone() {
			return fon_c_e2;
		}

		public String getFax() {
			return fax_c_e2;
		}

		public String getMobile() {
			return mobil_c_e2;
		}

		public String getEMail() {
			return mail_c_e2;
		}

		public String getUrl() {
			return url_c_e2;
		}

		public void setFunction(String value) {
			function_c_e2 = value;
		}

		public void setCompany(String value) {
			company_c_e2 = value;
		}

		public void setStreet(String value) {
			street_c_e2 = value;
		}

		public void setZipCode(String value) {
			zipcode_c_e2 = value;
		}

		public void setCity(String value) {
			city_c_e2 = value;
		}

		public void setCountry(String value) {
			country_c_e2 = value;
		}

		public void setPOBox(String value) {
			pobox_c_e2 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_c_e2 = value;
		}

		public void setSuffix1(String value) {
			suffix1_c_e2 = value;
		}

		public void setSuffix2(String value) {
			suffix2_c_e2 = value;
		}

		public void setPhone(String value) {
			fon_c_e2 = value;
		}

		public void setFax(String value) {
			fax_c_e2 = value;
		}

		public void setMobile(String value) {
			mobil_c_e2 = value;
		}

		public void setEMail(String value) {
			mail_c_e2 = value;
		}

		public void setUrl(String value) {
			url_c_e2 = value;
		}
	}

	/**
	 * Diese Klasse liefert eine Facade f�r die Zusatz-Adresse im Zusatzzeichensatz 2.
	 */
	private class OtherExtra2 implements PersonAddressFacade {
		public String getFunction() {
			return function_c_e3;
		}

		public String getCompany() {
			return company_c_e3;
		}

		public String getStreet() {
			return street_c_e3;
		}

		public String getZipCode() {
			return zipcode_c_e3;
		}

		public String getCity() {
			return city_c_e3;
		}

		public String getCountry() {
			return country_c_e3;
		}

		public String getPOBox() {
			return pobox_c_e3;
		}

		public String getPOBoxZipCode() {
			return poboxzipcode_c_e3;
		}

		public String getSuffix1() {
			return suffix1_c_e3;
		}

		public String getSuffix2() {
			return suffix2_c_e3;
		}

		public String getPhone() {
			return fon_c_e3;
		}

		public String getFax() {
			return fax_c_e3;
		}

		public String getMobile() {
			return mobil_c_e3;
		}

		public String getEMail() {
			return mail_c_e3;
		}

		public String getUrl() {
			return url_c_e3;
		}

		public void setFunction(String value) {
			function_c_e3 = value;
		}

		public void setCompany(String value) {
			company_c_e3 = value;
		}

		public void setStreet(String value) {
			street_c_e3 = value;
		}

		public void setZipCode(String value) {
			zipcode_c_e3 = value;
		}

		public void setCity(String value) {
			city_c_e3 = value;
		}

		public void setCountry(String value) {
			country_c_e3 = value;
		}

		public void setPOBox(String value) {
			pobox_c_e3 = value;
		}

		public void setPOBoxZipCode(String value) {
			poboxzipcode_c_e3 = value;
		}

		public void setSuffix1(String value) {
			suffix1_c_e3 = value;
		}

		public void setSuffix2(String value) {
			suffix2_c_e3 = value;
		}

		public void setPhone(String value) {
			fon_c_e3 = value;
		}

		public void setFax(String value) {
			fax_c_e3 = value;
		}

		public void setMobile(String value) {
			mobil_c_e3 = value;
		}

		public void setEMail(String value) {
			mail_c_e3 = value;
		}

		public void setUrl(String value) {
			url_c_e3 = value;
		}
	}
}
