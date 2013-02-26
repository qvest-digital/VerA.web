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
package de.tarent.ldap;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Objekt, welches ungef�hr das repr�sentiert, was inetOrgPerson im LDAP ist...
 * 
 * @author philipp
 */
public class LDAPContact {
	private String vorname = new String("");
	private String mittelname = new String("");
	private String nachname = new String("");
	private String spitzname = new String("");
	private String userid = new String("");
	private String arbeitOrt = new String("");
	private String arbeitPLZ = new String("");
	private String arbeitStrasse = new String("");
	private String arbeitBundesstaat = new String("");
	private String arbeitLand = new String("");
	private String arbeitFirma = new String("");
	private String arbeitJob = new String("");
	private String arbeitFax = new String("");
	private String arbeitTelefon = new String("");
	private String arbeitAbteilung = new String("");
	private String arbeitHandy = new String("");
	private String pager = new String("");
	private String privatTelefon = new String("");
	private String privatStrasse = new String("");
	private String arbeitEmail = new String("");
	private String beschreibung = new String("");
	
	private Map verteilergruppe = new HashMap();
	private Map allUsers;
	private List Users;
	private static Logger logger = Logger.getLogger(LDAPContact.class.getName());
	private String privatLand = new String("");
	private String privatOrt = new String("");
	private String privatPLZ = new String("");
	private String privatFax = new String("");
	private String privatHandy = new String("");
	private String privatEmail = new String("");
	
	private static final String[] INVALID_CHARS = {"�", "�", "�", "�", "�", "�", "�"};
	
	public LDAPContact(){
	}
	
	/**
	 * Erzeugt einen neuen LDAPContact mit gegebener Address-Map
	 * @param address
	 */
	public LDAPContact(Map address, String userID) {
		setContact(address);
		this.userid = userID;
	}
	
	private String get(Map address, String key){
		String wert = new String();
		if(address.get(key)!=null){wert = address.get(key).toString();}
		return wert;
	}

