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
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.ImportPersonCategorie;
import de.tarent.aa.veraweb.beans.ImportPersonDoctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.octopus.beans.BeanException;

/**
 * Diese Klasse stellt einen Import f�r XML-Daten im VerA.web-Schema dar.
 * 
 * @author mikel
 */
public class XMLImporter implements Importer, Exchanger, VerawebNamespaceConstants, CcmConstants {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor merkt sich nach <code>null</code>-Test die �bergebene Quelle.
     * 
     * @param source Datenquelle f�r den Import
     */
    public XMLImporter(InputSource source) {
        if (source == null)
            throw new NullPointerException("XMLImporter does not accept a null InputSource");
        this.source = source;
    }

    /**
     * Dieser Konstruktor ist {@link de.tarent.data.exchange.ExchangeFormat}-kompatibel.
     */
    public XMLImporter() {
        
    }
    
    //
    // Getter und Setter
    //
    /**
     * Die zu verwendende Datenquelle. Falls sie <code>null</code> ist, wird
     * <code>new {@link InputSource}({@link #getInputStream()})</code> benutzt. 
     */
    public InputSource getInputSource() {
        return source;
    }
    
    /**
     * Die zu verwendende Datenquelle. Falls sie <code>null</code> ist, wird
     * <code>new {@link InputSource}({@link #getInputStream()})</code> benutzt. 
     */
    public void setInputSource(InputSource newSource) {
        this.source = newSource;
    }
    
    //
    // Schnittstelle Exchanger
    //
    /**
     * Das zu verwendende Austauschformat
     * 
     * @see de.tarent.data.exchange.Exchanger#getExchangeFormat()
     */
    public ExchangeFormat getExchangeFormat() {
        return format;
    }
    /**
     * Das zu verwendende Austauschformat
     * 
     * @see de.tarent.data.exchange.Exchanger#setExchangeFormat(de.tarent.data.exchange.ExchangeFormat)
     */
    public void setExchangeFormat(ExchangeFormat format) {
        this.format = format;
    }

    /**
     * Der zu verwendende Eingabedatenstrom.
     * 
     * @see de.tarent.data.exchange.Exchanger#getInputStream()
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Der zu verwendende Eingabedatenstrom.
     * 
     * @see de.tarent.data.exchange.Exchanger#setInputStream(java.io.InputStream)
     */
    public void setInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    /**
     * Der zu verwendende Ausgabedatenstrom --- wird hier nicht benutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#getOutputStream()
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Der zu verwendende Ausgabedatenstrom --- wird hier nicht benutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#setOutputStream(java.io.OutputStream)
     */
    public void setOutputStream(OutputStream stream) {
        this.outputStream = stream;
    }
    
