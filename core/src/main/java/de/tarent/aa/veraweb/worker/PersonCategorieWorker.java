package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.PersonCategorie;
import de.tarent.aa.veraweb.utils.VerawebMessages;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dieser Octopus-Worker bearbeitet Personen-Kategorien-Listen.
 */
public class PersonCategorieWorker extends ListWorkerVeraWeb {
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public PersonCategorieWorker() {
        super("PersonCategorie");
    }

    //
    // Oberklasse BeanListWorker
    //
    @Override
    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        Bean bean = (Bean) octopusContext.contentAsObject("person");
        select.where(Expr.equal("fk_person", bean.getField("id")));
    }

    @Override
    protected void extendColumns(OctopusContext octopusContext, Select select) throws BeanException, IOException {
        select.join("veraweb.tcategorie", "fk_categorie", "tcategorie.pk");
        select.selectAs("tcategorie.rank", "catrank");
        select.selectAs("tcategorie.catname", "name");
        select.selectAs("tcategorie.flags", "flags");
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException, IOException {
        handleRemoveCategoriesFromGuest(octopusContext);

        super.saveList(octopusContext);

        String addRank = octopusContext.requestAsString("add-rank");
        String addCategorie = octopusContext.requestAsString("add-categorie");
        if (addRank != null && addRank.length() != 0 && (addCategorie == null || addCategorie.length() == 0)) {
            List errors = (List) octopusContext.contentAsObject(OUTPUT_saveListErrors);
            if (errors == null) {
                errors = new ArrayList();
                octopusContext.setContent(OUTPUT_saveListErrors, errors);
            }

            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);

            errors.add(languageProvider.getProperty("PERSON_CATEGORIES_SELECT_WARNING"));
        }

        Integer countRemove = (Integer) octopusContext.getContextField("countRemove");
        Integer countUpdate = (Integer) octopusContext.getContextField("countUpdate");
        Integer countInsert = (Integer) octopusContext.getContextField("countInsert");

        if (countRemove != null || countUpdate != null || countInsert != null) {
            VerawebMessages verawebMessages = new VerawebMessages(octopusContext);
            octopusContext.setContent("noChangesMessage", verawebMessages.getMessageNoChanges());
        }

        String entity = octopusContext.requestAsString("entity");
        if (entity != null && entity.length() != 0) {
            octopusContext.setContent("noChangesMessage", false);
            octopusContext.setContent("entityOverwrite", entity);

            if ("PERSON".equals(entity)) {
                octopusContext.setContextField("countInsert", null);
                octopusContext.setContextField("countUpdate", countInsert);
            }
        }
    }

    private void handleRemoveCategoriesFromGuest(OctopusContext octopusContext) throws BeanException {
        Map requestParameters = octopusContext.getRequestObject().getRequestParameters();

        // First we check, if we have an "delete" action
        if (requestParameters.get("remove") != null && requestParameters.get("remove").toString().equals("Entfernen")) {
            handleDeleteAction(octopusContext, requestParameters);
        }
    }

    private void handleDeleteAction(OctopusContext octopusContext, Map requestParameters) throws BeanException {
        for (Object requestParameter : requestParameters.entrySet()) {
            // We need to filter the request parameter(s) by pattern "bean.id-select".
            // The bean.id is the category id from the table tperson_category
            if (requestParameter.toString().contains("-")) {
                deleteCategoryFromGuest(octopusContext, requestParameters, requestParameter);
            }
        }
    }

    /**
     * Handle request parameter conform with the pattern bean.id-select. The bean.id is the category id from the table
     * tperson_category.
     *
     * @param octopusContext    The {@link de.tarent.octopus.server.OctopusContext}
     * @param requestParameters All request parameters
     * @param requestParameter  The current request parameter, which is inspected and potentially used for deletion
     * @throws BeanException BeanException
     */
    private void deleteCategoryFromGuest(OctopusContext octopusContext, Map requestParameters, Object requestParameter)
      throws BeanException {
        final String[] requestParameterParts = requestParameter.toString().split("-");
        final String[] selectParts = requestParameterParts[1].split("=");
        if (selectParts[0].equals("select") && selectParts[1].equals("true")) {
            final Integer categoryId = getCategoryId(requestParameters, requestParameterParts[0]);
            final Integer personId = new Integer(requestParameters.get("add-person").toString());
            updateGuestData(octopusContext, categoryId, personId);
        }
    }

    private void updateGuestData(OctopusContext octopusContext, Integer categoryId, Integer personId)
      throws BeanException {
        final DatabaseVeraWeb database = new DatabaseVeraWeb(octopusContext);
        final Update update = SQL.Update(database).
          table("veraweb.tguest").
          update("fk_category", null).
          where(Expr.equal("fk_category", categoryId)).
          whereAnd(Expr.equal("fk_person", personId));

        final TransactionContext transactionalContext = new DatabaseVeraWeb(octopusContext).getTransactionContext();
        transactionalContext.execute(update);
    }

    private Integer getCategoryId(Map requestParameters, String requestParameterPart) {
        final Integer personCategoryId = new Integer(requestParameterPart);
        return new Integer(requestParameters.get(personCategoryId + "-categorie").toString());
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext transactionContext)
      throws BeanException, IOException {
        super.saveBean(octopusContext, bean, transactionContext);
        WorkerFactory.getPersonDetailWorker(octopusContext).
          updatePerson(octopusContext, null, ((PersonCategorie) bean).person);
    }

    public void addCategoryAssignment(OctopusContext octopusContext, Integer categoryId, Integer personId,
      Database database, TransactionContext transactionContext)
      throws BeanException, IOException {
        addCategoryAssignment(octopusContext, categoryId, personId, database, transactionContext, true);
    }

    public PersonCategorie addCategoryAssignment(final OctopusContext octopusContext, Integer categoryId,
      Integer personId, Database database,
      TransactionContext transactionContext, boolean save)
      throws BeanException, IOException {
        Categorie category =
          (Categorie) database.getBean("Categorie", categoryId, transactionContext == null ? database : transactionContext);
        if (category != null) {
            Select select = database.getCount("PersonCategorie");
            select.where(Expr.equal("fk_person", personId));
            select.whereAnd(Expr.equal("fk_categorie", categoryId));
            Integer count = database.getCount(select);

            if (count.intValue() == 0) {
                PersonCategorie personCategory = (PersonCategorie) database.createBean("PersonCategorie");
                personCategory.categorie = categoryId;
                personCategory.person = personId;
                personCategory.rank = category.rank;

                personCategory.verify(octopusContext);

                if (personCategory.isCorrect()) {
                    if (save) {
                        this.saveBean(octopusContext, personCategory, transactionContext);
                    }
                    return personCategory;
                }
            }
        }

        return null;
    }

    public void removeAllCategoryAssignments(OctopusContext octopusContext, Integer personId, Database database,
      TransactionContext transactionContext) throws BeanException, IOException {
        try {
            transactionContext.execute(
              SQL.Delete(database).from("veraweb.tperson_categorie").where(Expr.equal("fk_person", personId))
            );
            transactionContext.commit();
        } catch (BeanException e) {
            throw new BeanException("Die Kategoriezuweisungen konnte nicht aufgehoben werden.", e);
        }
    }

    public void removeCategoryAssignment(OctopusContext octopusContext, Integer categoryId, Integer personId,
      Database database, TransactionContext transactionContext)
      throws BeanException, IOException {
        Categorie category =
          (Categorie) database.getBean("Categorie", categoryId, transactionContext == null ? database : transactionContext);
        if (category != null) {
            Select select = database.getSelect("PersonCategorie");
            select.where(Expr.equal("fk_person", personId));
            select.whereAnd(Expr.equal("fk_categorie", categoryId));
            PersonCategorie personCategory = (PersonCategorie) database.getBean("PersonCategorie", select, transactionContext);
            if (personCategory != null) {
                database.removeBean(personCategory, transactionContext);
            }
        }
    }
}
