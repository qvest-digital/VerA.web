package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
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
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nunez Alvarez (j.nunez-alvarez@tarent.de)
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

import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Octopus-Worker übernimmt das Suchen und Ersetzen von
 * Personendaten und entsprechendenen Dokumenttypen.
 *
 * Er kennt sowohl Funktionen zum Ersetzen von allen Treffern,
 * als auch das vorherige Anzeigen der Treffer.
 *
 * @author Christoph Jerolimov
 * @version $Revision: 1.1 $
 */
public class PersonReplaceWorker extends PersonListWorker {
    //
    // KONSTANTEN
    //

    /**
     * Replace-Gruppen-Parameter
     */
    private static final String PARAM_GROUPS[] = {
            "snr-group01", "snr-group02", "snr-group03",
            "snr-group04", "snr-group05", "snr-group06",
            "snr-group07", "snr-group08", "snr-group09",
            "snr-group10", "snr-group20" };
    /**
     * Vorname der Hauptperson und des Partners
     */
    static private final List GROUP_01 = new ArrayList();
    /**
     * Nachname der Hauptperson und des Partners
     */
    static private final List GROUP_02 = new ArrayList();
    /**
     * Amtsbezeichnung / Funktion
     */
    static private final List GROUP_03 = new ArrayList();
    /**
     * Anrede und Titel
     */
    static private final List GROUP_04 = new ArrayList();
    /**
     * Firma / Institution
     */
    static private final List GROUP_05 = new ArrayList();
    /**
     * Straße, Adresszusatz und Land
     */
    static private final List GROUP_06 = new ArrayList();
    /**
     * PLZ / Ort und PLZ / Postfach
     */
    static private final List GROUP_07 = new ArrayList();
    /**
     * Telefon, Telefax und Mobiltelefon
     */
    static private final List GROUP_08 = new ArrayList();
    /**
     * E-Mail-Adresse
     */
    static private final List GROUP_09 = new ArrayList();
    /**
     * Internet-Adresse
     */
    static private final List GROUP_10 = new ArrayList();

    static {
        initLists();
    }

    /**
     * Erstellt die Listen zum Suchen/Ersetzen.
     */
    static private void initLists() {
        initList(GROUP_01, "firstname", true);
        initList(GROUP_02, "lastname", true);
        initList(GROUP_04, "title", true);
        initList(GROUP_04, "salutation", true);
        initList(GROUP_03, "function", false);
        initList(GROUP_05, "company", false);
        initList(GROUP_06, "street", false);
        initList(GROUP_06, "suffix1", false);
        initList(GROUP_06, "suffix2", false);
        initList(GROUP_06, "country", false);
        initList(GROUP_07, "zipcode", false);
        initList(GROUP_07, "city", false);
        initList(GROUP_07, "pobox", false);
        initList(GROUP_07, "poboxzipcode", false);
        initList(GROUP_08, "fon", false);
        initList(GROUP_08, "fax", false);
        initList(GROUP_08, "mobil", false);
        initList(GROUP_09, "mail", false);
        initList(GROUP_10, "url", false);
    }

    /**
     * Erstellt die Listen zum Suchen/Ersetzen.
     */
    static private void initList(List list, String field, boolean member) {
        for (char i = 'a'; i <= (member ? 'b' : 'c'); i++) {
            for (char j = '1'; j <= '3'; j++) {
                list.add(field + "_" + i + "_e" + j);
            }
        }
    }

    //
    // ERWEITERUNG DES BEANLISTWORKERS
    //

    /**
     * Octopus-Aktion die eine <strong>blätterbare</strong> Liste
     * mit Beans aus der Datenbank in den Content stellt. Kann durch
     * {@link #extendColumns(OctopusContext, Select)} erweitert bzw.
     * {@link #extendWhere(OctopusContext, Select)} eingeschränkt werden.
     *
     * Lenkt hier die entsprechende getSelect - Anfrage an die
     * Ursprüngliche Version des BeanListWorkers.
     *
     * @param cntx Octopus-Context
     * @return Liste mit Beans, nie null.
     * @throws BeanException BeanException
     * @throws IOException   IOException
     * @see #getSelection(OctopusContext, Integer)
     */
    public List showList(OctopusContext cntx) throws BeanException, IOException {
        Database database = getDatabase(cntx);

        Integer start = getStart(cntx);
        Integer limit = getLimit(cntx);
        /*
         * fixes issue 1889
         * make this a dry run, since we actually will not replace anything right now
         * otherwise countData will return the wrong number of search results (0)
         * somehow getCount() was not building the same query as countData does
         */
        cntx.setContent("snr-dry-run", true);
        Integer count = countData(cntx);
        Map param = getParamMap(start, limit, count);

        Select select = getSelect(database);
        extendColumns(cntx, select);
        extendWhere(cntx, select);
        select.Limit(new Limit((Integer) param.get("limit"), (Integer) param.get("start")));

        cntx.setContent(OUTPUT_showListParams, param);
        cntx.setContent(OUTPUT_getSelection, getSelection(cntx, count));
        return getResultList(database, select);
    }