    //
    // Schnittstelle Importer
    //
    /**
     * Diese Methode f�hrt einen Import aus. Hierbei werden alle erkannten zu
     * importierenden Personendatens�tze und Zus�tze nacheinander dem �bergebenen 
     * {@link ImportDigester} �bergeben.
     * 
     * @param digester der {@link ImportDigester}, der die Datens�tze weiter
     *  verarbeitet.
     * @throws IOException 
     * @see de.tarent.aa.veraweb.utils.Importer#importAll(de.tarent.aa.veraweb.utils.ImportDigester)
     */
    public void importAll(ImportDigester digester) throws IOException {
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(new VerawebContentHandler(digester));
            parser.parse(source != null ? source : new InputSource(inputStream));
        } catch (SAXException e) {
            IOException ioe = new IOException("SAXException: " + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    //
    // innere Klassen
    //
    /**
     * Diese Klasse dient dem Import als SAX-{@link ContentHandler} f�r die 
     * Interpretation des VerA.web-Schemas.
     */
    static class VerawebContentHandler implements ContentHandler {
        //
        // Konstruktoren
        //
        /**
         * Dieser Konstruktor merkt sich lediglich den �bergebenen {@link ImportDigester}.
         * 
         * @param digester der {@link ImportDigester}, in den importiert werden soll.
         */
        VerawebContentHandler(ImportDigester digester) {
            assert digester != null;
            this.digester = digester;
        }
        
        //
        // Konstanten
        //
        /** nicht im Dokument */
        final static int MODE_OUTSIDE = 0;
        /** in /container */
        final static int MODE_CONTAINER = 1;
        /** in /container/person */
        final static int MODE_PERSON = 2 | MODE_CONTAINER;
        /** in /container/person/member */
        final static int MODE_MEMBER = 4 | MODE_PERSON;
        /** in /container/person/member/name */
        final static int MODE_NAME = 8 | MODE_MEMBER;
        /** in /container/person/member/note */
        final static int MODE_NOTE = 16 | MODE_MEMBER;
        /** in /container/person/address */
        final static int MODE_ADDRESS = 32 | MODE_PERSON;
        /** in /container/person/address/function */
        final static int MODE_FUNCTION = 64 | MODE_ADDRESS;
        /** in /container/person/address/company */
        final static int MODE_COMPANY = 128 | MODE_ADDRESS;
        /** in /container/person/address/street */
        final static int MODE_STREET = 256 | MODE_ADDRESS;
        /** in /container/person/address/city */
        final static int MODE_CITY = 512 | MODE_ADDRESS;
        /** in /container/person/address/pobox */
        final static int MODE_POBOX = 1024 | MODE_ADDRESS;
        /** in /container/person/address/suffix */
        final static int MODE_SUFFIX = 2048 | MODE_ADDRESS;
        /** in /container/person/address/phone */
        final static int MODE_PHONE = 4096 | MODE_ADDRESS;
        /** in /container/person/history */
        final static int MODE_HISTORY = 8192 | MODE_PERSON;
        /** in /container/person/category */
        final static int MODE_CATEGORY = 16384 | MODE_PERSON;
        /** in /container/person/doctype */
        final static int MODE_DOCTYPE = 32768 | MODE_PERSON;
        /** in /container/person/address/state */
        final static int MODE_STATE = 65536 | MODE_ADDRESS;
        
        //
        // Schnittstelle ContentHandler
        //
        /**
         * Receive notification of the beginning of a document.<br>
         * Dies wird so an den {@link #digester} weitergegeben.
         * 
         * @see ContentHandler#startDocument()
         */
        public void startDocument() throws SAXException {
            logger.entering(getClass().getName(), "startDocument");
            try {
                digester.startImport();
            } catch (BeanException e) {
                logger.log(Level.WARNING, "BeanException", e);
                throw new SAXException("BeanException: " + e.getMessage(), e);
            }
        }

        /**
         * Receive notification of the end of a document.<br>
         * Dies wird so an den {@link #digester} weitergegeben.
         *
         * @see ContentHandler#endDocument()
         */
        public void endDocument() throws SAXException {
            logger.entering(getClass().getName(), "endDocument");
            try {
                digester.endImport();
            } catch (BeanException e) {
                logger.log(Level.WARNING, "BeanException", e);
                throw new SAXException("BeanException: " + e.getMessage(), e);
            }
        }

        /**
         * Receive notification of the beginning of an element.<br>
         * Hier wird nach Test des Namensraums und des bestehenden Modus
         * in einen neuen Modus gesprungen und die Attribute passend verarbeitet.
         * 
         *  @see ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
            logger.entering(getClass().getName(), "startElement", new Object[] {namespaceURI, localName, qName, atts});
            if (CCM_NAMESPACE_URI.equals(namespaceURI))
                logger.finer("ignoriere Import-Element " + qName + " aus ZKM-Namensraum " + namespaceURI);
            else if (!VW_NAMESPACE_URI.equals(namespaceURI))
                logger.warning("ignoriere Import-Element " + qName + " aus unbekanntem Namensraum " + namespaceURI);
            else if (CONTAINER_ELEMENT.equals(localName)) { // in document
                setModeEntering(MODE_CONTAINER, localName);
            } else if (PERSON_ELEMENT.equals(localName)) { // in container
                setModeEntering(MODE_PERSON, localName);
                person = new ImportPerson();
                extras = new ArrayList();
                categoryBuffer.setLength(0);
                occasionBuffer.setLength(0);
                setPersonAttributes(atts);
            } else if (MEMBER_ELEMENT.equals(localName)) { // in person
                setModeEntering(MODE_MEMBER, localName);
                setMemberAttributes(atts);
            } else if (NAME_ELEMENT.equals(localName)) { // in member
                setModeEntering(MODE_NAME, localName);
                setMemberNameAttributes(atts);
            } else if (NOTE_ELEMENT.equals(localName)) { // in member
                setModeEntering(MODE_NOTE, localName);
                entityType = getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, NOTE_TYPE_ATTRIBUTE));
                charBuffer.setLength(0);
            } else if (ADDRESS_ELEMENT.equals(localName)) { // in person
                setModeEntering(MODE_ADDRESS, localName);
                setAddressAttributes(atts);
            } else if (FUNCTION_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_FUNCTION, localName);
                charBuffer.setLength(0);
            } else if (COMPANY_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_COMPANY, localName);
                charBuffer.setLength(0);
            } else if (STREET_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_STREET, localName);
                charBuffer.setLength(0);
            } else if (CITY_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_CITY, localName);
                setCityAttributes(atts);
                charBuffer.setLength(0);
            } else if (STATE_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_STATE, localName);
                charBuffer.setLength(0);
            } else if (POBOX_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_POBOX, localName);
                setPOBoxAttributes(atts);
                charBuffer.setLength(0);
            } else if (SUFFIX_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_SUFFIX, localName);
                entitySubType = getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, SUFFIX_INDEX_ATTRIBUTE));
                charBuffer.setLength(0);
            } else if (PHONE_ELEMENT.equals(localName)) { // in address
                setModeEntering(MODE_PHONE, localName);
                entitySubType = getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, PHONE_TYPE_ATTRIBUTE));
                charBuffer.setLength(0);
            } else if (HISTORY_ELEMENT.equals(localName)) { // in person
                setModeEntering(MODE_HISTORY, localName);
                setHistoryAttributes(atts);
            } else if (CATEGORY_ELEMENT.equals(localName)) { // in person
                setModeEntering(MODE_CATEGORY, localName);
                setCategoryAttributes(atts);
            } else if (DOCTYPE_ELEMENT.equals(localName)) { // in person
                setModeEntering(MODE_DOCTYPE, localName);
                setDoctypeAttributes(atts);
            } else
                throw new SAXException("Abbruch: Im VerA.web-Schema unbekanntes Element " + localName + " errreicht");
        }
        
        /**
         * Receive notification of the end of an element.<br>
         * Hier wird nach Test des Namensraums in einen alten Modus zur�ck
         * gesprungen und der Textinhalt des Elements passend verarbeitet.
         * 
         * @see ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
         */
        public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
            logger.entering(getClass().getName(), "endElement", new Object[] {namespaceURI, localName, qName});
            if (!VW_NAMESPACE_URI.equals(namespaceURI)) {
                if (CCM_NAMESPACE_URI.equals(namespaceURI)) 
                    logger.finer("ignoriere Import-Element " + qName + " aus ZKM-Namensraum " + namespaceURI);
                else
                    logger.warning("ignoriere Import-Element " + qName + " aus unbekanntem Namensraum " + namespaceURI);
                return;
            }
            setModeLeaving(localName);
            if (CONTAINER_ELEMENT.equals(localName)) { // in document
            } else if (PERSON_ELEMENT.equals(localName)) { // in container
                if (categoryBuffer.length() > 0)
                    person.category = categoryBuffer.toString();
                if (occasionBuffer.length() > 0)
                    person.occasion = occasionBuffer.toString();
                try {
                    digester.importPerson(person, extras);
                } catch (BeanException e) {
                    throw new SAXException("BeanException: " + e.getMessage(), e);
                } catch (IOException e) {
                    throw new SAXException("IOException: " + e.getMessage(), e);
                }
                person = null;
                extras = null;
            } else if (MEMBER_ELEMENT.equals(localName)) { // in person
            } else if (NAME_ELEMENT.equals(localName)) { // in member
            } else if (NOTE_ELEMENT.equals(localName)) { // in member
                setNote();
            } else if (ADDRESS_ELEMENT.equals(localName)) { // in person
            } else if (FUNCTION_ELEMENT.equals(localName)) { // in address
                setFunction();
            } else if (COMPANY_ELEMENT.equals(localName)) { // in address
                setCompany();
            } else if (STREET_ELEMENT.equals(localName)) { // in address
                setStreet();
            } else if (CITY_ELEMENT.equals(localName)) { // in address
                setCity();
            } else if (STATE_ELEMENT.equals(localName)) { // in address
                setState();
            } else if (POBOX_ELEMENT.equals(localName)) { // in address
                setPOBox();
            } else if (SUFFIX_ELEMENT.equals(localName)) { // in address
                setSuffix();
            } else if (PHONE_ELEMENT.equals(localName)) { // in address
                setPhone();
            } else if (HISTORY_ELEMENT.equals(localName)) { // in person
            } else if (CATEGORY_ELEMENT.equals(localName)) { // in person
            } else if (DOCTYPE_ELEMENT.equals(localName)) { // in person
            } else
                throw new SAXException("Abbruch: Im VerA.web-Schema unbekanntes Element " + localName + " errreicht");
        }

        /**
         * Receive notification of character data.<br>
         * Da Zeichendaten nur in Blattknoten auftauchen, reicht es, die Zeichen in
         * einen einzigen Zeichenpuffer zu hinterlegen.
         * 
         * @see ContentHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int length) throws SAXException {
            logger.entering(getClass().getName(), "characters", new String(ch, start, length));
            charBuffer.append(ch, start, length);
        }

        /**
         * Receive notification of ignorable whitespace in element content.<br>
         * Hier wird solcher tats�chlich ignoriert.
         * @see ContentHandler#ignorableWhitespace(char[], int, int)
         */
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            logger.entering(getClass().getName(), "ignorableWhitespace", new String(ch, start, length));
        }

        /**
         * Begin the scope of a prefix-URI Namespace mapping.<br>
         * Die Namensr�ume und ihre Pr�fixe werden in {@link #nameSpaces} gehalten.
         * 
         * @see ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
         */
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            logger.entering(getClass().getName(), "startPrefixMapping", new Object[] {prefix, uri});
            nameSpaces.put(prefix, uri);
        }

