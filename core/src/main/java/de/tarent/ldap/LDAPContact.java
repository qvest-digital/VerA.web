package de.tarent.ldap;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Objekt, welches ungefähr das repräsentiert, was inetOrgPerson im LDAP ist...
 *
 * @author philipp
 */
@Log4j2
public class LDAPContact {
    private static final String EMPTY_STRING = "";
    private String vorname = EMPTY_STRING;
    private String mittelname = EMPTY_STRING;
    private String nachname = EMPTY_STRING;
    private String spitzname = EMPTY_STRING;
    private String userid = EMPTY_STRING;
    private String arbeitOrt = EMPTY_STRING;
    private String arbeitPLZ = EMPTY_STRING;
    private String arbeitStrasse = EMPTY_STRING;
    private String arbeitBundesstaat = EMPTY_STRING;
    private String arbeitLand = EMPTY_STRING;
    private String arbeitFirma = EMPTY_STRING;
    private String arbeitJob = EMPTY_STRING;
    private String arbeitFax = EMPTY_STRING;
    private String arbeitTelefon = EMPTY_STRING;
    private String arbeitAbteilung = EMPTY_STRING;
    private String arbeitHandy = EMPTY_STRING;
    private String pager = EMPTY_STRING;
    private String privatTelefon = EMPTY_STRING;
    private String privatStrasse = EMPTY_STRING;
    private String arbeitEmail = EMPTY_STRING;
    private String beschreibung = EMPTY_STRING;

    private Map verteilergruppe = new HashMap();
    private Map allUsers;
    private List users;
    private String privatLand = EMPTY_STRING;
    private String privatOrt = EMPTY_STRING;
    private String privatPLZ = EMPTY_STRING;
    private String privatFax = EMPTY_STRING;
    private String privatHandy = EMPTY_STRING;
    private String privatEmail = EMPTY_STRING;

    public LDAPContact() {
    }

    /**
     * Erzeugt einen neuen LDAPContact mit gegebener Address-Map
     *
     * @param address FIXME
     * @param userID  FIXME
     */
    public LDAPContact(Map address, String userID) {
        setContact(address);
        this.userid = userID;
    }

    private String get(Map address, String key) {
        return address.get(key) == null ? EMPTY_STRING : address.get(key).toString();
    }

    /**
     * Setzt Kontakt auf gegebene Map mit Addressdaten
     *
     * @param address die AddressMap
     */
    public void setContact(Map address) {
        setVorname(get(address, "a4"));
        setNachname(get(address, "a5"));
        setArbeitFirma(get(address, "a7"));
        if ((getArbeitFirma() == null) || isBlank(getArbeitFirma())) {
            //PrivatAdresse
            setPrivatStrasse(get(address, "a8") + get(address, "a9"));
            setPrivatPLZ(get(address, "a10"));
            setPrivatOrt(get(address, "a11"));
            setPrivatLand(get(address, "a19"));
        } else {
            //Institutionsadresse
            setArbeitStrasse(get(address, "a8") + get(address, "a9"));
            setArbeitPLZ(get(address, "a10"));
            setArbeitOrt(get(address, "a11"));
            setArbeitLand(get(address, "a19"));
        }
        setSpitzname(get(address, "e2"));
        setMittelname(get(address, "e1"));
        setArbeitAbteilung(get(address, "e4"));
        //Get TKOMM
        Map tkomm = (Map) address.get("a22");
        setPrivatTelefon(get(tkomm, "101"));
        setArbeitTelefon(get(tkomm, "102"));
        setPrivatFax(get(tkomm, "103"));
        setArbeitFax(get(tkomm, "104"));
        setPrivatHandy(get(tkomm, "105"));
        setArbeitHandy(get(tkomm, "106"));
        setPrivatEmail(get(tkomm, "107"));
        setArbeitEmail(get(tkomm, "108"));

        checkNachName();
    }

    /**
     *
     */
    private void checkNachName() {
        if (isBlank(nachname)) {
            //Nachname leer, versuche Firma
            if (isBlank(arbeitFirma)) {
                //Firma auch leer, versuche Vorname
                if (isBlank(vorname)) {
                    nachname = EMPTY_STRING;
                } else {
                    nachname = "Vorname: vorname";
                }
            } else {
                nachname = arbeitFirma;
            }
        }
    }

