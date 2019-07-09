package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.WorkArea;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.engine.Result;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The class WorkAreaWorker is a concrete worker
 * for operations on the {@link WorkArea} entity
 * bean.
 *
 * @author cklein
 * @see de.tarent.aa.veraweb.beans.WorkArea
 * @since 1.2.0
 */
public class WorkAreaWorker extends StammdatenWorker {
    /**
     * Constructs a new instance of this.
     */
    public WorkAreaWorker() {
        super("WorkArea");
    }

    @Override
    protected void saveBean(OctopusContext octopusContext, Bean bean, TransactionContext context)
      throws BeanException, IOException {
        WorkArea workArea = (WorkArea) bean;
        if (workArea.orgunit == null) {
            workArea.orgunit = ((PersonalConfigAA) octopusContext.personalConfig()).getOrgUnitId();
        }

        if (workArea.orgunit == null) {
            handleMissingOrgunitErrorMessage(octopusContext, workArea);
        } else if (workArea.name.trim().isEmpty()) {
            handleWorkareaNameEmptyErrorMessage(octopusContext);
        } else {
            super.saveBean(octopusContext, workArea, context);
        }
    }

    @Override
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        // hide default entry with pk=0 from user, the workarea "Kein" with pk ::= 0
        // is only used internally in order to be able to use foreign key constraints
        // with individual workareas being assigned to one or multiple users.
        select.where(Expr.greater("pk", 0));
        select.where(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
    }

    @Override
    protected void extendColumns(OctopusContext cntx, Select select)
      throws BeanException, IOException {
        if (cntx.requestContains("order")) {
            String order = cntx.requestAsString("order");
            if ("name".equals(order)) {
                select.orderBy(Order.asc(order));
                cntx.setContent("order", order);
            } else if ("flags".equals(order)) {
                select.orderBy(Order.asc(order).andAsc("name"));
                cntx.setContent("order", order);
            }
        }
    }

    @Override
    /*
     * 2009-05-12 cklein
     *
     * fixed as part of issue #1530 - deletion of workareas and automatic unassignment from existing persons
     */
    protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext transactionContext)
      throws BeanException, IOException {
        Database database = transactionContext.getDatabase();
        // first remove all workArea assignments from all persons
        PersonListWorker.unassignWorkArea(transactionContext, ((WorkArea) bean).id, null);
        Delete stmt = database.getDelete("WorkArea");
        stmt.byId("pk", ((WorkArea) bean).id);
        transactionContext.execute(stmt);
        transactionContext.commit();
        return true;
    }

    /*
     * 2009-05-12 cklein
     *
     * introduced as part of fix for issue #1530 - deletion of orgunits and automatic deletion of associated work areas. will
     * not commit itself.
     */
    @SuppressWarnings("unchecked")
    public static void removeAllWorkAreasFromOrgUnit(TransactionContext transactionContext, Integer orgUnitId)
      throws BeanException, IOException {
        Select stmt = transactionContext.getDatabase().getSelect("WorkArea");
        stmt.select("pk");
        stmt.where(Expr.equal("fk_orgunit", orgUnitId));

        try {
            ResultSet beans = ((Result) stmt.execute()).resultSet();
            while (beans.next()) {
                // first remove all workArea assignments from all persons
                PersonListWorker.unassignWorkArea(transactionContext, beans.getInt("pk"), null);
                Delete delstmt = transactionContext.getDatabase().getDelete("WorkArea");
                delstmt.byId("pk", beans.getInt("pk"));
                transactionContext.execute(delstmt);
                transactionContext.commit();
            }
        } catch (SQLException e) {
            throw new BeanException("Die dem Mandanten zugeordneten Arbeitsbereiche konnten nicht entfernt werden.", e);
        }
    }

    @Override
    protected void extendAll(OctopusContext cntx, Select select)
      throws BeanException, IOException {
        // hide default entry with pk=0 from user, the workarea "Kein" with pk ::= 0
        // is only used internally in order to be able to use foreign key constraints
        // with individual workareas being assigned to one or multiple users.
        select.where(Expr.greater("pk", 0));
        select.where(Expr.equal("fk_orgunit", ((PersonalConfigAA) cntx.personalConfig()).getOrgUnitId()));
    }

    @Override
    public void getAll(OctopusContext cntx) throws BeanException, IOException {
        super.getAll(cntx);

        Integer count = cntx.requestAsInteger("count");
        if (count != null) {
            cntx.setContent("count", count);
        }
    }

    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
        if (octopusContext.getContextField("listerrors") != null) {
            Integer noMessages = -1;
            octopusContext.setContent("countInsert", noMessages);
            octopusContext.setContent("countUpdate", noMessages);
        }
        return super.showList(octopusContext);
    }

    private void handleWorkareaNameEmptyErrorMessage(OctopusContext octopusContext) {
        final LanguageProvider languageProvider = initLanguageProvider(octopusContext);
        List<String> errors = new ArrayList<>();
        errors.add(languageProvider.getProperty("WORKAREA_ERROR_MISSING_NAME"));
        octopusContext.setContent(OUTPUT_saveListErrors, errors);
    }

    private String getErrorMessageMissingOrgunit(OctopusContext octopusContext, WorkArea workArea) {
        final LanguageProvider languageProvider = initLanguageProvider(octopusContext);
        return languageProvider.getProperty("WORKAREA_ERROR_INCORRECT_NAME_ONE") + workArea.name +
          languageProvider.getProperty("WORKAREA_ERROR_INCORRECT_NAME_TWO");
    }

    private LanguageProvider initLanguageProvider(OctopusContext octopusContext) {
        final LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
        return languageProviderHelper.enableTranslation(octopusContext);
    }

    private void handleMissingOrgunitErrorMessage(OctopusContext octopusContext, WorkArea workArea) {
        List<String> errors;
        final String errorMessage;
        if (workArea.getErrors() != null && !workArea.getErrors().isEmpty()) {
            errors = workArea.getErrors();
        } else {
            errors = (List<String>) octopusContext.getContextField(OUTPUT_saveListErrors);
        }
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errorMessage = getErrorMessageMissingOrgunit(octopusContext, workArea);
        errors.add(errorMessage);
        octopusContext.setContent(OUTPUT_saveListErrors, errors);
    }
}
