package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.GuestSearch;
import de.tarent.aa.veraweb.beans.Location;
import de.tarent.aa.veraweb.utils.DatabaseHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dieser Worker stellt entsprechende Funktionen zur Erstellung von
 * Berichten zu Gästelisten bereit.
 *
 * @author Christoph Jerolimov
 */
@Log4j2
public class GuestReportWorker {
    //
    // Octopus-Aktionen
    //
    /**
     * Octopus-Eingabeparameter für {@link #createReport(OctopusContext)}
     */
    public static final String INPUT_createReport[] = {};
    public static final String INPUT_getLocationForReport[] = {};
    public static final String OUTPUT_getLocationForReport = "location";

    /**
     * Diese Octopus-Aktion erzeugt Daten für einen Bericht. Hierbei wird auf
     * die Werte unter den Schlüsseln "type" (Kategorisierung des Berichts) und
     * "sort1", "sort2" und "sort3" (Sortierkriterien für den Bericht) aus
     * dem Octopus-Request, "event" (die Veranstaltung) und "search" (Kriterien
     * für die Gästesuche) aus dem Octopus-Content, "selectionGuest" (Liste
     * ausgewählter Gäste-Einträge) aus der Session und "freitextfeld2" (ID des
     * Dokumenttyps für das Bericht-Freitextfeld) zurückgegriffen.<br>
     * Für den Bericht stehen zur Kategorisierung folgende Werte zur Verfügung:
     * <ul>
     * <li>"Kat01": Kategorien mit Partner
     * <li>"Kat02": Kategorien mit Telefon und Mobiltelefon
     * <li>"Kat03": Kategorien mit Partner und Fax
     * <li>"Alpha01": Alphabetisch mit Partner, Telefon und Mobiltelefon
     * </ul>
     * Weiterhin stehen folgende Sortierkriterien zur Verfügung:
     * <ul>
     * <li>"category": Kategorie-Rang, Kategorie-Name
     * <li>"country": Land (der geschäftliche Adresse in Latin)
     * <li>"name": Nachname, Vorname (der Hauptperson in Latin)
     * <li>"orderno": laufende Nummer
     * <li>"rank": Gast-Rang
     * <li>"table": Tischnummer
     * <li>"zipcode": Postleitzahl (der geschäftliche Adresse in Latin)
     * </ul>
     * Im Octopus-Content werden unter den Schlüsseln "datum" (das zu verwendende
     * aktuelle Datum), "titel" (Titel mit lesbarem Berichtstyp), "type" (wie oben
     * Kategorisierung des Berichts), "data" (Map mit Gästesummen unter "platz",
     * "reserve",  "all", "offen",  "zusagen" und "absagen"), "kategorie" (Flag für
     * "Kat*"-Typen), "alphabetisch" (Flag für "Alpha*"-Typen) und "guestlist"
     * (Liste der konkreten Gastdaten) Daten für den Bericht bereitgestellt.
     *
     * @param cntx {@link OctopusContext}
     * @throws BeanException beanexception
     */
    public void createReport(OctopusContext cntx) throws BeanException {
        Database database = new DatabaseVeraWeb(cntx);
        Event event = (Event) cntx.contentAsObject("event");
        GuestSearch search = (GuestSearch) cntx.contentAsObject("search");
        List selection = (List) cntx.sessionAsObject("selectionGuest");

        if (event == null) {
            return;
        }

        // type:
        //   kat01 = Kategorien mit Partner
        //   kat02 = Kategorien mit Telefon und Mobiltelefon
        //   kat03 = Kategorien mit Partner und Fax
        //   alpha01 = Alphabetisch mit Partner, Telefon und Mobiltelefon
        //   alpha02 = Alphabetisch mit interner ID und Adresse
        String type = cntx.requestAsString("type");
        if (!("Kat02".equals(type) || "Kat03".equals(type) || "Alpha01".equals(type) || "Alpha02".equals(type))) {
            type = "Kat01";
        }

        boolean kategorie = type.startsWith("Kat");
        boolean alphabetisch = type.startsWith("Alpha");

        String titel = validateTitel(search, alphabetisch);
        Select select = selectDatabaseFields(database);

        /*
         * modified to support ordering by workarea as per change request for version 1.2.0
         *
         * cklein
         * 2008-02-20
         */
        Boolean orderByWorkArea = cntx.requestAsBoolean("orderByWorkArea");

        if (orderByWorkArea.booleanValue()) {
            select.joinLeftOuter("veraweb.tworkarea", "tperson.fk_workarea", "tworkarea.pk");
            select.selectAs("CASE WHEN tworkarea.name <> 'Kein' THEN tworkarea.name ELSE 'Kein Arbeitsbereich' END",
              "workarea_name");
        }
        cntx.setContent("orderByWorkArea", orderByWorkArea);

        /** Nur die aktuelle Auswahl zurückgeben. */
        WhereList where = new WhereList();
        search.addGuestListFilter(where);
        if (selection != null && selection.size() > 0) {
            where.addAnd(Expr.in("tguest.pk", selection));
        }
        select.where(where);

        // Sortierung erstellen
        List order = new ArrayList();
        order.add("tguest.ishost");
        order.add("DESC");
        if (kategorie) {
            order.add("tcategorie.rank");
            order.add("tcategorie.catname");
        }
        /*
         * modified to support workareas as per change request for version 1.2.0
         * cklein
         * 2008-02-21
         */
        if (orderByWorkArea.booleanValue()) {
            setSortOrder(order, "workareaname");
        }
        setSortOrder(order, cntx.requestAsString("sort1"));
        setSortOrder(order, cntx.requestAsString("sort2"));
        setSortOrder(order, cntx.requestAsString("sort3"));
        select.orderBy(DatabaseHelper.getOrder(order));

        Map data = new HashMap();
        WorkerFactory.getGuestListWorker(cntx).getSums(database, data, search, selection);

        cntx.setContent("datum", new Date());
        cntx.setContent("titel", titel);
        cntx.setContent("type", type);
        cntx.setContent("data", data);
        cntx.setContent("kategorie", Boolean.valueOf(kategorie));
        cntx.setContent("alphabetisch", Boolean.valueOf(alphabetisch));
        cntx.setContent("guestlist", database.getList(select, database));
    }

