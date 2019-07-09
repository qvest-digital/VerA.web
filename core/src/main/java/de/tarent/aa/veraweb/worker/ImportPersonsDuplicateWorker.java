package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.ImportPerson;
import de.tarent.aa.veraweb.beans.Person;
import de.tarent.aa.veraweb.beans.facade.PersonConstants;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Dieser Octopus-Worker bearbeitet Import-Personen-Duplikatslisten.
 *
 * @author hendrik
 */
@Log4j2
public class ImportPersonsDuplicateWorker extends ListWorkerVeraWeb {
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public ImportPersonsDuplicateWorker() {
        super("ImportPerson");
    }

    /**
     * @see de.tarent.octopus.beans.BeanListWorker#showList(de.tarent.octopus.server.OctopusContext)
     */
    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
        final Map importDuplicatesProperties = (Map) octopusContext.moduleConfig().getParams().get("importDuplicatesProperties");
        if (importDuplicatesProperties == null) {
            logger.warn("Konfiguration für die Duplikatbearbeitung beim Personen-Import wurde nicht gefunden.");
        }
        if (octopusContext.sessionAsObject("limit" + BEANNAME) == null) {
            octopusContext.setSession("limit" + BEANNAME, Integer.parseInt((String) importDuplicatesProperties.get("dsCount")));
        }

        final List beans = super.showList(octopusContext);

        // Zu den Duplikatdatensätzen noch einige Beispiel-Duplikate hinzufügen.
        int dsCount = -1;
        if (importDuplicatesProperties != null) {
            dsCount = Integer.parseInt((String) importDuplicatesProperties.get("dupCount"));
        }

        final Database database = getDatabase(octopusContext);
        if (beans != null) {
            for (Iterator it = beans.iterator(); it.hasNext(); ) {
                final ImportPerson importPerson = (ImportPerson) it.next();
                importPerson.setMoreDuplicates(false);

                if (importPerson.getDuplicateList() == null) {
                    List dups = null;
                    StringTokenizer tokenizer = new StringTokenizer(
                      importPerson.duplicates,
                      Character.toString(ImportPerson.PK_SEPARATOR_CHAR)
                    );

                    int count = 0;
                    while (tokenizer.hasMoreTokens()) {
                        if (dsCount != -1 && count >= dsCount) {
                            importPerson.setMoreDuplicates(true);
                            break;
                        }
                        Integer pk = new Integer(tokenizer.nextToken());
                        final Select select = getPersonSelect(database, pk);
                        final Person person = (Person) database.getBean("Person", select);
                        dups = new LinkedList();
                        if (person.getId() != null) {
                            dups.add(person);
                            count++;
                        }
                    }
                    if (dups == null) {
                        dups = Collections.EMPTY_LIST;
                    }
                    importPerson.setDuplicateList(dups);
                }
            }
        }
        return beans;
    }

    private Select getPersonSelect(Database database, Integer pk) throws BeanException, IOException {
        Person person = new Person();
        person.setField("id", pk);
        Select select = database.getSelect(person);
        select.where(Where.and(
          Expr.equal("deleted", PersonConstants.DELETED_FALSE),
          database.getWhere(person)));
        return select;
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException, IOException {
        if (octopusContext.requestContains(INPUT_BUTTON_SAVE)) {
            final Database database = getDatabase(octopusContext);
            final TransactionContext transactionContext = database.getTransactionContext();
            final ImportPerson sample = new ImportPerson();
            final Long importId = getImportIdentifier(octopusContext);

            try {
                final Where whereClause = getWhereClause(importId);
                clearDuplicateFlags(database, transactionContext, sample, whereClause);

                final List selection = getSelection(octopusContext, null);

                if (selection != null && selection.size() > 0) {
                    setDuplicateFlag(database, transactionContext, sample, whereClause, selection);
                } else {
                    octopusContext.setContent("noDupsSelected", true);
                    transactionContext.commit();
                }
                setUpdateCounterInContext(octopusContext, selection);
            } catch (BeanException e) {
                // failed to commit
                transactionContext.rollBack();
                throw new BeanException(
                  "Die \u00c4nderungen an der Duplikatliste konnten nicht \u00fcbernommen werden.", e);
            }
        }
    }

    private void setUpdateCounterInContext(OctopusContext octopusContext, List selection) {
        if (selection != null) {
            octopusContext.setContent("countUpdate", selection.size());
        }
    }

    private void setDuplicateFlag(Database database, TransactionContext transactionContext, ImportPerson sample,
      Where whereClause, List selection)
      throws IOException, BeanException {
        // Markierungen wieder setzten.
        final Update update = SQL.Update(transactionContext);
        update.table(database.getProperty(sample, "table"));
        update.update("dupcheckstatus", ImportPerson.TRUE);
        update.where(Where.and(whereClause, Expr.in("pk", selection)));
        transactionContext.execute(update);
        transactionContext.commit();
    }

    private void clearDuplicateFlags(Database database, TransactionContext transactionContext, ImportPerson sample,
      Where whereClause)
      throws IOException, BeanException {
        // Entfernt alle markierungen in der Datenbank.
        final Update update = SQL.Update(transactionContext);
        update.table(database.getProperty(sample, "table"));
        update.update("dupcheckstatus", ImportPerson.FALSE);
        update.where(whereClause);
        transactionContext.execute(update);
        transactionContext.commit();
    }

    private Where getWhereClause(Long importId) {
        return Where.and(Expr.equal("deleted", PersonConstants.DELETED_FALSE), Expr.equal("fk_import", importId));
    }

    private Long getImportIdentifier(OctopusContext octopusContext) {
        final String importIdFromContext = octopusContext.requestAsString("importId");
        Long importId = null;
        if (importIdFromContext != null) {
            importId = new Long(octopusContext.requestAsString("importId"));
        }
        if (importId != null) {
            octopusContext.setSession("importId", importId);
        } else {
            importId = new Long(octopusContext.sessionAsObject("importId").toString());
        }
        return importId;
    }

    /**
     * Bedingung:
     * Es existieren Duplikate zu dem Datensatz.
     * Datensatz wurde noch nicht festgeschrieben.
     * Nur Datensätze von dem aktuellen Importvorgang.
     */
    @Override
    protected void extendWhere(OctopusContext octopusContext, Select select) throws BeanException {
        final Database database = getDatabase(octopusContext);
        final ImportPerson sample = new ImportPerson();
        final Long importId = getImportIdentifier(octopusContext);

        try {
            final WhereList list = getWhereList(database, sample, importId);
            select.where(list);
        } catch (IOException e) {
            throw new BeanException("Fehler beim Lesen von Bean-Parametern", e);
        }

        octopusContext.setContent("importId", importId);
    }

    private WhereList getWhereList(Database database, ImportPerson sample, Long importId) throws IOException {
        final WhereList list = new WhereList();
        list.addAnd(Expr.isNotNull(database.getProperty(sample, "duplicates")));
        list.addAnd(Expr.equal(database.getProperty(sample, "dupcheckaction"), ImportPerson.FALSE));
        list.addAnd(Expr.equal(database.getProperty(sample, "fk_import"), importId));
        return list;
    }

    @Override
    protected void extendColumns(OctopusContext octopusContext, Select select) throws BeanException {
        final Database database = getDatabase(octopusContext);
        try {
            select.orderBy(Order.asc(database.getProperty(database.createBean(BEANNAME), "lastname_a_e1")));
        } catch (IOException e) {
            throw new BeanException("Fehler beim Lesen von Bean-Parametern", e);
        }
    }
}
