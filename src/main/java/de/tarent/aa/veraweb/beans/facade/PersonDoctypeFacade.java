package de.tarent.aa.veraweb.beans.facade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.Salutation;
import de.tarent.aa.veraweb.beans.SalutationDoctype;
import de.tarent.aa.veraweb.worker.ConfigWorker;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Hilft beim Erstellen von Dokumenttypen und insbesondere beim
 * Erstellen der Freitextfelder und des Freitextfeld-Verbinders.
 * 
 * @author Christoph Jerolimov
 */
public class PersonDoctypeFacade {
	/** Alle Dokumenttypen werden neu erzeugt, ggf. alte überschrieben! */
	public static final String CREATEDOCTYPE_ALL = "all";
	/** Alle nicht existierenden Dokumenttypen werden erzeugt. */
	public static final String CREATEDOCTYPE_NEW = "new";
	/** Wie 'new', jedoch nur für Dokumenttypen mit dem Flag 99. */
	public static final String CREATEDOCTYPE_FLAG = "flag:";
	/** Die Dokumenttypen werden nicht automatisch erzeugt. */
	public static final String CREATEDOCTYPE_NONE = "none";

	/** Wird eingefügt wenn sowohl das Feld davor als auch danach NICHT leer sind. */
	public static final String CREATEDOCTYPE_BOTH = "BOTH:";
	/** Wird eingefügt wenn das Feld davor NICHT leer ist. */
	public static final String CREATEDOCTYPE_PREV = "PREV:";
	/** Wird eingefügt wenn das Feld danach NICHT leer ist. */
	public static final String CREATEDOCTYPE_NEXT = "NEXT:";

	protected final OctopusContext cntx;
	protected final Database database;
	protected final List doctypeFormatA;
	protected final List doctypeFormatB;

	protected Person person;
	protected PersonMemberFacade memberFacade;
	protected PersonAddressFacade addressFacade;

	protected Integer doctypeId;

	/**
	 * Konstruktor zur verwenden aus einem Octopus-Worker herraus.
	 * 
	 * @param cntx Octopus-Context
	 * @param person Person-Eintrag, darf null sein.
	 */
	public PersonDoctypeFacade(OctopusContext cntx, Person person) {
		this.cntx = cntx;
		this.database = new DatabaseVeraWeb(this.cntx);
		this.person = person != null ? person : new Person();
		this.doctypeFormatA = cntx.moduleConfig().getParamAsList("createDoctypeFormat");
		List format = cntx.moduleConfig().getParamAsList("createDoctypeFormatPartner");
		this.doctypeFormatB = format != null ? format : doctypeFormatA;
	}

	/**
	 * Konstruktor zum verwenden aus einer Testklasse herraus.
	 * 
	 * @param person Person-Eintrag, darf null sein.
	 * @param doctypeFormatA
	 * @param doctypeFormatB
	 */
	public PersonDoctypeFacade(Person person, List doctypeFormatA, List doctypeFormatB) {
		this.cntx = null;
		this.database = null;
		this.person = person != null ? person : new Person();
		this.doctypeFormatA = doctypeFormatA;
		this.doctypeFormatB = doctypeFormatB != null ? doctypeFormatB : doctypeFormatA;
	}

	/**
	 * Setzt die Member und AddressFacade.
	 * 
	 * @param addresstype
	 * @param locale
	 * @param hauptperson
	 */
	public void setFacade(Integer addresstype, Integer locale, boolean hauptperson) {
		memberFacade = person.getMemberFacade(hauptperson, locale);
		addressFacade = person.getAddressFacade(addresstype, locale);
	}

	/**
	 * Gibt einen Freitext zurück.
	 * 
	 * @param doctype PK eines Dokumenttypens
	 * @param addresstype Addresstype (1 = Privat, ...) - default 2
	 * @param locale Locale (1 = Latein, ...) - default 1
	 * @param hauptperson (true = Hauptperson, false = Partner)
	 * @return Freitextfeld
	 * @throws BeanException
	 * @throws IOException
	 */
	public String getFreitext(Integer doctype, Integer addresstype, Integer locale, boolean hauptperson) throws BeanException, IOException {
		doctypeId = doctype;
		setFacade(addresstype, locale, hauptperson);
		if (isEmpty(memberFacade)) {
			setFacade(
					new Integer(PersonConstants.ADDRESSTYPE_BUSINESS),
					new Integer(PersonConstants.LOCALE_LATIN), hauptperson);
			if (isEmpty(memberFacade))
				return "";
		}
		return getFreitext(hauptperson ? doctypeFormatA : doctypeFormatB);
	}

	/**
	 * Gibt einen Freitext-Verbinder zurück.
	 * 
	 * @return Freitextfeld-Verbinder
	 */
	public String getFreitextVerbinder(Integer doctype, Integer addresstype, Integer locale) {
		PersonMemberFacade main = person.getMemberFacade(true, locale);
		PersonMemberFacade partner = person.getMemberFacade(false, locale);
		if (isEmpty(main) || isEmpty(partner))
			return "";
		
		String verbinder = ConfigWorker.getString(cntx, "freitextfeldverbinder");
		if (verbinder != null && verbinder.length() != 0)
			return verbinder;
		return "und";
	}

