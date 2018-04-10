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
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov <jerolimov@gmx.de>
 *  © 2008, 2009, 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2014 Martin Ley <m.ley@tarent.de>
 *  © 2014, 2015 Max Marche <m.marche@tarent.de>
 *  © 2007 Jan Meyer <jan@evolvis.org>
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
