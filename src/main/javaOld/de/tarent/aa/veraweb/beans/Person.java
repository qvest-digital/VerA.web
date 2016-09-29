/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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

import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.aa.veraweb.utils.VerawebUtils;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.server.OctopusContext;
import de.tarent.octopus.server.PersonalConfig;
import org.apache.commons.lang.StringEscapeUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Set;

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
    /** Geändert am */
    public Timestamp changed;
    /** Geändert von */
    public String changedby;
    /** Als gelöscht markiert */
    public String deleted;
    /** Gültigkeit lauft ab am */
    public Timestamp expire;
    /** Flag ob diese Person eine Firma ist */
    public String iscompany;
    /** Datenherkunft */
    public String importsource;
    /** Workarea */
    public Integer workarea;
    /** Workarea Name for display purposes only */
    public String workarea_name;
    /** Username */
    public String username;

    // Hauptperson, Latein
    @Size(max=10)
    public String salutation_a_e1;
    public Integer fk_salutation_a_e1;
    @Size(max=250)
    public String title_a_e1;
    @Size(max=100)
    public String firstname_a_e1;
    @Size(max=100)
    public String lastname_a_e1;
    public String domestic_a_e1;
    public String sex_a_e1;
    public Timestamp birthday_a_e1;
    @Size(max=100)
    public String birthplace_a_e1;
    public Timestamp diplodate_a_e1;
    @Size(max=250)
    public String languages_a_e1;
    @Size(max=100)
    public String nationality_a_e1;
    @Size(max=2000)
    public String note_a_e1;
    @Size(max=2000)
    public String noteorga_a_e1;
    @Size(max=2000)
    public String notehost_a_e1;

    // Hauptperson, Zeichensatz 1
    @Size(max=10)
    public String salutation_a_e2;
    public Integer fk_salutation_a_e2;
    @Size(max=100)
    public String birthplace_a_e2;
    @Size(max=250)
    public String title_a_e2;
    @Size(max=100)
    public String firstname_a_e2;
    @Size(max=100)
    public String lastname_a_e2;

    // Hauptperson, Zeichensatz 2
    @Size(max=10)
    public String salutation_a_e3;
    public Integer fk_salutation_a_e3;
    @Size(max=100)
    public String birthplace_a_e3;
    @Size(max=250)
    public String title_a_e3;
    @Size(max=100)
    public String firstname_a_e3;
    @Size(max=100)
    public String lastname_a_e3;

    // Partner, Latein
    @Size(max=10)
    public String salutation_b_e1;
    public Integer fk_salutation_b_e1;
    @Size(max=250)
    public String title_b_e1;
    @Size(max=100)
    public String firstname_b_e1;
    @Size(max=100)
    public String lastname_b_e1;
    public String domestic_b_e1;
    public String sex_b_e1;
    public Timestamp birthday_b_e1;
    //public String birthplace_b_e1;
    public Timestamp diplodate_b_e1;
    @Size(max=250)
    public String languages_b_e1;
    @Size(max=100)
    public String nationality_b_e1;
    @Size(max=2000)
    public String note_b_e1;
    @Size(max=2000)
    public String noteorga_b_e1;
    @Size(max=2000)
    public String notehost_b_e1;

    // Partner, Zeichensatz 1
    @Size(max=10)
    public String salutation_b_e2;
    public Integer fk_salutation_b_e2;
    //public String birthplace_b_e2;
    @Size(max=250)
    public String title_b_e2;
    @Size(max=100)
    public String firstname_b_e2;
    @Size(max=100)
    public String lastname_b_e2;

    // Partner, Zeichensatz 2
    @Size(max=10)
    public String salutation_b_e3;
    public Integer fk_salutation_b_e3;
    //public String birthplace_b_e3;
    @Size(max=250)
    public String title_b_e3;
    @Size(max=100)
    public String firstname_b_e3;
    @Size(max=100)
    public String lastname_b_e3;

    // Adressdaten Geschäftlich, Latein
    @Size(max=250)
    public String function_a_e1;
    @Size(max=250)
    public String company_a_e1;
    @Size(max=100)
    public String street_a_e1;
    @Size(max=50)
    public String zipcode_a_e1;
    @Size(max=300)
    public String city_a_e1;
    @Size(max=100)
    public String state_a_e1;
    @Size(max=100)
    public String country_a_e1;
    @Size(max=50)
    public String pobox_a_e1;
    @Size(max=50)
    public String poboxzipcode_a_e1;
    @Size(max=100)
    public String suffix1_a_e1;
    @Size(max=100)
    public String suffix2_a_e1;
    @Size(max=100)
    public String fon_a_e1;
    @Size(max=100)
    public String fax_a_e1;
    @Size(max=100)
    public String mobil_a_e1;
    @Size(max=250)
    public String mail_a_e1;
    @Size(max=250)
    public String url_a_e1;

    // Adressdaten Geschäftlich, Zeichensatz 1
    @Size(max=250)
    public String function_a_e2;
    @Size(max=250)
    public String company_a_e2;
    @Size(max=100)
    public String street_a_e2;
    @Size(max=50)
    public String zipcode_a_e2;
    @Size(max=300)
    public String city_a_e2;
    @Size(max=100)
    public String state_a_e2;
    @Size(max=100)
    public String country_a_e2;
    @Size(max=50)
    public String pobox_a_e2;
    @Size(max=50)
    public String poboxzipcode_a_e2;
    @Size(max=100)
    public String suffix1_a_e2;
    @Size(max=100)
    public String suffix2_a_e2;
    @Size(max=100)
    public String fon_a_e2;
    @Size(max=100)
    public String fax_a_e2;
    @Size(max=100)
    public String mobil_a_e2;
    @Size(max=250)
    public String mail_a_e2;
    @Size(max=250)
    public String url_a_e2;

    // Adressdaten Geschäftlich, Zeichensatz 2
    @Size(max=250)
    public String function_a_e3;
    @Size(max=250)
    public String company_a_e3;
    @Size(max=100)
    public String street_a_e3;
    @Size(max=50)
    public String zipcode_a_e3;
    @Size(max=100)
    public String state_a_e3;
    @Size(max=300)
    public String city_a_e3;
    @Size(max=100)
    public String country_a_e3;
    @Size(max=50)
    public String pobox_a_e3;
    @Size(max=50)
    public String poboxzipcode_a_e3;
    @Size(max=100)
    public String suffix1_a_e3;
    @Size(max=100)
    public String suffix2_a_e3;
    @Size(max=100)
    public String fon_a_e3;
    @Size(max=100)
    public String fax_a_e3;
    @Size(max=100)
    public String mobil_a_e3;
    @Size(max=250)
    public String mail_a_e3;
    @Size(max=250)
    public String url_a_e3;

    // Adressdaten Privat, Latein
    @Size(max=250)
    public String function_b_e1;
    @Size(max=250)
    public String company_b_e1;
    @Size(max=100)
    public String street_b_e1;
    @Size(max=50)
    public String zipcode_b_e1;
    @Size(max=300)
    public String city_b_e1;
    @Size(max=100)
    public String state_b_e1;
    @Size(max=100)
    public String country_b_e1;
    @Size(max=50)
    public String pobox_b_e1;
    @Size(max=50)
    public String poboxzipcode_b_e1;
    @Size(max=100)
    public String suffix1_b_e1;
    @Size(max=100)
    public String suffix2_b_e1;
    @Size(max=100)
    public String fon_b_e1;
    @Size(max=100)
    public String fax_b_e1;
    @Size(max=100)
    public String mobil_b_e1;
    @Size(max=250)
    public String mail_b_e1;
    @Size(max=250)
    public String url_b_e1;

    // Adressdaten Privat, Zeichensatz 1
    @Size(max=250)
    public String function_b_e2;
    @Size(max=250)
    public String company_b_e2;
    @Size(max=100)
    public String street_b_e2;
    @Size(max=50)
    public String zipcode_b_e2;
    @Size(max=300)
    public String city_b_e2;
    @Size(max=100)
    public String state_b_e2;
    @Size(max=100)
    public String country_b_e2;
    @Size(max=50)
    public String pobox_b_e2;
    @Size(max=50)
    public String poboxzipcode_b_e2;
    @Size(max=100)
    public String suffix1_b_e2;
    @Size(max=100)
    public String suffix2_b_e2;
    @Size(max=100)
    public String fon_b_e2;
    @Size(max=100)
    public String fax_b_e2;
    @Size(max=100)
    public String mobil_b_e2;
    @Size(max=250)
    public String mail_b_e2;
    @Size(max=250)
    public String url_b_e2;

    // Adressdaten Privat, Zeichensatz 2
    @Size(max=250)
    public String function_b_e3;
    @Size(max=250)
    public String company_b_e3;
    @Size(max=100)
    public String street_b_e3;
    @Size(max=50)
    public String zipcode_b_e3;
    @Size(max=300)
    public String city_b_e3;
    @Size(max=100)
    public String state_b_e3;
    @Size(max=100)
    public String country_b_e3;
    @Size(max=50)
    public String pobox_b_e3;
    @Size(max=50)
    public String poboxzipcode_b_e3;
    @Size(max=100)
    public String suffix1_b_e3;
    @Size(max=100)
    public String suffix2_b_e3;
    @Size(max=100)
    public String fon_b_e3;
    @Size(max=100)
    public String fax_b_e3;
    @Size(max=100)
    public String mobil_b_e3;
    @Size(max=250)
    public String mail_b_e3;
    @Size(max=250)
    public String url_b_e3;

    // Adressdaten Weitere, Latein
    @Size(max=250)
    public String function_c_e1;
    @Size(max=250)
    public String company_c_e1;
    @Size(max=100)
    public String street_c_e1;
    @Size(max=50)
    public String zipcode_c_e1;
    @Size(max=100)
    public String state_c_e1;
    @Size(max=300)
    public String city_c_e1;
    @Size(max=100)
    public String country_c_e1;
    @Size(max=50)
    public String pobox_c_e1;
    @Size(max=50)
    public String poboxzipcode_c_e1;
    @Size(max=100)
    public String suffix1_c_e1;
    @Size(max=100)
    public String suffix2_c_e1;
    @Size(max=100)
    public String fon_c_e1;
    @Size(max=100)
    public String fax_c_e1;
    @Size(max=100)
    public String mobil_c_e1;
    @Size(max=250)
    public String mail_c_e1;
    @Size(max=250)
    public String url_c_e1;

    // Adressdaten Weitere, Zeichensatz 1
    @Size(max=250)
    public String function_c_e2;
    @Size(max=250)
    public String company_c_e2;
    @Size(max=100)
    public String street_c_e2;
    @Size(max=50)
    public String zipcode_c_e2;
    @Size(max=100)
    public String state_c_e2;
    @Size(max=300)
    public String city_c_e2;
    @Size(max=100)
    public String country_c_e2;
    @Size(max=50)
    public String pobox_c_e2;
    @Size(max=50)
    public String poboxzipcode_c_e2;
    @Size(max=100)
    public String suffix1_c_e2;
    @Size(max=100)
    public String suffix2_c_e2;
    @Size(max=100)
    public String fon_c_e2;
    @Size(max=100)
    public String fax_c_e2;
    @Size(max=100)
    public String mobil_c_e2;
    @Size(max=250)
    public String mail_c_e2;
    @Size(max=250)
    public String url_c_e2;

    // Adressdaten Weitere, Zeichensatz 2
    @Size(max=250)
    public String function_c_e3;
    @Size(max=250)
    public String company_c_e3;
    @Size(max=100)
    public String street_c_e3;
    @Size(max=50)
    public String zipcode_c_e3;
    @Size(max=100)
    public String state_c_e3;
    @Size(max=300)
    public String city_c_e3;
    @Size(max=100)
    public String country_c_e3;
    @Size(max=50)
    public String pobox_c_e3;
    @Size(max=50)
    public String poboxzipcode_c_e3;
    @Size(max=100)
    public String suffix1_c_e3;
    @Size(max=100)
    public String suffix2_c_e3;
    @Size(max=100)
    public String fon_c_e3;
    @Size(max=100)
    public String fax_c_e3;
    @Size(max=100)
    public String mobil_c_e3;
    @Size(max=250)
    public String mail_c_e3;
    @Size(max=250)
    public String url_c_e3;

    public void verify(final OctopusContext octopusContext) throws BeanException {
        AddressHelper.checkPerson(this);
        final VerawebMessages messages = new VerawebMessages(octopusContext);
//		solveXSS(); TODO Get a better solution

        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<Person>> constraintViolations = validator.validate(this);
        for (ConstraintViolation<Person> violation : constraintViolations ) {
            final String column = violation.getPropertyPath().toString();
            addError(messages.getPersonMessageField(column));
        }

        checkExtendedMailValidation(messages);

        if (iscompany != null && iscompany.equals(PersonConstants.ISCOMPANY_TRUE)) {
            if (company_a_e1 == null || company_a_e1.equals("") || company_a_e1.trim().equals("")) {
                addError(messages.getMessagePersonNoCompanyName());
            }
        } else if (
            (firstname_a_e1 == null || firstname_a_e1.equals("") && firstname_a_e1.trim().length() == 0) &&
            (lastname_a_e1 == null || lastname_a_e1.equals("") && lastname_a_e1.trim().length() == 0)
        ) {
            addError(messages.getMessageBothNameFieldsAreEmpty());
        } else if (firstname_a_e1 == null || firstname_a_e1.equals("") && firstname_a_e1.trim().length() == 0) {
            addError(messages.getMessageNameFieldEmpty());
        } else if (lastname_a_e1 == null || lastname_a_e1.equals("") && lastname_a_e1.trim().length() == 0) {
            addError(messages.getMessageLastnameFieldEmptry());
        }

		/*
         * 2009-05-17 cklein
		 * temporarily fixes issue #1529 until i gain access to the old octopus repository
		 */
        DateHelper.temporary_fix_translateErrormessageEN2DE(this.getErrors(), octopusContext);
    }

    private void checkExtendedMailValidation(VerawebMessages messages) {
        if (mail_a_e1 == null || mail_a_e1.equals("")){
            addError(messages.getMessageNoMail());
        }
        if (!VerawebUtils.isValidEmailAddress(mail_a_e1)){
            addError(messages.getMessageNotValidMail());
        }
    }

    /**
     * Method which skips the XSS code injection
     * MUST: if you need to check the script-value of a field JUST ADD IT HERE
     */
    private void solveXSS() {
        company_a_e1 = StringEscapeUtils.escapeJavaScript(company_a_e1);
        firstname_a_e1 = StringEscapeUtils.escapeJavaScript(firstname_a_e1);
        lastname_a_e1 = StringEscapeUtils.escapeJavaScript(lastname_a_e1);
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
            note_a_e1 = null;
            note_b_e1 = null;
            notehost_a_e1 = null;
            notehost_b_e1 = null;
            noteorga_a_e1 = null;
            noteorga_b_e1 = null;
        }
        super.clearRestrictedFields(octopusContext);
    }

    /**
     * Diese Methode gibt an, ob ein Partner für diesen Gast mit auf der Gästeliste steht.
     *
     * @return boolean true falls ein existierender Partner mit eingeladen wurde
     */
    // added as per change request for version 1.2.0
    public boolean getHasPartner() {
    	/* check for partner latin */
        PartnerLatin p = (PartnerLatin) this.getMemberFacade(new Integer(MEMBER_PARTNER), new Integer(LOCALE_LATIN));
        PartnerExtra1 p1 = (PartnerExtra1) this.getMemberFacade(new Integer(MEMBER_PARTNER), new Integer(LOCALE_EXTRA1));
        PartnerExtra2 p2 = (PartnerExtra2) this.getMemberFacade(new Integer(MEMBER_PARTNER), new Integer(LOCALE_EXTRA2));
        // partner is always expected to have a lastname or a firstname
        return
                (
                        ((p.getLastname() != null || !p.getLastname().equals("")) && (p.getLastname().length() > 0))
                                || ((p.getFirstname() != null || !p.getFirstname().equals("")) && (p.getFirstname().length() > 0))
                                || ((p1.getLastname() != null || !p1.getLastname().equals("")) && (p1.getLastname().length() > 0))
                                || ((p1.getFirstname() != null || !p1.getFirstname().equals("")) && (p1.getFirstname().length() > 0))
                                || ((p2.getLastname() != null || !p2.getLastname().equals("")) && (p2.getLastname().length() > 0))
                                || ((p2.getFirstname() != null || !p2.getFirstname().equals("")) && (p2.getFirstname().length() > 0))
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
     * Diese Methode liefert eine Facade für die Hauptperson-Daten im Latin-Zeichensatz.
     */
    public PersonMemberFacade getMainLatin() {
        return new MainLatin(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Hauptperson-Daten im Zusatzzeichensatz 1.
     */
    public PersonMemberFacade getMainExtra1() {
        return new MainExtra1(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Hauptperson-Daten im Zusatzzeichensatz 2.
     */
    public PersonMemberFacade getMainExtra2() {
        return new MainExtra2(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Partner-Daten im Latin-Zeichensatz.
     */
    public PersonMemberFacade getPartnerLatin() {
        return new PartnerLatin(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Partner-Daten im Zusatzzeichensatz 1.
     */
    public PersonMemberFacade getPartnerExtra1() {
        return new PartnerExtra1(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Partner-Daten im Zusatzzeichensatz 2.
     */
    public PersonMemberFacade getPartnerExtra2() {
        return new PartnerExtra2(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Dienst-Adresse im Latin-Zeichensatz.
     */
    public PersonAddressFacade getBusinessLatin() {
        return new BusinessLatin(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Dienst-Adresse im Zusatzzeichensatz 1.
     */
    public PersonAddressFacade getBusinessExtra1() {
        return new BusinessExtra1(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Dienst-Adresse im Zusatzzeichensatz 2.
     */
    public PersonAddressFacade getBusinessExtra2() {
        return new BusinessExtra2(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Privat-Adresse im Latin-Zeichensatz.
     */
    public PersonAddressFacade getPrivateLatin() {
        return new PrivateLatin(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Privat-Adresse im Zusatzzeichensatz 1.
     */
    public PersonAddressFacade getPrivateExtra1() {
        return new PrivateExtra1(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Privat-Adresse im Zusatzzeichensatz 2.
     */
    public PersonAddressFacade getPrivateExtra2() {
        return new PrivateExtra2(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Zusatz-Adresse im Latin-Zeichensatz.
     */
    public PersonAddressFacade getOtherLatin() {
        return new OtherLatin(this);
    }

    /**
     * Diese Methode liefert eine Facade für die Zusatz-Adresse im Zusatzzeichensatz 1.
     */
    public PersonAddressFacade getOtherExtra1() {
        return new OtherExtra1(this);
    }

    /**
     * Diese Klasse liefert eine Facade für die Zusatz-Adresse im Zusatzzeichensatz 2.
     */
    public PersonAddressFacade getOtherExtra2() {
        return new OtherExtra2(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
