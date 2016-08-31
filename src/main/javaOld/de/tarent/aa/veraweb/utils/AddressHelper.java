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
package de.tarent.aa.veraweb.utils;

import de.tarent.aa.veraweb.beans.Guest;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.Salutation;
import de.tarent.aa.veraweb.beans.facade.PersonAddressFacade;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.beans.facade.PersonMemberFacade;
import de.tarent.aa.veraweb.worker.ColorWorker;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Diese Klasse soll Adress-Spezifische entscheidungen, insbesondere
 * Default-Werte zentral halten und aus den unterschiedlichen Klassen
 * (import, dataaccess, ...) in einer sammeln.
 *
 * @author Christoph
 */
public class AddressHelper implements PersonConstants {
	//
	// PUBLIC METHODEN
	//
    /**
     * Diese Methode setzte die Gast-Farben basierend auf den Gast-Eigenschaften
     * Domestic und Sex.
     *
     * @param guest Gast, dessen Farben zu ermitteln sind.
     */
	public static void setColor(Guest guest) {
		guest.fk_color_a = ColorWorker.getColor(guest.domestic_a, guest.sex_a);
		guest.fk_color_b = ColorWorker.getColor(guest.domestic_b, guest.sex_b);
	}

    /**
     * Diese Methode Überprüft eine Reihe Felder einer {@link Person}-Instanz
     * und setzt gegebenenfalls sinnvolle Standardwerte ein.<br>
     * Es findet auch eine Sonderbehandlung spezieller {@link ImportPerson}-Felder
     * statt.
     *
     * @param person {@link Person}-Instanz, deren Felder anzupassen sind.
     */
	public static void checkPerson(Person person) {
        if (person.deleted == null) {
			person.deleted = PersonConstants.DELETED_FALSE;
        } else if (person.deleted.length() > 1) {
			person.deleted = PersonConstants.DELETED_FALSE;
        }
		if (person.iscompany == null) {
			person.iscompany = PersonConstants.ISCOMPANY_FALSE;
		} else if (person.iscompany.length() > 1) {
			person.iscompany = PersonConstants.ISCOMPANY_FALSE;
		}
		if (person.expire == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, 3);
			person.expire = new Timestamp(calendar.getTimeInMillis());
		}

		checkPersonFieldsEx(person.getMainLatin());
		checkPersonFieldsEx(person.getPartnerLatin());