    public String toString() {
        StringBuffer ruckgabe = new StringBuffer(240)
          .append("Vorname: " + vorname + "\n")
          .append("Mittlerer Name: " + mittelname + "\n")
          .append("Nachname:" + nachname + "\n")
          .append("Spitzname:" + spitzname + "\n")
          .append("UserID:" + userid + "\n")
          .append("Geschäftlich:\n")
          .append("Firma: " + arbeitFirma + "\n")
          .append("Abteilung: " + arbeitAbteilung + "\n")
          .append("Strasse: " + arbeitStrasse + "\n")
          .append("PLZ/Ort: " + arbeitPLZ + " " + arbeitOrt + "\n")
          .append("Staat: " + arbeitBundesstaat + "\n")
          .append("Land: " + arbeitLand + "\n")
          .append("Job: " + arbeitJob + "\n")
          .append("EMail: " + arbeitEmail + "\n")
          .append("Telefon: " + arbeitTelefon + "\n")
          .append("Fax: " + arbeitFax + "\n")
          .append("Pager: " + pager + "\n")
          .append("Handy: " + arbeitHandy + "\n")
          .append("Privat: \n")
          .append("Strasse: " + privatStrasse + "\n")
          .append("Telefon: " + privatTelefon + "\n")
          .append("\n")
          .append("Beschreibung: " + beschreibung + "\n");

        return ruckgabe.toString();
    }

    /**
     * Getter für Nachname
     *
     * @return Nachname
     */
    public String getNachname() {
        checkNachName();
        return isBlank(nachname) ? "unbekannt" : nachname;
    }

    /**
     * Getter für Spitzname
     *
     * @return Spitzname
     */
    public String getSpitzname() {
        return spitzname;
    }

    /**
     * Getter für Vorname
     *
     * @return Vorname
     */
    public String getVorname() {
        return vorname;
    }

    /**
     * Setter für Nachname
     *
     * @param string Nachname
     */
    public void setNachname(String string) {
        nachname = string;
        checkNachName();
    }

    /**
     * Setter für Spitzname
     *
     * @param string Spitzname
     */
    public void setSpitzname(String string) {
        spitzname = string;
    }

    /**
     * Setter für Vorname
     *
     * @param string Vorname
     */
    public void setVorname(String string) {
        vorname = string;
    }

    /**
     * Getter für UserID
     *
     * @return UserID
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Setter für UserID
     *
     * @param string UserID
     */
    public void setUserid(String string) {
        userid = string;
    }

    /**
     * Getter für ArbeitFirma
     *
     * @return ArbeitFirma
     */
    public String getArbeitFirma() {
        return arbeitFirma;
    }

    /**
     * Getter für ArbeitOrt
     *
     * @return ArbeitOrt
     */
    public String getArbeitOrt() {
        return arbeitOrt;
    }

    /**
     * Getter für ArbeitPLZ
     *
     * @return ArbeitPLZ
     */
    public String getArbeitPLZ() {
        return arbeitPLZ;
    }

    /**
     * Getter für ArbeitStrasse
     *
     * @return ArbeitStrasse
     */
    public String getArbeitStrasse() {
        return arbeitStrasse;
    }

    /**
     * Setter für ArbeitFirma
     *
     * @param string ArbeitFirma
     */
    public void setArbeitFirma(String string) {
        arbeitFirma = string;
        checkNachName();
    }

    /**
     * Setter für ArbeitOrt
     *
     * @param string ArbeitOrt
     */
    public void setArbeitOrt(String string) {
        arbeitOrt = string;
    }

    /**
     * Setter für ArbeitPLZ
     *
     * @param string ArbeitPLZ
     */
    public void setArbeitPLZ(String string) {
        arbeitPLZ = string;
    }

    /**
     * Setter für ArbeitStrasse
     *
     * @param string ArbeitStrasse
     */
    public void setArbeitStrasse(String string) {
        arbeitStrasse = string;
    }

    /**
     * Getter für ArbeitJob
     *
     * @return ArbeitJob
     */
    public String getArbeitJob() {
        return arbeitJob;
    }

    /**
     * Getter für Email
     *
     * @return Email
     */
    public String getArbeitEmail() {
        return arbeitEmail;
    }

    /**
     * Setter für ArbeitJob
     *
     * @param string ArbeitJob
     */
    public void setArbeitJob(String string) {
        arbeitJob = string;
    }

    /**
     * Setter für Email
     *
     * @param string Email
     */
    public void setArbeitEmail(String string) {
        try {
            if (string != null) {
                InternetAddress address = new InternetAddress(string, true);
                address.validate();
                arbeitEmail = address.toString();
            }
        } catch (AddressException e) {
            //Wenn Fehler, setze Mail auf null
            arbeitEmail = EMPTY_STRING;
        }
    }

    /**
     * Getter für ArbeitFax
     *
     * @return ArbeitFax
     */
    public String getArbeitFax() {
        return arbeitFax;
    }

    /**
     * Setter für ArbeitFax
     *
     * @param string ArbeitFax
     */
    public void setArbeitFax(String string) {
        arbeitFax = string;
    }

    /**
     * Getter für ArbeitTelefon
     *
     * @return ArbeitTelefon
     */
    public String getArbeitTelefon() {
        return arbeitTelefon;
    }

