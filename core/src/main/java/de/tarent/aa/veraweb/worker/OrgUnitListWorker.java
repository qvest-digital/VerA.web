package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
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
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
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
     * @param errors         kummulierte Fehlerliste
     * @param bean           einzufügendes Bean
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    @Override
    @SuppressWarnings("unchecked")
    protected int insertBean(OctopusContext octopusContext, List errors, Bean bean, TransactionContext transactionContext)
      throws BeanException, IOException {
        int count = 0;
        boolean savedSuccessfully = updateOrgUnit(octopusContext, errors, transactionContext, bean, false);
        if (savedSuccessfully) {
            count++;
        }
        return count;
    }

    /**
     * Wird von {@link de.tarent.octopus.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll die übergebene Liste von Beans aktualisieren.
     *
     * @param octopusContext Octopus-Kontext
     * @param errors         kummulierte Fehlerliste
     * @param orgUnitList    Liste von zu aktualisierenden Beans
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    @Override
    @SuppressWarnings("unchecked")
    protected int updateBeanList(OctopusContext octopusContext, List errors, List orgUnitList,
      TransactionContext transactionContext)
      throws IOException, BeanException {
        int count = 0;
        for (Object obj : orgUnitList) {
            final Bean bean = (Bean) obj;

            boolean savedSuccessfully = updateOrgUnit(octopusContext, errors, transactionContext, bean, true);
            if (savedSuccessfully) {
                count++;
            }
        }
        return count;
    }

    private boolean updateOrgUnit(OctopusContext octopusContext, List errors, TransactionContext transactionContext, Bean bean,
      boolean isUpdate)
      throws BeanException, IOException {
        if (bean.isModified()) {
            if (bean.isCorrect()) {
                if (bean instanceof OrgUnit) {
                    boolean isValid =
                      executeAdditionalChecks(octopusContext, (OrgUnit) bean, transactionContext, errors, isUpdate);
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

    private boolean executeAdditionalChecks(OctopusContext octopusContext, OrgUnit orgUnit, TransactionContext transactionContext,
      List errors,
      boolean isUpdate) throws BeanException, IOException {
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

    private OrgUnit getExistingBean(boolean isUpdate, TransactionContext transactionContext, OrgUnit orgUnit)
      throws BeanException, IOException {
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

    private OrgUnit getOrgUnitByName(TransactionContext transactionContext, OrgUnit orgunitBean, Database database)
      throws BeanException, IOException {
        return (OrgUnit) database.getBean("OrgUnit",
          database.getSelect("OrgUnit").
            where(Expr.equal("unitname", orgunitBean.name)), transactionContext);
    }

    private OrgUnit getOrgUnitByNameAndId(TransactionContext transactionContext, OrgUnit orgunitBean, Database database)
      throws BeanException, IOException {
        return (OrgUnit) database.getBean("OrgUnit",
          database.getSelect("OrgUnit").
            where(Where.and(
              Expr.equal("unitname", orgunitBean.name),
              Expr.notEqual("pk", orgunitBean.id))), transactionContext);
    }

    //
    // weitere Octopus-Aktionen
    //

    /**
     * Octopus-Eingabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)}
     */
    public static final String INPUT_cleanupDatabase[] = { "orgunit" };
    /**
     * Octopus-Eingabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)}
     */
    public static final boolean MANDATORY_cleanupDatabase[] = { false };
    /**
     * Octopus-Ausgabe-Parameter für {@link #cleanupDatabase(OctopusContext, Integer)}
     */
    public static final String OUTPUT_cleanupDatabase = "missingorgunit";

    /**
     * Zeigt eine Statistik über 'verloren' gegangene Datensätze an.
     * Wenn der Parameter <code>orgunit</code> übergeben wird werden
     * alle Datensätze ohne gültigen Mandanten diesem zugeordnet.
     *
     * @param octopusContext Octopus-Context-Instanz
     * @param orgunit        Neue Orgunit-ID
     * @return missingorgunit
     * @throws BeanException FIXME
     * @throws IOException   FIXME
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
     * fixed as part of issue #1530 - deletion of orgunits and cascaded deletion of both workareas and person to workarea
     * assignments
     * 									note that in expectance of a major overhaul of the way that workareas are handled, the
     * 									sql datamodel
     * 									will not be changed now.
     * 2015-03-13 - We have one Press category for every Mandant. That will be deleted when we want to delete one of these
     * mandants
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean removeBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
      throws BeanException, IOException {
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
     * @param orgUnitId          Integer
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    private void deletePressCategoryByOrgUnit(TransactionContext transactionContext, Integer orgUnitId)
      throws BeanException, IOException {

        final Database database = transactionContext.getDatabase();

        final Delete deleteStatement = database.getDelete("Categorie");
        deleteStatement.where(Where.and(Expr.equal("fk_orgunit", orgUnitId), Expr.equal("catname", "Pressevertreter")));

        transactionContext.execute(deleteStatement);
        transactionContext.commit();
    }

    /**
     * Stellt für die Dauer der Bean-Löschung durch die Elternmethode
     * {@link de.tarent.octopus.beans.BeanListWorker#removeSelection(OctopusContext, List, List, TransactionContext)}
     * der #removeBean(OctopusContext, Bean, TransactionContext) Methode
     * die Fehlerliste zum Auffüllen zur Verfügung.
     *
     * @param cntx      OctopusContext
     * @param errors    kumulierte Fehlerliste
     * @param selection zu löschende Auswahl
     * @param context   TransactionContext
     * @return count
     * @throws BeanException BeanException
     * @throws IOException   IOException
     */
    @Override
    protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context)
      throws BeanException, IOException {
        this.tmp_errors = errors;
        int count = super.removeSelection(cntx, errors, selection, context);
        this.tmp_errors = null;
        return count;
    }
}