    //
    // Hilfsmethoden
    //

    /**
     * Diese Methode überprüft und setzt den validen Title anhand der GuestSearch Bean
     *
     * @param alphabetisch
     * @return
     */
    private String validateTitel(GuestSearch search, boolean alphabetisch) {
        String titel;
        if (search.invitationstatus != null && search.invitationstatus.intValue() == 1) {
            titel = "Offenliste";
        } else if (search.invitationstatus != null && search.invitationstatus.intValue() == 2) {
            titel = "Zusagenliste";
        } else if (search.invitationstatus != null && search.invitationstatus.intValue() == 3) {
            titel = "Absagenliste";
        } else {
            titel = "Gästeliste";
        }
        if (alphabetisch) {
            titel += " (alphabetisch)";
        }
        return titel;
    }

    /**
     * Diese Methode gibt ein select mit den benötigten Feldern zurück.
     *
     * @param database
     * @return
     */
    private Select selectDatabaseFields(Database database) {
        return SQL.Select(database).
          from("veraweb.tguest").
          selectAs("tguest.pk", "id").
          selectAs("CASE WHEN orderno IS NOT NULL THEN orderno ELSE orderno_p END", "someorderno").
          select("reserve").
          select("lastname_a_e1").
          select("title_a_e1").
          select("firstname_a_e1").
          select("lastname_b_e1").
          select("title_b_e1").
          select("firstname_b_e1").
          select("invitationtype").
          select("invitationstatus").
          select("invitationstatus_p").
          select("orderno").
          select("orderno_p").
          select("notehost").
          select("notehost_p").
          select("ishost").
          select("fon_a_e1").
          select("fon_b_e1").
          select("fon_c_e1").
          select("fax_a_e1").
          select("fax_b_e1").
          select("fax_c_e1").
          select("mobil_a_e1").
          select("mobil_b_e1").
          select("mobil_c_e1").
          select("function_a_e1").
          select("company_a_e1").
          select("street_a_e1").
          select("zipcode_a_e1").
          select("state_a_e1").
          select("city_a_e1").
          select("country_a_e1").
          select("street_b_e1").
          select("zipcode_b_e1").
          select("city_b_e1").
          select("internal_id").
          select("note_a_e1").
          select("note_b_e1").
          selectAs("tcategorie.catname", "category").
          joinLeftOuter("veraweb.tperson", "fk_person", "tperson.pk").
          joinLeftOuter("veraweb.tcategorie", "fk_category", "tcategorie.pk");
    }

    /**
     * Diese Methode fügt gemäß einem übergebenen Sortierkriterium ("orderno",
     * "name", "country", "zipcode", "category", "rank" oder "table") passende
     * ORDER-BY-Spaltennamen in die übergebene Liste ein.
     *
     * @param order Liste von ORDER-BY-Spaltennamen, die hier ergänzt werden soll.
     * @param sort  abstraktes Sortierkriterium
     */
    static void setSortOrder(List order, String sort) {
        if (sort == null) {
            // nothing
        } else if (sort.equals("orderno")) {
            order.add("someorderno");
        } else if (sort.equals("name")) {
            order.add("tperson.lastname_a_e1");
            order.add("tperson.firstname_a_e1");
        } else if (sort.equals("country")) {
            order.add("country_a_e1");
        } else if (sort.equals("zipcode")) {
            order.add("zipcode_a_e1");
        } else if (sort.equals("category")) {
            order.add("tcategorie.rank");
            order.add("tcategorie.catname");
        } else if (sort.equals("rank")) {
            order.add("tguest.rank");
        } else if (sort.equals("table")) {
            order.add("tguest.tableno");
        } else if (sort.equals("workareaname")) {
            /*
             * modified to support workareas as per change request for version 1.2.0
             * cklein
             * 2008-02-21
             */
            order.add("tworkarea.name");
        } else if (sort.equals("accept")) {
            order.add("tguest.invitationstatus = 0");
            order.add("ASC");
            order.add("tguest.invitationstatus");
        } else if (sort.equals("decline")) {
            order.add("tguest.invitationstatus = 0");
            order.add("ASC");
            order.add("tguest.invitationstatus = 2");
            order.add("DESC");
            order.add("tguest.invitationstatus");
        } else if (sort.equals("internal_id")) {
            order.add("tperson.internal_id");
        }
    }

    public Location getLocationForReport(OctopusContext cntx) throws IOException, BeanException {
        Event event = (Event) cntx.contentAsObject("event");
        Location location = LocationDetailWorker.getLocation(cntx, event.location);

        if (location == null) {
            Location emptyLocation = new Location();
            emptyLocation.name = "";
            logger.warn("Could not get location name by task: " + cntx.getTaskName() + "Set location name to default.");
            return emptyLocation;
        }
        return location;
    }
}
