/*
 * $Id: UserListWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $
 * 
 * Created on 29.07.2005
 */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.List;

import de.tarent.aa.veraweb.beans.OrgUnit;
import de.tarent.aa.veraweb.beans.Proxy;
import de.tarent.aa.veraweb.beans.User;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Diese Octopus-Worker-Klasse stellt Operationen zur Anzeige
 * von Benutzerlisten zur Verfügung. Details bitte dem
 * {@link de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb}
 * entnehmen.
 * 
 * @author mikel
 */
public class UserListWorker extends ListWorkerVeraWeb {
    //
    // öffentliche Konstanten
    //
    /** Parameter: Wer alles? */
    public final static String PARAM_DOMAIN = "domain";
    /** Parameter: Sortierreihenfolge */
    public final static String PARAM_ORDER = "order";
    
    /** Parameterwert: beliebige Benutzer */
    public final static String PARAM_DOMAIN_VALUE_ALL = "all";
    /** Parameterwert: Benutzer des gleichen Mandanten */
    public final static String PARAM_DOMAIN_VALUE_OU = "ou";
    /** Parameterwert: angemeldeter Benutzer */
    public final static String PARAM_DOMAIN_VALUE_SELF = "self";
    
    //
    // Konstruktoren
    //
    /**
     * Dieser Konstruktor bestimmt den Basis-Bean-Typ des Workers.
     */
    public UserListWorker() {
        super("User");
    }

    //
	// BeanListWorker
	//
	/**
	 * Methode für das Erweitern des ListWorkerVeraWeb-Select-Statements um Spalten.<br>
	 * Hier wird eine Sortierung eingefügt.
	 * 
	 * @param cntx
	 *          Octopus-Context
	 * @param select
	 *          Select-Statement
	 * @see de.tarent.octopus.custom.beans.BeanListWorker#extendColumns(de.tarent.octopus.server.OctopusContext,
	 *      de.tarent.dblayer.sql.statement.Select)
	 */
	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException
	{
		String order = cntx.contentAsString(PARAM_ORDER);
		if (order != null)
		{
			Database database = getDatabase(cntx);
			// Bug 1599 orgunit (Mandant) nach Name, nicht nach Schluessel sortieren
			if ("orgunit".equals(order))
			{
				select.select("torgunit.unitname");
				select.joinLeftOuter("torgunit", "torgunit.pk", "tuser.fk_orgunit");
				select.orderBy(Order.asc("torgunit.unitname").andAsc("tuser.username"));
			} 
			else
			{
				order = database.getProperty(new User(), order);
				if (order != null)
				{
					select.orderBy(Order.asc(order).andAsc("tuser.username"));
				}
			}
		}
	}

    /**
		 * Methode für das Erweitern des Select-Statements um Bedingungen.<br>
		 * Hier wird der Parameter {@link #PARAM_DOMAIN "domain"} ausgewertet.<br>
		 * {@link #PARAM_DOMAIN "domain"} kann neben einer Rollenbezeichnung die Werte {@link #PARAM_DOMAIN_VALUE_ALL "all"},
		 * {@link #PARAM_DOMAIN_VALUE_OU "ou"} und {@link #PARAM_DOMAIN_VALUE_SELF "self"} haben.
		 * 
		 * @param cntx
		 *          Octopus-Context
		 * @param select
		 *          Select-Statement
		 * @see de.tarent.octopus.custom.beans.BeanListWorker#extendWhere(de.tarent.octopus.server.OctopusContext,
		 *      de.tarent.dblayer.sql.statement.Select)
		 */
    protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
        PersonalConfigAA pCfg = (PersonalConfigAA) cntx.personalConfig();
        String domain = cntx.contentAsString(PARAM_DOMAIN);
        WhereList list = new WhereList();
        // TODO: Je nach Benutzergruppe passende Einschränkung machen
        //
        // domain: Wer alles?
        //
        if (PARAM_DOMAIN_VALUE_ALL.equals(domain)) {
            // alle Benutzer, keine Einschränkung
        } else if (PARAM_DOMAIN_VALUE_OU.equals(domain)) {
            if (pCfg == null || pCfg.getOrgUnitId() == null)
                list.addAnd(Expr.isNull("fk_orgunit"));
            else
                list.addAnd(Expr.equal("fk_orgunit", pCfg.getOrgUnitId()));
        } else if (PARAM_DOMAIN_VALUE_SELF.equals(domain)) {
            if (pCfg == null || (pCfg.getRole() == null && pCfg.getRoles() == null))
                list.addAnd(Expr.isNull("username"));
            else if (pCfg.getRole() != null)
                list.addAnd(Expr.equal("username", pCfg.getRole()));
            else
                list.addAnd(Expr.in("username", pCfg.getRoles()));
        } else if (domain == null)
            list.addAnd(Expr.isNull("username"));
        else
            list.addAnd(Expr.equal("username", domain));

        select.where(list);
    }
    
    /**
     * Wird von {@link de.tarent.octopus.custom.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll das übergebene Bean als neuen Eintrag speichern.
     * 
     * @see #saveBean(OctopusContext, Bean)
     * 
     * @param cntx Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param bean einzufügendes Bean
     * @throws BeanException
     * @throws IOException
     */
    protected int insertBean(OctopusContext cntx, List errors, Bean bean) throws BeanException, IOException {
    	int count = 0;
        if (bean.isModified() && bean.isCorrect()) {
            if (bean instanceof User) {
                User userBean = (User) bean;
                if (userBean.id != null) {
                    errors.add("Einzufügender Benutzer darf keine ID haben");
                    return count;
                }
                Database database = new DatabaseVeraWeb(cntx);
                User dupBean = (User) database.getBean("User",
                        database.getSelect("User").
                        where(Expr.equal("username", userBean.name)));
                if (dupBean != null) {
                    OrgUnit ou = (OrgUnit) database.getBean("OrgUnit", dupBean.orgunit);
                    if (ou != null) {
                        errors.add("Einzufügender Benutzer ist bereits dem Mandanten " + ((ou.name != null && ou.name.length() > 0) ? ou.name : ou.id.toString()) + " zugeordnet.");
                    } else {
                        errors.add("Einzufügender Benutzer ist bereits VerA.web zugeordnet.");
                    }
                    return count;
                }
            }
            saveBean(cntx, bean);
            count++;
        }
        return count;
    }
    
    /**
     * Wird von {@link de.tarent.octopus.custom.beans.BeanListWorker#saveList(OctopusContext)}
     * aufgerufen und soll das übergebene Bean als neuen Eintrag speichern.
     * 
     * @see #saveBean(OctopusContext, Bean)
     * 
     * @param cntx Octopus-Kontext
     * @param errors kummulierte Fehlerliste
     * @param bean einzufügendes Bean
     * @throws BeanException
     * @throws IOException
     */
   protected boolean removeBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
	    if (bean != null && ((User)bean).id != null) {
		    Proxy proxy = new Proxy();
		    proxy.user = ((User)bean).id;
	    	Database database = getDatabase(cntx);
	    	database.execute(
	    			database.getDelete("Proxy").where(
	    			database.getWhere(proxy)));
	    }
    	return super.removeBean(cntx, bean);
    }
}
