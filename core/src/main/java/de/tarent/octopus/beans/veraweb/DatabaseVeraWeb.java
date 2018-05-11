package de.tarent.octopus.beans.veraweb;

/*-
 * Veranstaltungsmanagement VerA.web (platform-independent
 * webservice-based event management) is Copyright
 *  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
 *  © 2018 Атанас Александров (sirakov@gmail.com)
 *  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
 *  © 2013 Patrick Apel (p.apel@tarent.de)
 *  © 2016 Eugen Auschew (e.auschew@tarent.de)
 *  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
 *  © 2013 Valentin But (v.but@tarent.de)
 *  © 2016 Lukas Degener (l.degener@tarent.de)
 *  © 2017 Axel Dirla (a.dirla@tarent.de)
 *  © 2015 Julian Drawe (j.drawe@tarent.de)
 *  © 2014 Dominik George (d.george@tarent.de)
 *  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
 *  © 2008 David Goemans (d.goemans@tarent.de)
 *  © 2018 Christian Gorski (c.gorski@tarent.de)
 *  © 2015 Viktor Hamm (v.hamm@tarent.de)
 *  © 2013 Katja Hapke (k.hapke@tarent.de)
 *  © 2013 Hendrik Helwich (h.helwich@tarent.de)
 *  © 2018 Thomas Hensel (t.hensel@tarent.de)
 *  © 2018 Titian Horvath (t.horvath@tarent.de)
 *  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
 *  © 2018 Timo Kanera (t.kanera@tarent.de)
 *  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
 *  © 2014 Martin Ley (m.ley@tarent.de)
 *  © 2014, 2015 Max Marche (m.marche@tarent.de)
 *  © 2007 Jan Meyer (jan@evolvis.org)
 *  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
 *  © 2016 Cristian Molina (c.molina@tarent.de)
 *  © 2018 Yorka Neumann (y.neumann@tarent.de)
 *  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
 *  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
 *  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
 *  © 2016 Jens Oberender (j.oberender@tarent.de)
 *  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
 *  © 2009 Martin Pelzer (m.pelzer@tarent.de)
 *  © 2013 Marc Radel (m.radel@tarent.de)
 *  © 2013 Sebastian Reimers (s.reimers@tarent.de)
 *  © 2015 Charbel Saliba (c.saliba@tarent.de)
 *  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
 *  © 2013 Volker Schmitz (v.schmitz@tarent.de)
 *  © 2014 Sven Schumann (s.schumann@tarent.de)
 *  © 2014 Sevilay Temiz (s.temiz@tarent.de)
 *  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
 *  © 2015 Stefan Walenda (s.walenda@tarent.de)
 *  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
 *  © 2013 Rebecca Weinz (r.weinz@tarent.de)
 *  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
 *  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Konkrete {@link BeanFactory}, die Beans aus einer Datenbank im Kontext des
 * Octopus-Modul veraweb ausliest.
 *
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public class DatabaseVeraWeb extends Database {
    private static final Logger logger = Logger.getLogger(DatabaseVeraWeb.class.getName());

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
            logger.log(Level.WARNING, getClass().getName() + " - getList(): No active context set.");
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
            logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
