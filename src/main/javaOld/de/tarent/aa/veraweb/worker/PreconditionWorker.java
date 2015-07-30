//package de.tarent.aa.veraweb.worker;
//
//import de.tarent.aa.veraweb.beans.Event;
//import de.tarent.dblayer.sql.SQL;
//import de.tarent.dblayer.sql.clause.*;
//import de.tarent.dblayer.sql.statement.Select;
//import de.tarent.octopus.PersonalConfigAA;
//import de.tarent.octopus.beans.Bean;
//import de.tarent.octopus.beans.BeanException;
//import de.tarent.octopus.beans.Database;
//import de.tarent.octopus.beans.TransactionContext;
//import de.tarent.octopus.config.TcPersonalConfig;
//import de.tarent.octopus.server.OctopusContext;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by sweiz - tarent solutions GmbH on 30.07.15.
// */
//public class PreconditionWorker extends StammdatenWorker {
//    //
//    // Öffentliche Konstanten
//    //
//    /**
//     * Parameter: Wessen Vorbedingung?
//     */
//    public final static String PARAM_DOMAIN = "domain";
//
//    /**
//     * Parameterwert: beliebige Vorbedingung
//     */
//    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
//    /**
//     * Parameterwert: Vorbedingung des gleichen Mandanten
//     */
//    public final static String PARAM_DOMAIN_VALUE_OU = "ou";
//
//    //
//    // Konstruktoren
//    //
//
//    /**
//     * Der Konstruktor legt den Bean-Namen fest.
//     */
//    public PreconditionWorker() {
//        super("Precondition");
//    }
//
////    //
////    // Basisklasse BeanListWorker
////    //
////    @Override
////    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
////        Clause orgUnitTest = getWhere(cntx);
////        if (orgUnitTest != null) {
////            select.where(orgUnitTest);
////        }
////    }
//
//    @Override
//    protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
//        if (cntx.requestContains("order")) {
//            String order = cntx.requestAsString("order");
//            if ("name".equals(order)) {
//                select.orderBy(Order.asc(order));
//                cntx.setContent("order", order);
//            } else if ("flags".equals(order)) {
//                select.orderBy(Order.asc(order).andAsc("name"));
//                cntx.setContent("order", order);
//            }
//        }
//    }
//
//    @Override
//    public void getAll(OctopusContext cntx) throws BeanException, IOException {
//        Integer count = cntx.requestAsInteger("count");
//        if (count != null) {
//            cntx.setContent("count", count);
//        }
//
////        super.getAll(cntx);
//        getAllAvailableEventPreconditions(cntx);
//    }
//
//    /**
//     * Returns all available person categories that have not been
//     * assigned to a specific event.
//     *
//     * @param cntx
//     * @throws BeanException
//     * @throws IOException
//     */
//    public static String[] INPUT_getAllAvailablePreconditions = {};
//    public static String[] MANDATOR_getAllAvailablePreconditions = {};
//
//    public void getAllAvailablePreconditions(OctopusContext cntx) throws BeanException, IOException {
//        final Integer orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
//        final Database database = getDatabase(cntx);
//        final Select select = database.getSelect(BEANNAME);
//        extendColumns(cntx, select);
//        extendAll(cntx, select);
//        select.whereAnd(Expr.isNull("fk_event"));
//        select.whereAndEq("fk_orgunit", orgunit);
//
//
//        Integer count = cntx.requestAsInteger("count");
//        if (count != null) {
//            cntx.setContent("count", count);
//        }
//
//        cntx.setContent("all" + BEANNAME, database.getList(select, database));
//    }
//
//    private void getAllAvailableEventConditions(OctopusContext cntx) throws BeanException, IOException {
//        final Integer orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
//        final Database database = getDatabase(cntx);
//        final Select select = database.getSelect(BEANNAME);
//        extendColumns(cntx, select);
//        extendAll(cntx, select);
//        select.whereAndEq("fk_orgunit", orgunit);
//
//        Integer count = cntx.requestAsInteger("count");
//        if (count != null) {
//            cntx.setContent("count", count);
//        }
//
//        cntx.setContent("all" + BEANNAME, database.getList(select, database));
//    }
//
//
//
//    @Override
//    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
//        Clause clause = null;
//        Precondition precondition = (Precondition) cntx.contentAsObject("precondition");
//        Event event = (Event) cntx.contentAsObject("event");
//        if (precondition != null && precondition.id != null) {
//            buildSelectForPrecondition(cntx, select, precondition, event);
//        } else if (event != null && event.id != null) {
//            select.where(new RawClause("pk NOT IN (SELECT fk_event_precondition FROM veraweb.tevent_main WHERE veraweb.tevent_main = " + event.id + ")"));
//        }
//    }
//
//    private void buildSelectForPrecondition(OctopusContext cntx, Select select, Precondition precondition, Event event) throws BeanException {
//        Clause clause = Where.and(
//                Expr.isNull("fk_event_main"),
//                //pk ersetzen?
//                new RawClause("pk NOT IN (SELECT fk_event_main FROM veraweb.tperson_precondition WHERE fk_event_precondition = " + precondition.fk_event_precondition + ")"));
//
//        Integer eventId = cntx.requestAsInteger("eventId");
//        if (eventId.intValue() == 0) {
//            if (event != null) {
//                eventId = event.id;
//            } else {
//                eventId = null;
//            }
//        }
//        if (eventId != null) {
//            clause = Where.or(
//                    Expr.isNull("fk_event_main"),
//                    Expr.equal("fk_event_main", eventId)
//            );
//        }
//
//        Clause orgUnitTest = getWhere(cntx);
//        if (orgUnitTest != null) {
//            clause = clause == null ? orgUnitTest : Where.and(orgUnitTest, clause);
//        }
//        if (clause != null) {
//            select.where(clause);
//        }
//    }
//
//    @Override
//    protected Clause getWhere(OctopusContext cntx) throws BeanException {
//        TcPersonalConfig pConfig = cntx.personalConfig();
//        if (pConfig instanceof PersonalConfigAA) {
//            PersonalConfigAA aaConfig = (PersonalConfigAA) pConfig;
//            String domain = cntx.contentAsString(PARAM_DOMAIN);
//            if (!(PARAM_DOMAIN_VALUE_ALL.equals(domain) && pConfig.isUserInGroup(PersonalConfigAA.GROUP_ADMIN))) {
//                Integer orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
//                if (orgunit == null)
//                    return Expr.isNull("tevent_precondition.fk_event_precondition");
//                else
//                    return Expr.equal("tevent_precondition.fk_event_precondition", aaConfig.getOrgUnitId());
//            }
//            return null;
//        } else {
//            throw new BeanException("Missing user information");
//        }
//    }
//
//    /**
//     * Overrides parent class' behaviour in order to safely implement
//     * change request 2.8 for version 1.2.0 which states that all authenticated
//     * users must have viewer access to the list of categories.
//     * The feature is implemented reusing existing code and templates and
//     * therefore we must first test whether the user is authorized to
//     * save the list. For that, the user must implement either the Administrator
//     * or the PartialAdmin role.
//     * Will delegate processing to the super class if the user implements one
//     * of the above roles and processing continues as normal.
//     *
//     * @throws BeanException in case that the user is not authorized to save the list
//     * @throws IOException
//     */
//    @Override
//    public void saveList(OctopusContext cntx) throws BeanException, IOException {
//        super.saveList(cntx);
//    }
//
//    @Override
//    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {
//        int count = 0;
//        if (bean.isModified()) {
//            if (bean.isCorrect()) {
//                Database database = context.getDatabase();
//
//                Clause sameOrgunit = getWhere(cntx);
//                Clause sameFkEventMain = Expr.equal(database.getProperty(bean, "fk_event_main"), bean.getField("fk_event_main"));
//                Clause sameFkEventPrecondition = sameOrgunit == null ? sameFkEventMain : Where.and(sameOrgunit, sameFkEventMain);
//                Integer exist = database.getCount(database.getCount(bean).where(sameFkEventPrecondition), context);
//
//                List groups = Arrays.asList(cntx.personalConfig().getUserGroups());
//                boolean admin = groups.contains(PersonalConfigAA.GROUP_ADMIN) || groups.contains(PersonalConfigAA.GROUP_PARTIAL_ADMIN);
//
//                if (exist.intValue() != 0) {
//                    cntx.getContentObject().setField("beanToAdd", bean);
//                    errors.add("Es existiert bereits ein Stammdaten-Eintrag unter der ID '" + bean.getField("fk_event_main") + "'.");
//                } else if (admin && !cntx.requestAsBoolean("questionConfirmed").booleanValue()) {
//                    cntx.getContentObject().setField("beanToAdd", bean);
//                    cntx.getContentObject().setField("resortQuestion", true);
//                } else {
//                    saveBean(cntx, bean, context);
//                    count++;
//                }
//            } else {
//                cntx.getContentObject().setField("beanToAdd", bean);
//                errors.addAll(bean.getErrors());
//            }
//        }
//        return count;
//    }
//
////    /**
////     * Die Kategorie bean innerhalb der bestehenden Kategorieen einsortieren. Dazu werden alle bestehenden Kategorien mit
////     * Rang >= bean.rank in ihrem Rang um eins erhoeht.
////     *
////     * @param cntx
////     * @param bean
////     * @throws BeanException
////     */
////    protected void incorporateBean(OctopusContext cntx, Categorie bean, TransactionContext context) throws BeanException {
////        assert bean != null;
////        assert cntx != null;
////
////        if (bean.rank != null) {
////            context.execute(SQL.Update(context.getDatabase()).
////                    table("veraweb.tcategorie").
////                    update("rank", new RawClause("rank + 1")).
////                    where(Expr.greaterOrEqual("rank", bean.rank)));
////        }
////    }
//
//    @Override
//    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
//        ((Precondition) bean).orgunit = ((PersonalConfigAA) (cntx.personalConfig())).getOrgUnitId();
//        if (bean.isModified() && bean.isCorrect()) {
//            if (((Categorie) bean).rank != null && cntx.requestAsBoolean("resort").booleanValue()) {
//                incorporateBean(cntx, (Categorie) bean, context);
//            }
//        }
//        super.saveBean(cntx, bean, context);
//    }
//
//    @Override
//    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
//        int count = super.removeSelection(cntx, errors, selection, context);
//
//        // now remove all stale person category assignments
//        context.execute(
//                SQL.Delete(context.getDatabase()).
//                        from("veraweb.tevent_precondition").
//                        where(new RawClause("fk_categorie NOT IN (" +
//                                "SELECT pk FROM veraweb.tcategorie)")));
//
//        return count;
//    }
//}
