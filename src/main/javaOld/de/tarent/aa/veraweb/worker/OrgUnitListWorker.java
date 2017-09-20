package de.tarent.aa.veraweb.worker;

/*-
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
import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Mandantenlisten zur Verfügung. Details bitte dem
 * {@link de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb}
 * entnehmen.
 *
 * @author mikel
 */
public class OrgUnitListWorker extends ListWorkerVeraWeb {

    private List tmp_errors = null;

    /**
     * Dieser Konstruktor bestimmt den Basis-Bean-Typ des Workers.
     */
    public OrgUnitListWorker() {
        super("OrgUnit");
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll das übergebene Bean als neuen Eintrag speichern.
     *
     * 2015-03-13 - We have one Press category for every Mandant.
     *
     * @param octopusContext Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param bean einzufügendes Bean
     * @throws BeanException FIXME
     * @throws IOException FIXME
     */
    @Override
    @SuppressWarnings("unchecked")
    protected int insertBean(OctopusContext octopusContext, List errors, Bean bean, TransactionContext transactionContext) throws BeanException, IOException {
        int count = 0;
        boolean savedSuccessfully = updateOrgUnit(octopusContext, errors, transactionContext, bean, false);
        if (savedSuccessfully){
            count++;
        }
        return count;
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll die übergebene Liste von Beans aktualisieren.
     *
     * @param octopusContext Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param orgUnitList Liste von zu aktualisierenden Beans
     * @throws BeanException FIXME
     * @throws IOException FIXME
     */
    @Override
    @SuppressWarnings("unchecked")
    protected int updateBeanList(OctopusContext octopusContext, List errors, List orgUnitList, TransactionContext transactionContext) throws IOException, BeanException {
        int count = 0;
        for (Object obj : orgUnitList) {
            final Bean bean = (Bean) obj;

            boolean savedSuccessfully = updateOrgUnit(octopusContext, errors, transactionContext, bean, true);
            if (savedSuccessfully){
                count++;
            }
        }
        return count;
    }

    private boolean updateOrgUnit(OctopusContext octopusContext, List errors, TransactionContext transactionContext, Bean bean, boolean isUpdate) throws BeanException, IOException {
        if (bean.isModified()) {
            if (bean.isCorrect()) {
                if (bean instanceof OrgUnit) {
                    boolean isValid = executeAdditionalChecks(octopusContext, (OrgUnit) bean, transactionContext, errors, isUpdate);
                    if (!isValid) {
                        return false;
                    }
                }
                saveBean(octopusContext, bean, transactionContext);
                return true;
            } else {
                errors.addAll(bean.getErrors());
            }
        }
        return false;
    }

    private boolean executeAdditionalChecks(OctopusContext octopusContext, OrgUnit orgUnit, TransactionContext transactionContext, List errors, boolean isUpdate) throws BeanException, IOException {
        final LanguageProvider languageProvider = initLanguageProvider(octopusContext);
        Boolean isValid = true;

        if ("".equals(orgUnit.name.trim())) {
            addErrorMessageNoOrgunitName(errors, languageProvider);
            isValid = false;
        }

        if (!isUpdate && orgUnit.id != null) {
            addErrorMessagePresentOrgunitId(errors, languageProvider);
            isValid = false;
        } else if (isUpdate && orgUnit.id == null) {
            addErrorMessageMissingOrgunitId(errors, orgUnit, languageProvider);
            isValid = false;
        }

        final OrgUnit alreadyExistingBean = getExistingBean(isUpdate, transactionContext, orgUnit);
        if (alreadyExistingBean != null) {
            addErrorMessageOrgunitExists(errors, orgUnit.name, languageProvider);
            isValid = false;
        }
        return isValid;
    }

    private OrgUnit getExistingBean(boolean isUpdate, TransactionContext transactionContext, OrgUnit orgUnit) throws BeanException, IOException{
        final Database database = transactionContext.getDatabase();
        if (!isUpdate) {
            return getOrgUnitByName(transactionContext, orgUnit, database);
        } else {
            return getOrgUnitByNameAndId(transactionContext, orgUnit, database);
        }
    }

    private LanguageProvider initLanguageProvider(OctopusContext cntx) {
        final LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
        return languageProviderHelper.enableTranslation(cntx);
    }

    private void addErrorMessageNoOrgunitName(List errors, LanguageProvider languageProvider) {
        final String errorMessageNoOrgunitNames = getErrorMessageNoOrgunitName(languageProvider);
        errors.add(errorMessageNoOrgunitNames);
    }

    private void addErrorMessagePresentOrgunitId(List errors, LanguageProvider languageProvider) {
        final String ErrorMessagePresentOrgunitId = getErrorMessagePresentOrgunitId(languageProvider);
        errors.add(ErrorMessagePresentOrgunitId);
    }

    private void addErrorMessageMissingOrgunitId(List errors, OrgUnit orgunitBean, LanguageProvider languageProvider) {
        final String ErrorMessageMissingOrgunitId = getErrorMessageMissingOrgunitId(orgunitBean, languageProvider);
        errors.add(ErrorMessageMissingOrgunitId);
    }

    private void addErrorMessageOrgunitExists(List errors, String orgunitName, LanguageProvider languageProvider) {
        final String errorMessageOrgunitExists = getErrorMessageOrgunitExists(orgunitName, languageProvider);
        errors.add(errorMessageOrgunitExists);
    }

    private String getErrorMessageNoOrgunitName(LanguageProvider languageProvider) {
        return languageProvider.getProperty("MESSAGE_ORG_UNIT_NO_NAME");
    }

    private String getErrorMessagePresentOrgunitId(LanguageProvider languageProvider) {
        return languageProvider.getProperty("ORG_UNIT_NO_MANDANT_ID");
    }

    private String getErrorMessageMissingOrgunitId(OrgUnit orgunitBean, LanguageProvider languageProvider) {
        return languageProvider.getProperty("ORG_UNIT_MANDANT_ID_COMPULSORY_ONE") +
                " " +
                orgunitBean.name +
                " " +
                languageProvider.getProperty("ORG_UNIT_MANDANT_ID_COMPULSORY_TWO");
    }

    private String getErrorMessageOrgunitExists(String orgunitName, LanguageProvider languageProvider) {
        return languageProvider.getProperty("ORG_UNIT_MANDANT_EXISTS_ONE") +
                " " +
                orgunitName +
                " " +
                languageProvider.getProperty("ORG_UNIT_MANDANT_EXISTS_TWO");
    }

    private OrgUnit getOrgUnitByName(TransactionContext transactionContext, OrgUnit orgunitBean, Database database) throws BeanException, IOException {
        return (OrgUnit) database.getBean("OrgUnit",
                database.getSelect("OrgUnit").
                        where(Expr.equal("unitname", orgunitBean.name)), transactionContext);
    }

    private OrgUnit getOrgUnitByNameAndId(TransactionContext transactionContext, OrgUnit orgunitBean, Database database) throws BeanException, IOException {
        return (OrgUnit) database.getBean("OrgUnit",
                                    database.getSelect("OrgUnit").
                                            where(Where.and(
                                                    Expr.equal("unitname", orgunitBean.name),
                                                    Expr.notEqual("pk", orgunitBean.id))), transactionContext);
    }


    //
    // weitere Octopus-Aktionen
    //

    /** Octopus-Eingabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)} */
    public static final String INPUT_cleanupDatabase[] = {"orgunit"};
    /** Octopus-Eingabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)} */
    public static final boolean MANDATORY_cleanupDatabase[] = {false};
    /** Octopus-Ausgabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)} */
    public static final String OUTPUT_cleanupDatabase = "missingorgunit";

    /**
     * Zeigt eine Statistik über 'verloren' gegangene Datensätze an.
     * Wenn der Parameter <code>orgunit</code> übergeben wird werden
     * alle Datensätze ohne gültigen Mandanten diesem zugeordnet.
     *
     * @param octopusContext Octopus-Context-Instanz
     * @param orgunit Neue Orgunit-ID
     * @throws BeanException FIXME
     * @throws IOException FIXME
     */
    @SuppressWarnings("unchecked")
    public Map cleanupDatabase(OctopusContext octopusContext, Integer orgunit) throws BeanException, IOException {
        final Database database = getDatabase(octopusContext);
        final Clause where = new RawClause("fk_orgunit IS NULL OR fk_orgunit NOT IN (SELECT pk FROM veraweb.torgunit)");
        final Map missingorgunit = new HashMap();

        if (orgunit != null) {
            final TransactionContext transactionContext = database.getTransactionContext();
            transactionContext.execute(database.getUpdate("Person").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("Event").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("Import").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("ImportPerson").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("Location").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("Mailinglist").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("Categorie").update("fk_orgunit", orgunit).where(where));
            transactionContext.execute(database.getUpdate("User").update("fk_orgunit", orgunit).where(where));
            transactionContext.commit();
            missingorgunit.put("result", "done");
        }

        missingorgunit.put("person", database.getCount(database.getCount("Person").where(where)));
        missingorgunit.put("event", database.getCount(database.getCount("Event").where(where)));
        missingorgunit.put("import", database.getCount(database.getCount("Import").where(where)));
        missingorgunit.put("importperson", database.getCount(database.getCount("ImportPerson").where(where)));
        missingorgunit.put("location", database.getCount(database.getCount("Location").where(where)));
        missingorgunit.put("mailinglist", database.getCount(database.getCount("Mailinglist").where(where)));
        missingorgunit.put("category", database.getCount(database.getCount("Categorie").where(where)));
        missingorgunit.put("user", database.getCount(database.getCount("User").where(where)));

        return missingorgunit;
    }

    /*
     * 2009-05-12 cklein
     *
     * fixed as part of issue #1530 - deletion of orgunits and cascaded deletion of both workareas and person to workarea assignments
     * 									note that in expectance of a major overhaul of the way that workareas are handled, the sql datamodel
     * 									will not be changed now.
     * 2015-03-13 - We have one Press category for every Mandant. That will be deleted when we want to delete one of these mandants
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean removeBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext) throws BeanException, IOException {
        final Database database = transactionContext.getDatabase();

        final Select select = SQL.Select(database);
        select.from("veraweb.tuser");
        select.select("COUNT(*)");
        select.where(Expr.equal("fk_orgunit", ((OrgUnit) bean).id));
        if (database.getCount(select) > 0) {
            if (this.tmp_errors == null) {
                throw new BeanException("Cannot delete Mandant while there are still users assigned to it");
            }
            final VerawebMessages messages = new VerawebMessages(octopusContext);
            this.tmp_errors.add(messages.getMessageOrgUnitBusy());
            return false;
        }

        // first remove all workArea assignments from all persons
        WorkAreaWorker.removeAllWorkAreasFromOrgUnit(transactionContext, ((OrgUnit) bean).id);
        final Delete deleteStatement = database.getDelete("OrgUnit");
        deleteStatement.byId("pk", ((OrgUnit) bean).id);
        transactionContext.execute(deleteStatement);
        transactionContext.commit();

        // Remove category pressevertreter of the current mandant
        deletePressCategoryByOrgUnit(transactionContext, ((OrgUnit) bean).id);

        return true;
    }

    /**
     * Delete press category linked to the deleted mandant
     *
     * @param transactionContext TransactionContext
     * @param orgUnitId Integer
     * @throws BeanException FIXME
     * @throws IOException FIXME
     */
    private void deletePressCategoryByOrgUnit(TransactionContext transactionContext, Integer orgUnitId) throws BeanException, IOException {

        final Database database = transactionContext.getDatabase();

        final Delete deleteStatement = database.getDelete("Categorie");
        deleteStatement.where(Where.and(Expr.equal("fk_orgunit", orgUnitId), Expr.equal("catname", "Pressevertreter")));

        transactionContext.execute(deleteStatement);
        transactionContext.commit();
    }

    /**
     * Stellt für die Dauer der Bean-Löschung durch die Elternmethode
     * {@link de.tarent.octopus.beans.BeanListWorker#removeSelection(OctopusContext, List, List, TransactionContext)
     * der #removeBean(OctopusContext, Bean, TransactionContext) Methode
     * die Fehlerliste zum Auffüllen zur Verfügung.
     *
     * @param octopusContext        OctopusContext
     * @param errors    kumulierte Fehlerliste
     * @param selection    zu löschende Auswahl
     * @param context    TransactionContext
     * @return Anzahl erfolgreich gelöschter Beans
     * @throws BeanException
     * @throws IOException
     */
    @Override
    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
        this.tmp_errors = errors;
        int count = super.removeSelection(cntx, errors, selection, context);
        this.tmp_errors = null;
        return count;
    }

}
