package de.tarent.octopus.beans.veraweb;
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