    /**
     * ändert die Suchanfrage der Personenliste auf die zu ersetzenen Personen.
     */
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException {
        WhereList where = new WhereList();
        where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
        where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        where.addAnd(addPersonListFilter(cntx, new WhereList()));
        select.where(where);
    }

    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
    }

    protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
        Database database = getDatabase(cntx);

        WhereList where = new WhereList();
        where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
        where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        where.addAnd(addPersonListFilter(cntx, new WhereList()));

        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        buffer.append(where.clauseToString());
        buffer.append(") AND lastname_a_e1 < '");
        Escaper.escape(buffer, start);
        buffer.append("'");

        Select select = database.getCount(BEANNAME);
        select.where(new RawClause(buffer));

        Integer i = database.getCount(select);
        return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
    }

    protected WhereList addPersonListFilter(OctopusContext cntx, WhereList list) {
        Map replaceRequest = getReplaceRequest(cntx);
        List fields = getFieldList(replaceRequest);
        String search = (String) replaceRequest.get("search");

        boolean wildcardPre = search.charAt(0) == '*';
        boolean wildcardPost = search.charAt(search.length() - 1) == '*';
        search = search.substring(
                wildcardPre ? 1 : 0, search.length() - (
                        wildcardPost ? 1 : 0));

        if (fields.size() > 0) {
            list.addOr(getReplaceWhere(fields, search, wildcardPre, wildcardPost));
        }
        return list;
    }

    //
    // NEUE OCTOPUS AKTIONEN
    //

    /**
     * Octopus-Eingabe-Parameter für {@link #getReplaceRequest(OctopusContext)}
     */
    public static final String INPUT_getReplaceRequest[] = {};
    /**
     * Octopus-Eingabe-Parameter für {@link #getReplaceRequest(OctopusContext)}
     */
    public static final String OUTPUT_getReplaceRequest = "replace-request";

    /**
     * Kopiert die Such &amp; Ersetz anfragen in den Content und merkt
     * sich entsprechende Anfrage ggf. in der Session.<br><br>
     *
     * Ist die Suchanfrage zu kurz wird der Status auf
     * <code>"invalidsearch"</code> gesetzt.
     *
     * @param octopusContext The {@link OctopusContext}
     * @return Such und Ersetz Anfrage, nie null.
     */
    public Map getReplaceRequest(OctopusContext octopusContext) {
        Map replaceRequest = (Map) octopusContext.contentAsObject(OUTPUT_getReplaceRequest);
        if (replaceRequest != null) {
            return replaceRequest;
        }

        String search = octopusContext.requestAsString("snr-search");
        String replace = octopusContext.requestAsString("snr-replace");

        if (search == null) {
            replaceRequest = (Map) octopusContext.sessionAsObject(OUTPUT_getReplaceRequest);
            if (replaceRequest == null) {
                octopusContext.setStatus("invalidsearch");
            }
            return replaceRequest;
        }

        replaceRequest = new HashMap();
        replaceRequest.put("search", search);
        replaceRequest.put("replace", replace);
        for (int i = 0; i < PARAM_GROUPS.length; i++) {
            replaceRequest.put(PARAM_GROUPS[i], octopusContext.requestAsBoolean(PARAM_GROUPS[i]));
        }

        if (search.replaceAll("\\*", "").length() < 1) {
            octopusContext.setContent("noSearchParam", true);
            octopusContext.setStatus("invalidsearch");
        }
        if (!(getFieldList(replaceRequest).size() > 0 || ((Boolean) replaceRequest.get("snr-group20")).booleanValue())) {
            octopusContext.setContent("noFieldsSelected", true);
            octopusContext.setStatus("invalidsearch");
        }

        octopusContext.setSession(OUTPUT_getReplaceRequest, replaceRequest);
        return replaceRequest;
    }

    /**
     * Gibt eine Liste entsprechend der aktuellen Anfrage
     * zum Suchen/Ersetzen zurück.
     *
     * @param replaceRequest Map, siehe {@link #getReplaceRequest(OctopusContext)}
     * @return Liste mit Feldern
     */
    protected List getFieldList(Map replaceRequest) {
        List fields = new ArrayList();
        if (((Boolean) replaceRequest.get("snr-group01")).booleanValue()) {
            fields.addAll(GROUP_01);
        }
        if (((Boolean) replaceRequest.get("snr-group02")).booleanValue()) {
            fields.addAll(GROUP_02);
        }
        if (((Boolean) replaceRequest.get("snr-group03")).booleanValue()) {
            fields.addAll(GROUP_03);
        }
        if (((Boolean) replaceRequest.get("snr-group04")).booleanValue()) {
            fields.addAll(GROUP_04);
        }
        if (((Boolean) replaceRequest.get("snr-group05")).booleanValue()) {
            fields.addAll(GROUP_05);
        }
        if (((Boolean) replaceRequest.get("snr-group06")).booleanValue()) {
            fields.addAll(GROUP_06);
        }
        if (((Boolean) replaceRequest.get("snr-group07")).booleanValue()) {
            fields.addAll(GROUP_07);
        }
        if (((Boolean) replaceRequest.get("snr-group08")).booleanValue()) {
            fields.addAll(GROUP_08);
        }
        if (((Boolean) replaceRequest.get("snr-group09")).booleanValue()) {
            fields.addAll(GROUP_09);
        }
        if (((Boolean) replaceRequest.get("snr-group10")).booleanValue()) {
            fields.addAll(GROUP_10);
        }
        return fields;
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #countData(OctopusContext)}
     */
    public static final String INPUT_countData[] = {};
    /**
     * Octopus-Eingabe-Parameter für {@link #countData(OctopusContext)}
     */
    public static final String OUTPUT_countData = "snr-count";

    /**
     * Berechnet wieviele Datensätze bei einem {@link #replaceAllData(OctopusContext)}
     * oder einem {@link #replaceSelectedData(OctopusContext)} ersetzt werden würden
     * und gibt diese Zahl zur Benutzerinformation zurück.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public Integer countData(OctopusContext octopusContext) throws BeanException, IOException {
        Database database = getDatabase(octopusContext);

        Select select = database.getEmptySelect(new Person());
        select.select("COUNT(DISTINCT(tperson.pk))");
        WhereList where = new WhereList();

        if ("replace".equals(octopusContext.contentAsString("snr-action")) && !octopusContext.contentContains("snr-dry-run")) {
            List selection = getSelection(octopusContext, null);
            if (selection == null || selection.size() == 0) {
                return new Integer(0);
            }
            where.addAnd(Expr.in("tperson.pk", selection));
        }

        where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId()));
        where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        where.addAnd(addPersonListFilter(octopusContext, new WhereList()));
        select.where(where);

        return database.getCount(select);
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #replaceAllData(OctopusContext)}
     */
    public static final String INPUT_replaceAllData[] = {};

    /**
     * Octopus-Aktion die alle Daten sucht und ersetzt.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException
     */
    public void replaceAllData(OctopusContext octopusContext) throws BeanException {
        Database database = getDatabase(octopusContext);
        Map replaceRequest = getReplaceRequest(octopusContext);
        List fields = getFieldList(replaceRequest);
        String search = (String) replaceRequest.get("search");
        String replace = (String) replaceRequest.get("replace");

        boolean wildcardPre = search.charAt(0) == '*';
        boolean wildcardPost = search.charAt(search.length() - 1) == '*';
        search = search.substring(
                wildcardPre ? 1 : 0, search.length() - (
                        wildcardPost ? 1 : 0));

        final TransactionContext transactionContext = database.getTransactionContext();
        if (fields.size() > 0) {
            WhereList where = new WhereList();
            where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId()));
            where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
            where.addAnd(getReplaceWhere(fields, search, wildcardPre, wildcardPost));

            transactionContext
                    .execute(getReplaceUpdate(database, fields, search, replace, wildcardPre, wildcardPost).where(where));
            transactionContext.commit();
        }
    }

    /**
     * Octopus-Eingabe-Parameter für {@link #replaceSelectedData(OctopusContext)}
     */
    public static final String INPUT_replaceSelectedData[] = {};

    /**
     * Ersetzt in der Gästeliste ausgewählte Gäste.
     *
     * @param octopusContext The {@link OctopusContext}
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    public void replaceSelectedData(OctopusContext octopusContext) throws BeanException, IOException {
        List selection = getSelection(octopusContext, null);
        if (selection == null || selection.size() == 0) {
            return;
        }

        Database database = getDatabase(octopusContext);
        Map replaceRequest = getReplaceRequest(octopusContext);
        List fields = getFieldList(replaceRequest);
        String search = (String) replaceRequest.get("search");
        String replace = (String) replaceRequest.get("replace");

        boolean wildcardPre = search.charAt(0) == '*';
        boolean wildcardPost = search.charAt(search.length() - 1) == '*';
        search = search.substring(
                wildcardPre ? 1 : 0, search.length() - (
                        wildcardPost ? 1 : 0));

        final TransactionContext transactionContext = database.getTransactionContext();
        if (fields.size() > 0) {
            WhereList where = new WhereList();
            where.addAnd(Expr.in("pk", selection));
            where.addAnd(Expr.equal("fk_orgunit", ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId()));
            where.addAnd(Expr.equal("deleted", PersonConstants.DELETED_FALSE));
            where.addAnd(getReplaceWhere(fields, search, wildcardPre, wildcardPost));

            transactionContext
                    .execute(getReplaceUpdate(database, fields, search, replace, wildcardPre, wildcardPost).where(where));
            transactionContext.commit();
        }
    }

    /**
     * Gibt eine Where-Bedingung für die übergebenen Spalten zurück.
     *
     * @param fields FIXME
     * @param search FIXME
     * @return Where
     */
    protected Clause getReplaceWhere(List fields, String search, boolean wildcardPre, boolean wildcardPost) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        for (int i = 0; i < fields.size(); i++) {
            if (i != 0) {
                buffer.append(" OR ");
            }
            buffer.append(fields.get(i));
            if (wildcardPre && !wildcardPost) {
                buffer.append(" LIKE '%");
                Escaper.escape(buffer, search);
                buffer.append("'");
            } else if (wildcardPre) {
                buffer.append(" LIKE '%");
                Escaper.escape(buffer, search);
                buffer.append("%'");
            } else if (wildcardPost) {
                buffer.append(" LIKE '");
                Escaper.escape(buffer, search);
                buffer.append("%'");
            } else {
                buffer.append(" = '");
                Escaper.escape(buffer, search);
                buffer.append("'");
            }
        }
        buffer.append(")");
        return new RawClause(buffer);
    }

    protected Clause getReplaceWhere(String field, String search, boolean wildcardPre, boolean wildcardPost) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(field);
        if (wildcardPre && !wildcardPost) {
            buffer.append(" LIKE '%");
            Escaper.escape(buffer, search);
            buffer.append("'");
        } else if (wildcardPre) {
            buffer.append(" LIKE '%");
            Escaper.escape(buffer, search);
            buffer.append("%'");
        } else if (wildcardPost) {
            buffer.append(" LIKE '");
            Escaper.escape(buffer, search);
            buffer.append("%'");
        } else {
            buffer.append(" = '");
            Escaper.escape(buffer, search);
            buffer.append("'");
        }
        return new RawClause(buffer);
    }

    /**
     * Gibt ein Update mit den entsprechenden Spalten zurück.
     *
     * @param fields  FIXME
     * @param search  FIXME
     * @param replace FIXME
     * @return Update
     */
    protected Update getReplaceUpdate(Database db, List fields, String search, String replace, boolean wildcardPre,
            boolean wildcardPost) {
        Update update = SQL.Update(db).table("veraweb.tperson");
        for (int i = 0; i < fields.size(); i++) {
            String field = (String) fields.get(i);
            update.update(field, getReplaceClause(field, search, replace, wildcardPre, wildcardPost));
        }
        return update;
    }

    protected Clause getReplaceClause(String field, String search, String replace, boolean wildcardPre, boolean wildcardPost) {
        if (wildcardPre || wildcardPost) {
            return new RawClause("replace(" + field +
                    ", '" + Escaper.escape(search) + "'" +
                    ", '" + Escaper.escape(replace) + "')");
        } else {
            return new RawClause("CASE WHEN " +
                    field + "='" + Escaper.escape(search) + "' THEN '" +
                    Escaper.escape(replace) + "'" + "ELSE " +
                    field + " END");
        }
    }
}
