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
package de.tarent.aa.veraweb.beans.facade;

/**
 * Definiert eine Address-Facade, inkl. Strasse, PLZ, Ort, etc.
 * 
 * @author Michael Klink, Christoph Jerolimov
 */
public interface PersonAddressFacade {
	/** @return Funktionsname */
	public String getFunction();

	/** @return Firma / Institution */
	public String getCompany();

	/** @return Stra�e und Hausnummer */
	public String getStreet();

	/** @return PLZ */
	public String getZipCode();
	
	/** @return sBundesland **/
	public String getState();

	/** @return Ort */
	public String getCity();

	/** @return Land */
	public String getCountry();

	/** @return Postfach-Nummer */
	public String getPOBox();

	/** @return Postfach-PLZ */
	public String getPOBoxZipCode();

	/** @return Adresszusatz Zeile 1 */
	public String getSuffix1();

	/** @return Adresszusatz Zeile 2 */
	public String getSuffix2();

	/** @return Telefonnummer */
	public String getPhone();

	/** @return Faxger�tnummer */
	public String getFax();

	/** @return Mobiltelefonnummer */
	public String getMobile();

	/** @return eMail-Adresse */
	public String getEMail();

	/** @return Internet-Adresse */
	public String getUrl();

	/** Setzt die Funktionsname */
	public void setFunction(String value);

	/** Setzt die Firma / Institutition */
	public void setCompany(String value);

	/** Setzt die Stra�e und Hausnummer */
	public void setStreet(String value);

	/** Setzt die PLZ */
	public void setZipCode(String value);
	
	/** Setzt das Bundesland **/
	public void setState(String value);

	/** Setzt den Ort */
	public void setCity(String value);

	/** Setzt das Land */
	public void setCountry(String value);

	/** Setzt die Postfach-Nummer */
	public void setPOBox(String value);

	/** Setzt die Postfach-PLZ */
	public void setPOBoxZipCode(String value);

	/** Setzt die erste Zeile des Adresszusatzes */
	public void setSuffix1(String value);

	/** Setzt die zweite Zeile des Adresszusatzes */
	public void setSuffix2(String value);

	/** Setzt die Telefonummer */
	public void setPhone(String value);

	/** Setzt die Faxger�tnummer */
	public void setFax(String value);

	/** Setzt die Mobiltelefonnummer */
	public void setMobile(String value);

	/** Setzt die eMail-Adresse */
	public void setEMail(String value);

	/** Setzt die Internet-Adresse */
	public void setUrl(String value);
}
