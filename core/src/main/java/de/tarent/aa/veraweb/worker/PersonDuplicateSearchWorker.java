package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.PersonSearch;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.aa.veraweb.utils.CharacterPropertiesReader;
import de.tarent.dblayer.helper.ResultMap;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.SyntaxErrorException;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This worker implements change request 2.3 for version 1.2.0.
 *
 * It enables the user to run a duplicate search over all persons in the
 * database. Duplicates are found by searching for equal firstname and lastname
 * names.
 *
 * @author cklein
 */
public class PersonDuplicateSearchWorker extends PersonListWorker {
    /**
     * Constructor
     */
    public PersonDuplicateSearchWorker() {
        super();
    }

    @Override
    public List showList(final OctopusContext octopusContext) throws BeanException, IOException {
        // code in part duplicated from PersonListWorker
        final Database database = getDatabase(octopusContext);

        final Integer start = getStart(octopusContext);
        final Integer limit = getLimit(octopusContext);
        final Integer count = getCount(octopusContext, database);
        final Map param = getParamMap(start, limit, count);
        octopusContext.setContent(OUTPUT_showListParams, param);
        octopusContext.setContent(OUTPUT_getSelection, getSelection(octopusContext, count));
        octopusContext.setContent("action", "duplicateSearch");

        final Select select = getSelect(database);
        extendColumns(octopusContext, select);
        extendWhere(octopusContext, select);
        extendLimit(octopusContext, select);
        select.orderBy(Order.asc("lastname_a_e1").andAsc("firstname_a_e1"));

        /*
         * FIXME remove this temporary fix ASAP cklein 2009-09-17 Temporary
         * workaround for NPE Exception in Conjunction with temporary Connection
         * Pooling Fix in tarent-database Somehow the resultlist returned by
         * getResultList or its underlying ResultSet will be NULL when entering
         * the view although, upon exiting this method the first time that it is
         * called, will return the correct resultlist with at most 10 entries in
         * the underlying resultset as is defined by the query.
         */
        final List resultList = getResultList(database, select);
        final ArrayList<Map> listWithOrdering = getListWithOrdering(convertFromResultListToArrayList(resultList));

        for (Map map : listWithOrdering){
            String personCategory = getPersonCategoriesList((Integer) map.get("id"), octopusContext);
            map.put("persontCategoryName", personCategory);
        }
        return listWithOrdering;
    }

    /**
     * Conversion to manipulate dinamic lists
     *
     * @param resultList List result list
     * @return ArrayList<Map>
     */
    public ArrayList<Map> convertFromResultListToArrayList(final List resultList) {
        final ArrayList<Map> result = new ArrayList<Map>();

        for (int i = 0; i < resultList.size(); i++) {
            final HashMap<String, Object> tmp = new HashMap<String, Object>();
            final Set<String> keys = ((ResultMap) resultList.get(i)).keySet();
            for (final String key : keys) {
                tmp.put(key, ((ResultMap) resultList.get(i)).get(key));
            }
            result.add(tmp);
        }

        return result;
    }

    /**
     * Giving correct order to the duplicates list - THE DUPLICATES MUST GO
     * TOGETHER
     *
     * @param initList ArrayList<Map>
     * @return ArrayList<Map>
     */
    public ArrayList<Map> getListWithOrdering(final ArrayList<Map> initList) {

        final ArrayList<Map> result = new ArrayList<Map>();

        for (int i = 0; i < initList.size(); i++) {
            final Map tmp = initList.get(i);

            for (int j = 0; j < initList.size(); j++) {
                final Map tmp2 = initList.get(j);

                if (checkDuplicateNames(tmp, tmp2) && i != j) {
                    result.add(tmp2);
                    initList.remove(j);
                    j--;
                }
            }
            result.add(tmp);
            if (!initList.isEmpty()) {
                initList.remove(i);
                i--;
            }
        }
        return result;
    }

    /**
     * Checking duplicates result between duplicates
     *
     * @param tmp  Map
     * @param tmp2 Map
     * @return Boolean
     */
    public Boolean checkDuplicateNames(final Map tmp, final Map tmp2) {
        final CharacterPropertiesReader cpr = new CharacterPropertiesReader();

        final String firstname1 = cpr.convertUmlauts((String) tmp.get("firstname_a_e1"));
        final String lastname1 = cpr.convertUmlauts((String) tmp.get("lastname_a_e1"));
        final String firstname2 = cpr.convertUmlauts((String) tmp2.get("firstname_a_e1"));
        final String lastname2 = cpr.convertUmlauts((String) tmp2.get("lastname_a_e1"));

        return (firstname1.equalsIgnoreCase(firstname2) && lastname1.equals(lastname2))
          || (firstname1.equalsIgnoreCase(lastname2) && lastname1.equals(firstname2));
    }

