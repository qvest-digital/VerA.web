/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.aa.veraweb.utils;

/**
 * Diese Schnittstelle enth�lt Konstanten, die f�r das Erstellen und Parsen von
 * VerA.web-XML-Dokumenten n�tig sind. 
 * 
 * @author mikel
 */
public interface VerawebNamespaceConstants {
    /** VerA.web-Namensraum-URI */
    public final static String VW_NAMESPACE_URI = "http://schemas.tarent.de/veraweb";
    /** xmlns-Namensraum-URI */
    public final static String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    /** xmlns:vw-Namensraum-Attribut */
    public final static String VW_NAMESPACE_ATTRIBUTE = "xmlns:vw";
    /** Dokumentelement-Name mit Namensraumm-Pr�fix */
    public final static String CONTAINER_ELEMENT_VW = "vw:container";
    /** lokaler Dokumentelement-Name */
    public final static String CONTAINER_ELEMENT = CONTAINER_ELEMENT_VW.substring(3);
    /** Personenelement-Name mit Namensraumm-Pr�fix */
    public final static String PERSON_ELEMENT_VW = "vw:person";
    /** lokaler Personenelement-Name */
    public final static String PERSON_ELEMENT = PERSON_ELEMENT_VW.substring(3);
    /** Personenelement-Mandant-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String PERSON_ORG_UNIT_ATTRIBUTE_VW = "vw:org-unit";
    /** lokaler Personenelement-Mandant-Attribut-Name */
    public final static String PERSON_ORG_UNIT_ATTRIBUTE = PERSON_ORG_UNIT_ATTRIBUTE_VW.substring(3);
    /** Personenelement-ID-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String PERSON_ID_ATTRIBUTE_VW = "vw:id";
    /** lokaler Personenelement-ID-Attribut-Name */
    public final static String PERSON_ID_ATTRIBUTE = PERSON_ID_ATTRIBUTE_VW.substring(3);
    /** Personenelement-SaveAs-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String PERSON_SAVE_AS_ATTRIBUTE_VW = "vw:save-as";
    /** lokaler Personenelement-SaveAs-Attribut-Name */
    public final static String PERSON_SAVE_AS_ATTRIBUTE = PERSON_SAVE_AS_ATTRIBUTE_VW.substring(3);
    /** Personenelement-IsCompany-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String PERSON_IS_COMPANY_ATTRIBUTE_VW = "vw:is-company";
    /** lokaler Personenelement-IsCompany-Attribut-Name */
    public final static String PERSON_IS_COMPANY_ATTRIBUTE = PERSON_IS_COMPANY_ATTRIBUTE_VW.substring(3);
    /** Personenelement-G�ltigkeit-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String PERSON_EXPIRATION_ATTRIBUTE_VW = "vw:expiration";
    /** lokaler Personenelement-G�ltigkeit-Attribut-Name */
    public final static String PERSON_EXPIRATION_ATTRIBUTE = PERSON_EXPIRATION_ATTRIBUTE_VW.substring(3);
    /** Historyelement-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_ELEMENT_VW = "vw:history";
    /** lokaler Historyelement-Name */
    public final static String HISTORY_ELEMENT = HISTORY_ELEMENT_VW.substring(3);
    /** Historyelement-Erstellerattribut-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_CREATOR_ATTRIBUTE_VW = "vw:creator";
    /** lokaler Historyelement-Erstellerattribut-Name */
    public final static String HISTORY_CREATOR_ATTRIBUTE = HISTORY_CREATOR_ATTRIBUTE_VW.substring(3);
    /** Historyelement-Erstellungattribut-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_CREATION_ATTRIBUTE_VW = "vw:creation";
    /** lokaler Historyelement-Erstellungattribut-Name */
    public final static String HISTORY_CREATION_ATTRIBUTE = HISTORY_CREATION_ATTRIBUTE_VW.substring(3);
    /** Historyelement-Bearbeiterattribut-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_EDITOR_ATTRIBUTE_VW = "vw:editor";
    /** lokaler Historyelement-Bearbeiterattribut-Name */
    public final static String HISTORY_EDITOR_ATTRIBUTE = HISTORY_EDITOR_ATTRIBUTE_VW.substring(3);
    /** Historyelement-Bearbeitungattribut-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_EDITING_ATTRIBUTE_VW = "vw:editing";
    /** lokaler Historyelement-Bearbeitungattribut-Name */
    public final static String HISTORY_EDITING_ATTRIBUTE = HISTORY_EDITING_ATTRIBUTE_VW.substring(3);
    /** Historyelement-Bearbeitungattribut-alternativ-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_EDITING_ALT_ATTRIBUTE_VW = "vw:editing-alt";
    /** lokaler Historyelement-Bearbeitungattribut-alternativ-Name */
    public final static String HISTORY_EDITING_ALT_ATTRIBUTE = HISTORY_EDITING_ALT_ATTRIBUTE_VW.substring(3);
    /** Historyelement-Quelleattribut-Name mit Namensraumm-Pr�fix */
    public final static String HISTORY_SOURCE_ATTRIBUTE_VW = "vw:source";
    /** lokaler Historyelement-Quelleattribut-Name */
    public final static String HISTORY_SOURCE_ATTRIBUTE = HISTORY_SOURCE_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_ELEMENT_VW = "vw:member";
    /** lokaler Memberelement-Name */
    public final static String MEMBER_ELEMENT = MEMBER_ELEMENT_VW.substring(3);
    /** Memberelement-Typattribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_TYPE_ATTRIBUTE_VW = "vw:type";
    /** lokaler Memberelement-Typattribut-Name */
    public final static String MEMBER_TYPE_ATTRIBUTE = MEMBER_TYPE_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Typattribut-Wert Hauptperson mit Namensraumm-Pr�fix */
    public final static String MEMBER_TYPE_ATTRIBUTE_MAIN_VW = "vw:main";
    /** lokaler Memberelement-Typattribut-Wert Hauptperson */
    public final static String MEMBER_TYPE_ATTRIBUTE_MAIN = MEMBER_TYPE_ATTRIBUTE_MAIN_VW.substring(3);
    /** Memberelement-Typattribut-Wert Partner mit Namensraumm-Pr�fix */
    public final static String MEMBER_TYPE_ATTRIBUTE_PARTNER_VW = "vw:partner";
    /** lokaler Memberelement-Typattribut-Wert Partner */
    public final static String MEMBER_TYPE_ATTRIBUTE_PARTNER = MEMBER_TYPE_ATTRIBUTE_PARTNER_VW.substring(3);
    /** Memberelement-Geburtstagattribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_BIRTHDAY_ATTRIBUTE_VW = "vw:birthday";
    /** lokaler Memberelement-Geburtstagattribut-Name */
    public final static String MEMBER_BIRTHDAY_ATTRIBUTE = MEMBER_BIRTHDAY_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Geschlechtattribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_GENDER_ATTRIBUTE_VW = "vw:gender";
    /** lokaler Memberelement-Geschlechtattribut-Name */
    public final static String MEMBER_GENDER_ATTRIBUTE = MEMBER_GENDER_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Domesticattribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_DOMESTIC_ATTRIBUTE_VW = "vw:domestic";
    /** lokaler Memberelement-Domesticattribut-Name */
    public final static String MEMBER_DOMESTIC_ATTRIBUTE = MEMBER_DOMESTIC_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Nationalit�tattribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_NATIONALITY_ATTRIBUTE_VW = "vw:nationality";
    /** lokaler Memberelement-Nationalit�tattribut-Name */
    public final static String MEMBER_NATIONALITY_ATTRIBUTE = MEMBER_NATIONALITY_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Sprachenattribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_LANGUAGES_ATTRIBUTE_VW = "vw:languages";
    /** lokaler Memberelement-Sprachenattribut-Name */
    public final static String MEMBER_LANGUAGES_ATTRIBUTE = MEMBER_LANGUAGES_ATTRIBUTE_VW.substring(3);
    /** Memberelement-Akkreditierung-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String MEMBER_ACCREDITATION_ATTRIBUTE_VW = "vw:accreditation";
    /** lokaler Memberelement-Akkreditierung-Attribut-Name */
    public final static String MEMBER_ACCREDITATION_ATTRIBUTE = MEMBER_ACCREDITATION_ATTRIBUTE_VW.substring(3);
    /** Notizelement-Name mit Namensraumm-Pr�fix */
    public final static String NOTE_ELEMENT_VW = "vw:note";
    /** lokaler Notizelement-Name */
    public final static String NOTE_ELEMENT = NOTE_ELEMENT_VW.substring(3);
    /** Notizelement-Typattribut-Name mit Namensraumm-Pr�fix */
    public final static String NOTE_TYPE_ATTRIBUTE_VW = "vw:type";
    /** lokaler Notizelement-Typattribut-Name */
    public final static String NOTE_TYPE_ATTRIBUTE = NOTE_TYPE_ATTRIBUTE_VW.substring(3);
    /** Notizelement-Typattribut-Wert allgemein mit Namensraumm-Pr�fix */
    public final static String NOTE_TYPE_ATTRIBUTE_GENERAL_VW = "vw:general";
    /** lokaler Notizelement-Typattribut-Wert allgemein */
    public final static String NOTE_TYPE_ATTRIBUTE_GENERAL = NOTE_TYPE_ATTRIBUTE_GENERAL_VW.substring(3);
    /** Notizelement-Typattribut-Wert Organisation mit Namensraumm-Pr�fix */
    public final static String NOTE_TYPE_ATTRIBUTE_ORGANIZATION_VW = "vw:organization";
    /** lokaler Notizelement-Typattribut-Wert Organisation */
    public final static String NOTE_TYPE_ATTRIBUTE_ORGANIZATION = NOTE_TYPE_ATTRIBUTE_ORGANIZATION_VW.substring(3);
    /** Notizelement-Typattribut-Wert Gastgeber mit Namensraumm-Pr�fix */
    public final static String NOTE_TYPE_ATTRIBUTE_HOST_VW = "vw:host";
    /** lokaler Notizelement-Typattribut-Wert Gastgeber */
    public final static String NOTE_TYPE_ATTRIBUTE_HOST = NOTE_TYPE_ATTRIBUTE_HOST_VW.substring(3);
    /** Namenelement-Name mit Namensraumm-Pr�fix */
    public final static String NAME_ELEMENT_VW = "vw:name";
    /** lokaler Namenelement-Name */
    public final static String NAME_ELEMENT = NAME_ELEMENT_VW.substring(3);
    /** Namenelement-Zeichensatzattribut-Name mit Namensraumm-Pr�fix */
    public final static String NAME_LANGUAGE_ATTRIBUTE_VW = "vw:language";
    /** lokaler Namenelement-Zeichensatzattribut-Name */
    public final static String NAME_LANGUAGE_ATTRIBUTE = NAME_LANGUAGE_ATTRIBUTE_VW.substring(3);
    /** Namenelement-Vornamenattribut-Name mit Namensraumm-Pr�fix */
    public final static String NAME_FIRSTNAME_ATTRIBUTE_VW = "vw:firstname";
    /** lokaler Namenelement-Vornamenattribut-Name */
    public final static String NAME_FIRSTNAME_ATTRIBUTE = NAME_FIRSTNAME_ATTRIBUTE_VW.substring(3);
    /** Namenelement-Nachnamenattribut-Name mit Namensraumm-Pr�fix */
    public final static String NAME_LASTNAME_ATTRIBUTE_VW = "vw:lastname";
    /** lokaler Namenelement-Nachnamenattribut-Name */
    public final static String NAME_LASTNAME_ATTRIBUTE = NAME_LASTNAME_ATTRIBUTE_VW.substring(3);
    /** Namenelement-Titelattribut-Name mit Namensraumm-Pr�fix */
    public final static String NAME_TITLE_ATTRIBUTE_VW = "vw:title";
    /** lokaler Namenelement-Titelattribut-Name */
    public final static String NAME_TITLE_ATTRIBUTE = NAME_TITLE_ATTRIBUTE_VW.substring(3);
    /** Namenelement-Anredeattribut-Name mit Namensraumm-Pr�fix */
    public final static String NAME_SALUTATION_ATTRIBUTE_VW = "vw:salutation";
    /** lokaler Namenelement-Anredeattribut-Name */
    public final static String NAME_SALUTATION_ATTRIBUTE = NAME_SALUTATION_ATTRIBUTE_VW.substring(3);
    /** Adresselement-Name mit Namensraumm-Pr�fix */
    public final static String ADDRESS_ELEMENT_VW = "vw:address";
    /** lokaler Adresselement-Name */
    public final static String ADDRESS_ELEMENT = ADDRESS_ELEMENT_VW.substring(3);
    /** Adresselement-Typattribut-Name mit Namensraumm-Pr�fix */
    public final static String ADDRESS_TYPE_ATTRIBUTE_VW = "vw:type";
    /** lokaler Adresselement-Typattribut-Name */
    public final static String ADDRESS_TYPE_ATTRIBUTE = ADDRESS_TYPE_ATTRIBUTE_VW.substring(3);
    /** Adresselement-Typattribut-Wert privat mit Namensraumm-Pr�fix */
    public final static String ADDRESS_TYPE_ATTRIBUTE_PRIVATE_VW = "vw:private";
    /** lokaler Adresselement-Typattribut-Wert privat */
    public final static String ADDRESS_TYPE_ATTRIBUTE_PRIVATE = ADDRESS_TYPE_ATTRIBUTE_PRIVATE_VW.substring(3);
    /** Adresselement-Typattribut-Wert dienst mit Namensraumm-Pr�fix */
    public final static String ADDRESS_TYPE_ATTRIBUTE_BUSINESS_VW = "vw:business";
    /** lokaler Adresselement-Typattribut-Wert dienst */
    public final static String ADDRESS_TYPE_ATTRIBUTE_BUSINESS = ADDRESS_TYPE_ATTRIBUTE_BUSINESS_VW.substring(3);
    /** Adresselement-Typattribut-Wert sonstig mit Namensraumm-Pr�fix */
    public final static String ADDRESS_TYPE_ATTRIBUTE_OTHER_VW = "vw:other";
    /** lokaler Adresselement-Typattribut-Wert sonstig */
    public final static String ADDRESS_TYPE_ATTRIBUTE_OTHER = ADDRESS_TYPE_ATTRIBUTE_OTHER_VW.substring(3);
    /** Adresselement-Zeichensatzattribut-Name mit Namensraumm-Pr�fix */
    public final static String ADDRESS_LANGUAGE_ATTRIBUTE_VW = "vw:language";
    /** lokaler Adresselement-Zeichensatzattribut-Name */
    public final static String ADDRESS_LANGUAGE_ATTRIBUTE = ADDRESS_LANGUAGE_ATTRIBUTE_VW.substring(3);
    /** Adresselement-Landattribut-Name mit Namensraumm-Pr�fix */
    public final static String ADDRESS_COUNTRY_ATTRIBUTE_VW = "vw:country";
    /** lokaler Adresselement-Landattribut-Name */
    public final static String ADDRESS_COUNTRY_ATTRIBUTE = ADDRESS_COUNTRY_ATTRIBUTE_VW.substring(3);
    /** Adresselement-EMailattribut-Name mit Namensraumm-Pr�fix */
    public final static String ADDRESS_EMAIL_ATTRIBUTE_VW = "vw:email";
    /** lokaler Adresselement-EMailattribut-Name */
    public final static String ADDRESS_EMAIL_ATTRIBUTE = ADDRESS_EMAIL_ATTRIBUTE_VW.substring(3);
    /** Adresselement-URLattribut-Name mit Namensraumm-Pr�fix */
    public final static String ADDRESS_URL_ATTRIBUTE_VW = "vw:url";
    /** lokaler Adresselement-URLattribut-Name */
    public final static String ADDRESS_URL_ATTRIBUTE = ADDRESS_URL_ATTRIBUTE_VW.substring(3);
    /** Funktionelement-Name mit Namensraumm-Pr�fix */
    public final static String FUNCTION_ELEMENT_VW = "vw:function";
    /** lokaler Funktionelement-Name */
    public final static String FUNCTION_ELEMENT = FUNCTION_ELEMENT_VW.substring(3);
    /** Firmaelement-Name mit Namensraumm-Pr�fix */
    public final static String COMPANY_ELEMENT_VW = "vw:company";
    /** lokaler Firmaelement-Name */
    public final static String COMPANY_ELEMENT = COMPANY_ELEMENT_VW.substring(3);
    /** Stra�enelement-Name mit Namensraumm-Pr�fix */
    public final static String STREET_ELEMENT_VW = "vw:street";
    /** lokaler Stra�enelement-Name */
    public final static String STREET_ELEMENT = STREET_ELEMENT_VW.substring(3);
    /** Stadtelement-Name mit Namensraumm-Pr�fix */
    public final static String CITY_ELEMENT_VW = "vw:city";
    /** lokaler Stadtelement-Name */
    public final static String CITY_ELEMENT = CITY_ELEMENT_VW.substring(3);
    /** Bundesland-Name mit Namensraumm-Präfix */
    public final static String STATE_ELEMENT_VW = "vw:state";
    /** lokaler Bundesland-Name */
    public final static String STATE_ELEMENT = STATE_ELEMENT_VW.substring(3);
    /** Stadtelement-PLZattribut-Name mit Namensraumm-Pr�fix */
    public final static String CITY_ZIPCODE_ATTRIBUTE_VW = "vw:zipcode";
    /** lokaler Stadtelement-PLZattribut-Name */
    public final static String CITY_ZIPCODE_ATTRIBUTE = CITY_ZIPCODE_ATTRIBUTE_VW.substring(3);
    /** Postfachelement-Name mit Namensraumm-Pr�fix */
    public final static String POBOX_ELEMENT_VW = "vw:pobox";
    /** lokaler Postfachelement-Name */
    public final static String POBOX_ELEMENT = POBOX_ELEMENT_VW.substring(3);
    /** Postfachelement-PLZattribut-Name mit Namensraumm-Pr�fix */
    public final static String POBOX_ZIPCODE_ATTRIBUTE_VW = "vw:zipcode";
    /** lokaler Postfachelement-PLZattribut-Name */
    public final static String POBOX_ZIPCODE_ATTRIBUTE = POBOX_ZIPCODE_ATTRIBUTE_VW.substring(3);
    /** Suffixelement-Name mit Namensraumm-Pr�fix */
    public final static String SUFFIX_ELEMENT_VW = "vw:suffix";
    /** lokaler Suffixelement-Name */
    public final static String SUFFIX_ELEMENT = SUFFIX_ELEMENT_VW.substring(3);
    /** Suffixelement-Indexattribut-Name mit Namensraumm-Pr�fix */
    public final static String SUFFIX_INDEX_ATTRIBUTE_VW = "vw:index";
    /** lokaler Suffixelement-Indexattribut-Name */
    public final static String SUFFIX_INDEX_ATTRIBUTE = SUFFIX_INDEX_ATTRIBUTE_VW.substring(3);
    /** Suffixelement-Indexattribut-Wert 1 mit Namensraumm-Pr�fix */
    public final static String SUFFIX_INDEX_ATTRIBUTE_1_VW = "vw:1";
    /** lokaler Suffixelement-Indexattribut-Wert 1 */
    public final static String SUFFIX_INDEX_ATTRIBUTE_1 = SUFFIX_INDEX_ATTRIBUTE_1_VW.substring(3);
    /** Suffixelement-Indexattribut-Wert 1 mit Namensraumm-Pr�fix */
    public final static String SUFFIX_INDEX_ATTRIBUTE_2_VW = "vw:2";
    /** lokaler Suffixelement-Indexattribut-Wert 2 */
    public final static String SUFFIX_INDEX_ATTRIBUTE_2 = SUFFIX_INDEX_ATTRIBUTE_2_VW.substring(3);
    /** Telefonelement-Name mit Namensraumm-Pr�fix */
    public final static String PHONE_ELEMENT_VW = "vw:phone";
    /** lokaler Telefonelement-Name */
    public final static String PHONE_ELEMENT = PHONE_ELEMENT_VW.substring(3);
    /** Telefonelement-Typattribut-Name mit Namensraumm-Pr�fix */
    public final static String PHONE_TYPE_ATTRIBUTE_VW = "vw:type";
    /** lokaler Telefonelement-Typattribut-Name */
    public final static String PHONE_TYPE_ATTRIBUTE = PHONE_TYPE_ATTRIBUTE_VW.substring(3);
    /** Telefonelement-Typattribut-Wert Festnetz mit Namensraumm-Pr�fix */
    public final static String PHONE_TYPE_ATTRIBUTE_FIXED_VW = "vw:fixed";
    /** lokaler Telefonelement-Typattribut-Wert Festnetz */
    public final static String PHONE_TYPE_ATTRIBUTE_FIXED = PHONE_TYPE_ATTRIBUTE_FIXED_VW.substring(3);
    /** Telefonelement-Typattribut-Wert Fax mit Namensraumm-Pr�fix */
    public final static String PHONE_TYPE_ATTRIBUTE_FAX_VW = "vw:fax";
    /** lokaler Telefonelement-Typattribut-Wert Fax */
    public final static String PHONE_TYPE_ATTRIBUTE_FAX = PHONE_TYPE_ATTRIBUTE_FAX_VW.substring(3);
    /** Telefonelement-Typattribut-Wert Mobil mit Namensraumm-Pr�fix */
    public final static String PHONE_TYPE_ATTRIBUTE_MOBILE_VW = "vw:mobile";
    /** lokaler Telefonelement-Typattribut-Wert Mobil */
    public final static String PHONE_TYPE_ATTRIBUTE_MOBILE = PHONE_TYPE_ATTRIBUTE_MOBILE_VW.substring(3);
    /** Kategorieelement-Name mit Namensraumm-Pr�fix */
    public final static String CATEGORY_ELEMENT_VW = "vw:category";
    /** lokaler Kategorieelement-Name */
    public final static String CATEGORY_ELEMENT = CATEGORY_ELEMENT_VW.substring(3);
    /** Kategorie-ID-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String CATEGORY_ID_ATTRIBUTE_VW = "vw:id";
    /** lokaler Kategorie-ID-Attribut-Name */
    public final static String CATEGORY_ID_ATTRIBUTE = CATEGORY_ID_ATTRIBUTE_VW.substring(3);
    /** Kategorie-Veranstaltung-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String CATEGORY_EVENT_ATTRIBUTE_VW = "vw:event";
    /** lokaler Kategorie-Veranstaltung-Attribut-Name */
    public final static String CATEGORY_EVENT_ATTRIBUTE = CATEGORY_EVENT_ATTRIBUTE_VW.substring(3);
    /** Kategorie-Name-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String CATEGORY_NAME_ATTRIBUTE_VW = "vw:name";
    /** lokaler Kategorie-Name-Attribut-Name */
    public final static String CATEGORY_NAME_ATTRIBUTE = CATEGORY_NAME_ATTRIBUTE_VW.substring(3);
    /** Kategorie-Flags-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String CATEGORY_FLAGS_ATTRIBUTE_VW = "vw:flags";
    /** lokaler Kategorie-Flags-Attribut-Name */
    public final static String CATEGORY_FLAGS_ATTRIBUTE = CATEGORY_FLAGS_ATTRIBUTE_VW.substring(3);
    /** Kategorie-Rang-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String CATEGORY_RANK_ATTRIBUTE_VW = "vw:rank";
    /** lokaler Kategorie-Rang-Attribut-Name */
    public final static String CATEGORY_RANK_ATTRIBUTE = CATEGORY_RANK_ATTRIBUTE_VW.substring(3);
    /** Dokumenttypelement-Name mit Namensraumm-Pr�fix */
    public final static String DOCTYPE_ELEMENT_VW = "vw:doctype";
    /** lokaler Dokumenttypelement-Name */
    public final static String DOCTYPE_ELEMENT = DOCTYPE_ELEMENT_VW.substring(3);
    /** Dokumenttyp-ID-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String DOCTYPE_ID_ATTRIBUTE_VW = "vw:id";
    /** lokaler Dokumenttyp-ID-Attribut-Name */
    public final static String DOCTYPE_ID_ATTRIBUTE = DOCTYPE_ID_ATTRIBUTE_VW.substring(3);
    /** Dokumenttypname-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String DOCTYPE_NAME_ATTRIBUTE_VW = "vw:name";
    /** lokaler Dokumenttypname-Attribut-Name */
    public final static String DOCTYPE_NAME_ATTRIBUTE = DOCTYPE_NAME_ATTRIBUTE_VW.substring(3);
    /** Dokumenttypfreitext-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String DOCTYPE_TEXT_ATTRIBUTE_VW = "vw:text";
    /** lokaler Dokumenttypfreitext-Attribut-Name */
    public final static String DOCTYPE_TEXT_ATTRIBUTE = DOCTYPE_TEXT_ATTRIBUTE_VW.substring(3);
    /** Dokumenttyppartnerfreitext-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String DOCTYPE_TEXT_PARTNER_ATTRIBUTE_VW = "vw:textpartner";
    /** lokaler Dokumenttyppartnerfreitext-Attribut-Name */
    public final static String DOCTYPE_TEXT_PARTNER_ATTRIBUTE = DOCTYPE_TEXT_PARTNER_ATTRIBUTE_VW.substring(3);
    /** Dokumenttypfreitextverbinder-Attribut-Name mit Namensraumm-Pr�fix */
    public final static String DOCTYPE_TEXT_JOIN_ATTRIBUTE_VW = "vw:textjoin";
    /** lokaler Dokumenttypfreitextverbinder-Attribut-Name */
    public final static String DOCTYPE_TEXT_JOIN_ATTRIBUTE = DOCTYPE_TEXT_JOIN_ATTRIBUTE_VW.substring(3);

