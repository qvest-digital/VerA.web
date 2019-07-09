package de.tarent.aa.veraweb.worker;
import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse übernimmt einige Aufräumarbeiten in der Datenbank.
 *
 * @author Christoph Jerolimov
 */
@Log4j2
public class CleanupWorker {
    /**
     * Octopus Input-Parameter für {@link #summarizeCategories(OctopusContext)}.
     */
    public static final String INPUT_summarizeCategories[] = {};

    /**
     * Diese Methode fasst mehrere Kategorien als eine Zusammen.
     * Alle Kategorien des Schemas <code>catname n</code> werden
     * in die Kategorie <code>catname</code> übernommen.
     *
     * @param cntx Octopus context
     * @throws BeanException beanException
     * @throws IOException   ioException
     */
    public void summarizeCategories(OctopusContext cntx) throws BeanException, IOException {
        summarizeCategoriesB(cntx, new DatabaseVeraWeb(cntx));
    }

    /**
     * @param cntx     octupus context
     * @param database database
     * @throws BeanException beanException
     * @throws IOException   ioexception
     * @see #summarizeCategories(OctopusContext)
     */
    protected void summarizeCategoriesB(OctopusContext cntx, Database database) throws BeanException, IOException {
        if (logger.isInfoEnabled()) {
            logger.info("Fasse automatisch mehrere Kategorien / Ereignisse zusammen. (summarizeCategoriesB)");
        }

        WhereList whereList = new WhereList();
        whereList.addAnd(Expr.greater("length(catname)", new Integer(2)));
        whereList.addAnd(Expr.equal("substr(catname, length(catname) - 1, 1)", " "));
        whereList.addAnd(Expr.greaterOrEqual("substr(catname, length(catname), 1)", "0"));
        whereList.addAnd(Expr.lessOrEqual("substr(catname, length(catname), 1)", "9"));

        List cleanupOrgunits = (List) cntx.contentAsObject("cleanupOrgunits");
        if (cleanupOrgunits != null) {
            WhereList wl = whereList;
            if (cleanupOrgunits.contains(null)) {
                cleanupOrgunits.remove(null);
                if (cleanupOrgunits.isEmpty()) {
                    whereList = new WhereList();
                    whereList.addAnd(Expr.isNull("fk_orgunit"));
                    whereList.addAnd(wl);
                } else {
                    whereList = new WhereList();
                    whereList.addAnd(Where.or(
                      Expr.isNull("fk_orgunit"),
                      Expr.in("fk_orgunit", cleanupOrgunits)));
                    whereList.addAnd(wl);
                }
            } else if (!cleanupOrgunits.isEmpty()) {
                whereList = new WhereList();
                whereList.addAnd(Expr.in("fk_orgunit", cleanupOrgunits));
                whereList.addAnd(wl);
            }
        }

        List illegalCategories =
          database.getList(
            database.getSelect("Categorie").where(
              whereList), database);

        for (Iterator it = illegalCategories.iterator(); it.hasNext(); ) {
            Map illegalCategory = (Map) it.next();
            Integer catpk = (Integer) illegalCategory.get("id");
            String catname = (String) illegalCategory.get("name");
            Integer orgunit = (Integer) illegalCategory.get("orgunit");

            if (catpk == null || catname == null || catname.length() <= 2) {
                continue;
            }

            String plainname = catname.substring(0, catname.length() - 2);

            Categorie topcategorie;
            if (orgunit == null) {
                topcategorie = (Categorie)
                  database.getBean("Categorie",
                    database.getSelect("Categorie").where(Where.and(
                      Expr.equal("catname", plainname),
                      Expr.isNull("fk_orgunit"))));
            } else {
                topcategorie = (Categorie)
                  database.getBean("Categorie",
                    database.getSelect("Categorie").where(Where.and(
                      Expr.equal("catname", plainname),
                      Expr.equal("fk_orgunit", orgunit))));
            }

            if (topcategorie != null) {
                addMessage(cntx, "\u00DCberf\u00FChre Daten " +
                  "aus Kategorie \"" + catname + "\" (" + catpk + ")" +
                  " in Kategorie \"" + topcategorie.name + "\" (" + topcategorie.id + ").");

                if (isActivated(cntx)) {
                    conferCategorie(database, catpk, topcategorie.id);
                    cntx.setContent("cleanupdone", Boolean.TRUE);
                }
            } else {
                addMessage(cntx, "Korrigiere Kategorienamen von " +
                  " \"" + catname + "\" (" + catpk + ")" +
                  " nach \"" + plainname + "\".");

                if (isActivated(cntx)) {
                    renameCategorie(database, catpk, plainname);
                    cntx.setContent("cleanupdone", Boolean.TRUE);
                }
            }
        }
    }

    protected void conferCategorie(Database database, Integer subcategorypk, Integer topcategorypk)
      throws BeanException {

        final TransactionContext transactionContext = database.getTransactionContext();
        try {
            transactionContext.execute(SQL.Update(database).
              table("veraweb.tguest").
              update("fk_category", topcategorypk).
              where(Expr.equal("fk_category", subcategorypk)));
            transactionContext.execute(SQL.Update(database).
              table("veraweb.tperson_categorie").
              update("fk_categorie", topcategorypk).
              where(Expr.equal("fk_categorie", subcategorypk)));
            transactionContext.execute(SQL.Delete(database).from("veraweb.tperson_categorie").where(
              Expr.in("pk", new RawClause("(" +
                "SELECT tpc1.pk FROM veraweb.tperson_categorie tpc1" +
                " JOIN veraweb.tperson_categorie tpc2 ON (" +
                "tpc1.fk_person = tpc2.fk_person AND " +
                "tpc1.fk_categorie = tpc2.fk_categorie AND " +
                "tpc1.pk < tpc2.pk))"))));
            transactionContext.execute(SQL.Delete(database).
              from("veraweb.tcategorie").
              where(Expr.equal("pk", subcategorypk)));
            transactionContext.commit();
        } catch (BeanException e) {
            logger.error("Persisting username failed", e);
        }
    }

    protected void renameCategorie(Database database, Integer categorypk, String newname) throws BeanException {
        final TransactionContext transactionContext = database.getTransactionContext();
        try {
            transactionContext.execute(SQL.Update(database).
              table("veraweb.tcategorie").
              update("catname", newname).
              where(Expr.equal("pk", categorypk)));
            transactionContext.commit();
        } catch (BeanException e) {
            logger.error("Updating category failed", e);
        }
    }

    protected void addMessage(OctopusContext cntx, String message) {
        logger.info(message);

        List list = (List) cntx.contentAsObject("cleanup");
        if (list == null) {
            list = new ArrayList();
            cntx.setContent("cleanup", list);
        }
        list.add(message);
    }

    protected boolean isActivated(OctopusContext cntx) {
        String r = cntx.requestAsString("force");
        String c = cntx.contentAsString("force");
        return (r != null && r.equals("true")) || (c != null && c.equals("true"));
    }
}
