package de.tarent.aa.veraweb.utils;

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
/**
 * Diese Schnittstelle enthält Konstanten, die für das Erstellen und Parsen von
 * VerA.web-XML-Dokumenten nötig sind.
 *
 * @author mikel
 */
public interface VerawebNamespaceConstants {
    /**
     * VerA.web-Namensraum-URI
     */
    String VW_NAMESPACE_URI = "http://schemas.tarent.de/veraweb";
    /**
     * xmlns-Namensraum-URI
     */
    String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    /**
     * xmlns:vw-Namensraum-Attribut
     */
    String VW_NAMESPACE_ATTRIBUTE = "xmlns:vw";
    /**
     * Dokumentelement-Name mit Namensraumm-Præfix
     */
    String CONTAINER_ELEMENT_VW = "vw:container";
    /**
     * lokaler Dokumentelement-Name
     */
    String CONTAINER_ELEMENT = CONTAINER_ELEMENT_VW.substring(3);
    /**
     * Personenelement-Name mit Namensraumm-Præfix
     */
    String PERSON_ELEMENT_VW = "vw:person";
    /**
     * lokaler Personenelement-Name
     */
    String PERSON_ELEMENT = PERSON_ELEMENT_VW.substring(3);
    /**
     * Personenelement-Mandant-Attribut-Name mit Namensraumm-Præfix
     */
    String PERSON_ORG_UNIT_ATTRIBUTE_VW = "vw:org-unit";
    /**
     * lokaler Personenelement-Mandant-Attribut-Name
     */
    String PERSON_ORG_UNIT_ATTRIBUTE = PERSON_ORG_UNIT_ATTRIBUTE_VW.substring(3);
    /**
     * Personenelement-ID-Attribut-Name mit Namensraumm-Præfix
     */
    String PERSON_ID_ATTRIBUTE_VW = "vw:id";
    /**
     * lokaler Personenelement-ID-Attribut-Name
     */
    String PERSON_ID_ATTRIBUTE = PERSON_ID_ATTRIBUTE_VW.substring(3);
    /**
     * Personenelement-SaveAs-Attribut-Name mit Namensraumm-Præfix
     */
    String PERSON_SAVE_AS_ATTRIBUTE_VW = "vw:save-as";
    /**
     * lokaler Personenelement-SaveAs-Attribut-Name
     */
    String PERSON_SAVE_AS_ATTRIBUTE = PERSON_SAVE_AS_ATTRIBUTE_VW.substring(3);
    /**
     * Personenelement-IsCompany-Attribut-Name mit Namensraumm-Præfix
     */
    String PERSON_IS_COMPANY_ATTRIBUTE_VW = "vw:is-company";
    /**
     * lokaler Personenelement-IsCompany-Attribut-Name
     */
    String PERSON_IS_COMPANY_ATTRIBUTE = PERSON_IS_COMPANY_ATTRIBUTE_VW.substring(3);
    /**
     * Personenelement-Gültigkeit-Attribut-Name mit Namensraumm-Præfix
     */
    String PERSON_EXPIRATION_ATTRIBUTE_VW = "vw:expiration";
    /**
     * lokaler Personenelement-Gültigkeit-Attribut-Name
     */
    String PERSON_EXPIRATION_ATTRIBUTE = PERSON_EXPIRATION_ATTRIBUTE_VW.substring(3);
    /**
     * Historyelement-Name mit Namensraumm-Præfix
     */
    String HISTORY_ELEMENT_VW = "vw:history";
    /**
     * lokaler Historyelement-Name
     */
    String HISTORY_ELEMENT = HISTORY_ELEMENT_VW.substring(3);
    /**
     * Historyelement-Erstellerattribut-Name mit Namensraumm-Præfix
     */
    String HISTORY_CREATOR_ATTRIBUTE_VW = "vw:creator";
    /**
     * lokaler Historyelement-Erstellerattribut-Name
     */
    String HISTORY_CREATOR_ATTRIBUTE = HISTORY_CREATOR_ATTRIBUTE_VW.substring(3);
    /**
     * Historyelement-Erstellungattribut-Name mit Namensraumm-Præfix
     */
    String HISTORY_CREATION_ATTRIBUTE_VW = "vw:creation";
    /**
     * lokaler Historyelement-Erstellungattribut-Name
     */
    String HISTORY_CREATION_ATTRIBUTE = HISTORY_CREATION_ATTRIBUTE_VW.substring(3);
    /**
     * Historyelement-Bearbeiterattribut-Name mit Namensraumm-Præfix
     */
    String HISTORY_EDITOR_ATTRIBUTE_VW = "vw:editor";
    /**
     * lokaler Historyelement-Bearbeiterattribut-Name
     */
    String HISTORY_EDITOR_ATTRIBUTE = HISTORY_EDITOR_ATTRIBUTE_VW.substring(3);
    /**
     * Historyelement-Bearbeitungattribut-Name mit Namensraumm-Præfix
     */
    String HISTORY_EDITING_ATTRIBUTE_VW = "vw:editing";
    /**
     * lokaler Historyelement-Bearbeitungattribut-Name
     */
    String HISTORY_EDITING_ATTRIBUTE = HISTORY_EDITING_ATTRIBUTE_VW.substring(3);
    /**
     * Historyelement-Bearbeitungattribut-alternativ-Name mit Namensraumm-Præfix
     */
    String HISTORY_EDITING_ALT_ATTRIBUTE_VW = "vw:editing-alt";
    /**
     * lokaler Historyelement-Bearbeitungattribut-alternativ-Name
     */
    String HISTORY_EDITING_ALT_ATTRIBUTE = HISTORY_EDITING_ALT_ATTRIBUTE_VW.substring(3);
    /**
     * Historyelement-Quelleattribut-Name mit Namensraumm-Præfix
     */
    String HISTORY_SOURCE_ATTRIBUTE_VW = "vw:source";
    /**
     * lokaler Historyelement-Quelleattribut-Name
     */
    String HISTORY_SOURCE_ATTRIBUTE = HISTORY_SOURCE_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Name mit Namensraumm-Præfix
     */
    String MEMBER_ELEMENT_VW = "vw:member";
    /**
     * lokaler Memberelement-Name
     */
    String MEMBER_ELEMENT = MEMBER_ELEMENT_VW.substring(3);
    /**
     * Memberelement-Typattribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_TYPE_ATTRIBUTE_VW = "vw:type";
    /**
     * lokaler Memberelement-Typattribut-Name
     */
    String MEMBER_TYPE_ATTRIBUTE = MEMBER_TYPE_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Typattribut-Wert Hauptperson mit Namensraumm-Præfix
     */
    String MEMBER_TYPE_ATTRIBUTE_MAIN_VW = "vw:main";
    /**
     * lokaler Memberelement-Typattribut-Wert Hauptperson
     */
    String MEMBER_TYPE_ATTRIBUTE_MAIN = MEMBER_TYPE_ATTRIBUTE_MAIN_VW.substring(3);
    /**
     * Memberelement-Typattribut-Wert Partner mit Namensraumm-Præfix
     */
    String MEMBER_TYPE_ATTRIBUTE_PARTNER_VW = "vw:partner";
    /**
     * lokaler Memberelement-Typattribut-Wert Partner
     */
    String MEMBER_TYPE_ATTRIBUTE_PARTNER = MEMBER_TYPE_ATTRIBUTE_PARTNER_VW.substring(3);
    /**
     * Memberelement-Geburtstagattribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_BIRTHDAY_ATTRIBUTE_VW = "vw:birthday";
    /**
     * lokaler Memberelement-Geburtstagattribut-Name
     */
    String MEMBER_BIRTHDAY_ATTRIBUTE = MEMBER_BIRTHDAY_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Geschlechtattribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_GENDER_ATTRIBUTE_VW = "vw:gender";
    /**
     * lokaler Memberelement-Geschlechtattribut-Name
     */
    String MEMBER_GENDER_ATTRIBUTE = MEMBER_GENDER_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Domesticattribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_DOMESTIC_ATTRIBUTE_VW = "vw:domestic";
    /**
     * lokaler Memberelement-Domesticattribut-Name
     */
    String MEMBER_DOMESTIC_ATTRIBUTE = MEMBER_DOMESTIC_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Nationalitätattribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_NATIONALITY_ATTRIBUTE_VW = "vw:nationality";
    /**
     * lokaler Memberelement-Nationalitätattribut-Name
     */
    String MEMBER_NATIONALITY_ATTRIBUTE = MEMBER_NATIONALITY_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Sprachenattribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_LANGUAGES_ATTRIBUTE_VW = "vw:languages";
    /**
     * lokaler Memberelement-Sprachenattribut-Name
     */
    String MEMBER_LANGUAGES_ATTRIBUTE = MEMBER_LANGUAGES_ATTRIBUTE_VW.substring(3);
    /**
     * Memberelement-Akkreditierung-Attribut-Name mit Namensraumm-Præfix
     */
    String MEMBER_ACCREDITATION_ATTRIBUTE_VW = "vw:accreditation";
    /**
     * lokaler Memberelement-Akkreditierung-Attribut-Name
     */
    String MEMBER_ACCREDITATION_ATTRIBUTE = MEMBER_ACCREDITATION_ATTRIBUTE_VW.substring(3);
    /**
     * Notizelement-Name mit Namensraumm-Præfix
     */
    String NOTE_ELEMENT_VW = "vw:note";
    /**
     * lokaler Notizelement-Name
     */
    String NOTE_ELEMENT = NOTE_ELEMENT_VW.substring(3);
    /**
     * Notizelement-Typattribut-Name mit Namensraumm-Præfix
     */
    String NOTE_TYPE_ATTRIBUTE_VW = "vw:type";
    /**
     * lokaler Notizelement-Typattribut-Name
     */
    String NOTE_TYPE_ATTRIBUTE = NOTE_TYPE_ATTRIBUTE_VW.substring(3);
    /**
     * Notizelement-Typattribut-Wert allgemein mit Namensraumm-Præfix
     */
    String NOTE_TYPE_ATTRIBUTE_GENERAL_VW = "vw:general";
    /**
     * lokaler Notizelement-Typattribut-Wert allgemein
     */
    String NOTE_TYPE_ATTRIBUTE_GENERAL = NOTE_TYPE_ATTRIBUTE_GENERAL_VW.substring(3);
    /**
     * Notizelement-Typattribut-Wert Organisation mit Namensraumm-Præfix
     */
    String NOTE_TYPE_ATTRIBUTE_ORGANIZATION_VW = "vw:organization";
    /**
     * lokaler Notizelement-Typattribut-Wert Organisation
     */
    String NOTE_TYPE_ATTRIBUTE_ORGANIZATION = NOTE_TYPE_ATTRIBUTE_ORGANIZATION_VW.substring(3);
    /**
     * Notizelement-Typattribut-Wert Gastgeber mit Namensraumm-Præfix
     */
    String NOTE_TYPE_ATTRIBUTE_HOST_VW = "vw:host";
    /**
     * lokaler Notizelement-Typattribut-Wert Gastgeber
     */
    String NOTE_TYPE_ATTRIBUTE_HOST = NOTE_TYPE_ATTRIBUTE_HOST_VW.substring(3);
    /**
     * Namenelement-Name mit Namensraumm-Præfix
     */
    String NAME_ELEMENT_VW = "vw:name";
    /**
     * lokaler Namenelement-Name
     */
    String NAME_ELEMENT = NAME_ELEMENT_VW.substring(3);
    /**
     * Namenelement-Zeichensatzattribut-Name mit Namensraumm-Præfix
     */
    String NAME_LANGUAGE_ATTRIBUTE_VW = "vw:language";
    /**
     * lokaler Namenelement-Zeichensatzattribut-Name
     */
    String NAME_LANGUAGE_ATTRIBUTE = NAME_LANGUAGE_ATTRIBUTE_VW.substring(3);
    /**
     * Namenelement-Vornamenattribut-Name mit Namensraumm-Præfix
     */
    String NAME_FIRSTNAME_ATTRIBUTE_VW = "vw:firstname";
    /**
     * lokaler Namenelement-Vornamenattribut-Name
     */
    String NAME_FIRSTNAME_ATTRIBUTE = NAME_FIRSTNAME_ATTRIBUTE_VW.substring(3);
    /**
     * Namenelement-Nachnamenattribut-Name mit Namensraumm-Præfix
     */
    String NAME_LASTNAME_ATTRIBUTE_VW = "vw:lastname";
    /**
     * lokaler Namenelement-Nachnamenattribut-Name
     */
    String NAME_LASTNAME_ATTRIBUTE = NAME_LASTNAME_ATTRIBUTE_VW.substring(3);
    /**
     * Namenelement-Titelattribut-Name mit Namensraumm-Præfix
     */
    String NAME_TITLE_ATTRIBUTE_VW = "vw:title";
    /**
     * lokaler Namenelement-Titelattribut-Name
     */
    String NAME_TITLE_ATTRIBUTE = NAME_TITLE_ATTRIBUTE_VW.substring(3);
    /**
     * Namenelement-Anredeattribut-Name mit Namensraumm-Præfix
     */
    String NAME_SALUTATION_ATTRIBUTE_VW = "vw:salutation";
    /**
     * lokaler Namenelement-Anredeattribut-Name
     */
    String NAME_SALUTATION_ATTRIBUTE = NAME_SALUTATION_ATTRIBUTE_VW.substring(3);
    /**
     * Adresselement-Name mit Namensraumm-Præfix
     */
    String ADDRESS_ELEMENT_VW = "vw:address";
    /**
     * lokaler Adresselement-Name
     */
    String ADDRESS_ELEMENT = ADDRESS_ELEMENT_VW.substring(3);
    /**
     * Adresselement-Typattribut-Name mit Namensraumm-Præfix
     */
    String ADDRESS_TYPE_ATTRIBUTE_VW = "vw:type";
    /**
     * lokaler Adresselement-Typattribut-Name
     */
    String ADDRESS_TYPE_ATTRIBUTE = ADDRESS_TYPE_ATTRIBUTE_VW.substring(3);
    /**
     * Adresselement-Typattribut-Wert privat mit Namensraumm-Præfix
     */
    String ADDRESS_TYPE_ATTRIBUTE_PRIVATE_VW = "vw:private";
    /**
     * lokaler Adresselement-Typattribut-Wert privat
     */
    String ADDRESS_TYPE_ATTRIBUTE_PRIVATE = ADDRESS_TYPE_ATTRIBUTE_PRIVATE_VW.substring(3);
    /**
     * Adresselement-Typattribut-Wert dienst mit Namensraumm-Præfix
     */
    String ADDRESS_TYPE_ATTRIBUTE_BUSINESS_VW = "vw:business";
    /**
     * lokaler Adresselement-Typattribut-Wert dienst
     */
    String ADDRESS_TYPE_ATTRIBUTE_BUSINESS = ADDRESS_TYPE_ATTRIBUTE_BUSINESS_VW.substring(3);
    /**
     * Adresselement-Typattribut-Wert sonstig mit Namensraumm-Præfix
     */
    String ADDRESS_TYPE_ATTRIBUTE_OTHER_VW = "vw:other";
    /**
     * lokaler Adresselement-Typattribut-Wert sonstig
     */
    String ADDRESS_TYPE_ATTRIBUTE_OTHER = ADDRESS_TYPE_ATTRIBUTE_OTHER_VW.substring(3);
    /**
     * Adresselement-Zeichensatzattribut-Name mit Namensraumm-Præfix
     */
    String ADDRESS_LANGUAGE_ATTRIBUTE_VW = "vw:language";
    /**
     * lokaler Adresselement-Zeichensatzattribut-Name
     */
    String ADDRESS_LANGUAGE_ATTRIBUTE = ADDRESS_LANGUAGE_ATTRIBUTE_VW.substring(3);
    /**
     * Adresselement-Landattribut-Name mit Namensraumm-Præfix
     */
    String ADDRESS_COUNTRY_ATTRIBUTE_VW = "vw:country";
    /**
     * lokaler Adresselement-Landattribut-Name
     */
    String ADDRESS_COUNTRY_ATTRIBUTE = ADDRESS_COUNTRY_ATTRIBUTE_VW.substring(3);
    /**
     * Adresselement-EMailattribut-Name mit Namensraumm-Præfix
     */
    String ADDRESS_EMAIL_ATTRIBUTE_VW = "vw:email";
    /**
     * lokaler Adresselement-EMailattribut-Name
     */
    String ADDRESS_EMAIL_ATTRIBUTE = ADDRESS_EMAIL_ATTRIBUTE_VW.substring(3);
    /**
     * Adresselement-URLattribut-Name mit Namensraumm-Præfix
     */
    String ADDRESS_URL_ATTRIBUTE_VW = "vw:url";
    /**
     * lokaler Adresselement-URLattribut-Name
     */
    String ADDRESS_URL_ATTRIBUTE = ADDRESS_URL_ATTRIBUTE_VW.substring(3);
    /**
     * Funktionelement-Name mit Namensraumm-Præfix
     */
    String FUNCTION_ELEMENT_VW = "vw:function";
    /**
     * lokaler Funktionelement-Name
     */
    String FUNCTION_ELEMENT = FUNCTION_ELEMENT_VW.substring(3);
    /**
     * Firmaelement-Name mit Namensraumm-Præfix
     */
    String COMPANY_ELEMENT_VW = "vw:company";
    /**
     * lokaler Firmaelement-Name
     */
    String COMPANY_ELEMENT = COMPANY_ELEMENT_VW.substring(3);
    /**
     * Straßenelement-Name mit Namensraumm-Præfix
     */
    String STREET_ELEMENT_VW = "vw:street";
    /**
     * lokaler Straßenelement-Name
     */
    String STREET_ELEMENT = STREET_ELEMENT_VW.substring(3);
    /**
     * Stadtelement-Name mit Namensraumm-Præfix
     */
    String CITY_ELEMENT_VW = "vw:city";
    /**
     * lokaler Stadtelement-Name
     */
    String CITY_ELEMENT = CITY_ELEMENT_VW.substring(3);
    /**
     * Bundesland-Name mit Namensraumm-Präfix
     */
    String STATE_ELEMENT_VW = "vw:state";
    /**
     * lokaler Bundesland-Name
     */
    String STATE_ELEMENT = STATE_ELEMENT_VW.substring(3);
    /**
     * Stadtelement-PLZattribut-Name mit Namensraumm-Præfix
     */
    String CITY_ZIPCODE_ATTRIBUTE_VW = "vw:zipcode";
    /**
     * lokaler Stadtelement-PLZattribut-Name
     */
    String CITY_ZIPCODE_ATTRIBUTE = CITY_ZIPCODE_ATTRIBUTE_VW.substring(3);
    /**
     * Postfachelement-Name mit Namensraumm-Præfix
     */
    String POBOX_ELEMENT_VW = "vw:pobox";
    /**
     * lokaler Postfachelement-Name
     */
    String POBOX_ELEMENT = POBOX_ELEMENT_VW.substring(3);
    /**
     * Postfachelement-PLZattribut-Name mit Namensraumm-Præfix
     */
    String POBOX_ZIPCODE_ATTRIBUTE_VW = "vw:zipcode";
    /**
     * lokaler Postfachelement-PLZattribut-Name
     */
    String POBOX_ZIPCODE_ATTRIBUTE = POBOX_ZIPCODE_ATTRIBUTE_VW.substring(3);
    /**
     * Suffixelement-Name mit Namensraumm-Præfix
     */
    String SUFFIX_ELEMENT_VW = "vw:suffix";
    /**
     * lokaler Suffixelement-Name
     */
    String SUFFIX_ELEMENT = SUFFIX_ELEMENT_VW.substring(3);
    /**
     * Suffixelement-Indexattribut-Name mit Namensraumm-Præfix
     */
    String SUFFIX_INDEX_ATTRIBUTE_VW = "vw:index";
    /**
     * lokaler Suffixelement-Indexattribut-Name
     */
    String SUFFIX_INDEX_ATTRIBUTE = SUFFIX_INDEX_ATTRIBUTE_VW.substring(3);
    /**
     * Suffixelement-Indexattribut-Wert 1 mit Namensraumm-Præfix
     */
    String SUFFIX_INDEX_ATTRIBUTE_1_VW = "vw:1";
    /**
     * lokaler Suffixelement-Indexattribut-Wert 1
     */
    String SUFFIX_INDEX_ATTRIBUTE_1 = SUFFIX_INDEX_ATTRIBUTE_1_VW.substring(3);
    /**
     * Suffixelement-Indexattribut-Wert 1 mit Namensraumm-Præfix
     */
    String SUFFIX_INDEX_ATTRIBUTE_2_VW = "vw:2";
    /**
     * lokaler Suffixelement-Indexattribut-Wert 2
     */
    String SUFFIX_INDEX_ATTRIBUTE_2 = SUFFIX_INDEX_ATTRIBUTE_2_VW.substring(3);
    /**
     * Telefonelement-Name mit Namensraumm-Præfix
     */
    String PHONE_ELEMENT_VW = "vw:phone";
    /**
     * lokaler Telefonelement-Name
     */
    String PHONE_ELEMENT = PHONE_ELEMENT_VW.substring(3);
    /**
     * Telefonelement-Typattribut-Name mit Namensraumm-Præfix
     */
    String PHONE_TYPE_ATTRIBUTE_VW = "vw:type";
    /**
     * lokaler Telefonelement-Typattribut-Name
     */
    String PHONE_TYPE_ATTRIBUTE = PHONE_TYPE_ATTRIBUTE_VW.substring(3);
    /**
     * Telefonelement-Typattribut-Wert Festnetz mit Namensraumm-Præfix
     */
    String PHONE_TYPE_ATTRIBUTE_FIXED_VW = "vw:fixed";
    /**
     * lokaler Telefonelement-Typattribut-Wert Festnetz
     */
    String PHONE_TYPE_ATTRIBUTE_FIXED = PHONE_TYPE_ATTRIBUTE_FIXED_VW.substring(3);
    /**
     * Telefonelement-Typattribut-Wert Fax mit Namensraumm-Præfix
     */
    String PHONE_TYPE_ATTRIBUTE_FAX_VW = "vw:fax";
    /**
     * lokaler Telefonelement-Typattribut-Wert Fax
     */
    String PHONE_TYPE_ATTRIBUTE_FAX = PHONE_TYPE_ATTRIBUTE_FAX_VW.substring(3);
    /**
     * Telefonelement-Typattribut-Wert Mobil mit Namensraumm-Præfix
     */
    String PHONE_TYPE_ATTRIBUTE_MOBILE_VW = "vw:mobile";
    /**
     * lokaler Telefonelement-Typattribut-Wert Mobil
     */
    String PHONE_TYPE_ATTRIBUTE_MOBILE = PHONE_TYPE_ATTRIBUTE_MOBILE_VW.substring(3);
    /**
     * Kategorieelement-Name mit Namensraumm-Præfix
     */
    String CATEGORY_ELEMENT_VW = "vw:category";
    /**
     * lokaler Kategorieelement-Name
     */
    String CATEGORY_ELEMENT = CATEGORY_ELEMENT_VW.substring(3);
    /**
     * Kategorie-ID-Attribut-Name mit Namensraumm-Præfix
     */
    String CATEGORY_ID_ATTRIBUTE_VW = "vw:id";
    /**
     * lokaler Kategorie-ID-Attribut-Name
     */
    String CATEGORY_ID_ATTRIBUTE = CATEGORY_ID_ATTRIBUTE_VW.substring(3);
    /**
     * Kategorie-Veranstaltung-Attribut-Name mit Namensraumm-Præfix
     */
    String CATEGORY_EVENT_ATTRIBUTE_VW = "vw:event";
    /**
     * lokaler Kategorie-Veranstaltung-Attribut-Name
     */
    String CATEGORY_EVENT_ATTRIBUTE = CATEGORY_EVENT_ATTRIBUTE_VW.substring(3);
    /**
     * Kategorie-Name-Attribut-Name mit Namensraumm-Præfix
     */
    String CATEGORY_NAME_ATTRIBUTE_VW = "vw:name";
    /**
     * lokaler Kategorie-Name-Attribut-Name
     */
    String CATEGORY_NAME_ATTRIBUTE = CATEGORY_NAME_ATTRIBUTE_VW.substring(3);
    /**
     * Kategorie-Flags-Attribut-Name mit Namensraumm-Præfix
     */
    String CATEGORY_FLAGS_ATTRIBUTE_VW = "vw:flags";
    /**
     * lokaler Kategorie-Flags-Attribut-Name
     */
    String CATEGORY_FLAGS_ATTRIBUTE = CATEGORY_FLAGS_ATTRIBUTE_VW.substring(3);
    /**
     * Kategorie-Rang-Attribut-Name mit Namensraumm-Præfix
     */
    String CATEGORY_RANK_ATTRIBUTE_VW = "vw:rank";
    /**
     * lokaler Kategorie-Rang-Attribut-Name
     */
    String CATEGORY_RANK_ATTRIBUTE = CATEGORY_RANK_ATTRIBUTE_VW.substring(3);
    /**
     * Zeichensatzattribut-Wert Latin mit Namensraumm-Præfix
     */
    String LANGUAGE_ATTRIBUTE_LATIN_VW = "vw:latin";
    /**
     * lokaler Zeichensatzattribut-Wert Latin
     */
    String LANGUAGE_ATTRIBUTE_LATIN = LANGUAGE_ATTRIBUTE_LATIN_VW.substring(3);
    /**
     * Zeichensatzattribut-Wert Extra 1 mit Namensraumm-Præfix
     */
    String LANGUAGE_ATTRIBUTE_EXTRA_1_VW = "vw:extra-1";
    /**
     * lokaler Zeichensatzattribut-Wert Extra 1
     */
    String LANGUAGE_ATTRIBUTE_EXTRA_1 = LANGUAGE_ATTRIBUTE_EXTRA_1_VW.substring(3);
    /**
     * Zeichensatzattribut-Wert Extra 2 mit Namensraumm-Præfix
     */
    String LANGUAGE_ATTRIBUTE_EXTRA_2_VW = "vw:extra-2";
    /**
     * lokaler Zeichensatzattribut-Wert Extra 2
     */
    String LANGUAGE_ATTRIBUTE_EXTRA_2 = LANGUAGE_ATTRIBUTE_EXTRA_2_VW.substring(3);
}