	/**
	 * Setzt Kontakt auf gegebene Map mit Addressdaten
	 * @param address die AddressMap 
	 */
	public void setContact(Map address){
		setVorname(get(address, "a4"));
		setNachname(get(address, "a5"));
		setArbeitFirma(get(address, "a7"));
		if((getArbeitFirma()==null)||"".equals(getArbeitFirma())) {
			//PrivatAdresse
			setPrivatStrasse(get(address, "a8") + get(address, "a9"));
			setPrivatPLZ(get(address, "a10"));
			setPrivatOrt(get(address, "a11"));
			setPrivatLand(get(address, "a19"));
		}else {
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
		setPrivatFax(get (tkomm, "103"));
		setArbeitFax(get (tkomm, "104"));
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
		if(NullOrEmpty(nachname)){
			//Nachname leer, versuche Firma
			if(NullOrEmpty(arbeitFirma)){
				//Firma auch leer, versuche Vorname
				if(NullOrEmpty(vorname)){
					nachname="";
				}else{
					nachname = "Vorname: vorname";
				}
			}else{
				nachname = arbeitFirma;
			}
		}
	}
	
	private boolean NullOrEmpty(String string){
		if(string==null){
			return true;
		}else{
			if(string.equals("")){
				return true;
			}else{
				return false;
			}
		}
	}
	
	
	private boolean contains_invalid_chars(String string){
		for(int i=0; i< INVALID_CHARS.length; i++){
			String character = INVALID_CHARS[i];
			if(string.indexOf(character)!=-1){
				return true;
			}
		}
		return false;
	}

	public String toString(){
	String ruckgabe = new String();
		ruckgabe += "Vorname: "+vorname+"\n";
		ruckgabe +="Mittlerer Name: " + mittelname + "\n";
		ruckgabe +="Nachname:" + nachname + "\n";
		ruckgabe +="Spitzname:" + spitzname + "\n";
		ruckgabe +="UserID:" + userid + "\n"; 
		ruckgabe +="Gesch�ftlich:\n";
		ruckgabe +="Firma: "+ arbeitFirma + "\n";
		ruckgabe +="Abteilung: " + arbeitAbteilung + "\n";
		ruckgabe +="Strasse: " + arbeitStrasse +"\n";
		ruckgabe +="PLZ/Ort: " + arbeitPLZ + " " + arbeitOrt + "\n";
		ruckgabe +="Staat: " + arbeitBundesstaat + "\n";
		ruckgabe +="Land: " + arbeitLand + "\n";
		ruckgabe +="Job: " + arbeitJob + "\n";
		ruckgabe +="EMail: " + arbeitEmail + "\n";
		ruckgabe +="Telefon: " + arbeitTelefon + "\n";
		ruckgabe +="Fax: " + arbeitFax + "\n";
		ruckgabe +="Pager: " + pager + "\n";
		ruckgabe +="Handy: " + arbeitHandy + "\n";
		ruckgabe +="Privat: \n";
		ruckgabe +="Strasse: " + privatStrasse + "\n";
		ruckgabe +="Telefon: " + privatTelefon + "\n";
		ruckgabe +="\n";
		ruckgabe +="Beschreibung: " + beschreibung +"\n";
		
		return ruckgabe;
	}
	
	/**
	 * Getter f�r Nachname
	 * @return Nachname
	 */
	public String getNachname() {
		checkNachName();
		return "".equals(nachname)?"unbekannt":nachname;
	}

	/**
	 * Getter f�r Spitzname
	 * @return Spitzname
	 */
	public String getSpitzname() {
		return spitzname;
	}

	/**
	 * Getter f�r Vorname
	 * @return Vorname
	 */
	public String getVorname() {
		return vorname;
	}

	/**
	 * Setter f�r Nachname
	 * @param string Nachname
	 */
	public void setNachname(String string) {
		nachname = string;
		checkNachName();
	}

	/**
	 * Setter f�r Spitzname
	 * @param string Spitzname
	 */
	public void setSpitzname(String string) {
		spitzname = string;
	}

	/**
	 * Setter f�r Vorname
	 * @param string Vorname
	 */
	public void setVorname(String string) {
		vorname = string;
	}

	/**
	 * Getter f�r UserID
	 * @return UserID
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * Setter f�r UserID
	 * @param string UserID
	 */
	public void setUserid(String string) {
		userid = string;
	}

	/**
	 * Getter f�r ArbeitFirma
	 * @return ArbeitFirma
	 */
	public String getArbeitFirma() {
		return arbeitFirma;
	}

	/**
	 * Getter f�r ArbeitOrt
	 * @return ArbeitOrt
	 */
	public String getArbeitOrt() {
		return arbeitOrt;
	}

	/**
	 * Getter f�r ArbeitPLZ
	 * @return ArbeitPLZ
	 */
	public String getArbeitPLZ() {
		return arbeitPLZ;
	}

	/**
	 * Getter f�r ArbeitStrasse
	 * @return ArbeitStrasse
	 */
	public String getArbeitStrasse() {
		return arbeitStrasse;
	}

	/**
	 * Setter f�r ArbeitFirma
	 * @param string ArbeitFirma
	 */
	public void setArbeitFirma(String string) {
		arbeitFirma = string;
		checkNachName();
	}

	/**
	 * Setter f�r ArbeitOrt
	 * @param string ArbeitOrt
	 */
	public void setArbeitOrt(String string) {
		arbeitOrt = string;
	}

	/**
	 * Setter f�r ArbeitPLZ
	 * @param string ArbeitPLZ
	 */
	public void setArbeitPLZ(String string) {
		arbeitPLZ = string;
	}

	/**
	 * Setter f�r ArbeitStrasse
	 * @param string ArbeitStrasse
	 */
	public void setArbeitStrasse(String string) {
		arbeitStrasse = string;
	}

	/**
	 * Getter f�r ArbeitJob
	 * @return ArbeitJob
	 */
	public String getArbeitJob() {
		return arbeitJob;
	}

	/**
	 * Getter f�r Email
	 * @return Email
	 */
	public String getArbeitEmail() {
		return arbeitEmail;
	}

	/**
	 * Setter f�r ArbeitJob
	 * @param string ArbeitJob
	 */
	public void setArbeitJob(String string) {
		arbeitJob = string;
	}

	/**
	 * Setter f�r Email
	 * @param string Email
	 */
	public void setArbeitEmail(String string) {
		try {
			if(string!=null){
				InternetAddress address = new InternetAddress(string, true);
				address.validate();
				arbeitEmail = address.toString();
				if(contains_invalid_chars(arbeitEmail)){
					arbeitEmail="";;
				}
			}
		} catch (AddressException e) {
			//Wenn Fehler, setze Mail auf null
			arbeitEmail = "";;
		}
		
	}

	/**
	 * Getter f�r ArbeitFax
	 * @return ArbeitFax
	 */
	public String getArbeitFax() {
		return arbeitFax;
	}

	/**
	 * Setter f�r ArbeitFax
	 * @param string ArbeitFax
	 */
	public void setArbeitFax(String string) {
		if(contains_invalid_chars(string)){
			arbeitFax = "";;
		}else{
			arbeitFax = string;
		}
	}

	/**
	 * Getter f�r ArbeitTelefon
	 * @return ArbeitTelefon
	 */
	public String getArbeitTelefon() {
		return arbeitTelefon;
	}

	/**
	 * Setter f�r ArbeitTelefon
	 * @param string ArbeitTelefon
	 */
	public void setArbeitTelefon(String string) {
		if(contains_invalid_chars(string)){
			arbeitTelefon = "";;
		}else{
			arbeitTelefon = string;
		}
	}

	/**
	 * Getter f�r Handy
	 * @return Handy
	 */
	public String getArbeitHandy() {
		return arbeitHandy;
	}

	/**
	 * Setter f�r Handy
	 * @param string Handy
	 */
	public void setArbeitHandy(String string) {
		if(contains_invalid_chars(string)){
			arbeitHandy = "";;
		}else{
			arbeitHandy = string;
		}
	}

	/**
	 * Getter f�r HomeTelefon
	 * @return HomeTelefon
	 */
	public String getPrivatTelefon() {
		return privatTelefon;
	}

	/**
	 * Getter f�r Pager
	 * @return Pager
	 */
	public String getPager() {
		return pager;
	}

	/**
	 * Setter f�r HomeTelefon
	 * @param string HomeTelefon
	 */
	public void setPrivatTelefon(String string) {
		if(contains_invalid_chars(string)){
			privatTelefon = "";;
		}else{
			privatTelefon = string;
		}
	}

	/**
	 * Setter f�r Pager
	 * @param string Pager
	 */
	public void setPager(String string) {
		pager = string;
	}

	/**
	 * Getter f�r ArbeitAbteilung
	 * @return ArbeitAbteilung
	 */
	public String getArbeitAbteilung() {
		return arbeitAbteilung;
	}

	/**
	 * Setter f�r ArbeitAbteilung
	 * @param string
	 */
	public void setArbeitAbteilung(String string) {
		arbeitAbteilung = string;
	}

	/**
	 * Getter f�r Beschreibung
	 * @return Beschreibung
	 */
	public String getBeschreibung() {
		return beschreibung;
	}

	/**
	 * Setter f�r Beschreibung
	 * @param string Beschreibung
	 */
	public void setBeschreibung(String string) {
		beschreibung = string;
	}

	/**
	 * Getter f�r HomeStrasse
	 * @return HomeStrasse
	 */
	public String getPrivatStrasse() {
		return privatStrasse;
	}

	/**
	 * Setter f�r HomeStrase
	 * @param string HomeStrasse
	 */
	public void setPrivatStrasse(String string) {
		privatStrasse = string;
	}

	/**
	 * Getter f�r ArbeitLand
	 * @return ArbeitLand
	 */
	public String getArbeitLand() {
		return arbeitLand;
	}

	/**
	 * Setter f�r ArbeitLand
	 * @param string ArbeitLand
	 */
	public void setArbeitLand(String string) {
		arbeitLand = string;
	}

	/**
	 * Getter f�r Mittelname
	 * @return Mittelname
	 */
	public String getMittelname() {
		return mittelname;
	}

	/**
	 * Setter f�r Mittelname
	 * @param string Mittelname
	 */
	public void setMittelname(String string) {
		mittelname = string;
	}

	/**
	 * Getter f�r ArbeitBundesstaat
	 * @return ArbeitBundesstaat
	 */
	public String getArbeitBundesstaat() {
		return arbeitBundesstaat;
	}

	/**
	 * Setter f�r ArbeitBundesstart
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
 * @param map
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
		return Users;
	}

	/**
	 * @param users The users to set.
	 */
	public void setUsers(List users) {
		Users = users;
	}

	/**
	 * Generiert aus diesem LDAPContact ein BasicAttributes
	 *
	 * @return BasicAttributes, die man dann im LDAP verwenden kann
	 * @throws LDAPException Wenn was daneben geht
	 * @see de.tarent.octopus.sync.ldap.LDAPContact
	 *  @see javax.naming.directory.BasicAttributes
	 */
	public BasicAttributes generate_Attributes_restricted(LDAPManager manager) throws LDAPException{
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
		for(int i=0; i<objectClassList.getLength(); i++){
			objectclass.add(objectClassList.item(i).getAttributes().item(0).getNodeValue());
		}
		attr.put(objectclass);
		// Hole alles andere aus dem XML
		NodeList childs = XMLUtil.getRelevantChildren(mapping);
		for(int i=0; i<childs.getLength();i++){
			String attribut = childs.item(i).getAttributes().item(0).getNodeValue();
			String value = getValue(childs.item(i).getAttributes());
			if((value!=null)&(!value.equals(""))){ //$NON-NLS-1$
				attr.put(attribut, value);
			}
		}
		BasicAttribute users = new BasicAttribute("member"); //$NON-NLS-1$
		Iterator it = getUsers().iterator();
		while(it.hasNext()){
			String adduser = (String) it.next();
			try {
			    String adduser2 = manager.getUserDN(adduser)+manager.relativeUser+manager.baseDN; //$NON-NLS-1$
			    users.add(adduser2);
			}catch(LDAPException le) {
			    logger.log(Level.WARNING, "User " + adduser + " existiert im LDAP leider nicht! Bitte bereinigen Sie die User.");
			}
		}
		if(users.size()==0){
			throw new NoMemberException("Es muss mindestens ein User Rechte auf diese Adresse haben, damit Sie exportiert werden kann.");
		}
		attr.put(users);
		return attr;
	}

	/**
	 * Methode, die zu einem gegebenen Attribut die passende Variable aus einem @see LDAPContact ausliest..
	 * 
	 * @param this - LDAPContact , aus dem gelesen werden soll
	 * 					@see de.tarent.octopus.sync.ldap.LDAPContact
	 * @param attribute - Attribut, welches den Wert beschreibt
	 * @return String mit dem gew�nschen Wert
	 * @throws LDAPException - wenn etwas schief l�uft
	 */
	protected String getValue(NamedNodeMap attribute) throws LDAPException{
		Method getter;
		String value;
		String attribut = attribute.item(0).getNodeValue();
		try {
			getter = getClass().getDeclaredMethod("get" + attribute.item(1).getNodeValue(),	null); //$NON-NLS-1$
			value = (String) getter.invoke(this, null);
			if(attribute.getLength()>2){
				getter = getClass().getDeclaredMethod("get" + attribute.item(2).getNodeValue(), null); //$NON-NLS-1$
				value += Messages.getString("LDAPManager.whitespace_01") + (String) getter.invoke(this, null); //$NON-NLS-1$
			}
		} catch (Exception e) {
			throw new LDAPException(Messages.getString("LDAPManager.getter_not_found_01")+ attribut +Messages.getString("LDAPManager.getter_not_found_02") + e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
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
			if(privatEmail!=null){
				InternetAddress address = new InternetAddress(privatEmail);
				address.validate();
				this.privatEmail = privatEmail;
				if(contains_invalid_chars(this.privatEmail)){
					this.privatEmail="";;
				}

			}
		} catch (AddressException e) {
			//Wenn Fehler, setze Mail auf null
			this.privatEmail = "";;
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
		if(contains_invalid_chars(privatFax)){
			this.privatFax = "";;
		}else{
			this.privatFax = privatFax;
		}
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
		if(contains_invalid_chars(privatHandy)){
			this.privatHandy = "";;
		}else{
			this.privatHandy = privatHandy;
		}
	}
}
