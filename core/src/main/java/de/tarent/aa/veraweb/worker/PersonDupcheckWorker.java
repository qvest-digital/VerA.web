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
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
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
import de.tarent.aa.veraweb.utils.AddressHelper;
import de.tarent.aa.veraweb.utils.CharacterPropertiesReader;
import de.tarent.aa.veraweb.utils.DateHelper;
import de.tarent.aa.veraweb.utils.PersonDuplicateCheckHelper;
import de.tarent.aa.veraweb.utils.PersonNameTrimmer;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.Request;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.beans.veraweb.RequestVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.List;

/**
 * Dieser Octopus-Worker bearbeitet Personenlisten unter besonderer Beachtung, ob
 * ein Duplikat zu einem bestimmten Datensatz vorliegt.
 */
public class PersonDupcheckWorker extends ListWorkerVeraWeb {

    /**
     * Helper class to find duplicates.
     */
    private PersonDuplicateCheckHelper dupCheckHelper;

    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public PersonDupcheckWorker() {
        super("Person");
        dupCheckHelper = new PersonDuplicateCheckHelper();
    }

    //
    // Oberklasse BeanListWorker
    //
    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        Person person = (Person) cntx.contentAsObject("person");

        //Specific handling to differ between company and person dupcheck.
        String isCompany = cntx.requestAsString("person-iscompany");

        if (isCompany != null && isCompany.equals("t")) {
            select.where(dupCheckHelper.getDuplicateExprCompany(cntx, person));
        } else {
            select.where(dupCheckHelper.getDuplicateExprPerson(cntx, person));
        }
    }

    //
    // Octopus-Aktionen
    //
    /**
     * Eingabe-Parameter der Octopus-Aktion {@link #check(OctopusContext, Boolean)}
     */
    public static final String INPUT_check[] = { "person-nodupcheck" };
    /**
     * Eingabe-Parameterzwang der Octopus-Aktion {@link #check(OctopusContext, Boolean)}
     */
    public static final boolean MANDATORY_check[] = { false };

    /**
     * Diese Octopus-Aktion holt eine Person aus dem Octopus-Request
     * (unter "person-*") oder der Octopus-Session (unter "dupcheck-person"),
     * legt sie und ihr Akkreditierungsdatum unter "person" bzw. "person-diplodatetime"
     * in den Octopus-Content und testet das übergebene Flag. Ist es
     * <code>true</code>, so wird der Eintrag in der Octopus-Session unter
     * "dupcheck-person" geloescht. Ansonsten wird dieser auf die eingelesene
     * Person gesetzt und ein Duplikats-Check durchgefuehrt; falls dieser Duplikate
     * zur Person findet, wird der Status "dupcheck" gesetzt.
     *
     * @param cntx       Octopus-Kontext
     * @param nodupcheck Flag zum übergehen des Duplikat-Checks
     */
    public void check(OctopusContext cntx, Boolean nodupcheck) throws BeanException, IOException {
        Request request = new RequestVeraWeb(cntx);
        Database database = new DatabaseVeraWeb(cntx);

        // Person Daten laden und in den Content stellen!
        Person person = (Person) cntx.sessionAsObject("dupcheck-person");
        if (cntx.requestContains("id")) {
            person = (Person) request.getBean("Person", "person");
            /*
             * fixes issue 1865
             * must add time information from person-diplotime_a_e1
             */
            DateHelper.addTimeToDate(person.diplodate_a_e1, cntx.requestAsString("person-diplotime_a_e1"), person.getErrors());
            DateHelper.addTimeToDate(person.diplodate_b_e1, cntx.requestAsString("person-diplotime_b_e1"), person.getErrors());
        }
        AddressHelper.checkPersonSalutation(person, database, database.getTransactionContext());
        PersonNameTrimmer.trimAllPersonNames(person);
        cntx.setContent("person", person);
        cntx.setContent("person-diplodatetime", Boolean.valueOf(DateHelper.isTimeInDate(person.diplodate_a_e1)));

        if (nodupcheck != null && nodupcheck.booleanValue()) {
            cntx.setSession("dupcheck-person", null);
            return;
        }
        cntx.setSession("dupcheck-person", person);

        String isCompany = cntx.requestAsString("person-iscompany");

        Select select = database.getCount("Person");

        if (isCompany != null && isCompany.equals("t")) {
            select.where(dupCheckHelper.getDuplicateExprCompany(cntx, person));
        } else {
            select.where(dupCheckHelper.getDuplicateExprPerson(cntx, person));
        }

        if (database.getCount(select).intValue() != 0) {
            cntx.setStatus("dupcheck");
        }
    }

    /* (non-Javadoc)
     * @see de.tarent.octopus.custom.beans.BeanListWorker#showList(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public List showList(OctopusContext cntx) throws BeanException, IOException {
        cntx.setContent("originalPersonId", cntx.requestAsInteger("originalPersonId"));
        return super.showList(cntx);
    }

    //
    // geschuetzte Hilfsmethoden
    //
    protected Clause getDuplicateExprPerson(OctopusContext cntx, Person person) {
        Clause clause = Where.and(
          Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
          Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        String ln = person == null || person.lastname_a_e1 == null || person.lastname_a_e1.equals("") ? "" : person.lastname_a_e1;
        String fn =
          person == null || person.firstname_a_e1 == null || person.firstname_a_e1.equals("") ? "" : person.firstname_a_e1;

        Clause normalNamesClause = Where.and(Expr.equal("lastname_a_e1", fn), Expr.equal("firstname_a_e1", ln));
        Clause revertedNamesClause = Where.and(Expr.equal("lastname_a_e1", ln), Expr.equal("firstname_a_e1", fn));
        Clause checkMixChanges = Where.or(normalNamesClause, revertedNamesClause);

        // Checking changes between first and lastname
        Clause dupNormalCheck = Where.and(clause, checkMixChanges);

        CharacterPropertiesReader cpr = new CharacterPropertiesReader();

        for (final String key : cpr.properties.stringPropertyNames()) {
            String value = cpr.properties.getProperty(key);

            if (ln.contains(value)) {
                ln = ln.replaceAll(value, key);
            } else if (ln.contains(key)) {
                ln = ln.replaceAll(key, value);
            }

            if (fn.contains(value)) {
                fn = fn.replaceAll(value, key);
            } else if (fn.contains(key)) {
                fn = fn.replaceAll(key, value);
            }
        }

        Clause normalNamesEncoding = Where.and(Expr.equal("lastname_a_e1", fn), Expr.equal("firstname_a_e1", ln));
        Clause revertedNamesEncoding = Where.and(Expr.equal("lastname_a_e1", ln), Expr.equal("firstname_a_e1", fn));
        Clause checkMixChangesEncoding = Where.or(normalNamesEncoding, revertedNamesEncoding);
        // With encoding changes
        return Where.or(dupNormalCheck, checkMixChangesEncoding);
    }

    protected Clause getDuplicateExprCompany(OctopusContext cntx, Person person) {
        Clause clause = Where.and(
          Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
          Expr.equal("deleted", PersonConstants.DELETED_FALSE));
        String companyName =
          person == null || person.company_a_e1 == null || person.company_a_e1.equals("") ? "" : person.company_a_e1;
        return Where.and(clause, Expr.equal("company_a_e1", companyName));
    }
}