        if (person instanceof ImportPerson)
            checkImportPersonFields((ImportPerson)person);
	}

	/**
	 * Setzt die 'Anrede'-Felder wenn diese eine Zahl sind.
	 * @throws IOException
	 * @throws BeanException
	 */
	public static void checkPersonSalutation(Person person, Database database, ExecutionContext context) throws BeanException, IOException {
		new AddressHelper().updateSalutation(person.getMainLatin(), database, context);
		new AddressHelper().updateSalutation(person.getMainExtra1(), database, context);
		new AddressHelper().updateSalutation(person.getMainExtra2(), database, context);
		new AddressHelper().updateSalutation(person.getPartnerLatin(), database, context);
		new AddressHelper().updateSalutation(person.getPartnerExtra1(), database, context);
		new AddressHelper().updateSalutation(person.getPartnerExtra2(), database, context);
	}

	void updateSalutation(PersonMemberFacade facade, Database database, ExecutionContext context) throws BeanException, IOException {
		try {
			facade.setSalutationFK(new Integer(facade.getSalutation()));
			facade.setSalutation(((Salutation)database.getBean("Salutation", facade.getSalutationFK(), context)).name);
		} catch (NullPointerException e) {
			facade.setSalutationFK(null);
			facade.setSalutation("");
		} catch (NumberFormatException e) {
			if (facade.getSalutation() != null && facade.getSalutation().length() != 0) {
				Salutation salutation = new Salutation();
				salutation.name = facade.getSalutation();
				salutation = (Salutation)
						database.getBean("Salutation",
						database.getSelect("Salutation").where(
						database.getWhere(salutation)), context);

				if (salutation != null) {
					facade.setSalutationFK(salutation.id);
					if (salutation.gender != null && "FfWw".indexOf(salutation.gender) != -1) {
						facade.setSex(PersonConstants.SEX_FEMALE);
					} else {
						facade.setSex(PersonConstants.SEX_MALE);
					}
				} else {
					salutation = (Salutation)database.getBean("Salutation", facade.getSalutationFK(), context);
					if (salutation != null) {
						facade.setSalutationFK(salutation.id);
						if (salutation.gender != null && "FfWw".indexOf(salutation.gender) != -1) {
							facade.setSex(PersonConstants.SEX_FEMALE);
						} else {
							facade.setSex(PersonConstants.SEX_MALE);
						}
					} else {
						facade.setSalutationFK(null);
					}
				}
			} else {
				facade.setSalutationFK(null);
			}
		}
	}

    /**
     * Diese Methode prüft die speziellen ImportPerson-Felder und kürzt sie ggf.
     *
     * @param importPerson zu prüfende ImportPerson.
     */
    private static void checkImportPersonFields(ImportPerson importPerson) {
        assert importPerson != null;
    }

	private static void checkPersonFieldsEx(PersonMemberFacade facade) {
		if (facade.getDomestic() == null) {
			facade.setDomestic(PersonConstants.DOMESTIC_INLAND);
		}
		if (facade.getSex() == null) {
			facade.setSex(PersonConstants.SEX_MALE);
		}
	}

	/**
	 * Kopiert die Adressdaten einer Person je nach vorhanden sein in
	 * andere Adresstypen und Zeichensätze. Die aktuelle Implementierung
	 * kopiert dabei die Daten nur in eine Richtung:
	 *
	 * <ul>
	 * <li>Latein-Geschäftlich nach Latain-Privat und Latein-Weitere.</li>
	 * <li>Latein-Geschäftlich nach Zeichensatz 1-Geschäftlich und Zeichensatz 2-Geschäftlich.</li>
	 * <li>Latein-Privat nach Zeichensatz 1-Privat und Zeichensatz 2-Privat.</li>
	 * <li>Latein-Weitere nach Zeichensatz 1-Weitere und Zeichensatz 2-Weitere.</li>
	 * </ul>
	 *
	 * @param octopusContext Octopus-Context
	 * @param person neuer Personen-Eintrag
	 * @param personOld alter Personen Eintrag (oder null)
	 */
	public static void copyAddressData(OctopusContext octopusContext, Person person, Person personOld) {
		final boolean forceCopy = false;

		String l2e = octopusContext.moduleConfig().getParam("copyPersonDataLatinToExtra");
		boolean l2eName =    l2e != null && (l2e.equals("all") || l2e.indexOf("name") != -1);
		boolean l2eAddress = l2e != null && (l2e.equals("all") || l2e.indexOf("address") != -1);
		boolean l2eContact = l2e != null && (l2e.equals("all") || l2e.indexOf("contact") != -1);

		PersonAddressFacade businessLatin = person.getBusinessLatin();
		PersonAddressFacade privateLatin = person.getPrivateLatin();
		PersonAddressFacade otherLatin = person.getOtherLatin();

		if (personOld == null) {
			copyAddressData(person.getMainLatin(), person.getMainExtra1(), forceCopy, l2eName);
			copyAddressData(person.getMainLatin(), person.getMainExtra2(), forceCopy, l2eName);
			copyAddressData(person.getPartnerLatin(), person.getPartnerExtra1(), forceCopy, l2eName);
			copyAddressData(person.getPartnerLatin(), person.getPartnerExtra2(), forceCopy, l2eName);

			copyAddressData(businessLatin, person.getBusinessExtra1(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(businessLatin, person.getBusinessExtra2(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(privateLatin, person.getPrivateExtra1(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(privateLatin, person.getPrivateExtra2(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(otherLatin, person.getOtherExtra1(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(otherLatin, person.getOtherExtra2(), forceCopy, l2eAddress, l2eAddress, l2eContact);
		} else {
			copyAddressData(person.getMainLatin(), person.getMainExtra1(), personOld.getMainLatin(), personOld.getMainExtra1(), forceCopy, l2eName);
			copyAddressData(person.getMainLatin(), person.getMainExtra2(), personOld.getMainLatin(), personOld.getMainExtra2(), forceCopy, l2eName);
			copyAddressData(person.getPartnerLatin(), person.getPartnerExtra1(), personOld.getPartnerLatin(), personOld.getPartnerExtra1(), forceCopy, l2eName);
			copyAddressData(person.getPartnerLatin(), person.getPartnerExtra2(), personOld.getPartnerLatin(), personOld.getPartnerExtra2(), forceCopy, l2eName);

			copyAddressData(businessLatin, person.getBusinessExtra1(), personOld.getBusinessLatin(), personOld.getBusinessExtra1(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(businessLatin, person.getBusinessExtra2(), personOld.getBusinessLatin(), personOld.getBusinessExtra2(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(privateLatin, person.getPrivateExtra1(), personOld.getPrivateLatin(), personOld.getPrivateExtra1(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(privateLatin, person.getPrivateExtra2(), personOld.getPrivateLatin(), personOld.getPrivateExtra2(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(otherLatin, person.getOtherExtra1(), personOld.getOtherLatin(), personOld.getOtherExtra1(), forceCopy, l2eAddress, l2eAddress, l2eContact);
			copyAddressData(otherLatin, person.getOtherExtra2(), personOld.getOtherLatin(), personOld.getOtherExtra2(), forceCopy, l2eAddress, l2eAddress, l2eContact);
		}
	}

	/**
	 * Kopiert den Vornamen, den Nachnamen, den Akad. Titel und die Anrede
	 * einer Facade in eine andere wenn das entsprechende Feld dort nicht
	 * gefüllt ist.
	 *
	 * @param source
	 * @param target
	 * @param forceCopy Erzwingt das kopieren.
	 * @param copyName Kopiert Namensdaten.
	 */
	public static void copyAddressData(PersonMemberFacade source, PersonMemberFacade target, boolean forceCopy, boolean copyName) {
		assert source != null && target != null;

		if (copyName) {
			if (forceCopy || (
					empty(target.getFirstname()) &&
					empty(target.getLastname()) &&
					empty(target.getTitle()) &&
					empty(target.getSalutation())
					)) {

				target.setFirstname(source.getFirstname());
				target.setLastname(source.getLastname());
				target.setTitle(source.getTitle());
				target.setSalutation(source.getSalutation());
				target.setSalutationFK(source.getSalutationFK());
			}
		}
	}

	/**
	 * Kopiert den Vornamen, den Nachnamen, den Akad. Titel und die Anrede
	 * einer Facade in eine andere wenn die entsprechenden Felder vorher
	 * identisch waren und das Zielfeld nicht verändert wurde.
	 *
	 * @param source
	 * @param target
	 * @param sourceOld
	 * @param targetOld
	 * @param forceCopy Erzwingt das kopieren.
	 * @param copyName Kopiert Namensdaten.
	 */
	public static void copyAddressData(PersonMemberFacade source, PersonMemberFacade target, PersonMemberFacade sourceOld, PersonMemberFacade targetOld, boolean forceCopy, boolean copyName) {
		assert source != null && target != null && sourceOld != null && targetOld != null;

		if (copyName) {
			if (forceCopy || (equal(sourceOld.getFirstname(), targetOld.getFirstname()) && equal(target.getFirstname(), targetOld.getFirstname())))
				target.setFirstname(source.getFirstname());
			if (forceCopy || (equal(sourceOld.getLastname(), targetOld.getLastname()) && equal(target.getLastname(), targetOld.getLastname())))
				target.setLastname(source.getLastname());
			if (forceCopy || (equal(sourceOld.getTitle(), targetOld.getTitle()) && equal(target.getTitle(), targetOld.getTitle())))
				target.setTitle(source.getTitle());
			if (forceCopy || (equal(sourceOld.getSalutation(), targetOld.getSalutation()) && equal(target.getSalutation(), targetOld.getSalutation()))) {
				target.setSalutation(source.getSalutation());
				target.setSalutationFK(source.getSalutationFK());
			}
		}
	}

	/**
	 * Kopiert alle Adress- und Kommunikationsdaten einer AddressFacade
	 * in eine andere, wenn das entsprechende Feld dort nicht gefüllt ist.
	 *
	 * @param source
	 * @param target
	 * @param forceCopy Kopiert auch wenn ihm Zielfeld bereits was steht.
	 * @param copyCompany Kopiert wenn wahr den Firmennamen
	 * @param copyAddress Kopiert wenn wahr entsprechende Adressdaten.
	 * @param copyContact Kopiert wenn wahr entsprechende Kommunikationsdaten.
	 */
	public static void copyAddressData(PersonAddressFacade source, PersonAddressFacade target, boolean forceCopy, boolean copyCompany, boolean copyAddress, boolean copyContact) {
		assert source != null && target != null;

		if (copyCompany) {
			if (forceCopy || empty(target.getCompany()))
				target.setCompany(source.getCompany());
		}
		if (copyAddress) {
			if (forceCopy || empty(target.getStreet()))
				target.setStreet(source.getStreet());
			if (forceCopy || empty(target.getZipCode()))
				target.setZipCode(source.getZipCode());
			if (forceCopy || empty(target.getState()))
				target.setState(source.getState());
			if (forceCopy || empty(target.getCity()))
				target.setCity(source.getCity());
			if (forceCopy || empty(target.getCountry()))
				target.setCountry(source.getCountry());
			if (forceCopy || empty(target.getPOBoxZipCode()))
				target.setPOBoxZipCode(source.getPOBoxZipCode());
			if (forceCopy || empty(target.getPOBox()))
				target.setPOBox(source.getPOBox());
			if (forceCopy || empty(target.getSuffix1()))
				target.setSuffix1(source.getSuffix1());
			if (forceCopy || empty(target.getSuffix2()))
				target.setSuffix2(source.getSuffix2());
		}
		if (copyContact) {
			if (forceCopy || empty(target.getPhone()))
				target.setPhone(source.getPhone());
			if (forceCopy || empty(target.getFax()))
				target.setFax(source.getFax());
			if (forceCopy || empty(target.getMobile()))
				target.setMobile(source.getMobile());
			if (forceCopy || empty(target.getEMail()))
				target.setEMail(source.getEMail());
			if (forceCopy || empty(target.getUrl()))
				target.setUrl(source.getUrl());
		}
	}

	/**
	 * Kopiert alle Adress- und Kommunikationsdaten einer AddressFacade
	 * in eine andere, wenn das entsprechende Feld übereinstimmte und
	 * nun nur in der Quelle verändert worden ist.
	 *
	 * @param source
	 * @param target
	 * @param sourceOld
	 * @param targetOld
	 * @param forceCopy Kopiert auch wenn ihm Zielfeld bereits was steht.
	 * @param copyCompany Kopiert wenn wahr den Firmennamen
	 * @param copyAddress Kopiert wenn wahr entsprechende Adressdaten.
	 * @param copyContact Kopiert wenn wahr entsprechende Kommunikationsdaten.
	 */
	public static void copyAddressData(PersonAddressFacade source, PersonAddressFacade target, PersonAddressFacade sourceOld, PersonAddressFacade targetOld, boolean forceCopy, boolean copyCompany, boolean copyAddress, boolean copyContact) {
		assert source != null && target != null && sourceOld != null && targetOld != null;

		if (copyCompany) {
			if (forceCopy || (equal(sourceOld.getCompany(), targetOld.getCompany()) && equal(target.getCompany(), targetOld.getCompany())))
				target.setCompany(source.getCompany());
		}
		if (copyAddress) {
			if (forceCopy || (equal(sourceOld.getStreet(), targetOld.getStreet()) && equal(target.getStreet(), targetOld.getStreet())))
				target.setStreet(source.getStreet());
			if (forceCopy || (equal(sourceOld.getZipCode(), targetOld.getZipCode()) && equal(target.getZipCode(), targetOld.getZipCode())))
				target.setZipCode(source.getZipCode());
			if (forceCopy || (equal(sourceOld.getState(), targetOld.getState()) && equal(target.getState(), targetOld.getState())))
				target.setState(source.getState());
			if (forceCopy || (equal(sourceOld.getCity(), targetOld.getCity()) && equal(target.getCity(), targetOld.getCity())))
				target.setCity(source.getCity());
			if (forceCopy || (equal(sourceOld.getCountry(), targetOld.getCountry()) && equal(target.getCountry(), targetOld.getCountry())))
				target.setCountry(source.getCountry());
			if (forceCopy || (equal(sourceOld.getPOBoxZipCode(), targetOld.getPOBoxZipCode()) && equal(target.getPOBoxZipCode(), targetOld.getPOBoxZipCode())))
				target.setPOBoxZipCode(source.getPOBoxZipCode());
			if (forceCopy || (equal(sourceOld.getPOBox(), targetOld.getPOBox()) && equal(target.getPOBox(), targetOld.getPOBox())))
				target.setPOBox(source.getPOBox());
			if (forceCopy || (equal(sourceOld.getSuffix1(), targetOld.getSuffix1()) && equal(target.getSuffix1(), targetOld.getSuffix1())))
				target.setSuffix1(source.getSuffix1());
			if (forceCopy || (equal(sourceOld.getSuffix2(), targetOld.getSuffix2()) && equal(target.getSuffix2(), targetOld.getSuffix2())))
				target.setSuffix2(source.getSuffix2());
		}
		if (copyContact) {
			if (forceCopy || (equal(sourceOld.getPhone(), targetOld.getPhone()) && equal(target.getPhone(), targetOld.getPhone())))
				target.setPhone(source.getPhone());
			if (forceCopy || (equal(sourceOld.getFax(), targetOld.getFax()) && equal(target.getFax(), targetOld.getFax())))
				target.setFax(source.getFax());
			if (forceCopy || (equal(sourceOld.getMobile(), targetOld.getMobile()) && equal(target.getMobile(), targetOld.getMobile())))
				target.setMobile(source.getMobile());
			if (forceCopy || (equal(sourceOld.getEMail(), targetOld.getEMail()) && equal(target.getEMail(), targetOld.getEMail())))
				target.setEMail(source.getEMail());
			if (forceCopy || (equal(sourceOld.getUrl(), targetOld.getUrl()) && equal(target.getUrl(), targetOld.getUrl())))
				target.setUrl(source.getUrl());
		}
	}

    /**
     * Diese Methode setzt die Felder einer {@link PersonMemberFacade} zurück.
     *
     * @param facade zurückzusetzende {@link PersonMemberFacade}
     */
	public static void clearAddressData(PersonMemberFacade facade) {
		facade.setBirthday(null);
		/*
		 * issue 1865
		 * will now be copied, too
		 */
		//facade.setDiplodate(null);
		facade.setDomestic(null);
		facade.setFirstname(null);
		facade.setLanguages(null);
		facade.setLastname(null);
		facade.setNationality(null);
		facade.setNote(null);
		facade.setNoteHost(null);
		facade.setNoteOrga(null);
		facade.setSalutation(null);
		facade.setSalutationFK(null);
		facade.setSex(PersonConstants.SEX_MALE);
		facade.setTitle(null);
	}

    /**
     * Diese Methode setzt die Felder einer {@link PersonAddressFacade} zurück.
     *
     * @param facade zurückzusetzende {@link PersonAddressFacade}
     */
	public static void clearAddressData(PersonAddressFacade facade) {
		facade.setCity(null);
		facade.setCompany(null);
		facade.setCountry(null);
		facade.setEMail(null);
		facade.setFax(null);
		facade.setFunction(null);
		facade.setMobile(null);
		facade.setPhone(null);
		facade.setPOBox(null);
		facade.setPOBoxZipCode(null);
		facade.setStreet(null);
		facade.setSuffix1(null);
		facade.setSuffix2(null);
		facade.setUrl(null);
		facade.setZipCode(null);
		facade.setState(null);
	}

	private static boolean equal(String s1, String s2) {
		return s1 == null ? (s2 == null || s2.length() == 0) : (s1.length() == 0 && (s2 == null || s2.length() == 0)) || s1.equals(s2);
	}

	public static boolean empty(String s) {
		return s == null || s.length() == 0;
	}
}