    /** Zeichensatzattribut-Wert Latin mit Namensraumm-Pr�fix */
    public final static String LANGUAGE_ATTRIBUTE_LATIN_VW = "vw:latin";
    /** lokaler Zeichensatzattribut-Wert Latin */
    public final static String LANGUAGE_ATTRIBUTE_LATIN = LANGUAGE_ATTRIBUTE_LATIN_VW.substring(3);
    /** Zeichensatzattribut-Wert Extra 1 mit Namensraumm-Pr�fix */
    public final static String LANGUAGE_ATTRIBUTE_EXTRA_1_VW = "vw:extra-1";
    /** lokaler Zeichensatzattribut-Wert Extra 1 */
    public final static String LANGUAGE_ATTRIBUTE_EXTRA_1 = LANGUAGE_ATTRIBUTE_EXTRA_1_VW.substring(3);
    /** Zeichensatzattribut-Wert Extra 2 mit Namensraumm-Pr�fix */
    public final static String LANGUAGE_ATTRIBUTE_EXTRA_2_VW = "vw:extra-2";
    /** lokaler Zeichensatzattribut-Wert Extra 2 */
    public final static String LANGUAGE_ATTRIBUTE_EXTRA_2 = LANGUAGE_ATTRIBUTE_EXTRA_2_VW.substring(3);
}
