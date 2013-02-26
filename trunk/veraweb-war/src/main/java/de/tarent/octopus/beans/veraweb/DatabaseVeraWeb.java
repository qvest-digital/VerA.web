/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004-2008 tarent GmbH
 * Copyright © 2013 tarent solutions GmbH
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see: http://www.gnu.org/licenses/
 */
package de.tarent.octopus.beans.veraweb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * Konkrete {@link BeanFactory}, die Beans aus einer Datenbank im Kontext des
 * Octopus-Modul veraweb ausliest. 
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public class DatabaseVeraWeb extends Database {
	private static final Logger logger = Logger.getLogger(DatabaseVeraWeb.class.getName());
	
    /** VerA.web-Bean-Package */
    public static final String BEANPACKAGE = "de.tarent.aa.veraweb.beans";

    private final OctopusContext cntx;
    private Connection defaultcon = null;
    
    //
    // Konstruktor
    //
    /**
     * Dieser Konstruktor initialisiert die {@link Database}-{@link BeanFactory}
     * mit dem �bergebenen {@link OctopusContext} und dem VerA.web-Bean-Package
     * {@link #BEANPACKAGE "de.tarent.aa.veraweb.beans"}. Als Modulnamen f�r den
     * DB-Zugriff wird das Modul des �bergebenen Octopus-Kontexts genommen.
     * 
     * @param cntx Octopus-Kontext, der DB-Modulnamen und
     *  Bean-Property-Dateipfade bestimmt.
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
     * History-Felder-Aktualisierung f�r {@link Database#saveBean(Bean, ExecutionContext, boolean)}
     * und implizit {@link Database#saveBean(Bean)}.<br>
     * Berechtigungs-�berpr�fung geschieht durch die �berschreibungen {@link #getInsert(Bean)}
     * und {@link #getUpdate(Bean)}.
     */
    @Override
    public void saveBean(Bean bean, ExecutionContext context, boolean updateID) throws BeanException, IOException {
//        wird in getInsert / getUpdate gemacht
//        if (bean instanceof AbstractBean)
//            ((AbstractBean)bean).checkWrite(cntx);
        if (bean instanceof AbstractHistoryBean)
            ((AbstractHistoryBean)bean).updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
        super.saveBean(bean, context, updateID);
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getBean(String, Select, ExecutionContext)}
     * und implizit {@link Database#getBean(String, Integer)}, {@link Database#getBean(String, Select)}
     * und {@link Database#getBean(String, Integer, ExecutionContext)}.
     */
    @Override
    public Bean getBean(String beanname, Select select, ExecutionContext context) throws BeanException {
        Bean bean = super.getBean(beanname, select, context);
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkRead(cntx);
        return bean;
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getBeanList(String, Select, ExecutionContext)}
     * und implizit {@link Database#getBeanList(String, Select)}.
     */
    @Override
    public List getBeanList(String beanname, Select select, ExecutionContext context) throws BeanException {
        List beans = super.getBeanList(beanname, select, context);
        if (beans != null && beans.size() > 0) {
            Object firstEntry = beans.get(0);
            if (firstEntry instanceof AbstractBean)
                ((AbstractBean)firstEntry).checkRead(cntx);
        }
        return beans;
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getDelete(Bean)} und implizit
     * {@link Database#getDelete(String)}.
     */
    @Override
    public Delete getDelete(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        return super.getDelete(bean);
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getInsert(Bean)} und implizit
     * {@link Database#saveBean(Bean)} und
     * {@link Database#saveBean(Bean, ExecutionContext, boolean)}.
     */
    @Override
    public Insert getInsert(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        return super.getInsert(bean);
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getSelect(Bean)} und implizit
     * {@link Database#getSelect(String)}.
     */
    @Override
    public Select getSelect(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkRead(cntx);
        return super.getSelect(bean);
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getUpdate(Bean)} und implizit
     * {@link Database#saveBean(Bean)} und
     * {@link Database#saveBean(Bean, ExecutionContext, boolean)}.
     */
    @Override
    public Update getUpdate(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        return super.getUpdate(bean);
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#getDelete(Bean)} und implizit
     * {@link Database#getDelete(String)}.
     */
    @Override
    public void removeBean(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        super.removeBean(bean);
    }

    /**
     * Berechtigungs�berpr�fung f�r {@link Database#prepareUpdate(Bean, Collection, Collection, ExecutionContext)}.
     */
    @Override
    public BeanStatement prepareUpdate(Bean sample, Collection keyFields, Collection updateFields, ExecutionContext context) throws BeanException, IOException {
        if (sample instanceof AbstractBean)
            ((AbstractBean)sample).checkWrite(cntx);
        return super.prepareUpdate(sample, keyFields, updateFields, context);
    }

    /**
     * Gibt ein verifiziertes Bean zur�ck. Nach dem eigentlichen F�llen der Bean (vergleiche
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
        if (result instanceof AbstractBean)
            ((AbstractBean)result).clearRestrictedFields(cntx);
        return result;
    }

    @Override
    public ResultList getList(Select statement, ExecutionContext context) throws BeanException {
        ResultList list = super.getList(statement, context);
        if (Context.getActive() != null)  
        	Context.getActive().addCleanupCode(list);
        else
        	logger.log(Level.WARNING, getClass().getName() + " - getList(): No active context set.");
        return list;
    }

	public Connection getDefaultConnection() throws SQLException {
		if(defaultcon==null||defaultcon.isClosed()){
			defaultcon = getPool().getConnection();
            if (Context.getActive() != null)
                Context.getActive().addCleanupCode(new DBConnectionCloser(defaultcon));
            else
            	logger.log(Level.WARNING, getClass().getName() + " - getDefaultConnection(): No active context set.");
		}
		return defaultcon;
	}
	
	public void close() {
        try {
            if(defaultcon != null && ! defaultcon.isClosed()) {               
                defaultcon.close();
                defaultcon = null;
            }
        } catch (SQLException e) {
			logger.log(Level.WARNING, e.getLocalizedMessage(), e);
        }
    }
}
