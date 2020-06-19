package de.tarent.octopus.beans.veraweb;

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

import de.tarent.aa.veraweb.beans.AbstractBean;
import de.tarent.aa.veraweb.beans.AbstractHistoryBean;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.BeanStatement;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;
import de.tarent.octopus.server.Context;
import de.tarent.octopus.server.OctopusContext;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Konkrete {@link BeanFactory}, die Beans aus einer Datenbank im Kontext des
 * Octopus-Modul veraweb ausliest.
 *
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
@Log4j2
public class DatabaseVeraWeb extends Database {
    /**
     * VerA.web-Bean-Package
     */
    public static final String BEANPACKAGE = "de.tarent.aa.veraweb.beans";

    private final OctopusContext cntx;
    private Connection defaultConnection = null;

    //
    // Konstruktor
    //

    /**
     * Dieser Konstruktor initialisiert die {@link Database}-{@link BeanFactory}
     * mit dem übergebenen {@link OctopusContext} und dem VerA.web-Bean-Package
     * {@link #BEANPACKAGE "de.tarent.aa.veraweb.beans"}. Als Modulnamen für den
     * DB-Zugriff wird das Modul des übergebenen Octopus-Kontexts genommen.
     *
     * @param cntx Octopus-Kontext, der DB-Modulnamen und
     *             Bean-Property-Dateipfade bestimmt.
     */
    public DatabaseVeraWeb(OctopusContext cntx) {
        super(cntx, BEANPACKAGE);
        this.cntx = cntx;
    }

    /**
     * Creates a new Database object refering the same datasource
     */
    public DatabaseVeraWeb createCopy() {
        return new DatabaseVeraWeb(cntx);
    }

    //
    // Oberklasse Database
    //

    /**
     * History-Felder-Aktualisierung für {@link Database#saveBean(Bean, ExecutionContext, boolean)}
     * und implizit {@link Database#saveBean(Bean)}.<br>
     * Berechtigungsüberprüfung geschieht durch die überschreibungen {@link #getInsert(Bean)}
     * und {@link #getUpdate(Bean)}.
     */
    @Override
    public void saveBean(Bean bean, ExecutionContext context, boolean updateID) throws BeanException, IOException {
        //        wird in getInsert / getUpdate gemacht
        //        if (bean instanceof AbstractBean)
        //            ((AbstractBean)bean).checkWrite(octopusContext);
        if (bean instanceof AbstractHistoryBean) {
            ((AbstractHistoryBean) bean).updateHistoryFields(((PersonalConfigAA) cntx.personalConfig()).getRoleWithProxy());
        }
        super.saveBean(bean, context, updateID);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getBean(String, Select, ExecutionContext)}
     * und implizit {@link Database#getBean(String, Integer)}, {@link Database#getBean(String, Select)}
     * und {@link Database#getBean(String, Integer, ExecutionContext)}.
     */
    @Override
    public Bean getBean(String beanname, Select select, ExecutionContext context) throws BeanException {
        Bean bean = super.getBean(beanname, select, context);
        if (bean instanceof AbstractBean) {
            ((AbstractBean) bean).checkRead(cntx);
        }
        return bean;
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getBeanList(String, Select, ExecutionContext)}
     * und implizit {@link Database#getBeanList(String, Select)}.
     */
    @Override
    public List getBeanList(String beanname, Select select, ExecutionContext context) throws BeanException {
        List beans = super.getBeanList(beanname, select, context);
        if (beans != null && beans.size() > 0) {
            Object firstEntry = beans.get(0);
            if (firstEntry instanceof AbstractBean) {
                ((AbstractBean) firstEntry).checkRead(cntx);
            }
        }
        return beans;
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getDelete(Bean)} und implizit
     * {@link Database#getDelete(String)}.
     */
    @Override
    public Delete getDelete(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean) {
            ((AbstractBean) bean).checkWrite(cntx);
        }
        return super.getDelete(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getInsert(Bean)} und implizit
     * {@link Database#saveBean(Bean)} und
     * {@link Database#saveBean(Bean, ExecutionContext, boolean)}.
     */
    @Override
    public Insert getInsert(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean) {
            ((AbstractBean) bean).checkWrite(cntx);
        }
        return super.getInsert(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getSelect(Bean)} und implizit
     * {@link Database#getSelect(String)}.
     */
    @Override
    public Select getSelect(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean) {
            ((AbstractBean) bean).checkRead(cntx);
        }
        return super.getSelect(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getUpdate(Bean)} und implizit
     * {@link Database#saveBean(Bean)} und
     * {@link Database#saveBean(Bean, ExecutionContext, boolean)}.
     */
    @Override
    public Update getUpdate(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean) {
            ((AbstractBean) bean).checkWrite(cntx);
        }
        return super.getUpdate(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getDelete(Bean)} und implizit
     * {@link Database#getDelete(String)}.
     */
    @Override
    public void removeBean(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean) {
            ((AbstractBean) bean).checkWrite(cntx);
        }
        super.removeBean(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#prepareUpdate(Bean, Collection, Collection, ExecutionContext)}.
     */
    @Override
    public BeanStatement prepareUpdate(Bean sample, Collection keyFields, Collection updateFields, ExecutionContext context)
      throws BeanException, IOException {
        if (sample instanceof AbstractBean) {
            ((AbstractBean) sample).checkWrite(cntx);
        }
        return super.prepareUpdate(sample, keyFields, updateFields, context);
    }

    /**
     * Gibt ein verifiziertes Bean zurück. Nach dem eigentlichen Fällen der Bean (vergleiche
     * {@link de.tarent.octopus.beans.BeanFactory#fillBean(de.tarent.octopus.beans.Bean)})
     * werden hier die Felder wieder geleert, auf die der Nutzer keinen Zugriff hat.
     *
     * @param bean Bean-Instanz
     * @return Bean, nie null.
     * @throws BeanException
     * @see AbstractBean#clearRestrictedFields(OctopusContext)
     * @see de.tarent.octopus.beans.BeanFactory#fillBean(de.tarent.octopus.beans.Bean)
     */
    @Override
    protected Bean fillBean(Bean bean) throws BeanException {
        Bean result = super.fillBean(bean);
        if (result instanceof AbstractBean) {
            ((AbstractBean) result).clearRestrictedFields(cntx);
        }
        return result;
    }

    @Override
    public ResultList getList(Select statement, ExecutionContext context) throws BeanException {
        ResultList list = super.getList(statement, context);
        if (Context.getActive() != null) {
            Context.getActive().addCleanupCode(list);
        } else {
            logger.warn(getClass().getName() + " - getList(): No active context set.");
        }
        return list;
    }

    public Connection getDefaultConnection() throws SQLException {
        if (defaultConnection == null || defaultConnection.isClosed()) {
            defaultConnection = getPool().getConnection();
        }
        return defaultConnection;
    }

    public void close() {
        try {
            if (defaultConnection != null && !defaultConnection.isClosed()) {
                defaultConnection.close();
                defaultConnection = null;
            }
        } catch (SQLException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }
}
