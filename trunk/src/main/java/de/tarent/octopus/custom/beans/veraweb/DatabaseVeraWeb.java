/* $Id: DatabaseVeraWeb.java,v 1.1 2007/06/20 11:56:52 christoph Exp $ */
package de.tarent.octopus.custom.beans.veraweb;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import de.tarent.aa.veraweb.beans.AbstractBean;
import de.tarent.aa.veraweb.beans.AbstractHistoryBean;
import de.tarent.dblayer.sql.statement.Delete;
import de.tarent.dblayer.sql.statement.Insert;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.BeanFactory;
import de.tarent.octopus.custom.beans.BeanStatement;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.ExecutionContext;
import de.tarent.octopus.server.OctopusContext;

/**
 * Konkrete {@link BeanFactory}, die Beans aus einer Datenbank im Kontext des
 * Octopus-Modul veraweb ausliest. 
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public class DatabaseVeraWeb extends Database {
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
     *  Bean-Property-Dateipfade bestimmt.
     */
	public DatabaseVeraWeb(OctopusContext cntx) {
		super(cntx, BEANPACKAGE);
	}

    // nur zu Testzwecken, Kontext ist sonst wichtig.
    /**
     * Dieser Konstruktor initialisiert die {@link Database}-{@link BeanFactory}
     * mit dem übergebenen Modulnamen für den dblayer-DB-Zugriff und dem
     * VerA.web-Bean-Package {@link #BEANPACKAGE "de.tarent.aa.veraweb.beans"}.<br>
     * Achtung: in diesem Kontext gibt es keine sinnvolle Vorgabe für Pfade
     * von Bean-Property-Dateien.<br>
     * TODO: sinnvolle Property-Dateipfade einführen 
     * 
     * @param module Modulnamen für den DB-Zugriff.
     */
    public DatabaseVeraWeb(String module) {
        super(module, BEANPACKAGE);
    }

    //
    // Oberklasse Database
    //
    /**
     * History-Felder-Aktualisierung für {@link Database#saveBean(Bean, ExecutionContext, boolean)}
     * und implizit {@link Database#saveBean(Bean)}.<br>
     * Berechtigungs-Überprüfung geschieht durch die Überschreibungen {@link #getInsert(Bean)}
     * und {@link #getUpdate(Bean)}.
     */
    public void saveBean(Bean bean, ExecutionContext context, boolean updateID) throws BeanException, IOException {
//        wird in getInsert / getUpdate gemacht
//        if (bean instanceof AbstractBean)
//            ((AbstractBean)bean).checkWrite(cntx);
        if (bean instanceof AbstractHistoryBean)
            ((AbstractHistoryBean)bean).updateHistoryFields(null, ((PersonalConfigAA)cntx.personalConfig()).getRoleWithProxy());
        super.saveBean(bean, context, updateID);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getBean(String, Select, ExecutionContext)}
     * und implizit {@link Database#getBean(String, Integer)}, {@link Database#getBean(String, Select)}
     * und {@link Database#getBean(String, Integer, ExecutionContext)}.
     */
    public Bean getBean(String beanname, Select select, ExecutionContext context) throws BeanException {
        Bean bean = super.getBean(beanname, select, context);
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkRead(cntx);
        return bean;
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getBeanList(String, Select, ExecutionContext)}
     * und implizit {@link Database#getBeanList(String, Select)}.
     */
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
     * Berechtigungsüberprüfung für {@link Database#getDelete(Bean)} und implizit
     * {@link Database#getDelete(String)}.
     */
    public Delete getDelete(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        return super.getDelete(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getInsert(Bean)} und implizit
     * {@link Database#saveBean(Bean)} und
     * {@link Database#saveBean(Bean, ExecutionContext, boolean)}.
     */
    public Insert getInsert(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        return super.getInsert(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getSelect(Bean)} und implizit
     * {@link Database#getSelect(String)}.
     */
    public Select getSelect(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkRead(cntx);
        return super.getSelect(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getUpdate(Bean)} und implizit
     * {@link Database#saveBean(Bean)} und
     * {@link Database#saveBean(Bean, ExecutionContext, boolean)}.
     */
    public Update getUpdate(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        return super.getUpdate(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#getDelete(Bean)} und implizit
     * {@link Database#getDelete(String)}.
     */
    public void removeBean(Bean bean) throws BeanException, IOException {
        if (bean instanceof AbstractBean)
            ((AbstractBean)bean).checkWrite(cntx);
        super.removeBean(bean);
    }

    /**
     * Berechtigungsüberprüfung für {@link Database#prepareUpdate(Bean, Collection, Collection, ExecutionContext)}.
     */
    public BeanStatement prepareUpdate(Bean sample, Collection keyFields, Collection updateFields, ExecutionContext context) throws BeanException, IOException {
        if (sample instanceof AbstractBean)
            ((AbstractBean)sample).checkWrite(cntx);
        return super.prepareUpdate(sample, keyFields, updateFields, context);
    }

    /**
     * Gibt ein verifiziertes Bean zurück. Nach dem eigentlichen Füllen der Bean (vergleiche
     * {@link de.tarent.octopus.custom.beans.BeanFactory#fillBean(de.tarent.octopus.custom.beans.Bean)})
     * werden hier die Felder wieder geleert, auf die der Nutzer keinen Zugriff hat.
     * 
     * @param bean Bean-Instanz
     * @return Bean, nie null.
     * @throws BeanException 
     * @see AbstractBean#clearRestrictedFields(OctopusContext)
     * @see de.tarent.octopus.custom.beans.BeanFactory#fillBean(de.tarent.octopus.custom.beans.Bean)
     */
    protected Bean fillBean(Bean bean) throws BeanException {
        Bean result = super.fillBean(bean);
        if (result instanceof AbstractBean)
            ((AbstractBean)result).clearRestrictedFields(cntx);
        return result;
    }

    //
    // Konstanten
    //
    /** VerA.web-Bean-Package */
    public static final String BEANPACKAGE = "de.tarent.aa.veraweb.beans";
}
