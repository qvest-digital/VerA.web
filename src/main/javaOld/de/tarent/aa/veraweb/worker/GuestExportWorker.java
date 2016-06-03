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
package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Location;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.EventURLHandler;
import de.tarent.aa.veraweb.utils.ExportHelper;
import de.tarent.aa.veraweb.utils.PersonURLHandler;
import de.tarent.aa.veraweb.utils.PropertiesReader;
import de.tarent.aa.veraweb.utils.URLGenerator;
import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.request.TcRequest;
import de.tarent.octopus.server.OctopusContext;
import org.apache.log4j.Logger;
import org.evolvis.veraweb.util.DelegationPasswordGenerator;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Diese Octopus-Worker-Klasse exportiert Dokumenttypen einer Gästeliste
 * in ein OpenDocument-SpreadSheet.
 *
 * @author Christoph
 */
public class GuestExportWorker {
    //
    // Octopus-Aktionen
    //
    /** Octopus-Eingabeparameter für {@link #calc(OctopusContext, Integer)} */
    public static final String INPUT_calc[] = { "doctype" };
    /** Octopus-Eingabeparameter für {@link #calc(OctopusContext, Integer)} */
    public static final boolean MANDATORY_calc[] = { false };
    /** Octopus-Ausgabeparameter für {@link #calc(OctopusContext, Integer)} */
    public static final String OUTPUT_calc = "exportCalc";

	public static final String EMPTY_FIELD_VALUE = "-";

    /** Logger dieser Klasse */
	private final Logger logger = Logger.getLogger(getClass());

	private boolean isOnlineRegistrationActive = true;

	private PropertiesReader propertiesReader = new PropertiesReader();

