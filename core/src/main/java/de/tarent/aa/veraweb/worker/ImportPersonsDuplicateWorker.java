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
 *  © 2007, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020
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
 * and older code, Copyright © 2001–2008 ⮡ tarent GmbH and contributors.
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