    /**
     * Setter für ArbeitTelefon
     *
     * @param string ArbeitTelefon
     */
    public void setArbeitTelefon(String string) {
        arbeitTelefon = string;
    }

    /**
     * Getter für Handy
     *
     * @return Handy
     */
    public String getArbeitHandy() {
        return arbeitHandy;
    }

    /**
     * Setter für Handy
     *
     * @param string Handy
     */
    public void setArbeitHandy(String string) {
        arbeitHandy = string;
    }

    /**
     * Getter für HomeTelefon
     *
     * @return HomeTelefon
     */
    public String getPrivatTelefon() {
        return privatTelefon;
    }

    /**
     * Getter für Pager
     *
     * @return Pager
     */
    public String getPager() {
        return pager;
    }

    /**
     * Setter für HomeTelefon
     *
     * @param string HomeTelefon
     */
    public void setPrivatTelefon(String string) {
        privatTelefon = string;
    }

    /**
     * Setter für Pager
     *
     * @param string Pager
     */
    public void setPager(String string) {
        pager = string;
    }

    /**
     * Getter für ArbeitAbteilung
     *
     * @return ArbeitAbteilung
     */
    public String getArbeitAbteilung() {
        return arbeitAbteilung;
    }

    /**
     * Setter für ArbeitAbteilung
     *
     * @param string FIXME
     */
    public void setArbeitAbteilung(String string) {
        arbeitAbteilung = string;
    }

    /**
     * Getter für Beschreibung
     *
     * @return Beschreibung
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    /**
     * Setter für Beschreibung
     *
     * @param string Beschreibung
     */
    public void setBeschreibung(String string) {
        beschreibung = string;
    }

    /**
     * Getter für HomeStrasse
     *
     * @return HomeStrasse
     */
    public String getPrivatStrasse() {
        return privatStrasse;
    }

    /**
     * Setter für HomeStrase
     *
     * @param string HomeStrasse
     */
    public void setPrivatStrasse(String string) {
        privatStrasse = string;
    }

    /**
     * Getter für ArbeitLand
     *
     * @return ArbeitLand
     */
    public String getArbeitLand() {
        return arbeitLand;
    }

    /**
     * Setter für ArbeitLand
     *
     * @param string ArbeitLand
     */
    public void setArbeitLand(String string) {
        arbeitLand = string;
    }

    /**
     * Getter für Mittelname
     *
     * @return Mittelname
     */
    public String getMittelname() {
        return mittelname;
    }

    /**
     * Setter für Mittelname
     *
     * @param string Mittelname
     */
    public void setMittelname(String string) {
        mittelname = string;
    }

    /**
     * Getter für ArbeitBundesstaat
     *
     * @return ArbeitBundesstaat
     */
    public String getArbeitBundesstaat() {
        return arbeitBundesstaat;
    }

    /**
     * Setter für ArbeitBundesstart
     *
     * @param string ArbeitBundesstaat
     */
    public void setArbeitBundesstaat(String string) {
        arbeitBundesstaat = string;
    }

    /**
     * @return Verteilergruppe
     */
    public Map getVerteilergruppe() {
        return verteilergruppe;
    }

    /**
     * @param vertgrp Map mit Verteilergruppen
     */
    public void setVerteilergruppe(Map vertgrp) {
        verteilergruppe = vertgrp;
    }

    /**
     * @param map FIXME
     */
    public void setAllUsers(Map map) {
        this.allUsers = map;
    }

    /**
     * @return Returns the allUsers.
     */
    public Map getAllUsers() {
        return allUsers;
    }

    /**
     * @return Returns the users.
     */
    public List getUsers() {
        return users;
    }

    /**
     * @param users The users to set.
     */
    public void setUsers(List users) {
        this.users = users;
    }

