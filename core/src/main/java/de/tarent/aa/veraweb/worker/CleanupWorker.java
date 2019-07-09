package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web, comprised of…
 * VerA.web, platform-independent webservice-based event management
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 * … is newly MIT-licenced and Copyright
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2005, 2006, 2007 asteban (s.mancke@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2009 Sven Frommeyer (s.frommeyer@tarent.de)
 *  © 2014, 2018 Dominik George (d.george@tarent.de)
 *  © 2013 Martin Gernhardt (m.gernhardt@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2007, 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2006, 2007, 2010, 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018, 2019 Benedict Hoeger (b.hoeger@tarent.de)
 *  © 2018, 2019 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018, 2019 Timo Kanera (t.kanera@tarent.de)
 *  © 2006 Philipp Kirchner (p.kirchner@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2006 Michael Kleinhenz (m.kleinhenz@tarent.de)
 *  © 2006 Michael Klink (m.klink@tarent.de)
 *  © 2007 Fabian Köster (f.koester@tarent.de)
 *  © 2006, 2014 Martin Ley (m.ley@tarent.de)
 *  © 2007 Alex Maier (a.maier@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019
 *     mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2006, 2007 Jens Neumaier (j.neumaier@tarent.de)
 *  © 2006 Nils Neumaier (n.neumaier@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2007, 2008, 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2008, 2009 Christian Preilowski (c.thiel@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2006, 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2007 Robert Schuster (r.schuster@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2008, 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
