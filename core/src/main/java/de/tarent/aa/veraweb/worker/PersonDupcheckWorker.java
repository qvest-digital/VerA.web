package de.tarent.aa.veraweb.worker;
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