    /**
     * Generiert aus diesem LDAPContact ein BasicAttributes
     *
     * @return BasicAttributes, die man dann im LDAP verwenden kann
     * @throws LDAPException Wenn was daneben geht
     * @see de.tarent.ldap.LDAPContact
     * @see javax.naming.directory.BasicAttributes
     */
    public BasicAttributes generateAttributesRestricted(LDAPManager manager) throws LDAPException {
        BasicAttributes attr = new BasicAttributes();
        Element mapping = null;
        //Hole XML-Mapping
        try {
            InputStream is = LDAPContact.class.getResourceAsStream("/de/tarent/ldap/resources/mapping.xml");
            mapping = XMLUtil.getParsedDocument(new InputSource(is)).getDocumentElement();
        } catch (Exception e) {
            throw new LDAPException(e.getMessage());
        }
        Node objectclassnode = XMLUtil.getObjectClass(mapping);
        NodeList objectClassList = objectclassnode.getChildNodes();
        //Hole objectclass aus dem XML
        BasicAttribute objectclass = new BasicAttribute("objectclass"); //$NON-NLS-1$ //$NON-NLS-2$
        //System.out.println(nl);
        for (int i = 0; i < objectClassList.getLength(); i++) {
            objectclass.add(objectClassList.item(i).getAttributes().item(0).getNodeValue());
        }
        attr.put(objectclass);
        // Hole alles andere aus dem XML
        NodeList childs = XMLUtil.getRelevantChildren(mapping);
        for (int i = 0; i < childs.getLength(); i++) {
            String attribut = childs.item(i).getAttributes().item(0).getNodeValue();
            String value = getValue(childs.item(i).getAttributes());
            if ((value != null) && (StringUtils.isNotBlank(value))) { //$NON-NLS-1$
                attr.put(attribut, value);
            }
        }
        BasicAttribute users = new BasicAttribute("member"); //$NON-NLS-1$
        Iterator it = getUsers().iterator();
        while (it.hasNext()) {
            String adduser = (String) it.next();
            try {
                String adduser2 = manager.fullUserDN(adduser); //$NON-NLS-1$
                users.add(adduser2);
            } catch (LDAPException le) {
                logger.warn("User " + adduser + " existiert im LDAP leider nicht! Bitte bereinigen Sie die User.");
            }
        }
        if (users.size() == 0) {
            throw new NoMemberException(
              "Es muß mindestens ein User Rechte auf diese Adresse haben, damit Sie exportiert werden kann.");
        }
        attr.put(users);
        return attr;
    }

    /**
     * Methode, die zu einem gegebenen Attribut die passende Variable aus einem @see LDAPContact ausliest..
     *
     * @param attribute - Attribut, welches den Wert beschreibt
     * @return String mit dem gewünschen Wert
     * @throws LDAPException - wenn etwas schief läuft
     * @see de.tarent.ldap.LDAPContact
     */
    protected String getValue(NamedNodeMap attribute) throws LDAPException {
        Method getter;
        String value;
        String attribut = attribute.item(0).getNodeValue();
        try {
            getter = getClass().getDeclaredMethod("get" + attribute.item(1).getNodeValue(), null); //$NON-NLS-1$
            value = (String) getter.invoke(this, null);
            if (attribute.getLength() > 2) {
                getter = getClass().getDeclaredMethod("get" + attribute.item(2).getNodeValue(), null); //$NON-NLS-1$
                value += Messages.getString("LDAPManager.whitespace_01") + getter.invoke(this, null); //$NON-NLS-1$
            }
        } catch (Exception e) {
            throw new LDAPException(
              Messages.getString("LDAPManager.getter_not_found_01") + attribut +
                Messages.getString("LDAPManager.getter_not_found_02") +
                e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
        } //$NON-NLS-1$

        return value;
    }

    /**
     * @return Returns the privatLand.
     */
    public String getPrivatLand() {
        return privatLand;
    }

    /**
     * @param privatLand The privatLand to set.
     */
    public void setPrivatLand(String privatLand) {
        this.privatLand = privatLand;
    }

    /**
     * @return Returns the privatOrt.
     */
    public String getPrivatOrt() {
        return privatOrt;
    }

    /**
     * @param privatOrt The privatOrt to set.
     */
    public void setPrivatOrt(String privatOrt) {
        this.privatOrt = privatOrt;
    }

    /**
     * @return Returns the privatPLZ.
     */
    public String getPrivatPLZ() {
        return privatPLZ;
    }

    /**
     * @param privatPLZ The privatPLZ to set.
     */
    public void setPrivatPLZ(String privatPLZ) {
        this.privatPLZ = privatPLZ;
    }

    /**
     * @return Returns the privatEmail.
     */
    public String getPrivatEmail() {
        return privatEmail;
    }

    /**
     * @param privatEmail The privatEmail to set.
     */
    public void setPrivatEmail(String privatEmail) {
        try {
            if (privatEmail != null) {
                InternetAddress address = new InternetAddress(privatEmail);
                address.validate();
                this.privatEmail = privatEmail;
            }
        } catch (AddressException e) {
            //Wenn Fehler, setze Mail auf null
            this.privatEmail = EMPTY_STRING;
        }
    }

    /**
     * @return Returns the privatFax.
     */
    public String getPrivatFax() {
        return privatFax;
    }

    /**
     * @param privatFax The privatFax to set.
     */
    public void setPrivatFax(String privatFax) {
        this.privatFax = privatFax;
    }

    /**
     * @return Returns the privatHandy.
     */
    public String getPrivatHandy() {
        return privatHandy;
    }

    /**
     * @param privatHandy The privatHandy to set.
     */
    public void setPrivatHandy(String privatHandy) {
        this.privatHandy = privatHandy;
    }
}