        /**
         * End the scope of a prefix-URI mapping.<br>
         * Die Namensr�ume und ihre Pr�fixe werden in {@link #nameSpaces} gehalten.
         * 
         * @see ContentHandler#endPrefixMapping(java.lang.String)
         */
        public void endPrefixMapping(String prefix) throws SAXException {
            logger.entering(getClass().getName(), "endPrefixMapping", prefix);
            nameSpaces.remove(prefix);
        }

        /** @see ContentHandler#processingInstruction(java.lang.String, java.lang.String) */
        public void processingInstruction(String target, String data) throws SAXException {
            logger.entering(getClass().getName(), "processingInstruction", new Object[] {target, data});
        }

        /** @see ContentHandler#setDocumentLocator(org.xml.sax.Locator) */
        public void setDocumentLocator(Locator locator) {
            logger.entering(getClass().getName(), "setDocumentLocator", locator);
        }

        /** @see ContentHandler#skippedEntity(java.lang.String) */
        public void skippedEntity(String name) throws SAXException {
            logger.entering(getClass().getName(), "skippedEntity", name);
        }
        
        //
        // Hilfsmethoden
        //
        /**
         * Diese Methode setzt die Person-Attribute in
         * {@link ImportPerson}-Eigenschaften um.<br>
         * 
         * Ignoriert werden: {@link VerawebNamespaceConstants#PERSON_ID_ATTRIBUTE},
         * {@link VerawebNamespaceConstants#PERSON_SAVE_AS_ATTRIBUTE},
         * {@link VerawebNamespaceConstants#PERSON_ORG_UNIT_ATTRIBUTE}
         * 
         * @see XMLExporter#insertMetadata(org.w3c.dom.Element, Person)
         */
        void setPersonAttributes(Attributes atts) {
            String isCompany = atts.getValue(VW_NAMESPACE_URI, PERSON_IS_COMPANY_ATTRIBUTE);
            String expiration = atts.getValue(VW_NAMESPACE_URI, PERSON_EXPIRATION_ATTRIBUTE);
            if (isCompany != null)
                person.iscompany = isCompany;
            if (expiration != null)
                person.expire = Timestamp.valueOf(expiration);
        }
        
