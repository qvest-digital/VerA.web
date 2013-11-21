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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Doctype;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.data.exchange.ExchangeFormat;
import de.tarent.data.exchange.Exchanger;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.DatabaseUtilizer;

/**
 * Diese Klasse dient dem Erzeugen eines VerA.web-XML-Exports.<br>
 * TODO: Umstellung von DOM auf SAX
 * 
 * @author mikel
 */
public class XMLExporter implements Exporter, Exchanger, DatabaseUtilizer,
        AlternativeDestination, VerawebNamespaceConstants, CcmConstants {
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor legt die Parameter lokal ab und initialisiert ein DOM-Dokument.
     * 
     * @throws UnsupportedEncodingException
     * @throws ParserConfigurationException
     */
    public XMLExporter(OutputStream os, Database db) throws ParserConfigurationException {
        this();
        this.os = os;
        this.db = db;
    }
    
    /**
     * Dieser Konstruktor ist {@link de.tarent.data.exchange.ExchangeFormat}-kompatibel
     * und initialisiert ledeiglich das interne DOM-Dokument. 
     * 
     * @throws ParserConfigurationException
     */
    public XMLExporter() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        document = factory.newDocumentBuilder().newDocument();
    }

    //
    // Konstanten
    //
    /** intern: Adresstyp privat */
    public final static int ADDRESS_TYPE_PRIVATE = 1;
    /** intern: Adresstyp dienst */
    public final static int ADDRESS_TYPE_BUSINESS = 2;
    /** intern: Adresstyp sonstig */
    public final static int ADDRESS_TYPE_OTHER = 3;
    /** intern: Zeichensatz Latin */
    public final static int LANGUAGE_LATIN = 1;
    /** intern: Zeichensatz Extra-1 */
    public final static int LANGUAGE_EXTRA_1 = 2;
    /** intern: Zeichensatz Extra-2 */
    public final static int LANGUAGE_EXTRA_2 = 3;
    /** intern: Bermkungstyp allgemein */
    public final static int NOTE_TYPE_GENERAL = 0;
    /** intern: Bermkungstyp Organisation */
    public final static int NOTE_TYPE_ORGANIZATION = 1;
    /** intern: Bermkungstyp Gastgeber */
    public final static int NOTE_TYPE_HOST = 2;
    /** Property-Schl�ssel f�r das Encoding der Ausgabedatei */
    public final static String PROPERTY_ENCODING = "encoding";
    /** Vorgabewert: Character-Encoding */
    public final static String DEFAULT_ENCODING = "UTF-8";
    /** Property-Schl�ssel f�r das Verwenden eines CCM-Envelopes */
    public final static String PROPERTY_CCM_ENVELOPE = "ccm-envelope";
    /** Vorgabewert: CCM-Envelope */
    public final static boolean DEFAULT_CCM_ENVELOPE = false;
    /** Property-Schl�ssel f�r den CCM-Bezeichner f�r die Applikation VerA.web */
    public final static String PROPERTY_CCM_APPLICATION = "ccm-application";
    /** Vorgabewert: CCM-Applikationsbezeichner */
    public final static String DEFAULT_CCM_APPLICATION = "veraweb";
    /** Property-Schl�ssel f�r das CCM-K�rzel der lokalen Installation */
    public final static String PROPERTY_CCM_ENDPOINT = "ccm-endpoint";
    /** Property-Schl�ssel f�r das CCM-K�rzel der entfernten Installation */
    public final static String PROPERTY_CCM_RECEIVER = "ccm-receiver";
    /** Property-Schl�ssel f�r das Verzeichnis f�r CCM-Exporte */
    public final static String PROPERTY_CCM_OUTGOING_FOLDER = "ccm-outgoing-folder";
    /** Vorgabewert: Verzeichnis f�r CCM-Exporte */
    public final static String DEFAULT_CCM_OUTGOING_FOLDER = "/var/spool/ccm/in/";
    
    //
    // Schnittstelle DatabaseUtilizer
    //
    /** Die zu nutzende Datenbank */
    public void setDatabase(Database database) {
        this.db = database;
    }

    /** Die zu nutzende Datenbank */
    public Database getDatabase() {
        return db;
    }
    
    //
    // Schnittstelle Exchanger
    //
    /** Das zu verwendende Austauschformat */
    public ExchangeFormat getExchangeFormat() {
        return format;
    }

    /** Das zu verwendende Austauschformat */
    public void setExchangeFormat(ExchangeFormat format) {
        this.format = format;
        
        useCcmEnvelope = DEFAULT_CCM_ENVELOPE;
        ccmApplication = DEFAULT_CCM_APPLICATION;
        ccmOutgoingFolder = DEFAULT_CCM_OUTGOING_FOLDER;
        ccmSenderEndpoint = null;
        ccmReceiverEndpoint = null;
        if (format != null && format.getProperties() != null) {
            Map properties = format.getProperties();
            Object property = properties.get(PROPERTY_CCM_ENVELOPE);
            if (property != null)
                useCcmEnvelope = Boolean.valueOf(property.toString()).booleanValue();
            property = properties.get(PROPERTY_CCM_APPLICATION);
            if (property instanceof String)
                ccmApplication = property.toString();
            property = properties.get(PROPERTY_CCM_OUTGOING_FOLDER);
            if (property instanceof String)
                ccmOutgoingFolder = property.toString();
            property = properties.get(PROPERTY_CCM_ENDPOINT);
            if (property instanceof String)
                ccmSenderEndpoint = property.toString();
            property = properties.get(PROPERTY_CCM_RECEIVER);
            if (property instanceof String)
                ccmReceiverEndpoint = property.toString();
        }
    }
    
    /**
     * Der zu verwendende Eingabedatenstrom --- wird intern nicht genutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#getInputStream()
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Der zu verwendende Eingabedatenstrom --- wird intern nicht genutzt.
     * 
     * @see de.tarent.data.exchange.Exchanger#setInputStream(java.io.InputStream)
     */
    public void setInputStream(InputStream stream) {
        this.inputStream = stream;
    }

    /** Der zu verwendende Ausgabedatenstrom */
    public OutputStream getOutputStream() {
        return os;
    }

    /** Der zu verwendende Ausgabedatenstrom */
    public void setOutputStream(OutputStream stream) {
        this.os = stream;
    }
    
    //
    // Schnittstelle Exporter
    //
    /**
     * Diese Methode f�gt dem XML-Dokument eine Beschreibung der �bergebenen VerA.web-Person
     * hinzu.
     * 
     * @param person {@link Person}-Bean
     */
    public void exportPerson(Person person) throws BeanException, IOException {
        assert baseElement != null;
        // Ohne Personeninformationen passiert nichts
        if (person == null)
            return;
        // Personenelement erstellen
        Element personElement = document.createElementNS(VW_NAMESPACE_URI, PERSON_ELEMENT_VW);
        insertMetadata(personElement, person);
        insertMembers(personElement, person);
        insertAddresses(personElement, person);
        insertCategories(personElement, person);
        insertDocTypes(personElement, person);
        // Personenelement in das Dokument einf�gen
        baseElement.appendChild(personElement);
    }

    /**
     * Diese Methode wird zu Beginn eines Exports aufgerufen.
     */
    public void startExport() throws IOException {
        baseElement = document.createElementNS(VW_NAMESPACE_URI, CONTAINER_ELEMENT_VW);
        baseElement.setAttributeNS(XMLNS_NAMESPACE_URI, VW_NAMESPACE_ATTRIBUTE, VW_NAMESPACE_URI);
        if (useCcmEnvelope) {
            // <ccm:message xmlns:ccm="http://www.auswaertiges-amt.de/CCM">
            Element messageElement = document.createElementNS(CCM_NAMESPACE_URI, MESSAGE_ELEMENT_CCM);
            messageElement.setAttributeNS(XMLNS_NAMESPACE_URI, CCM_NAMESPACE_ATTRIBUTE, CCM_NAMESPACE_URI);
            //   <ccm:version>1.0</ccm:version>
            Element element = document.createElementNS(CCM_NAMESPACE_URI, VERSION_ELEMENT_CCM);
            element.appendChild(document.createTextNode(VERSION_TEXT));
            messageElement.appendChild(element);
            //   <ccm:id>dow mon dd hh:mm:ss zzz yyyy</ccm:id>
            element = document.createElementNS(CCM_NAMESPACE_URI, ID_ELEMENT_CCM);
            element.appendChild(document.createTextNode(new Date().toString()));
            messageElement.appendChild(element);
            //   <ccm:header>
            Element headerElement = document.createElementNS(CCM_NAMESPACE_URI, HEADER_ELEMENT_CCM);
            //     <ccm:priority>normal</ccm:priority>
            element = document.createElementNS(CCM_NAMESPACE_URI, PRIORITY_ELEMENT_CCM);
            element.appendChild(document.createTextNode(PRIOTITY_TEXT_NORMAL));
            headerElement.appendChild(element);
            //     <!--<ccm:timelimit>86400</ccm:timelimit>-->
//            element = document.createElementNS(CCM_NAMESPACE_URI, TIMELIMIT_ELEMENT_CCM);
//            element.appendChild(document.createTextNode("86400"));
//            headerElement.appendChild(element);
            //     <ccm:sender>
            Element senderElement = document.createElementNS(CCM_NAMESPACE_URI, SENDER_ELEMENT_CCM);
            //       <ccm:place>
            Element placeElement = document.createElementNS(CCM_NAMESPACE_URI, PLACE_ELEMENT_CCM);
            //         <ccm:endpoint>...</ccm:endpoint>
            element = document.createElementNS(CCM_NAMESPACE_URI, ENDPOINT_ELEMENT_CCM);
            element.appendChild(document.createTextNode(ccmSenderEndpoint));
            placeElement.appendChild(element);
            //         <ccm:application>veraweb</ccm:application>
            element = document.createElementNS(CCM_NAMESPACE_URI, APPLICATION_ELEMENT_CCM);
            element.appendChild(document.createTextNode(ccmApplication));
            placeElement.appendChild(element);
            //       </ccm:place>
            senderElement.appendChild(placeElement);
            //     </ccm:sender>
            headerElement.appendChild(senderElement);
            //     <ccm:receiver>
            Element receiverElement = document.createElementNS(CCM_NAMESPACE_URI, RECEIVER_ELEMENT_CCM);
            //       <ccm:place>
            placeElement = document.createElementNS(CCM_NAMESPACE_URI, PLACE_ELEMENT_CCM);
            //         <ccm:endpoint>...</ccm:endpoint>
            element = document.createElementNS(CCM_NAMESPACE_URI, ENDPOINT_ELEMENT_CCM);
            element.appendChild(document.createTextNode(ccmReceiverEndpoint));
            placeElement.appendChild(element);
            //         <ccm:application>veraweb</ccm:application>
            element = document.createElementNS(CCM_NAMESPACE_URI, APPLICATION_ELEMENT_CCM);
            element.appendChild(document.createTextNode(ccmApplication));
            placeElement.appendChild(element);
            //       </ccm:place>
            receiverElement.appendChild(placeElement);
            //     </ccm:receiver>
            headerElement.appendChild(receiverElement);
            //   </ccm:header>
            messageElement.appendChild(headerElement);
            //   <ccm:body>
            element = document.createElementNS(CCM_NAMESPACE_URI, BODY_ELEMENT_CCM);
            element.appendChild(baseElement);
            messageElement.appendChild(element);
            //   </ccm:body>
            document.appendChild(messageElement);
            // </ccm:message>
        } else
            document.appendChild(baseElement);
    }

    /**
     * Diese Methode schreibt das bisher gesammelte Dokument fest. 
     * 
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public void endExport() throws IOException, UnsupportedEncodingException {
        try {
            String encoding = DEFAULT_ENCODING;
            if (format != null && format.getProperties() != null) {
                Object property = format.getProperties().get(PROPERTY_ENCODING);
                if (property instanceof String)
                    encoding = property.toString();
            }
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            Source source = new DOMSource(document);
            Result result = new StreamResult(new OutputStreamWriter(os, encoding));
            transformer.transform(source, result);
        } catch (TransformerException e) {
            IOException ioe = new IOException("TransformerException: " + e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }
    
    //
    // Schnittstelle AlternativeDestination
    //
    /**
     * Diese Methode liefert ein alternatives Ziel in Form eines {@link OutputStream}s. 
     * 
     * @return ein {@link OutputStream} oder <code>null</code>.
     * @throws IOException
     * @see de.tarent.aa.veraweb.utils.AlternativeDestination#getAlternativeOutputStream()
     */
    public OutputStream getAlternativeOutputStream() throws IOException {
        if (useCcmEnvelope && ccmOutgoingFolder != null && ccmOutgoingFolder.length() > 0) {
            Date now = new Date();
            String millis = String.valueOf(now.getTime());
            millis = millis.substring(millis.length() - 3);
            StringBuffer buffer = new StringBuffer();
            buffer.append("rte") // Pr�fix --- "ctn" bei hoher und "rte" bei normaler Priorit�t, "sys" bei Fehlerdateien
                .append('_')
                .append(ccmApplication) // Applikation
                .append('_')
                .append(ccmSenderEndpoint) // Dienstort
                .append('_')
                .append(ccmDateFormat.format(now)) // Zeitstempel
                .append(millis) // Millisekunden
                .append(".xml");
            ccmOutgoingFile = new File(ccmOutgoingFolder, buffer.toString());
            return new FileOutputStream(ccmOutgoingFile);
        }
        return null;
    }

    /**
     * Diese Methode f�hrt einen Rollback auf das alternative Ziel durch,
     * l�scht dabei z.B. neu erstellte Dateien wieder.
     */
    public void rollback() {
    	if (ccmOutgoingFile != null && ccmOutgoingFile.exists()) {
    		ccmOutgoingFile.delete();
    	}
    }

    //
    // gesch�tzte Methoden
    //
    /**
     * Diese Methode f�gt dem VerA.web-Personen-Element Verwaltungsinformationen
     * hinzu.
     * 
     * @param personElement VerA.web-Personen-Element
     * @param person {@link Person}-Bean
     */
    void insertMetadata(Element personElement, Person person) {
        setAttribute(personElement, PERSON_ID_ATTRIBUTE_VW, person.id);
        setAttribute(personElement, PERSON_SAVE_AS_ATTRIBUTE_VW, person.getMainLatin().getSaveAs());
        setAttribute(personElement, PERSON_IS_COMPANY_ATTRIBUTE_VW, person.iscompany);
        setAttribute(personElement, PERSON_EXPIRATION_ATTRIBUTE_VW, person.expire);
        // TODO: PERSON_ORG_UNIT_ATTRIBUTE
        //setAttribute(personElement, PERSON_ORG_UNIT_ATTRIBUTE_VW, ???);
        appendChild(personElement, createHistoryElement(person));
    }

    /**
     * Diese Methode f�gt dem VerA.web-Personen-Element Unterelemente f�r
     * Hauptperson und Pertner hinzu. 
     * 
     * @param personElement VerA.web-Personen-Element
     * @param person {@link Person}-Bean
     */
    void insertMembers(Element personElement, Person person) {
        boolean partner = false;
        do {
            appendChild(personElement, createMemberElement(person, partner));
            partner = !partner;
        } while (partner);
    }

    /**
     * Diese Methode f�gt dem VerA.web-Personen-Element Unterelemente f�r
     * die verschiedenen Adressen hinzu.
     * 
     * @param personElement VerA.web-Personen-Element
     * @param person {@link Person}-Bean
     */
    void insertAddresses(Element personElement, Person person) {
        for (int type = ADDRESS_TYPE_PRIVATE; type <= ADDRESS_TYPE_OTHER; type++)
            for (int lang = LANGUAGE_LATIN; lang <= LANGUAGE_EXTRA_2; lang++)
                appendChild(personElement, createAddressElement(person, type, lang));
    }

    /**
     * Diese Methode f�gt dem VerA.web-Personen-Element Unterelemente f�r
     * die verschiedenen Kategorien (echte Kategorien und Ereignisse) hinzu.
     * 
     * @param personElement VerA.web-Personen-Element
     * @param person {@link Person}-Bean
     * @throws IOException 
     * @throws BeanException 
     */
    void insertCategories(Element personElement, Person person) throws BeanException, IOException {
        Categorie sampleCategory = (Categorie) db.createBean("Categorie");
        Bean samplePersonCategory = db.createBean("PersonCategorie");
        Select select = db.getSelect(sampleCategory)
            .join(db.getProperty(samplePersonCategory, "table"),
                    db.getProperty(sampleCategory, "id"),
                    db.getProperty(samplePersonCategory, "categorie"))
            .selectAs(db.getProperty(samplePersonCategory, "rank"), "individualRank")
            .where(Expr.equal(db.getProperty(samplePersonCategory, "person"), person.id));
        List list = db.getList(select, db);
        for (Iterator itList = list.iterator(); itList.hasNext(); ) {
            Map data = (Map) itList.next();
            // sampleCategory.putAll(data) w�rde versuchen, auch den Wert von individualRank
            // zu setzen (-> Fehler); au�erdem wird hier ein EntrySet.iterator() benutzt, den
            // ResultMap nicht unterst�tzt.
            for (Iterator itFields = sampleCategory.getFields().iterator(); itFields.hasNext(); ) {
                Object field = itFields.next();
                sampleCategory.put(field, data.get(field));
            }
            appendChild(personElement, createCategoryElement(sampleCategory, (Integer) data.get("individualRank")));
        }
    }
    
    /**
     * Diese Methode f�gt dem VerA.web-Personen-Element Unterelemente f�r
     * die verschiedenen Dokumenttyp-Freitexte hinzu.
     * 
     * @param personElement VerA.web-Personen-Element
     * @param person {@link Person}-Bean
     * @throws BeanException 
     * @throws IOException 
     */
    void insertDocTypes(Element personElement, Person person) throws BeanException, IOException {
        logger.entering(getClass().getName(), "insertDocTypes", new Object[] {personElement, person});
        Doctype sampleDoctype = (Doctype) db.createBean("Doctype");
        Bean samplePersonDoctype = db.createBean("PersonDoctype");
        Select select = db.getSelect(sampleDoctype)
            .join(db.getProperty(samplePersonDoctype, "table"),
                    db.getProperty(sampleDoctype, "id"),
                    db.getProperty(samplePersonDoctype, "doctype"))
            .selectAs(db.getProperty(samplePersonDoctype, "textfield"), "textfield")
            .selectAs(db.getProperty(samplePersonDoctype, "textfieldPartner"), "textfieldPartner")
            .selectAs(db.getProperty(samplePersonDoctype, "textfieldJoin"), "textfieldJoin")
            .where(Expr.equal(db.getProperty(samplePersonDoctype, "person"), person.id));
        List list = db.getList(select, db);
        for (Iterator itList = list.iterator(); itList.hasNext(); ) {
            Map data = (Map) itList.next();
            appendChild(personElement, createDocTypeElement(data));
        }
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:history</code>-Element. 
     * 
     * @param person {@link Person}-Bean
     * @return ein <code>vw:history</code>-{@link Element} oder <code>null</code>
     */
    Element createHistoryElement(Person person) {
        boolean notEmpty = false;
        Element member = document.createElementNS(VW_NAMESPACE_URI, HISTORY_ELEMENT_VW);
        notEmpty |= setAttribute(member, HISTORY_CREATOR_ATTRIBUTE_VW, person.createdby);
        notEmpty |= setAttribute(member, HISTORY_CREATION_ATTRIBUTE_VW, person.created);
        notEmpty |= setAttribute(member, HISTORY_EDITOR_ATTRIBUTE_VW, person.changedby);
        notEmpty |= setAttribute(member, HISTORY_EDITING_ATTRIBUTE_VW, person.changed);
        notEmpty |= setAttribute(member, HISTORY_EDITING_ALT_ATTRIBUTE_VW, person.changed);
        notEmpty |= setAttribute(member, HISTORY_SOURCE_ATTRIBUTE_VW, person.importsource);
        return notEmpty ? member : null;
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:category</code>-Element.
     * 
     * @param category {@link Categorie}-Bean
     * @param individualRank individueller Rang bez�glich der Kategorie
     * @return ein <code>vw:category</code>-{@link Element} oder <code>null</code>
     */
    Element createCategoryElement(Categorie category, Integer individualRank) {
        boolean notEmpty = false;
        Element categoryElement = document.createElementNS(VW_NAMESPACE_URI, CATEGORY_ELEMENT_VW);
        notEmpty |= setAttribute(categoryElement, CATEGORY_ID_ATTRIBUTE_VW, category.id);
        notEmpty |= setAttribute(categoryElement, CATEGORY_EVENT_ATTRIBUTE_VW, category.event);
        notEmpty |= setAttribute(categoryElement, CATEGORY_NAME_ATTRIBUTE_VW, category.name);
        notEmpty |= setAttribute(categoryElement, CATEGORY_FLAGS_ATTRIBUTE_VW, category.flags);
        notEmpty |= setAttribute(categoryElement, CATEGORY_RANK_ATTRIBUTE_VW, individualRank == null ? category.rank : individualRank);
        return notEmpty ? categoryElement : null;
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:doctype</code>-Element.
     * 
     * @param doctypeData Dokumenttypdaten
     * @return ein <code>vw:category</code>-{@link Element} oder <code>null</code>
     */
    Element createDocTypeElement(Map doctypeData) {
        boolean notEmpty = false;
        Element doctypeElement = document.createElementNS(VW_NAMESPACE_URI, DOCTYPE_ELEMENT_VW);
        setAttribute(doctypeElement, DOCTYPE_ID_ATTRIBUTE_VW, doctypeData.get("id"));
        setAttribute(doctypeElement, DOCTYPE_NAME_ATTRIBUTE_VW, doctypeData.get("name"));
        notEmpty |= setAttribute(doctypeElement, DOCTYPE_TEXT_ATTRIBUTE_VW, doctypeData.get("textfield"));
        notEmpty |= setAttribute(doctypeElement, DOCTYPE_TEXT_PARTNER_ATTRIBUTE_VW, doctypeData.get("textfieldPartner"));
        notEmpty |= setAttribute(doctypeElement, DOCTYPE_TEXT_JOIN_ATTRIBUTE_VW, doctypeData.get("textfieldJoin"));
        return notEmpty ? doctypeElement : null;
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:member</code>-Element. 
     * 
     * @param person {@link Person}-Bean
     * @param partner Flag: Partner (true) oder Hauptperson (false)
     * @return ein <code>vw:member</code>-{@link Element} oder <code>null</code>
     */
    Element createMemberElement(Person person, boolean partner) {
        boolean notEmpty = false;
        Element member = document.createElementNS(VW_NAMESPACE_URI, MEMBER_ELEMENT_VW);
        setAttribute(member, MEMBER_TYPE_ATTRIBUTE_VW, partner ? MEMBER_TYPE_ATTRIBUTE_PARTNER_VW : MEMBER_TYPE_ATTRIBUTE_MAIN_VW);
        notEmpty |= setAttribute(member, MEMBER_BIRTHDAY_ATTRIBUTE_VW, partner ? person.birthday_b_e1 : person.birthday_a_e1);
        notEmpty |= setAttribute(member, MEMBER_ACCREDITATION_ATTRIBUTE_VW, partner ? person.diplodate_b_e1 : person.diplodate_a_e1);
        notEmpty |= setAttribute(member, MEMBER_GENDER_ATTRIBUTE_VW, partner ? person.sex_b_e1 : person.sex_a_e1);
        notEmpty |= setAttribute(member, MEMBER_DOMESTIC_ATTRIBUTE_VW, partner ? person.domestic_b_e1 : person.domestic_a_e1);
        notEmpty |= setAttribute(member, MEMBER_NATIONALITY_ATTRIBUTE_VW, partner ? person.nationality_b_e1 : person.nationality_a_e1);
        notEmpty |= setAttribute(member, MEMBER_LANGUAGES_ATTRIBUTE_VW, partner ? person.languages_b_e1 : person.languages_a_e1);
        for (int type = NOTE_TYPE_GENERAL; type <= NOTE_TYPE_HOST; type++)
            notEmpty |= appendChild(member, createNoteElement(person, partner, type));
        for (int lang = LANGUAGE_LATIN; lang <= LANGUAGE_EXTRA_2; lang++)
            notEmpty |= appendChild(member, createNameElement(person, partner, lang));
        return notEmpty ? member : null;
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:note</code>-Element. 
     * 
     * @param person {@link Person}-Bean
     * @param partner Flag: Partner (true) oder Hauptperson (false)
     * @param type Notiztyp, {@link #NOTE_TYPE_GENERAL}, {@link #NOTE_TYPE_ORGANIZATION}
     *  oder {@link #NOTE_TYPE_HOST}
     * @return ein <code>vw:note</code>-{@link Element} oder <code>null</code>
     */
    Element createNoteElement(Person person, boolean partner, int type) {
        String typeValue = null;
        String noteText = null;
        switch(type) {
        case NOTE_TYPE_GENERAL:
            typeValue = NOTE_TYPE_ATTRIBUTE_GENERAL_VW;
            noteText = partner ? person.note_b_e1 : person.note_a_e1;
            break;
        case NOTE_TYPE_ORGANIZATION:
            typeValue = NOTE_TYPE_ATTRIBUTE_ORGANIZATION_VW;
            noteText = partner ? person.noteorga_b_e1 : person.noteorga_a_e1;
            break;
        case NOTE_TYPE_HOST:
            typeValue = NOTE_TYPE_ATTRIBUTE_HOST_VW;
            noteText = partner ? person.notehost_b_e1 : person.notehost_a_e1;
            break;
        }
        return createTextElement(NOTE_ELEMENT_VW, noteText, NOTE_TYPE_ATTRIBUTE_VW, typeValue, false);
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:name</code>-Element. 
     * 
     * @param person {@link Person}-Bean
     * @param partner Flag: Partner (true) oder Hauptperson (false)
     * @param lang Sprache, {@link #LANGUAGE_LATIN}, {@link #LANGUAGE_EXTRA_1}
     *  oder {@link #LANGUAGE_EXTRA_2}
     * @return ein <code>vw:name</code>-{@link Element} oder <code>null</code>
     */
    Element createNameElement(Person person, boolean partner, int lang) {
        PersonMemberFacade facade = null;
        String language = null;
        switch(lang) {
        case LANGUAGE_LATIN:
            language = LANGUAGE_ATTRIBUTE_LATIN_VW;
            facade = partner ? person.getPartnerLatin() : person.getMainLatin();
            break;
        case LANGUAGE_EXTRA_1:
            language = LANGUAGE_ATTRIBUTE_EXTRA_1_VW;
            facade = partner ? person.getPartnerExtra1() : person.getMainExtra1();
            break;
        case LANGUAGE_EXTRA_2:
            language = LANGUAGE_ATTRIBUTE_EXTRA_2_VW;
            facade = partner ? person.getPartnerExtra2() : person.getMainExtra2();
            break;
        }
        assert facade != null;
        
        boolean notEmpty = false;
        Element name = document.createElementNS(VW_NAMESPACE_URI, NAME_ELEMENT_VW);
        setAttribute(name, NAME_LANGUAGE_ATTRIBUTE_VW, language);
        notEmpty |= setAttribute(name, NAME_FIRSTNAME_ATTRIBUTE_VW, facade.getFirstname());
        notEmpty |= setAttribute(name, NAME_LASTNAME_ATTRIBUTE_VW, facade.getLastname());
        notEmpty |= setAttribute(name, NAME_TITLE_ATTRIBUTE_VW, facade.getTitle());
        notEmpty |= setAttribute(name, NAME_SALUTATION_ATTRIBUTE_VW, facade.getSalutation());
        return notEmpty ? name : null;
    }
    
    /**
     * Diese Methode erzeugt ein <code>vw:address</code>-Element. 
     * 
     * @param person {@link Person}-Bean
     * @param type Adresstyp, {@link #ADDRESS_TYPE_PRIVATE}, {@link #ADDRESS_TYPE_BUSINESS}
     *  oder {@link #ADDRESS_TYPE_OTHER}
     * @param lang Sprache, {@link #LANGUAGE_LATIN}, {@link #LANGUAGE_EXTRA_1}
     *  oder {@link #LANGUAGE_EXTRA_2}
     * @return ein <code>vw:address</code>-{@link Element} oder <code>null</code>
     */
    Element createAddressElement(Person person, int type, int lang) {
        PersonAddressFacade facade = null;
        String language = null;
        String addressType = null;
        switch(lang) {
        case LANGUAGE_LATIN:
            language = LANGUAGE_ATTRIBUTE_LATIN_VW;
            break;
        case LANGUAGE_EXTRA_1:
            language = LANGUAGE_ATTRIBUTE_EXTRA_1_VW;
            break;
        case LANGUAGE_EXTRA_2:
            language = LANGUAGE_ATTRIBUTE_EXTRA_2_VW;
            break;
        }
        switch(type) {
        case ADDRESS_TYPE_PRIVATE:
            addressType = ADDRESS_TYPE_ATTRIBUTE_PRIVATE_VW;
            switch(lang) {
            case LANGUAGE_LATIN:
                facade = person.getPrivateLatin();
                break;
            case LANGUAGE_EXTRA_1:
                facade = person.getPrivateExtra1();
                break;
            case LANGUAGE_EXTRA_2:
                facade = person.getPrivateExtra2();
                break;
            }
            break;
        case ADDRESS_TYPE_BUSINESS:
            addressType = ADDRESS_TYPE_ATTRIBUTE_BUSINESS_VW;
            switch(lang) {
            case LANGUAGE_LATIN:
                facade = person.getBusinessLatin();
                break;
            case LANGUAGE_EXTRA_1:
                facade = person.getBusinessExtra1();
                break;
            case LANGUAGE_EXTRA_2:
                facade = person.getBusinessExtra2();
                break;
            }
            break;
        case ADDRESS_TYPE_OTHER:
            addressType = ADDRESS_TYPE_ATTRIBUTE_OTHER_VW;
            switch(lang) {
            case LANGUAGE_LATIN:
                facade = person.getOtherLatin();
                break;
            case LANGUAGE_EXTRA_1:
                facade = person.getOtherExtra1();
                break;
            case LANGUAGE_EXTRA_2:
                facade = person.getOtherExtra2();
                break;
            }
            break;
        }
        if (facade != null) {
            boolean notEmpty = false;
            Element address = document.createElementNS(VW_NAMESPACE_URI, ADDRESS_ELEMENT_VW);
            setAttribute(address, ADDRESS_LANGUAGE_ATTRIBUTE_VW, language);
            setAttribute(address, ADDRESS_TYPE_ATTRIBUTE_VW, addressType);
            notEmpty |= setAttribute(address, ADDRESS_COUNTRY_ATTRIBUTE_VW, facade.getCountry());
            notEmpty |= setAttribute(address, ADDRESS_EMAIL_ATTRIBUTE_VW, facade.getEMail());
            notEmpty |= setAttribute(address, ADDRESS_URL_ATTRIBUTE_VW, facade.getUrl());
            notEmpty |= appendChild(address, createTextElement(FUNCTION_ELEMENT_VW, facade.getFunction()));
            notEmpty |= appendChild(address, createTextElement(COMPANY_ELEMENT_VW, facade.getCompany()));
            notEmpty |= appendChild(address, createTextElement(STREET_ELEMENT_VW, facade.getStreet()));
            notEmpty |= appendChild(address, createTextElement(CITY_ELEMENT_VW, facade.getCity(), CITY_ZIPCODE_ATTRIBUTE_VW, facade.getZipCode(), true));
            notEmpty |= appendChild(address, createTextElement(STATE_ELEMENT_VW, facade.getState()));
            notEmpty |= appendChild(address, createTextElement(POBOX_ELEMENT_VW, facade.getPOBox(), POBOX_ZIPCODE_ATTRIBUTE_VW, facade.getPOBoxZipCode(), true));
            notEmpty |= appendChild(address, createTextElement(SUFFIX_ELEMENT_VW, facade.getSuffix1(), SUFFIX_INDEX_ATTRIBUTE_VW, SUFFIX_INDEX_ATTRIBUTE_1_VW, false));
            notEmpty |= appendChild(address, createTextElement(SUFFIX_ELEMENT_VW, facade.getSuffix2(), SUFFIX_INDEX_ATTRIBUTE_VW, SUFFIX_INDEX_ATTRIBUTE_2_VW, false));
            notEmpty |= appendChild(address, createTextElement(PHONE_ELEMENT_VW, facade.getPhone(), PHONE_TYPE_ATTRIBUTE_VW, PHONE_TYPE_ATTRIBUTE_FIXED_VW, false));
            notEmpty |= appendChild(address, createTextElement(PHONE_ELEMENT_VW, facade.getFax(), PHONE_TYPE_ATTRIBUTE_VW, PHONE_TYPE_ATTRIBUTE_FAX_VW, false));
            notEmpty |= appendChild(address, createTextElement(PHONE_ELEMENT_VW, facade.getMobile(), PHONE_TYPE_ATTRIBUTE_VW, PHONE_TYPE_ATTRIBUTE_MOBILE_VW, false));
            if (notEmpty)
                return address;
        }
        return null;
    }
    
    /**
     * Diese Methode erzeugt ein Element mit Textinhalt, falls der �bergebene
     * Text nicht leer ist. 
     * 
     * @param name Name des zu erzeugenden Elements
     * @param text Text des zu erzeugenden Elements
     * @return ein {@link Element} oder <code>null</code>
     */
    Element createTextElement(String name, String text) {
        assert name != null;
        if (text != null && text.length() > 0) {
            Element element = document.createElementNS(VW_NAMESPACE_URI, name);
            element.appendChild(document.createTextNode(text));
            return element;
        }
        return null;
    }
    
    /**
     * Diese Methode erzeugt ein Element mit Textinhalt und einem Attribut,
     * falls die �bergebenen Texte nicht leer sind. 
     * 
     * @param name Name des zu erzeugenden Elements
     * @param text Text des zu erzeugenden Elements
     * @param attributeName Name des zu erzeugenden Attributs
     * @param attributeValue Wert des zu erzeugenden Attributs
     * @param checkAttribute Flag: Soll zur Erzeugung auch ein nicht-leerer
     *  Attributswert reichen?
     * @return ein {@link Element} oder <code>null</code>
     */
    Element createTextElement(String name, String text, String attributeName, String attributeValue, boolean checkAttribute) {
        assert name != null;
        assert attributeName != null;
        if ((text != null && text.length() > 0) || (checkAttribute && attributeValue != null && attributeValue.length() > 0)) {
            Element element = document.createElementNS(VW_NAMESPACE_URI, name);
            if (text != null && text.length() > 0)
                element.appendChild(document.createTextNode(text));
            setAttribute(element, attributeName, attributeValue);
            return element;
        }
        return null;
    }
    
    /**
     * Diese statische Methode setzt ein Attribut, falls der �bergebene Wert weder
     * <code>null</code> noch <code>""</code> ist. 
     * 
     * @param element {@link Element}, bei dem das Attribut gesetzt werden soll
     * @param name Name des zu setzenden Attributs
     * @param value neuer Wert des zu setzenden Attributs.
     * @return <code>true</code> falls das Attribut gesetzt wurde
     */
    static boolean setAttribute(Element element, String name, Object value) {
        assert element != null;
        assert name != null;
        
        if (value != null) {
            String valueString = value.toString();
            if (valueString.length() > 0) {
                element.setAttributeNS(VW_NAMESPACE_URI, name, valueString);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Diese statische Methode f�gt ein Kindelement hinzu, falls jenes nicht
     * <code>null</code> ist. 
     * 
     * @param element {@link Element}, das ein neues Kind-{@link Element}
     *  erhalten soll
     * @param child das neue Kind-{@link Element}
     * @return <code>true</code> falls das Kind-Element angef�gt wurde
     */
    static boolean appendChild(Element element, Element child) {
        assert element != null;
        
        if (child != null) {
            element.appendChild(child);
            return true;
        }
        return false;
    }
    
    //
    // gesch�tzte Member-Variablen
    //
    /** Der zu verwendende Eingabedatenstrom --- wird intern nicht genutzt */
    InputStream inputStream = null;

    OutputStream os = null;

    Database db = null;
    
    ExchangeFormat format = null;

    final Document document;
    
    Element baseElement = null;
    
    boolean useCcmEnvelope = DEFAULT_CCM_ENVELOPE;
    
    String ccmApplication = DEFAULT_CCM_APPLICATION;
    
    String ccmSenderEndpoint = null;
    
    String ccmReceiverEndpoint = null;
    
    String ccmOutgoingFolder = DEFAULT_CCM_OUTGOING_FOLDER;
    
    File ccmOutgoingFile = null;
    
    final SimpleDateFormat ccmDateFormat = new SimpleDateFormat("ddMMyyyy-HHmmss"); 
    
    /** Logger dieser Klasse */
    static Logger logger = Logger.getLogger(XMLExporter.class.getName());
    
    /* (non-Javadoc)
		 * @see de.tarent.aa.veraweb.utils.Exporter#setOrgUnitId(java.lang.Integer)
		 */
		public void setOrgUnitId(Integer orgUnitId)
		{
			// obsolete
			
		}
		/* (non-Javadoc)
		 * @see de.tarent.aa.veraweb.utils.Exporter#setCategoryId(java.lang.Integer)
		 */
		public void setCategoryId(Integer categoryId)
		{
			// obsolete
			
		}
}