    @Override
    protected Select getSelect(final PersonSearch search, final Database database) throws IOException, BeanException {
        final Person template = new Person();
        final Select result = database.getSelectIds(template);

        // replicated from PersonListWorker.
        return result.select("firstname_a_e1").select("internal_id").select("lastname_a_e1").select("firstname_b_e1").select("lastname_b_e1")
          .select("function_a_e1")
          .select("company_a_e1").select("street_a_e1").select("zipcode_a_e1").select("city_a_e1");
    }

    @Override
    protected Integer getAlphaStart(final OctopusContext cntx, final String start) throws BeanException, IOException {
        // code duplicated from PersonListWorker.getAlphaStart()
        final Database database = getDatabase(cntx);
        final Select select = SQL.Select(database).from("veraweb.tperson");
        select.select("COUNT(DISTINCT(tperson.pk))");
        select.from("veraweb.tperson person2");
        select.setDistinct(false);

        extendWhere(cntx, select);
        if (start != null && start.length() > 0) {
            select.whereAnd(Expr.less("tperson.lastname_a_e1", Escaper.escape(start)));
        }

        final Integer count = database.getCount(select);
        return new Integer(count.intValue() - (count.intValue() % getLimit(cntx).intValue()));
    }

    @Override
    protected Integer getCount(final OctopusContext cntx, final Database database) throws BeanException, IOException {
        // final Select select = database.getEmptySelect(new Person());
        final Select select = SQL.Select(database).from("veraweb.TPERSON_NORMALIZED");
        select.select("COUNT(DISTINCT(TPERSON_NORMALIZED.pk))");
        select.setDistinct(false);
        extendSubselect(cntx, select);

        return database.getCount(select);
    }

    /**
     * Extend the subselect statement.
     *
     * @param cntx      Context
     * @param subselect Given statement
     */
    protected void extendSubselect(final OctopusContext cntx, final Select subselect) {
        subselect.from("veraweb.TPERSON_NORMALIZED person2");

        subselect.whereAnd(getClauseForOrgunit(cntx)).whereAnd(getClausePersonNotDeleted())
          .whereAnd(getClausePkIsDifferentOrgunitIsSame())
          .whereAnd(Where.or(getClauseFirstnameAndLastnameEquals(), getClauseFirstAndLastnameSwapped()))
          .whereAnd(getClauseFirstOrLastnameNotEmpty());
    }

    /**
     * Set the limit for the result list.
     *
     * @param cntx   Context
     * @param select Select
     * @throws BeanException TODO
     * @throws IOException   TODO
     */
    protected void extendLimit(final OctopusContext cntx, final Select select) throws BeanException, IOException {
        final Integer start = getStart(cntx);
        final Integer limit = getLimit(cntx);
        select.Limit(new Limit(limit, start));
    }

    @Override
    protected void extendWhere(final OctopusContext octopusContext, final Select select) {

        final Database database = getDatabase(octopusContext);

        final Select subselect = SQL.Select(database).from("veraweb.TPERSON_NORMALIZED").selectAs("TPERSON_NORMALIZED.pk", "id");

        extendSubselect(octopusContext, subselect);
        subselect.setDistinct(false);
        subselect.orderBy(Order.asc("TPERSON_NORMALIZED.lastname_a_e1").andAsc("TPERSON_NORMALIZED.firstname_a_e1"));
        try {
            select.whereAnd(new RawClause("tperson.pk IN (" + subselect.statementToString() + ")"));
        } catch (final SyntaxErrorException e) {
            new SyntaxErrorException("Konvertierung der Statement zu String fehlgeschlagen");
        }
    }

    private Where getClauseForOrgunit(final OctopusContext cntx) {
        return Where.and(Expr.equal("TPERSON_NORMALIZED.fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()),
          Expr.equal("TPERSON_NORMALIZED.deleted", PersonConstants.DELETED_FALSE));
    }

    private Where getClausePkIsDifferentOrgunitIsSame() {
        return Where.and(new RawClause("TPERSON_NORMALIZED.pk!=person2.pk"),
          new RawClause("TPERSON_NORMALIZED.fk_orgunit=person2.fk_orgunit"));
    }

    private Where getClauseFirstOrLastnameNotEmpty() {
        return Where.and(new RawClause("TPERSON_NORMALIZED.lastname_a_e1<>''"),
          new RawClause("TPERSON_NORMALIZED.firstname_a_e1<>''"));
    }

    private Where getClauseFirstAndLastnameSwapped() {
        return Where.and( // Reverted names
          new RawClause("veraweb.TPERSON_NORMALIZED.firstname_normalized=person2.lastname_normalized"), new RawClause(
            "veraweb.TPERSON_NORMALIZED.lastname_normalized=person2.firstname_normalized"));
    }

    private Where getClauseFirstnameAndLastnameEquals() {
        return Where
          .and(new RawClause("veraweb.TPERSON_NORMALIZED.firstname_normalized=person2.firstname_normalized"), new RawClause(
            "veraweb.TPERSON_NORMALIZED.lastname_normalized=person2.lastname_normalized"));
    }

    private Where getClausePersonNotDeleted() {
        return Expr.equal("person2.deleted", PersonConstants.DELETED_FALSE);
    }
}