        /**
         * Diese Methode setzt die Member-Attribute in
         * {@link ImportPerson}-Eigenschaften um.
         * 
         * @see XMLExporter#createMemberElement(Person, boolean)
         */
        void setMemberAttributes(Attributes atts) {
            partner = MEMBER_TYPE_ATTRIBUTE_PARTNER.equals(getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, MEMBER_TYPE_ATTRIBUTE)));
            String birthday = atts.getValue(VW_NAMESPACE_URI, MEMBER_BIRTHDAY_ATTRIBUTE);
            String gender = atts.getValue(VW_NAMESPACE_URI, MEMBER_GENDER_ATTRIBUTE);
            String domestic = atts.getValue(VW_NAMESPACE_URI, MEMBER_DOMESTIC_ATTRIBUTE);
            String nationality = atts.getValue(VW_NAMESPACE_URI, MEMBER_NATIONALITY_ATTRIBUTE);
            String languages = atts.getValue(VW_NAMESPACE_URI, MEMBER_LANGUAGES_ATTRIBUTE);
            String accreditation = atts.getValue(VW_NAMESPACE_URI, MEMBER_ACCREDITATION_ATTRIBUTE);

            PersonMemberFacade facade = person.getMemberFacade(!partner, LOCALE_LATIN);
            if (birthday != null)
                facade.setBirthday(Timestamp.valueOf(birthday));
            if (accreditation != null)
                facade.setDiplodate(Timestamp.valueOf(accreditation));
            facade.setSex(gender);
            facade.setDomestic(domestic);
            facade.setNationality(nationality);
            facade.setLanguages(languages);
        }
        
        /**
         * Diese Methode setzt die Member/Name-Attribute in
         * {@link ImportPerson}-Eigenschaften um.
         * 
         * @see XMLExporter#createNameElement(Person, boolean, int)
         */
        void setMemberNameAttributes(Attributes atts) {
            setEntityLanguage(getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, NAME_LANGUAGE_ATTRIBUTE)));
            String firstname = atts.getValue(VW_NAMESPACE_URI, NAME_FIRSTNAME_ATTRIBUTE);
            String lastname = atts.getValue(VW_NAMESPACE_URI, NAME_LASTNAME_ATTRIBUTE);
            String title = atts.getValue(VW_NAMESPACE_URI, NAME_TITLE_ATTRIBUTE);
            String salutation = atts.getValue(VW_NAMESPACE_URI, NAME_SALUTATION_ATTRIBUTE);

            PersonMemberFacade facade =  person.getMemberFacade(!partner, entityLocale);
            facade.setFirstname(firstname);
            facade.setLastname(lastname);
            facade.setTitle(title);
            facade.setSalutation(salutation);
        }
        
        /**
         * Diese Methode setzt den Notiztext des aktuellen Members und Typs.
         */
        void setNote() {
            String noteText = charBuffer.toString();
            PersonMemberFacade facade = person.getMemberFacade(!partner, LOCALE_LATIN);
            if (NOTE_TYPE_ATTRIBUTE_GENERAL.equals(entityType))
                facade.setNote(noteText);
            else if (NOTE_TYPE_ATTRIBUTE_HOST.equals(entityType))
                facade.setNoteHost(noteText);
            else if (NOTE_TYPE_ATTRIBUTE_ORGANIZATION.equals(entityType))
                facade.setNoteOrga(noteText);
        }

        /**
         * Diese Methode setzt die Address-Attribute in
         * Flags und {@link ImportPerson}-Eigenschaften um.
         * 
         * @see XMLExporter#createAddressElement(Person, int, int)
         */
        void setAddressAttributes(Attributes atts) {
            setEntityLanguage(getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, ADDRESS_LANGUAGE_ATTRIBUTE)));
            entityType = getVWLocalPart(atts.getValue(VW_NAMESPACE_URI, ADDRESS_TYPE_ATTRIBUTE));
            String country = atts.getValue(VW_NAMESPACE_URI, ADDRESS_COUNTRY_ATTRIBUTE);
            String email = atts.getValue(VW_NAMESPACE_URI, ADDRESS_EMAIL_ATTRIBUTE);
            String url = atts.getValue(VW_NAMESPACE_URI, ADDRESS_URL_ATTRIBUTE);
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setCountry(country);
            facade.setEMail(email);
            facade.setUrl(url);
        }

        /**
         * Diese Methode setzt den Funktionstext der aktuellen Adresse, passend
         * zu Sprache und Typ.
         */
        void setFunction() {
            String functionText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setFunction(functionText);
        }

        /**
         * Diese Methode setzt den Funktionstext der aktuellen Adresse, passend
         * zu Sprache und Typ.
         */
        void setCompany() {
            String companyText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setCompany(companyText);
        }
        
        /**
         * Diese Methode setzt den Stra�entext der aktuellen Adresse, passend
         * zu Sprache und Typ.
         */
        void setStreet() {
            String streetText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setStreet(streetText);
        }
        
        /**
         * Diese Methode setzt die City-Attribute in
         * {@link ImportPerson}-Eigenschaften um.
         * 
         * @see XMLExporter#createAddressElement(Person, int, int)
         */
        void setCityAttributes(Attributes atts) {
            String zipCode = atts.getValue(VW_NAMESPACE_URI, CITY_ZIPCODE_ATTRIBUTE);
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setZipCode(zipCode);
        }

        /**
         * Diese Methode setzt den Stadttext der aktuellen Adresse, passend
         * zu Sprache und Typ.
         */
        void setCity() {
            String cityText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setCity(cityText);
        }
        
        /**
         * Diese Methode setzt den Bundeslandtext der aktuellen Adresse, passend
         * zu Sprache und Typ.
         */
        void setState() {
            String stateText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setState(stateText);
        }
        
        /**
         * Diese Methode setzt die POBox-Attribute in
         * {@link ImportPerson}-Eigenschaften um.
         * 
         * @see XMLExporter#createAddressElement(Person, int, int)
         */
        void setPOBoxAttributes(Attributes atts) {
            String zipCode = atts.getValue(VW_NAMESPACE_URI, POBOX_ZIPCODE_ATTRIBUTE);
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setPOBoxZipCode(zipCode);
        }

        /**
         * Diese Methode setzt den Postfachtext der aktuellen Adresse, passend
         * zu Sprache und Typ.
         */
        void setPOBox() {
            String poBoxText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            facade.setPOBox(poBoxText);
        }

        /**
         * Diese Methode setzt den Suffixtext der aktuellen Adresse, passend
         * zu Sprache, Typ und Untertyp (= Index).
         */
        void setSuffix() {
            String suffixText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            if (SUFFIX_INDEX_ATTRIBUTE_1.equals(entitySubType))
                facade.setSuffix1(suffixText);
            else if (SUFFIX_INDEX_ATTRIBUTE_2.equals(entitySubType))
                facade.setSuffix2(suffixText);
        }

        /**
         * Diese Methode setzt den Telefontext der aktuellen Adresse, passend
         * zu Sprache, Typ und Untertyp (= Festnetz / Mobil / Fax).
         */
        void setPhone() {
            String phoneText = charBuffer.toString();
            
            PersonAddressFacade facade = person.getAddressFacade(getAddressType(), entityLocale);
            if (PHONE_TYPE_ATTRIBUTE_FIXED.equals(entitySubType))
                facade.setPhone(phoneText);
            else if (PHONE_TYPE_ATTRIBUTE_FAX.equals(entitySubType))
                facade.setFax(phoneText);
            else if (PHONE_TYPE_ATTRIBUTE_MOBILE.equals(entitySubType))
                facade.setMobile(phoneText);
        }

        /**
         * Diese Methode setzt die History-Attribute in
         * {@link ImportPerson}-Eigenschaften um.<br>
         * 
         * Ignoriert werden: {@link VerawebNamespaceConstants#HISTORY_EDITING_ALT_ATTRIBUTE}
         * 
         * @see XMLExporter#createHistoryElement(Person)
         */
        void setHistoryAttributes(Attributes atts) {
            String creator = atts.getValue(VW_NAMESPACE_URI, HISTORY_CREATOR_ATTRIBUTE);
            String creation = atts.getValue(VW_NAMESPACE_URI, HISTORY_CREATION_ATTRIBUTE);
            String editor = atts.getValue(VW_NAMESPACE_URI, HISTORY_EDITOR_ATTRIBUTE);
            String editing = atts.getValue(VW_NAMESPACE_URI, HISTORY_EDITING_ATTRIBUTE);
            person.createdby = creator;
            if (creation != null)
                person.created = Timestamp.valueOf(creation);
            person.changedby = editor;
            if (editing != null)
                person.changed = Timestamp.valueOf(editing);
        }
        
        /**
         * Diese Methode setzt die Kategorie-Attribute in
         * {@link ImportPersonCategorie}-Instanzen um.<br>
         * 
         * Ignoriert werden: {@link VerawebNamespaceConstants#CATEGORY_ID_ATTRIBUTE},
         * {@link VerawebNamespaceConstants#CATEGORY_EVENT_ATTRIBUTE}<br>
         * 
         * @see XMLExporter#createCategoryElement(de.tarent.aa.veraweb.beans.Categorie, Integer)
         */
        void setCategoryAttributes(Attributes atts) {
            String name = atts.getValue(VW_NAMESPACE_URI, CATEGORY_NAME_ATTRIBUTE);
            String flags = atts.getValue(VW_NAMESPACE_URI, CATEGORY_FLAGS_ATTRIBUTE);
            String rank = atts.getValue(VW_NAMESPACE_URI, CATEGORY_RANK_ATTRIBUTE);
            Integer flagValue = new Integer(Categorie.FLAG_DEFAULT);
            if (flags != null)
                flagValue = new Integer(flags);
            Integer rankValue = null;
            if (rank != null)
                rankValue = new Integer(rank);
            
            ImportPersonCategorie category = new ImportPersonCategorie();
            category.flags = flagValue;
            category.rank = rankValue;
            category.name = name;
            extras.add(category);

//            StringBuffer buffer = null;
//            switch (flagValue) {
//            case Categorie.FLAG_DEFAULT:
//                buffer = categoryBuffer;
//                break;
//            case Categorie.FLAG_EVENT:
//                buffer = occasionBuffer;
//                break;
//            }
//
//            if (buffer != null) {
//                if (buffer.length() > 0)
//                    buffer.append('\n');
//                buffer.append(name);
//            }
        }
        
        /**
         * Diese Methode setzt die Dokumenttyp-Attribute in
         * {@link ImportPersonDoctype}-Instanzen um.<br>
         * 
         * Ignoriert wird: {@link VerawebNamespaceConstants#DOCTYPE_ID_ATTRIBUTE}
         * 
         * @see XMLExporter#createDocTypeElement(Map)
         */
        void setDoctypeAttributes(Attributes atts) {
            String name = atts.getValue(VW_NAMESPACE_URI, DOCTYPE_NAME_ATTRIBUTE);
            String text = atts.getValue(VW_NAMESPACE_URI, DOCTYPE_TEXT_ATTRIBUTE);
            String textPartner = atts.getValue(VW_NAMESPACE_URI, DOCTYPE_TEXT_PARTNER_ATTRIBUTE);
            String textJoin = atts.getValue(VW_NAMESPACE_URI, DOCTYPE_TEXT_JOIN_ATTRIBUTE);
            
            ImportPersonDoctype doctype = new ImportPersonDoctype();
            doctype.textfield = text;
            doctype.textfieldPartner = textPartner;
            doctype.textfieldJoin = textJoin;
            doctype.name = name;
            extras.add(doctype);
        }
        
        /**
         * Diese Methode betrachtet einen String als einen QName, ermittelt,
         * ob dieser QName im Veraweb-Namensraum liegt und gibt in diesem
         * Fall den lokalen Anteil zur�ck, sonst <code>null</code>.
         * 
         * @param value als QName betrachteter Wert
         * @return lokaler Part im Veraweb-Namensraum, sonst <code>null</code>.
         */
        final String getVWLocalPart(String value) {
            if (value != null) {
                String[] parts = value.split(":");
                if (VW_NAMESPACE_URI.equals(nameSpaces.get(parts[0])))
                    return value.substring(parts[0].length() + 1);
            }
            return null;
        }
        
        /**
         * Diese Methode versucht, aus dem aktuellen {@link #mode Modus} in
         * einen anderen zu wechseln, pr�ft hierbei, ob die dadurch implizierte
         * Verschachtelung von Elementen erlaubt ist, und wirft gegebenenfalls
         * eine Ausnahme.
         * 
         * @param newMode neuer Modus, vergleiche MODE_*-Konstanten
         * @param nodeName Name des XML-Elements, dessentwegen der Moduswechsel
         *  gew�nscht wird
         * @throws SAXException bei unerlaubten Moduswechselw�nschen
         */
        void setModeEntering(int newMode, String nodeName) throws SAXException {
            int expected = -1; 
            switch(newMode) {
            case MODE_OUTSIDE:
                break;
            case MODE_CONTAINER:
                expected = MODE_OUTSIDE;
                break;
            case MODE_PERSON:
                expected = MODE_CONTAINER;
                break;
            case MODE_MEMBER:
            case MODE_ADDRESS:
            case MODE_HISTORY:
            case MODE_CATEGORY:
            case MODE_DOCTYPE:
                expected = MODE_PERSON;
                break;
            case MODE_NAME:
            case MODE_NOTE:
                expected = MODE_MEMBER;
                break;
            case MODE_FUNCTION:
            case MODE_COMPANY:
            case MODE_STREET:
            case MODE_CITY:
            case MODE_STATE:
            case MODE_POBOX:
            case MODE_SUFFIX:
            case MODE_PHONE:
                expected = MODE_ADDRESS;
                break;
            default:
                break;
            }
            if (mode != expected) {
                throw new SAXException("XML-Format-Fehler: Element " + nodeName + " darf nur im Modus " + expected + ", nicht " + mode + " auftauchen.");
            }
            mode = newMode;
        }

        /**
         * Diese Methode versucht, aus dem aktuellen {@link #mode Modus} in
         * den Vormodus zu wechseln, pr�ft hierbei, ob dies erlaubt ist, und
         * wirft gegebenenfalls eine Ausnahme.
         * 
         * @param nodeName Name des XML-Elements, dessentwegen der Moduswechsel
         *  gew�nscht wird
         * @throws SAXException bei unerlaubten Moduswechselw�nschen
         */
        void setModeLeaving(String nodeName) throws SAXException {
            switch(mode) {
            case MODE_OUTSIDE:
                mode = -1;
                break;
            case MODE_CONTAINER:
                mode = MODE_OUTSIDE;
                break;
            case MODE_PERSON:
                mode = MODE_CONTAINER;
                break;
            case MODE_MEMBER:
            case MODE_ADDRESS:
            case MODE_HISTORY:
            case MODE_CATEGORY:
            case MODE_DOCTYPE:
                mode = MODE_PERSON;
                break;
            case MODE_NAME:
            case MODE_NOTE:
                mode = MODE_MEMBER;
                break;
            case MODE_FUNCTION:
            case MODE_COMPANY:
            case MODE_STREET:
            case MODE_CITY:
            case MODE_STATE:
            case MODE_POBOX:
            case MODE_SUFFIX:
            case MODE_PHONE:
                mode = MODE_ADDRESS;
                break;
            default:
                throw new SAXException("XML-Format-Fehler: Element " + nodeName + " kann im Modus " + mode + " nicht verlassen werden.");
            }
        }
        
        /**
         * Diese Methode setzt die {@link #entityLanguage} und als Nebeneffekt die
         * {@link #entityLocale} gleich passend mit. 
         * 
         * @param entityLanguage die Sprache der aktuellen Entit�t, ein Wert von
         *  {@link #LANGUAGE_ATTRIBUTE_LATIN}, {@link #LANGUAGE_ATTRIBUTE_EXTRA_1}
         *  und {@link #LANGUAGE_ATTRIBUTE_EXTRA_2}.
         */
        void setEntityLanguage(String entityLanguage) {
            this.entityLanguage = entityLanguage;
            if (LANGUAGE_ATTRIBUTE_LATIN.equals(entityLanguage))
                entityLocale = LOCALE_LATIN;
            else if (LANGUAGE_ATTRIBUTE_EXTRA_1.equals(entityLanguage))
                entityLocale = LOCALE_EXTRA1;
            else if (LANGUAGE_ATTRIBUTE_EXTRA_2.equals(entityLanguage))
                entityLocale = LOCALE_EXTRA2;
            else
                entityLocale = null;
        }
        
        /**
         * Diese Methode liefert aus {@link #entityType} berechnet den
         * aktuellen Adresstyp. 
         * 
         * @return {@link #ADDRESSTYPE_PRIVATE}, {@link #ADDRESSTYPE_BUSINESS},
         *  {@link #ADDRESSTYPE_OTHER} oder <code>null</code>.
         */
        Integer getAddressType() {
            if (ADDRESS_TYPE_ATTRIBUTE_PRIVATE.equals(entityType))
                return ADDRESSTYPE_PRIVATE;
            else if (ADDRESS_TYPE_ATTRIBUTE_BUSINESS.equals(entityType))
                return ADDRESSTYPE_BUSINESS; 
            else if (ADDRESS_TYPE_ATTRIBUTE_OTHER.equals(entityType))
                return ADDRESSTYPE_OTHER;
            return null;
        }
        
        //
        // gesch�tzte Member
        //
        /** Der aktuelle Modus, vergleiche MODE_*-Konstanten */
        int mode = MODE_OUTSIDE;
        /** Diese Klasse soll mit Ereignissen bedient werden. */
        final ImportDigester digester;
        /** Hier entsteht die aktuelle Person. */
        ImportPerson person = null;
        /** Hier entsteht die Liste der Extras der aktuellen Person. */
        List extras = null;
        /** Zeigt an, ob das aktuelle Member Hauptperson oder Partner ist */
        boolean partner = false;
        /** Zeigt an, von welchem Typ das aktuelle Element gerade eingelesen wird. */
        String entityType = null;
        /** Zeigt an, von welchem Untertyp das aktuelle Element gerade eingelesen wird. */
        String entitySubType = null;
        /** Zeigt an, in welcher Sprache das aktuelle Element gerade eingelesen wird. */
        String entityLanguage = null;
        /** Die aufgel�ste {@link #entityLanguage}. */
        Integer entityLocale = null;
        /** Hier werden Textdaten gesammelt. Da wir Textelemente nur in Blattknoten, reicht ein Buffer. */
        final StringBuffer charBuffer = new StringBuffer();
        /** Hier werden Kategorien gesammelt. */
        final StringBuffer categoryBuffer = new StringBuffer();
        /** Hier werden Ereignisse gesammelt. */
        final StringBuffer occasionBuffer = new StringBuffer();
        /** Hier werden aktuell g�ltige Pr�fixe auf Namensraum-URIs gemapt. */
        final Map nameSpaces = new HashMap();
    }
    
    //
    // gesch�tzte Member
    //
    /** Das zu verwendende Austauschformat */
    ExchangeFormat format = null;

    /** Der zu verwendende Eingabedatenstrom */
    InputStream inputStream = null;
    
    /** Der zu verwendende Ausgabedatenstrom */
    OutputStream outputStream = null;
    
    /** Datenquelle f�r die XML-Transformation */
    InputSource source = null;
    
    /** Locale-Konstante {@link PersonConstants#ADDRESSTYPE_BUSINESS} als Objekt */
    static final Integer ADDRESSTYPE_BUSINESS = new Integer(PersonConstants.ADDRESSTYPE_BUSINESS);

    /** Locale-Konstante {@link PersonConstants#ADDRESSTYPE_PRIVATE} als Objekt */
    static final Integer ADDRESSTYPE_PRIVATE = new Integer(PersonConstants.ADDRESSTYPE_PRIVATE);

    /** Locale-Konstante {@link PersonConstants#ADDRESSTYPE_OTHER} als Objekt */
    static final Integer ADDRESSTYPE_OTHER = new Integer(PersonConstants.ADDRESSTYPE_OTHER);

    /** Locale-Konstante {@link PersonConstants#LOCALE_LATIN} als Objekt */
    final static Integer LOCALE_LATIN = new Integer(PersonConstants.LOCALE_LATIN);
    
    /** Locale-Konstante {@link PersonConstants#LOCALE_EXTRA1} als Objekt */
    final static Integer LOCALE_EXTRA1 = new Integer(PersonConstants.LOCALE_EXTRA1);
    
    /** Locale-Konstante {@link PersonConstants#LOCALE_EXTRA2} als Objekt */
    final static Integer LOCALE_EXTRA2 = new Integer(PersonConstants.LOCALE_EXTRA2);
    
    /** Logger dieser Klasse */
    final static Logger logger = Logger.getLogger(XMLImporter.class.getName());
}
