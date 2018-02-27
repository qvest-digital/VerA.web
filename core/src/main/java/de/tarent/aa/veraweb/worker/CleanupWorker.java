package de.tarent.aa.veraweb.worker;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров <a.alexandrov@tarent.de>
 *  © 2018 Атанас Александров <sirakov@gmail.com>
 *  © 2013 Иванка Александрова <i.alexandrova@tarent.de>
 *  © 2013 Patrick Apel <p.apel@tarent.de>
 *  © 2016 Eugen Auschew <e.auschew@tarent.de>
 *  © 2013 Andrei Boulgakov <a.boulgakov@tarent.de>
 *  © 2013 Valentin But <v.but@tarent.de>
 *  © 2016 Lukas Degener <l.degener@tarent.de>
 *  © 2017 Axel Dirla <a.dirla@tarent.de>
 *  © 2015 Julian Drawe <j.drawe@tarent.de>
 *  © 2014 Dominik George <d.george@tarent.de>
 *  © 2013 Sascha Girrulat <s.girrulat@tarent.de>
 *  © 2008 David Goemans <d.goemans@tarent.de>
 *  © 2015 Viktor Hamm <v.hamm@tarent.de>
 *  © 2013 Katja Hapke <k.hapke@tarent.de>
 *  © 2013 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2007 jan <jan@evolvis.org>
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2016 Cristian Molina <c.molina@tarent.de>
 *  © 2017 Michael Nienhaus <m.nienhaus@tarent.de>
 *  © 2013 Claudia Nuessle <c.nuessle@tarent.de>
 *  © 2014, 2015 Jon Nunez Alvarez <j.nunez-alvarez@tarent.de>
 *  © 2016 Jens Oberender <j.oberender@tarent.de>
 *  © 2016, 2017 Miluška Pech <m.pech@tarent.de>
 *  © 2009 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2013 Marc Radel <m.radel@tarent.de>
 *  © 2013 Sebastian Reimers <s.reimers@tarent.de>
 *  © 2015 Charbel Saliba <c.saliba@tarent.de>
 *  © 2008, 2009, 2010 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2013 Volker Schmitz <v.schmitz@tarent.de>
 *  © 2014 Sven Schumann <s.schumann@tarent.de>
 *  © 2014 Sevilay Temiz <s.temiz@tarent.de>
 *  © 2013 Kevin Viola Schmitz <k.schmitz@tarent.de>
 *  © 2015 Stefan Walenda <s.walenda@tarent.de>
 *  © 2015, 2016, 2017 Max Weierstall <m.weierstall@tarent.de>
 *  © 2013 Rebecca Weinz <r.weinz@tarent.de>
 *  © 2015, 2016 Stefan Weiz <s.weiz@tarent.de>
 *  © 2015, 2016 Tim Zimmer <t.zimmer@tarent.de>
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
import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.dblayer.sql.Join;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.server.OctopusContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class CleanupWorker {
	/** Log4j logger Instanz. */
	private static final Logger logger = LogManager.getLogger(CleanupWorker.class);

	/** Octopus Input-Parameter für {@link #summarizeCategories(OctopusContext)}. */
	public static final String INPUT_summarizeCategories[] = {};
	/**
	 * Diese Methode fasst mehrere Kategorien als eine Zusammen.
	 * Alle Kategorien des Schemas <code>catname n</code> werden
	 * in die Kategorie <code>catname</code> übernommen.
	 *
	 * @param cntx Octopus context
	 */
	public void summarizeCategories(OctopusContext cntx) throws BeanException, IOException {
//		summarizeCategoriesA(octopusContext, new DatabaseVeraWeb(octopusContext));
		summarizeCategoriesB(cntx, new DatabaseVeraWeb(cntx));
	}

	/**
	 * @see #summarizeCategories(OctopusContext)
	 */
	protected void summarizeCategoriesA(OctopusContext cntx, Database database) throws BeanException {
		if (logger.isInfoEnabled())
			logger.info("Fasse automatisch mehrere Kategorien / Ereignisse zusammen. (summarizeCategoriesA)");

		Select orgunitsSelect = SQL.SelectDistinct( database ).
				selectAs("fk_orgunit", "fk_orgunit").
				from("veraweb.tcategorie");

		List orgunits = database.getList(orgunitsSelect, database);
		for (Iterator orgunitIt = orgunits.iterator(); orgunitIt.hasNext(); ) {
			Integer orgunit = (Integer)((Map)orgunitIt.next()).get("fk_orgunit");

			if (logger.isInfoEnabled())
				logger.info("Fasse automatisch mehrere Kategorien / Ereignisse für " +
						"den Mandanten #" + orgunit + " zusammen.");

			Select subcategoriesSelect = SQL.Select( database ).
			selectAs("c1.pk", "subcategorypk").
			selectAs("c1.catname", "subcategoryname").
			selectAs("c2.pk", "topcategorypk").
			selectAs("c2.catname", "topcategoryname").
			from("veraweb.tcategorie c1").
			join(new Join(Join.INNER, "veraweb.tcategorie c2", new RawClause(
					"c1.pk != c2.pk AND (" +
					"c1.catname = c2.catname OR " +
					"c1.catname = c2.catname || ' 0' OR c1.catname = c2.catname || ' 1' OR " +
					"c1.catname = c2.catname || ' 2' OR c1.catname = c2.catname || ' 3' OR " +
					"c1.catname = c2.catname || ' 4' OR c1.catname = c2.catname || ' 5' OR " +
					"c1.catname = c2.catname || ' 6' OR c1.catname = c2.catname || ' 7' OR " +
					"c1.catname = c2.catname || ' 8' OR c1.catname = c2.catname || ' 9')")));
			if (orgunit == null)
				subcategoriesSelect.where(Where.and(
						Expr.isNull("c1.fk_orgunit"),
						Expr.isNull("c2.fk_orgunit")));
			else
				subcategoriesSelect.where(Where.and(
						Expr.equal("c1.fk_orgunit", orgunit),
						Expr.equal("c2.fk_orgunit", orgunit)));

			List subcategories = database.getList(subcategoriesSelect, database);
			for (Iterator it = subcategories.iterator(); it.hasNext(); ) {
				Map entry = (Map)it.next();
				Integer subcategorypk = (Integer)entry.get("subcategorypk");
				String subcategoryname = (String)entry.get("subcategoryname");
				Integer topcategorypk = (Integer)entry.get("topcategorypk");
				String topcategoryname = (String)entry.get("topcategoryname");

				assert subcategorypk != null && topcategorypk != null;

				addMessage(cntx, "\u00DCberf\u00FChre Daten " +
						"aus Kategorie \"" + subcategoryname + "\" (" + subcategorypk + ")" +
						" in Kategorie \"" + topcategoryname + "\" (" + topcategorypk + ").");

				if (isActivated(cntx)) {
					conferCategorie(cntx, database, subcategorypk, topcategorypk);
					cntx.setContent("cleanupdone", Boolean.TRUE);
				}
			}
		}
	}

	/**
	 * @see #summarizeCategories(OctopusContext)
	 */
	protected void summarizeCategoriesB(OctopusContext cntx, Database database) throws BeanException, IOException {
		if (logger.isInfoEnabled())
			logger.info("Fasse automatisch mehrere Kategorien / Ereignisse zusammen. (summarizeCategoriesB)");

		WhereList whereList = new WhereList();

//		whereList.addOr(Expr.like("catname", "% 0"));
//		whereList.addOr(Expr.like("catname", "% 1"));
//		whereList.addOr(Expr.like("catname", "% 2"));
//		whereList.addOr(Expr.like("catname", "% 3"));
//		whereList.addOr(Expr.like("catname", "% 4"));
//		whereList.addOr(Expr.like("catname", "% 5"));
//		whereList.addOr(Expr.like("catname", "% 6"));
//		whereList.addOr(Expr.like("catname", "% 7"));
//		whereList.addOr(Expr.like("catname", "% 8"));
//		whereList.addOr(Expr.like("catname", "% 9"));

		whereList.addAnd(Expr.greater("length(catname)", new Integer(2)));
		whereList.addAnd(Expr.equal("substr(catname, length(catname) - 1, 1)", " "));
		whereList.addAnd(Expr.greaterOrEqual("substr(catname, length(catname), 1)", "0"));
		whereList.addAnd(Expr.lessOrEqual("substr(catname, length(catname), 1)", "9"));

		List cleanupOrgunits = (List)cntx.contentAsObject("cleanupOrgunits");
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
			Map illegalCategory = (Map)it.next();
			Integer catpk = (Integer)illegalCategory.get("id");
			String catname = (String)illegalCategory.get("name");
			Integer orgunit = (Integer)illegalCategory.get("orgunit");

			if (catpk == null || catname == null || catname.length() <= 2)
				continue;

			String plainname = catname.substring(0, catname.length() - 2);

			Categorie topcategorie;
			if (orgunit == null)
				topcategorie = (Categorie)
						database.getBean("Categorie",
						database.getSelect("Categorie").where(Where.and(
								Expr.equal("catname", plainname),
								Expr.isNull("fk_orgunit"))));
			else
				topcategorie = (Categorie)
						database.getBean("Categorie",
						database.getSelect("Categorie").where(Where.and(
								Expr.equal("catname", plainname),
								Expr.equal("fk_orgunit", orgunit))));

			if (topcategorie != null) {
				addMessage(cntx, "\u00DCberf\u00FChre Daten " +
						"aus Kategorie \"" + catname + "\" (" + catpk + ")" +
						" in Kategorie \"" + topcategorie.name + "\" (" + topcategorie.id + ").");

				if (isActivated(cntx)) {
					conferCategorie(cntx, database, catpk, topcategorie.id);
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

	protected void conferCategorie(OctopusContext cntx, Database database,
			Integer subcategorypk, Integer topcategorypk)
			throws BeanException {

		final TransactionContext transactionContext = database.getTransactionContext();
		try {
			transactionContext.execute(SQL.Update( database ).
					table("veraweb.tguest").
					update("fk_category", topcategorypk).
					where(Expr.equal("fk_category", subcategorypk)));
			transactionContext.execute(SQL.Update( database ).
					table("veraweb.tperson_categorie").
					update("fk_categorie", topcategorypk).
					where(Expr.equal("fk_categorie", subcategorypk)));
			transactionContext.execute(SQL.Delete( database ).from("veraweb.tperson_categorie").where(
					Expr.in("pk", new RawClause("(" +
							"SELECT tpc1.pk FROM veraweb.tperson_categorie tpc1" +
							" JOIN veraweb.tperson_categorie tpc2 ON (" +
							"tpc1.fk_person = tpc2.fk_person AND " +
							"tpc1.fk_categorie = tpc2.fk_categorie AND " +
							"tpc1.pk < tpc2.pk))"))));
			transactionContext.execute(SQL.Delete( database ).
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
			transactionContext.execute(SQL.Update( database ).
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

		List list = (List)cntx.contentAsObject("cleanup");
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
