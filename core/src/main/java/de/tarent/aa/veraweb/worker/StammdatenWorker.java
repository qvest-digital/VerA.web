package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.WorkArea;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Testet ob bereits ein Stammdaten-Eintrag mit dem selben Namen existiert.
 * Bietet zusätzlich einen Task für Direkteinsprungsmarken an.
 */
public class StammdatenWorker extends ListWorkerVeraWeb {
    //
    // Konstruktoren
    //

    /**
     * Dieser Konstruktor gibt den Namen der zugrunde liegenden Bean weiter.
     */
    protected StammdatenWorker(String beanName) {
        super(beanName);
    }

    //
    // Hilfsmethoden
    //
    @Override
    protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
        Database database = getDatabase(cntx);

        Clause clause = getWhere(cntx);

        StringBuffer buffer = new StringBuffer();
        if (clause != null && clause.clauseToString().length() != 0) {
            buffer.append("(");
            buffer.append(clause.clauseToString());
            buffer.append(") AND ");
        }
        buffer.append(getAlphaStartColumn(database));
        buffer.append(" < '");

        Escaper.escape(buffer, start);
        buffer.append("'");

        Select select = database.getCount(BEANNAME);
        select.where(new RawClause(buffer));

        Integer i = database.getCount(select);
        return new Integer(i.intValue() - (i.intValue() % getLimit(cntx).intValue()));
    }

    protected String getAlphaStartColumn(Database database) throws BeanException, IOException {
        return database.getProperty(database.createBean(BEANNAME), "name");
    }

    protected Clause getWhere(OctopusContext cntx) throws BeanException {
        return null;
    }

    @Override
    protected int insertBean(OctopusContext octopusContext, List errors, Bean bean, TransactionContext context)
      throws BeanException, IOException {
        int count = 0;
        if (bean.isModified()) {
            if (bean instanceof WorkArea) {
                ((WorkArea) bean).verify(octopusContext);
            }

            if (bean.isCorrect()) {
                Database database = context.getDatabase();

                String orgunit = database.getProperty(bean, "orgunit");
                Clause clause = Expr.equal(
                  database.getProperty(bean, "name"),
                  bean.getField("name"));
                if (orgunit != null) {
                    clause = Where.and(Expr.equal(orgunit, ((PersonalConfigAA) (octopusContext.personalConfig())).getOrgUnitId()),
                      clause);
                }

                Integer exist =
                  database.getCount(
                    database.getCount(bean).
                      where(clause), context);
                if (exist.intValue() != 0) {
                    LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
                    LanguageProvider languageProvider = languageProviderHelper.enableTranslation(octopusContext);

                    errors.add(languageProvider.getProperty("MAIN_DATA_WARN_ALREADY_EXISTS") +
                      " '" + bean.getField("name") + "'.");
                } else {
                    count += super.insertBean(octopusContext, errors, bean, context);
                }
            } else {
                errors.addAll(bean.getErrors());
            }
        }

        return count;
    }

    protected int updateBeanList(OctopusContext cntx, List errors, List beanlist, TransactionContext context)
      throws BeanException, IOException {
        int count = 0;
        for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
            Bean bean = (Bean) it.next();
            if (bean.isModified()) {
                if (bean.isCorrect()) {
                    saveBean(cntx, bean, context);
                    count++;
                } else {
                    errors.addAll(bean.getErrors());
                }
            }
        }
        return count;
    }
}
