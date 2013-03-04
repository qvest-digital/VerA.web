/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
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
package de.tarent.aa.veraweb.beans;

import java.sql.Timestamp;

import de.tarent.aa.veraweb.beans.facade.AbstractMember;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
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
	/** Workarea */
	public Integer workarea;
	/** Workarea Name for display purposes only */
	public String workarea_name;

	// Hauptperson, Latein
	public String salutation_a_e1;
	public Integer fk_salutation_a_e1;
	public String title_a_e1;
	public String firstname_a_e1;
	public String lastname_a_e1;
	public String domestic_a_e1;
	public String sex_a_e1;
	public Timestamp birthday_a_e1;
	public String birthplace_a_e1;
	public Timestamp diplodate_a_e1;
	public String languages_a_e1;
	public String nationality_a_e1;
	public String note_a_e1;
	public String noteorga_a_e1;
	public String notehost_a_e1;

	// Hauptperson, Zeichensatz 1
	public String salutation_a_e2;
	public Integer fk_salutation_a_e2;
	public String birthplace_a_e2;
	public String title_a_e2;
	public String firstname_a_e2;
	public String lastname_a_e2;

	// Hauptperson, Zeichensatz 2
	public String salutation_a_e3;
	public Integer fk_salutation_a_e3;
	public String birthplace_a_e3;
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
	//public String birthplace_b_e1;
	public Timestamp diplodate_b_e1;
	public String languages_b_e1;
	public String nationality_b_e1;
	public String note_b_e1;
	public String noteorga_b_e1;
	public String notehost_b_e1;

	// Partner, Zeichensatz 1
	public String salutation_b_e2;
	public Integer fk_salutation_b_e2;
	//public String birthplace_b_e2;
	public String title_b_e2;
	public String firstname_b_e2;
	public String lastname_b_e2;

	// Partner, Zeichensatz 2
	public String salutation_b_e3;
	public Integer fk_salutation_b_e3;
	//public String birthplace_b_e3;
	public String title_b_e3;
	public String firstname_b_e3;
	public String lastname_b_e3;

	// Adressdaten Gesch�ftlich, Latein
	public String function_a_e1;
	public String company_a_e1;
	public String street_a_e1;
	public String zipcode_a_e1;
	public String city_a_e1;
	public String state_a_e1;
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
	public String state_a_e2;
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
	public String state_a_e3;
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
	public String state_b_e1;
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
	public String state_b_e2;
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
	public String state_b_e3;
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
	public String state_c_e1;
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
	public String state_c_e2;
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
	public String state_c_e3;
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

	@Override
    public void verify() throws BeanException {
		AddressHelper.checkPerson(this);
		
		if (!(
				(firstname_a_e1 != null && firstname_a_e1.length() != 0) ||
				(lastname_a_e1 != null && lastname_a_e1.length() != 0) ||
				(company_a_e1 != null && company_a_e1.length() != 0))) {
			addError("Sie müssen einen Vornamen, einen Nachnamen oder eine Firma angeben.");
		}

		/*
		 * 2009-05-17 cklein
		 * temporarily fixes issue #1529 until i gain access to the old octopus repository
		 */
		DateHelper.temporary_fix_translateErrormessageEN2DE( this.getErrors() );
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
	@Override
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
	@Override
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
    @Override
    public void clearRestrictedFields(OctopusContext cntx) throws BeanException {
        PersonalConfig personalConfig = cntx != null ? cntx.personalConfig() : null;
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
    
    /**
     * Diese Methode gibt an, ob ein Partner für diesen Gast mit auf der Gästeliste steht.
     *
     * @return boolean true falls ein existierender Partner mit eingeladen wurde
     */
     // added as per change request for version 1.2.0
    public boolean getHasPartner()
    {
    	/* check for partner latin */
    	PartnerLatin p = ( PartnerLatin ) this.getMemberFacade( new Integer( MEMBER_PARTNER ), new Integer( LOCALE_LATIN ) );
    	PartnerExtra1 p1 = ( PartnerExtra1 ) this.getMemberFacade( new Integer( MEMBER_PARTNER ), new Integer( LOCALE_EXTRA1 ) );
    	PartnerExtra2 p2 = ( PartnerExtra2 ) this.getMemberFacade( new Integer( MEMBER_PARTNER ), new Integer( LOCALE_EXTRA2 ) );
    	// partner is always expected to have a lastname or a firstname
    	return
    	( 
    		( ( p.getLastname() != null ) && ( p.getLastname().length() > 0 ) )
    		|| ( ( p.getFirstname() != null ) && ( p.getFirstname().length() > 0 ) )
    		|| ( ( p1.getLastname() != null ) && ( p1.getLastname().length() > 0 ) )
    		|| ( ( p1.getFirstname() != null ) && ( p1.getFirstname().length() > 0 ) )
    		|| ( ( p2.getLastname() != null ) && ( p2.getLastname().length() > 0 ) )
    		|| ( ( p2.getFirstname() != null ) && ( p2.getFirstname().length() > 0 ) )
    	);
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

		@Override
        public String getDomestic() {
			return domestic_a_e1;
		}

		@Override
        public String getSex() {
			return sex_a_e1;
		}

		@Override
        public Timestamp getBirthday() {
			return birthday_a_e1;
		}

		@Override
        public String getBirthplace() {
			return birthplace_a_e1;
		}

		@Override
        public Timestamp getDiplodate() {
			return diplodate_a_e1;
		}

		@Override
        public String getLanguages() {
			return languages_a_e1;
		}

		@Override
        public String getNationality() {
			return nationality_a_e1;
		}

		@Override
        public String getNote() {
			return note_a_e1;
		}

		@Override
        public String getNoteOrga() {
			return noteorga_a_e1;
		}

		@Override
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

		@Override
        public void setDomestic(String value) {
			domestic_a_e1 = value;
		}

		@Override
        public void setSex(String value) {
			sex_a_e1 = value;
		}

		@Override
        public void setBirthday(Timestamp value) {
			birthday_a_e1 = value;
		}

		@Override
        public void setBirthplace(String value) {
			birthplace_a_e1 = value;
		}

		@Override
        public void setDiplodate(Timestamp value) {
			diplodate_a_e1 = value;
		}

		@Override
        public void setLanguages(String value) {
			languages_a_e1 = value;
		}

		@Override
        public void setNationality(String value) {
			nationality_a_e1 = value;
		}

		@Override
        public void setNote(String value) {
			note_a_e1 = value;
		}

		@Override
        public void setNoteOrga(String value) {
			noteorga_a_e1 = value;
		}

		@Override
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

		@Override
        public String getBirthplace() {
			return birthplace_a_e2;
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

		@Override
        public void setBirthplace(String value) {
			birthplace_a_e2 = value;
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

		@Override
        public String getBirthplace() {
			return birthplace_a_e3;
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

		@Override
        public void setBirthplace(String value) {
			birthplace_a_e3 = value;
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

		@Override
        public String getDomestic() {
			return domestic_b_e1;
		}

		@Override
        public String getSex() {
			return sex_b_e1;
		}

		@Override
        public Timestamp getBirthday() {
			return birthday_b_e1;
		}

		@Override
        public String getBirthplace() {
			throw new RuntimeException( "Not implemented." );
			//return birthplace_b_e1;
		}

		@Override
        public Timestamp getDiplodate() {
			return diplodate_b_e1;
		}

		@Override
        public String getLanguages() {
			return languages_b_e1;
		}

		@Override
        public String getNationality() {
			return nationality_b_e1;
		}

		@Override
        public String getNote() {
			return note_b_e1;
		}

		@Override
        public String getNoteOrga() {
			return noteorga_b_e1;
		}

		@Override
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

		@Override
        public void setDomestic(String value) {
			domestic_b_e1 = value;
		}

		@Override
        public void setSex(String value) {
			sex_b_e1 = value;
		}

		@Override
        public void setBirthday(Timestamp value) {
			birthday_b_e1 = value;
		}

		@Override
        public void setBirthplace(String value) {
			throw new RuntimeException( "Not implemented." );
			//birthplace_b_e2 = value;
		}

		@Override
        public void setDiplodate(Timestamp value) {
			diplodate_b_e1 = value;
		}

		@Override
        public void setLanguages(String value) {
			languages_b_e1 = value;
		}

		@Override
        public void setNationality(String value) {
			nationality_b_e1 = value;
		}

		@Override
        public void setNote(String value) {
			note_b_e1 = value;
		}

		@Override
        public void setNoteOrga(String value) {
			noteorga_b_e1 = value;
		}

		@Override
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

		@Override
        public String getBirthplace() {
			throw new RuntimeException( "Not implemented." );
			//return birthplace_b_e2;
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

		@Override
        public void setBirthplace(String value) {
			throw new RuntimeException( "Not implemented." );
			//birthplace_b_e2 = value;
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

		@Override
        public String getBirthplace() {
			throw new RuntimeException( "Not implemented." );
			//return birthplace_b_e3;
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

		@Override
        public void setBirthplace(String value) {
			throw new RuntimeException( "Not implemented." );
			//birthplace_b_e3 = value;
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
		
		public String getState() {
			return state_a_e1;
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
		
		public void setState(String state) {
			state_a_e1 = state;
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
		
		public String getState() {
			return state_a_e2;
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
		
		public void setState(String state) {
			state_a_e2 = state;
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
		
		public String getState() {
			return state_a_e3;
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
		
		public void setState(String state) {
			state_a_e3 = state;
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
		
		public String getState() {
			return state_b_e1;
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
		
		public void setState(String state) {
			state_b_e1 = state;
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
		
		public String getState() {
			return state_b_e2;
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
		
		public void setState(String state) {
			state_b_e2 = state;
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
		
		public String getState() {
			return state_b_e3;
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
		
		public void setState(String state) {
			state_b_e3 = state;
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
		
		public String getState() {
			return state_c_e1;
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
		
		public void setState(String state) {
			state_c_e1 = state;
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
		
		public String getState() {
			return state_c_e2;
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
		
		public void setState(String state) {
			state_c_e2 = state;
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
		
		public String getState() {
			return state_c_e3;
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
		
		public void setState(String state) {
			state_c_e3 = state;
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