	/**
	 * <p>
	 * Berechnet wieviele Dokumenttypen einer Gästeliste exportiert
	 * werden sollen und können.
	 * </p>
	 *
	 * @param octopusContext OctopusContext
	 * @param doctypeid Dokumenttyp der exportiert werden soll.
	 */
	public Map calc(OctopusContext octopusContext, Integer doctypeid) throws BeanException, IOException {
		final Database database = new DatabaseVeraWeb(octopusContext);
		final Event event = (Event) octopusContext.contentAsObject("event");
		final GuestSearch search = (GuestSearch) octopusContext.contentAsObject("search");
		final List selection = (List) octopusContext.sessionAsObject("selectionGuest");

		if (doctypeid == null) {
			doctypeid = setDoctypeId(octopusContext, doctypeid);
		}
		if (doctypeid == null) {
			return null;
		}

		Integer total = null;
		Integer available = null;

		if (selection != null && selection.size() > 0) {
			total = new Integer(selection.size());
		} else if (event != null && event.id != null && event.id.intValue() != 0) {
			final WhereList where = new WhereList();
			search.addGuestListFilter(where);

			total = database.getCount(
				database.getCount("Guest").
				where(where)
			);
		}
		octopusContext.setContent("extension", ExportHelper.getExtension(SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_CSV_DOCUMENT).getFileExtension()));
		final Map result = new HashMap();
		result.put("doctype", doctypeid);
		result.put("total", total);
		result.put("available", available);
		result.put("sessionId", octopusContext.requestAsString(TcRequest.PARAM_SESSION_ID));
		return result;
	}

	/**
	 * Schreibt die überschriften des Export-Dokumentes.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 */
	protected void exportHeader(SpreadSheet spreadSheet, OctopusContext cntx, Event event) {

		checkIfOnlineRegistrationIsAvailable(cntx);
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell("Dokument_Typ"); // Name des Dokument-Typs
		spreadSheet.addCell("Freitextfeld");
		spreadSheet.addCell("Partner_Freitextfeld");
		spreadSheet.addCell("Verbinder");

		spreadSheet.addCell("Anschrift"); // P, G oder S - Vorgabe aus Person, überschreibbar
		spreadSheet.addCell("Zeichensatz"); // L, F1 oder F2 - Vorgabe aus Person, überschreibbar

		spreadSheet.addCell("Funktion");
		spreadSheet.addCell("Anrede");
		spreadSheet.addCell("Akad_Titel");
		spreadSheet.addCell("Vorname");
		spreadSheet.addCell("Nachname");

		spreadSheet.addCell("Partner_Anrede");
		spreadSheet.addCell("Partner_Akad_Titel");
		spreadSheet.addCell("Partner_Vorname");
		spreadSheet.addCell("Partner_Nachname");

		spreadSheet.addCell("PLZ");
		spreadSheet.addCell("Ort");
		spreadSheet.addCell("Strasse");
		spreadSheet.addCell("Land");
		spreadSheet.addCell("Adresszusatz_1");
		spreadSheet.addCell("Adresszusatz_2");

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Geburtsort");
		spreadSheet.addCell("Telefon");
		spreadSheet.addCell("Fax");
		spreadSheet.addCell("Email");
		spreadSheet.addCell("WWW");
		spreadSheet.addCell("Mobil");
		spreadSheet.addCell("Firma");
		spreadSheet.addCell("Postfach_Nr");
		spreadSheet.addCell("Postfach_PLZ");

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell("Kategorie"); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell("Kategorie_Rang"); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell("Rang"); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Arbeitsbereich");
		spreadSheet.addCell("Reserve"); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		spreadSheet.addCell("Status"); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell("Tisch");
		spreadSheet.addCell("Platz");
		spreadSheet.addCell("Lfd_Nr");
		spreadSheet.addCell("Farbe"); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell("Inland"); // Ja / Nein
		spreadSheet.addCell("Sprachen");
		spreadSheet.addCell("Geschlecht"); // M oder F
		spreadSheet.addCell("Nationalität");
		spreadSheet.addCell("Hinweis_Gastgeber");
		spreadSheet.addCell("Hinweis_Orgateam");

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		spreadSheet.addCell("Partner_Status"); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell("Partner_Tisch");
		spreadSheet.addCell("Partner_Platz");
		spreadSheet.addCell("Partner_Lfd_Nr");
		spreadSheet.addCell("Partner_Farbe"); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell("Partner_Inland"); // Ja / Nein
		spreadSheet.addCell("Partner_Sprachen");
		spreadSheet.addCell("Partner_Geschlecht"); // M oder F
		spreadSheet.addCell("Partner_Nationalität");
		spreadSheet.addCell("Partner_Hinweis_Gastgeber");
		spreadSheet.addCell("Partner_Hinweis_Orgateam");

		//
		// Sonstiges
		//
		spreadSheet.addCell("Anzahl_Zusagen");
		spreadSheet.addCell("Anzahl_Absagen");
		spreadSheet.addCell("Anzahl_Offene");

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell("Anzahl_Teilnahmen");
		spreadSheet.addCell("Anzahl_Gaeste_auf_Platz");
		spreadSheet.addCell("Anzahl_Gaeste_auf_Reserve");
		spreadSheet.addCell("Veranstaltungsname");
		spreadSheet.addCell("Veranstaltung_Beginn");
		spreadSheet.addCell("Veranstaltung_Ende");
		if (isOnlineRegistrationActive) {
			spreadSheet.addCell("Veranstaltung URL");
		}
		spreadSheet.addCell("Veranstaltungsort_Beschreibung");
		spreadSheet.addCell("Veranstaltungsort_Ansprechpartner");
		spreadSheet.addCell("Veranstaltungsort_Straße");
		spreadSheet.addCell("Veranstaltungsort_PLZ");
		spreadSheet.addCell("Veranstaltungsort_Ort");
		spreadSheet.addCell("Veranstaltungsort_Telefonnummer");
		spreadSheet.addCell("Veranstaltungsort_Faxnummer");
		spreadSheet.addCell("Veranstaltungsort_E-Mailadresse ");
		spreadSheet.addCell("Veranstaltungsort_Bemerkungsfeld");
		spreadSheet.addCell("Veranstaltungsort_URL");
		spreadSheet.addCell("Veranstaltungsort_GPS-Daten");
		spreadSheet.addCell("Veranstaltungsort_Raumnummer");

		if(isOnlineRegistrationActive) {
			//OSIAM Login
			spreadSheet.addCell("Anmeldename");
			spreadSheet.addCell("Passwort");
			spreadSheet.addCell("URL");
			if (!event.login_required) {
				spreadSheet.addCell("Anmeldung URL");
			}
		}
		spreadSheet.addCell("Bemerkung");
	}

	/**
	 * Export die Gast- und Partner Daten in eine Zeile.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 * @throws IOException Wenn die Generierung von URL fur Delegation fehlschlug.
	 * @throws BeanException
	 */
	protected void exportBothInOneLine(SpreadSheet spreadSheet, Event event, Location location, boolean showA, boolean showB, Map guest, Map data, OctopusContext cntx) throws IOException, BeanException {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs

        final String textA = (String)guest.get("textfield");
        final String textB = (String)guest.get("textfield_p");
		if (showA) {
			spreadSheet.addCell(textA);
		} else {
			spreadSheet.addCell(null);
		}
		if (showB) {
			spreadSheet.addCell(textB);
		} else {
			spreadSheet.addCell(null);
		}
		if (showA && showB && textA != null && textA.length() != 0 && textB != null && textB.length() != 0) {
			spreadSheet.addCell(guest.get("textjoin"));
		} else {
			spreadSheet.addCell(null);
		}

		spreadSheet.addCell(getAddresstype((Integer)guest.get("addresstype")));
		spreadSheet.addCell(getLocale((Integer)guest.get("locale")));

		if (showA) {
			spreadSheet.addCell(guest.get("function"));
			spreadSheet.addCell(guest.get("salutation"));
			spreadSheet.addCell(guest.get("titel"));
			spreadSheet.addCell(guest.get("firstname"));
			spreadSheet.addCell(guest.get("lastname"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		if (showB) {
			spreadSheet.addCell(guest.get("salutation_p"));
			spreadSheet.addCell(guest.get("titel_p"));
			spreadSheet.addCell(guest.get("firstname_p"));
			spreadSheet.addCell(guest.get("lastname_p"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		spreadSheet.addCell(guest.get("zipcode"));
		spreadSheet.addCell(guest.get("city"));
		spreadSheet.addCell(guest.get("street"));
		spreadSheet.addCell(guest.get("country"));
		spreadSheet.addCell(guest.get("suffix1"));
		spreadSheet.addCell(guest.get("suffix2"));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("birthplace_a_e1"));
		spreadSheet.addCell(guest.get("fon"));
		spreadSheet.addCell(guest.get("fax"));
		spreadSheet.addCell(guest.get("mail"));
		spreadSheet.addCell(guest.get("www"));
		spreadSheet.addCell(guest.get("mobil"));
		spreadSheet.addCell(guest.get("company"));
		spreadSheet.addCell(guest.get("pobox"));
		spreadSheet.addCell(guest.get("poboxzipcode"));

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell(guest.get("catrang")); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(guest.get("guestrang")); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("workareaname"));
		spreadSheet.addCell(getReserve((Integer)guest.get("reserve"))); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		if (showA) {
			spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus"))); // 0 = Offen, 1 = Zusage, 2 = Absage
			spreadSheet.addCell(guest.get("tableno"));
			spreadSheet.addCell(guest.get("seatno"));
			spreadSheet.addCell(guest.get("orderno"));
			spreadSheet.addCell(getColor((Integer)guest.get("color1"))); // Verweiss auf Farbe die verwendet werden soll.
			spreadSheet.addCell(getDomestic((Integer)guest.get("domestic"))); // Inland Ja / Nein
			spreadSheet.addCell(guest.get("language"));
			spreadSheet.addCell(getGender((String)guest.get("gender"))); // M oder F
			spreadSheet.addCell(guest.get("nationality"));
			spreadSheet.addCell(guest.get("notehost"));
			spreadSheet.addCell(guest.get("noteorga"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(getColor(null));
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		if (showB) {
			spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus_p"))); // 0 = Offen, 1 = Zusage, 2 = Absage
			spreadSheet.addCell(guest.get("tableno_p"));
			spreadSheet.addCell(guest.get("seatno_p"));
			spreadSheet.addCell(guest.get("orderno_p"));
			spreadSheet.addCell(getColor((Integer)guest.get("color2"))); // Verweiss auf Farbe die verwendet werden soll.
			spreadSheet.addCell(getDomestic((Integer)guest.get("domestic_p"))); // Inland Ja / Nein
			spreadSheet.addCell(guest.get("language_p"));
			spreadSheet.addCell(getGender((String)guest.get("gender_p"))); // M oder F
			spreadSheet.addCell(guest.get("nationality_p"));
			spreadSheet.addCell(guest.get("notehost_p"));
			spreadSheet.addCell(guest.get("noteorga_p"));
		} else {
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(getColor(null));
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
			spreadSheet.addCell(null);
		}

		//
		// Sonstiges
		//
		spreadSheet.addCell(data.get("zusagen"));
		spreadSheet.addCell(data.get("absagen"));
		spreadSheet.addCell(data.get("offenen"));

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(data.get("teilnahmen"));
		spreadSheet.addCell(data.get("platz"));
		spreadSheet.addCell(data.get("reserve"));
		spreadSheet.addCell(event.shortname);
		spreadSheet.addCell(event.begin);
		spreadSheet.addCell(event.end);

		addLocationCells(spreadSheet, location);
		if (isOnlineRegistrationActive) {
			addCredentialsDataColumns(spreadSheet, guest, event, cntx);
		}
		spreadSheet.addCell(event.note);
	}

	private void addLocationCells(SpreadSheet spreadSheet, Location location) {
		if (location != null) {
			spreadSheet.addCell(location.name);
			spreadSheet.addCell(location.contactperson);
			spreadSheet.addCell(location.address);
			spreadSheet.addCell(location.zip);
			spreadSheet.addCell(location.location);
			spreadSheet.addCell(location.callnumber);
			spreadSheet.addCell(location.faxnumber);
			spreadSheet.addCell(location.email);
			spreadSheet.addCell(location.comment);
			spreadSheet.addCell(location.url);
			spreadSheet.addCell(location.gpsdata);
			spreadSheet.addCell(location.roomnumber);
		} else {
			for(int i = 0; i != 12; i++) spreadSheet.addCell(null);
		}
	}

	private void checkIfOnlineRegistrationIsAvailable(OctopusContext cntx) {
		this.isOnlineRegistrationActive = Boolean.valueOf(cntx.getContextField("online-registration.activated").toString());
	}

	/**
	 * Columns username, password, url for delegations and persons.
	 *
	 * @param spreadSheet SpreadSheet
	 * @param guest Map with guests
	 * @param event Event
	 * @throws IOException
	 * @throws BeanException
	 */
	private void addCredentialsDataColumns(SpreadSheet spreadSheet, Map guest, Event event, OctopusContext cntx) throws IOException, BeanException {
		String password = EMPTY_FIELD_VALUE;
		Object username = EMPTY_FIELD_VALUE;
		String url = EMPTY_FIELD_VALUE;
		String directAccessURL = EMPTY_FIELD_VALUE;

		Database database = new DatabaseVeraWeb(cntx);

		String category = (String) guest.get("catname");
		if (category == null) { category = ""; }

		if(guestIsDelegationAndHasOsiamLogin(guest, category)) {

			url = generateLoginUrl(guest);

			password = generatePasswordForCompany(guest, event, password, database);

			username = guest.get("osiam_login");
		}
		else {
			username = guest.get("osiam_login");
			url = getResetPasswordURL(guest, cntx);
			if (!event.login_required) {
				directAccessURL = getLoginUUIDAndDirectAccessURL(guest, event, directAccessURL);
			}
		}

		spreadSheet.addCell(username);
		spreadSheet.addCell(password);
		spreadSheet.addCell(url);
		if (!event.login_required) {
			spreadSheet.addCell(directAccessURL);
		}
	}

//	private void updateGuestWithLoginUUID(Integer id, OctopusContext octopusContext, String loginUUID) throws BeanException {
//		Database database = new DatabaseVeraWeb(octopusContext);
//		TransactionContext transactionContext = database.getTransactionContext();
//
//		Update updateStatement = SQL.Update(database);
//		updateStatement.table("veraweb.tguest");
//		updateStatement.update("login_required_uuid", loginUUID);
//		updateStatement.whereAndEq("pk", id);
//
//		transactionContext.execute(updateStatement);
//	}
//
//	private String generateUUID() {
//		UUID uuid = UUID.randomUUID();
//		return uuid.toString();
//	}

	private Integer setDoctypeId(OctopusContext cntx, Integer doctypeid) {
        final List list = (List)cntx.contentAsObject("allEventDoctype");
        Iterator it = list.iterator();
        if (it.hasNext()) {
            doctypeid = (Integer)((Map)it.next()).get("doctype");
        }
        for (it = list.iterator(); it.hasNext(); ) {
            final Map data = (Map)it.next();
            if (((Integer)(data).get("isdefault")).intValue() == 1) {
                doctypeid = (Integer)data.get("doctype");
                break;
            }
        }
        return doctypeid;
    }

	private boolean guestIsDelegationAndHasOsiamLogin(Map guest, String category) {
        return guest.containsKey("delegation") &&
                guest.get("delegation") != null &&
                guest.get("delegation").toString().length() > 0 &&
                guest.containsKey("osiam_login") &&
                guest.get("osiam_login") != null &&
                guest.get("osiam_login").toString().length() > 0 &&
                !category.equals("Pressevertreter");
    }

    private String getLoginUUIDAndDirectAccessURL(Map guest, Event event, String directAccessURL) {
        String loginUUID = null;
        if (guest.get("login_required_uuid") != null) {
            loginUUID = guest.get("login_required_uuid").toString();
            directAccessURL = generateEventUrlWithoutLogin(event.hash, loginUUID);
        }
        return directAccessURL;
    }

    private String generatePasswordForCompany(Map guest, Event event, String password, Database database)
            throws BeanException, IOException {
        final Select select = database.getSelect("Person");
        select.joinLeftOuter("tguest", "tperson.pk", "tguest.fk_person");
        select.whereAndEq("tperson.pk", guest.get("fk_person"));

        final ResultList resultList = database.getList(select, database);

        for (int i = 0; i < resultList.size(); i++) {
            final ResultMap resultMap = (ResultMap) resultList.get(i);

            if(resultMap.get("iscompany").toString() != null && resultMap.get("iscompany").toString().equals("t")) {
                password = generatePassword(event, guest);
            }
        }
        return password;
    }

	private String getResetPasswordURL(Map guest, OctopusContext cntx) throws IOException, BeanException {
		String url;
		try {
            url = getURLLinkUUIDDataFromGuest(Integer.valueOf(guest.get("fk_person").toString()), cntx);
        } catch (NumberFormatException e) {
            logger.error("NumberFormatException - getting URL/LinkUUID of the person:" + guest.get("fk_person").toString(), e);
            throw e;
        } catch (BeanException e) {
            logger.error("BeanException - getting URL/LinkUUID of the person:" + guest.get("fk_person").toString(), e);
            throw e;
        }
		return url;
	}

	/**
	 * Getting URL to reseting password
	 *
	 * @param personId
	 * @return String the url
	 * @throws IOException
	 * @throws BeanException
	 */
	private String getURLLinkUUIDDataFromGuest(Integer personId, OctopusContext cntx) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);

		String url = null;
		WhereList whereCriterias = new WhereList();

        whereCriterias.addAnd(new Where("personid", personId, "="));
        whereCriterias.addAnd(new Where("linktype", "passwordreset", "="));

		final Select select2 = SQL.Select(database);
	      select2.from("link_uuid");
	      select2.select("uuid");
	      select2.where(whereCriterias);
		List list = database.getBeanList("LinkUUID", select2);

		if (!list.isEmpty() && database.getField("uuid") != null) {
			String uuid = database.getField("uuid").toString();
			PersonURLHandler pHandler = new PersonURLHandler();
			url = pHandler.generateResetPasswordUrl(uuid);
		}
		return url;
	}

	private String generatePassword(Event event, Map guest) {
        final String shortName = event.get("shortname").toString();
        final Date begin = event.begin;
        String companyName = "";
        if(!guest.get("company_a_e1").equals(null) && !guest.get("company_a_e1").equals("")) {
        	companyName = guest.get("company_a_e1").toString();
        }
        return new DelegationPasswordGenerator().generatePassword(shortName, begin, companyName);
	}

    private String generateLoginUrl(Map guest) throws IOException {
		final PropertiesReader propertiesReader = new PropertiesReader();
		final Properties properties = propertiesReader.getProperties();
		final URLGenerator url = new URLGenerator(properties);
		return url.getUrlForDelegation() + guest.get("delegation");
	}

	private String generateEventUrlWithoutLogin(String eventHash, String guestLoginUUID) {
		final URLGenerator urlGenerator = getUrlGenerator();
		return urlGenerator.getUrlForFreeVisitors() + eventHash + "/" + guestLoginUUID;
	}


	/**
	 * Export ausschließlich die Gast-Daten in eine Zeile.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 * @throws IOException Wenn die Generierung von URL fur Delegation fehlschlug.
	 * @throws BeanException
	 */
	protected void exportOnlyPerson(SpreadSheet spreadSheet, Event event, Location location, Map guest, Map data, Boolean isPressStaff, OctopusContext cntx) throws IOException, BeanException {
		checkIfOnlineRegistrationIsAvailable(cntx);
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		spreadSheet.addCell(guest.get("textfield"));
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(getAddresstype((Integer)guest.get("addresstype")));
		spreadSheet.addCell(getLocale((Integer)guest.get("locale")));

		spreadSheet.addCell(guest.get("function"));
		spreadSheet.addCell(guest.get("salutation"));
		spreadSheet.addCell(guest.get("titel"));
		spreadSheet.addCell(guest.get("firstname"));
		spreadSheet.addCell(guest.get("lastname"));

		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(guest.get("zipcode"));
		spreadSheet.addCell(guest.get("city"));
		spreadSheet.addCell(guest.get("street"));
		spreadSheet.addCell(guest.get("country"));
		spreadSheet.addCell(guest.get("suffix1"));
		spreadSheet.addCell(guest.get("suffix2"));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("birthplace_a_e1"));
		spreadSheet.addCell(guest.get("fon"));
		spreadSheet.addCell(guest.get("fax"));
		spreadSheet.addCell(guest.get("mail"));
		spreadSheet.addCell(guest.get("www"));
		spreadSheet.addCell(guest.get("mobil"));
		spreadSheet.addCell(guest.get("company"));
		spreadSheet.addCell(guest.get("pobox"));
		spreadSheet.addCell(guest.get("poboxzipcode"));

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell(guest.get("catrang")); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(guest.get("guestrang")); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("workareaname"));
		spreadSheet.addCell(getReserve((Integer)guest.get("reserve"))); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus"))); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(guest.get("tableno"));
		spreadSheet.addCell(guest.get("seatno"));
		spreadSheet.addCell(guest.get("orderno"));
		spreadSheet.addCell(getColor((Integer)guest.get("color1"))); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(getDomestic((Integer)guest.get("domestic"))); // Inland Ja / Nein
		spreadSheet.addCell(guest.get("language"));
		spreadSheet.addCell(getGender((String)guest.get("gender"))); // M oder F
		spreadSheet.addCell(guest.get("nationality"));
		spreadSheet.addCell(guest.get("notehost"));
		spreadSheet.addCell(guest.get("noteorga"));

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(null); // Inland Ja / Nein
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // M oder F
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		//
		// Sonstiges
		//
		spreadSheet.addCell(data.get("zusagen"));
		spreadSheet.addCell(data.get("absagen"));
		spreadSheet.addCell(data.get("offenen"));

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(data.get("teilnahmen"));
		spreadSheet.addCell(data.get("platz"));
		spreadSheet.addCell(data.get("reserve"));
		spreadSheet.addCell(event.shortname);
		spreadSheet.addCell(event.begin);
		spreadSheet.addCell(event.end);
		if (isOnlineRegistrationActive) {
			if ((guest.get("delegation")==null || guest.get("delegation").equals("")) && !isPressStaff) {
				final EventURLHandler eventURLHandler = new EventURLHandler();
				spreadSheet.addCell(eventURLHandler.generateEventUrl(event));
			} else {
				spreadSheet.addCell("");
			}
		}

		addLocationCells(spreadSheet, location);
		if(isOnlineRegistrationActive) {
			addCredentialsDataColumns(spreadSheet, guest, event, cntx);
			// Updating username in tperson
			updateDelegationUsername(cntx, guest.get("osiam_login"), (Integer)guest.get("fk_person"));
		}

		spreadSheet.addCell(event.note);
	}


	/**
	 * Checking if one guest is from the press staff
	 *
	 * @return Boolean
	 * @throws IOException
	 * @throws BeanException
	 */
	private void updateDelegationUsername(OctopusContext cntx, Object username, Integer personId) throws BeanException {
		final Database database = new DatabaseVeraWeb(cntx);
		if (username != null) {
			updatePerson(database, username, personId);
		} else {
			updatePerson(database, null, personId);
		}
	}

	private void updatePerson(final Database database, final Object username, final Integer personId) throws BeanException {

		final TransactionContext transactionContext = database.getTransactionContext();
		transactionContext.execute(SQL.Update( database ).
				table("veraweb.tperson").
				update("username", username).
				where(Expr.equal("pk", personId)));
		transactionContext.commit();
	}

	/**
	 * Export ausschließlich die Partner-Daten in eine Zeile.
	 *
	 * @param spreadSheet In das geschrieben werden soll.
	 * @param event Event das exportiert wird.
	 * @param guest Map mit den Gastdaten.
	 * @param data Zusatzinformationen.
	 * @throws IOException
	 * @throws BeanException
	 */
	protected void exportOnlyPartner(SpreadSheet spreadSheet, Event event, Location location, boolean showA, boolean showB, Map guest, Map data, OctopusContext cntx) throws IOException, BeanException {
		//
		// Gast spezifische Daten
		//
		spreadSheet.addCell(data.get("doctype")); // Name des Dokument-Typs
		spreadSheet.addCell(guest.get("textfield_p"));
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(getAddresstype((Integer)guest.get("addresstype")));
		spreadSheet.addCell(getLocale((Integer)guest.get("locale")));

		spreadSheet.addCell(null);
		spreadSheet.addCell(guest.get("salutation_p"));
		spreadSheet.addCell(guest.get("titel_p"));
		spreadSheet.addCell(guest.get("firstname_p"));
		spreadSheet.addCell(guest.get("lastname_p"));

		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		spreadSheet.addCell(guest.get("zipcode"));
		spreadSheet.addCell(guest.get("city"));
		spreadSheet.addCell(guest.get("street"));
		spreadSheet.addCell(guest.get("country"));
		spreadSheet.addCell(guest.get("suffix1"));
		spreadSheet.addCell(guest.get("suffix2"));

		/*
		 * modified to support birthplace as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(""); // birthplace does not exist for partner
		spreadSheet.addCell(guest.get("fon"));
		spreadSheet.addCell(guest.get("fax"));
		spreadSheet.addCell(guest.get("mail"));
		spreadSheet.addCell(guest.get("www"));
		spreadSheet.addCell(guest.get("mobil"));
		spreadSheet.addCell(guest.get("company"));
		spreadSheet.addCell(guest.get("pobox"));
		spreadSheet.addCell(guest.get("poboxzipcode"));

		//
		// Kategorie spezifische Daten, wenn nach Kategorie gefilter wurde.
		//
		spreadSheet.addCell(guest.get("catname")); // Verweis auf Kategorie, die zur Auswahl führte
		spreadSheet.addCell(guest.get("catrang")); // Der Rang der Kategorie innerhalb der Kategorien
		spreadSheet.addCell(guest.get("guestrang")); // Der Rang der Person innerhalb der Kategorie

		/*
		 * modified to support workareas as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(guest.get("workareaname"));
		spreadSheet.addCell(getReserve((Integer)guest.get("reserve"))); // 0 = Tisch, 1 = Reservce

		//
		// Veranstaltungsspezifische Attribute für Person
		//
		spreadSheet.addCell(getStatus((Integer)guest.get("invitationstatus_p"))); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(guest.get("tableno_p"));
		spreadSheet.addCell(guest.get("seatno_p"));
		spreadSheet.addCell(guest.get("orderno_p"));
		spreadSheet.addCell(getColor((Integer)guest.get("color2"))); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(getDomestic((Integer)guest.get("domestic_p"))); // Inland Ja / Nein
		spreadSheet.addCell(guest.get("language_p"));
		spreadSheet.addCell(getGender((String)guest.get("gender_p"))); // M oder F
		spreadSheet.addCell(guest.get("nationality_p"));
		spreadSheet.addCell(guest.get("notehost_p"));
		spreadSheet.addCell(guest.get("noteorga_p"));

		//
		// Veranstaltungsspezifische Attribute für Partner der Person
		//
		spreadSheet.addCell(null); // 0 = Offen, 1 = Zusage, 2 = Absage
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // Verweiss auf Farbe die verwendet werden soll.
		spreadSheet.addCell(null); // Inland Ja / Nein
		spreadSheet.addCell(null);
		spreadSheet.addCell(null); // M oder F
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);
		spreadSheet.addCell(null);

		//
		// Sonstiges
		//
		spreadSheet.addCell(data.get("zusagen"));
		spreadSheet.addCell(data.get("absagen"));
		spreadSheet.addCell(data.get("offenen"));

		/*
		 * modified to support fourth invitation state as per change request for version 1.2.0
		 * cklein
		 * 2008-02-26
		 */
		spreadSheet.addCell(data.get("teilnahmen"));
		spreadSheet.addCell(data.get("platz"));
		spreadSheet.addCell(data.get("reserve"));
		spreadSheet.addCell(event.shortname);
		spreadSheet.addCell(event.begin);
		spreadSheet.addCell(event.end);

		addLocationCells(spreadSheet, location);
		if (isOnlineRegistrationActive) {
			addCredentialsDataColumns(spreadSheet, guest, event, cntx);
		}

		spreadSheet.addCell(event.note);
	}

    //
    // Öffentliche Hilfsmethoden
    //
    /** Diese Methode liefert eine String-Darstellung für einen Reserve-Wert */
	public static String getReserve(Integer reserve) {
		return reserve == null ? null : (reserve.intValue() == 1 ? "Reserve" : "Tisch");
	}

    /** Diese Methode liefert eine String-Darstellung eines Gender-Werts */
	public static String getGender(String gender) {
		return gender;
	}

    /** Diese Methode liefert eine String-Darstellung eines Domestic-Werts */
	public static String getDomestic(Integer domestic) {
		return domestic == null ? null : (domestic.intValue() == 1 ? "Ja" : "Nein");
	}

    /**
     * Diese Methode liefert zu einem Integer den um 1 erhöhten Wert oder
     * 0, falls <code>null</code> übergeben worden war.
     */
	public static Integer getColor(Integer color) {
		return new Integer(color == null ? 0 : color.intValue() + 1);
	}

	/**
	 * Anschrifttyp als P,G,S zurückgeben.
	 *
	 * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
	 * Muss auch in anderen Bereichen umgesetzt werden.
	 * Z.B. beim "Neu Laden" in Worker und Template.
	 */
	public static String getAddresstype(Integer addresstype) {
		if (addresstype == null) {
			return null;
		} else if (addresstype.intValue() == 2) {
			return "P";
		} else if (addresstype.intValue() == 3) {
			return "W";
		} else { // addresstype.intValue() == 1
			return "G";
		}
	}

	/**
	 * Zeichensatz als L,F1,F2 zurückgeben.
	 *
	 * Vorgabe aus PersonDoctype, überschreibbar in GuestDoctype
	 * Muss auch in anderen Bereichen umgesetzt werden.
	 * Z.B. beim "Neu Laden" in Worker und Template.
	 */
	public static String getLocale(Integer locale) {
		if (locale == null) {
			return null;
		} else if (locale.intValue() == 2) {
			return "ZS1";
		} else if (locale.intValue() == 3) {
			return "ZS2";
		} else { // locale.intValue() == 1
			return "L";
		}
	}

    /** Diese Methode liefert eine String-Darstellung eines Veranstaltungstyps */
	public static String getType(Integer type) {
		if (type == null || type.intValue() == EventConstants.TYPE_MITPARTNER) {
			return "Mit Partner";
		} else if (type.intValue() == EventConstants.TYPE_OHNEPARTNER) {
			return "Ohne Partner";
		} else { // type.intValue() == EventConstants.TYPE_NURPARTNER
			return "Nur Partner";
		}
	}

    /** Diese Methode liefert eine String-Darstellung eines Einladungsstatus */
	public static String getStatus(Integer status) {
		if (status == null || status.intValue() == 0) {
			return "Offen";
		} else if (status.intValue() == 1) {
			return "Zusage";
		} else if (status.intValue() == 2) {
			return "Absage";
		} else { // status == 3
			/*
			 * modified to support forth invitation state as per change request for version 1.2.0
			 * cklein
			 * 2008-02-26
			 */
			return "Teilnahme";
		}
	}

	private URLGenerator getUrlGenerator() {
		final Properties properties = propertiesReader.getProperties();
		return new URLGenerator(properties);
	}

	/**
	 * Checking if one guest is from the press staff
	 *
	 * @return Boolean
	 * @throws IOException
	 * @throws BeanException
	 */
	private Boolean isPressGuest(OctopusContext cntx, String pressUuid, Integer guestId) throws BeanException, IOException {
		if (pressUuid != null) {
	        final Database database = new DatabaseVeraWeb(cntx);
	        Select select = database.getCount("Guest");
	        select.join("veraweb.tcategorie", "tcategorie.pk","tguest.fk_category");
	        select.join("veraweb.tevent", "tevent.fk_orgunit","tcategorie.fk_orgunit");
	        select.where(Expr.equal("tcategorie.catname", "Pressevertreter"));
	        select.whereAnd(Expr.equal("tguest.pk", guestId));
	        select.whereAnd(Expr.equal("tevent.mediarepresentatives", pressUuid));

	        Integer i = database.getCount(select);
	        if (i.equals(1)) {
	        	return true;
	        }
		}
		return false;
	}

}
