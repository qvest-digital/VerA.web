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

import de.tarent.aa.veraweb.beans.ChangeLogEntry;
import de.tarent.aa.veraweb.beans.ChangeLogReport;
import de.tarent.aa.veraweb.utils.i18n.LanguageProvider;
import de.tarent.aa.veraweb.utils.i18n.LanguageProviderHelper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The class ChangeLogReportsWorker is a concrete
 * worker for operations on the {@link ChangeLogReport}
 * bean in collaboration with the {@link ChangeLogEntry}
 * entity bean.
 *
 * @author cklein
 * @see de.tarent.aa.veraweb.beans.ChangeLogReport
 * @since 1.2.0
 */
public class ChangeLogReportsWorker extends ListWorkerVeraWeb {
    /**
     * Constructs a new instance of this.
     */
    public ChangeLogReportsWorker() {
        super("ChangeLogEntry");
    }

    /**
     * Input parameters for action {@link #loadConfig(OctopusContext, String, String) }
     */
    public static final String INPUT_loadConfig[] = { "begin", "end" };
    /**
     * Input parameter configuration for action
     */
    public static final boolean MANDATORY_loadConfig[] = { false, false };

    /**
     * Stores and loads the report configuration in / from the session.
     *
     * @param cntx  {@link OctopusContext}
     * @param begin FIXME
     * @param end   FIXME
     * @throws BeanException FIXME
     * @throws IOException   FIXME
     */
    @SuppressWarnings("unchecked")
    public void loadConfig(OctopusContext cntx, String begin, String end) throws BeanException, IOException {
        Map<String, Object> map = (Map<String, Object>) cntx.sessionAsObject("changeLogReportSettings");
        if (map == null) {
            map = new HashMap<String, Object>();
            cntx.setSession("changeLogReportSettings", map);
        }

        // fetch begin and end dates or sane defaults
        Date bd = (Date) BeanFactory.transform(begin, Date.class);
        if (bd == null) {
            bd = (Date) map.get("begin");
            if (bd == null) {
                bd = (Date) BeanFactory.transform("01.01." + Calendar.getInstance().get(Calendar.YEAR), Date.class);
            }
        }

        Date ed = (Date) BeanFactory.transform(end, Date.class);
        if (ed == null) {
            ed = (Date) map.get("end");
            if (ed == null) {
                ed = new Date(System.currentTimeMillis());
            }
        }

        // make sure that end is always after begin
        if (ed.after(bd)) {
            map.put("begin", bd);
            map.put("end", ed);
        } else {
            map.put("begin", ed);
            map.put("end", bd);
        }

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, new Locale("de", "DE"));
        cntx.setContent("begin", format.format(map.get("begin")));
        cntx.setContent("end", format.format(map.get("end")));

        Database database = getDatabase(cntx);
        Integer count = getCount(cntx, database);
        if (count.intValue() == 0) {
            LanguageProviderHelper languageProviderHelper = new LanguageProviderHelper();
            LanguageProvider languageProvider = languageProviderHelper.enableTranslation(cntx);

            cntx.setContent("noLogDataAvailableMessage",
                    languageProvider.getProperty("CHANGELOG_NO_PROTOCOL_AVAILABLE").toString());
        }
    }

    @Override
    public List showList(OctopusContext cntx) throws BeanException, IOException {
        Database database = getDatabase(cntx);
        Select select = getSelect(database);
        extendColumns(cntx, select);
        extendWhere(cntx, select);
        return getResultList(database, select);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void extendWhere(OctopusContext cntx, Select select)
            throws BeanException, IOException {
        Map<String, Object> map = (Map<String, Object>) cntx.sessionAsObject("changeLogReportSettings");
        Date begin = (Date) map.get("begin");
        Date end = (Date) map.get("end");
        Calendar calendar = Calendar.getInstance();
        // end date must point to day.month.year 23:59:59 in order to find todays change log entries
        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.SECOND, -1);
        select.where(Expr.greaterOrEqual("date", begin));
        select.whereAnd(Expr.lessOrEqual("date", calendar.getTime()));
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
    protected void extendAll(OctopusContext cntx, Select select)
            throws BeanException, IOException {
        Clause clause = getWhere();
        if (clause != null) {
            select.where(clause);
        }
    }

    protected Clause getWhere()
            throws BeanException {
        Clause clause = null;
        return clause;
    }

    @Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
        throw new RuntimeException("Change log entries cannot be modified. Not implemented.");
    }
}