	/**
	 * Erstellt ein Freitextfeld eines Dokumenttypens.
	 * 
	 * @return Liste mit 'richtigen' Inhalten.
	 */
	protected String getFreitext(List params) throws BeanException, IOException {
		List output = new ArrayList();
		for (int i = 0; i < params.size(); i++) {
			String key = (String)params.get(i);
			String field = getField(key);
			output.add(field);
		}
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < output.size(); i++) {
			String key = (String)output.get(i);
			if (i == 0) {
				if (key != null && key.length() != 0)
					buffer.append(key);
			} else if (i == output.size() - 1) {
				if (key != null && key.length() != 0)
					buffer.append(key);
			} else {
				if (key != null && key.length() != 0) {
					if (key.length() > 4 && key.charAt(4) == ':') {
						if (key.startsWith(CREATEDOCTYPE_BOTH)) {
							String prev = (String)output.get(i - 1);
							String next = (String)output.get(i + 1);
							if (prev != null && prev.length() != 0 && next != null && next.length() != 0)
								buffer.append(key.substring(CREATEDOCTYPE_BOTH.length()));
						} else if (key.startsWith(CREATEDOCTYPE_PREV)) {
							String prev = (String)output.get(i - 1);
							if (prev != null && prev.length() != 0)
								buffer.append(key.substring(CREATEDOCTYPE_PREV.length()));
						} else if (key.startsWith(CREATEDOCTYPE_NEXT)) {
							String next = (String)output.get(i + 1);
							if (next != null && next.length() != 0)
								buffer.append(key.substring(CREATEDOCTYPE_NEXT.length()));
						} else {
							buffer.append(key);
						}
					} else {
						buffer.append(key);
					}
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * Gibt zu einem übergebenen Key einen entsprechenden Eintrag
	 * aus der übergebenen Person oder der Facade zurück.
	 * Wenn die übergebene PersonMemberFacade null ist,
	 * werden die Daten der lateinischen Hauptperson verwendet.
	 * Wenn die übergebene PersonAddressFacade null ist,
	 * werden die lateinischen Geschäftsdaten verwendet.
	 * 
	 * @param key Bean-Key
	 * @return Feldinhalt
	 * @throws BeanException
	 * @throws IOException
	 */
	public String getField(String key) throws BeanException, IOException {
		if (person.containsKey(key)) {
			Object v = person.get(key);
			return v == null ? null : v.toString();
		} else if (key.equals("firstname")) {
			return memberFacade.getFirstname();
		} else if (key.equals("lastname")) {
			return memberFacade.getLastname();
		} else if (key.equals("title")) {
			return memberFacade.getTitle();
		} else if (key.equals("salutation")) {
			return getSalutation();
		} else if (key.equals("function")) {
			return addressFacade.getFunction();
		} else if (key.equals("company")) {
			return addressFacade.getCompany();
		} else if (key.equals("street")) {
			return addressFacade.getStreet();
		} else if (key.equals("city")) {
			return addressFacade.getCity();
		} else if (key.equals("zipcode")) {
			return addressFacade.getZipCode();
		} else if (key.equals("country")) {
			return addressFacade.getCountry();
		} else if (key.equals("pobox")) {
			return addressFacade.getPOBox();
		} else if (key.equals("poboxzipcode")) {
			return addressFacade.getPOBoxZipCode();
		} else if (key.equals("suffix1")) {
			return addressFacade.getSuffix1();
		} else if (key.equals("suffix2")) {
			return addressFacade.getSuffix1();
		} else if (key.equals("phone")) {
			return addressFacade.getPhone();
		} else if (key.equals("fax")) {
			return addressFacade.getFax();
		} else if (key.equals("mobile")) {
			return addressFacade.getMobile();
		} else if (key.equals("email")) {
			return addressFacade.getEMail();
		} else if (key.equals("url")) {
			return addressFacade.getUrl();
		}
		return key;
	}

	/**
	 * @return Anrede
	 * @throws BeanException
	 * @throws IOException
	 */
	public String getSalutation() throws BeanException, IOException {
		Integer salutationId = memberFacade.getSalutationFK();
		
		if (salutationId != null && salutationId.intValue() != 0) {
			SalutationDoctype sd = doctypeId == null ? null : (SalutationDoctype)
					database.getBean("SalutationDoctype",
					database.getSelect("SalutationDoctype").
					where(Where.and(
							Expr.equal("fk_salutation", salutationId),
							Expr.equal("fk_doctype", doctypeId))));
			if (sd != null) {
				return sd.text;
			} else {
				Salutation s = (Salutation)
						database.getBean("Salutation",
						database.getSelect("Salutation").
						where(Expr.equal("pk", salutationId)));
				if (s != null) {
					return s.name;
				}
			}
		}
		return memberFacade.getSalutation();
	}

	protected boolean isEmpty(PersonMemberFacade facade) {
		return (memberFacade.getLastname() == null || memberFacade.getLastname().length() == 0) && (
				memberFacade.getFirstname() == null || memberFacade.getFirstname().length() == 0);
	}
}
