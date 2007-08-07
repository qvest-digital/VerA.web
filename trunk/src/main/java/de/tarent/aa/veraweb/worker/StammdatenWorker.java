/* $Id: StammdatenWorker.java,v 1.1 2007/06/20 11:56:51 christoph Exp $ */
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.util.List;

import de.tarent.dblayer.sql.Escaper;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.custom.beans.Bean;
import de.tarent.octopus.custom.beans.BeanException;
import de.tarent.octopus.custom.beans.Database;
import de.tarent.octopus.custom.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

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

	protected int insertBean(OctopusContext cntx, List errors, Bean bean) throws BeanException, IOException {
		int count = 0;
		if (bean.isModified() && bean.isCorrect()) {
			Database database = getDatabase(cntx);
			
			String orgunit = database.getProperty(bean, "orgunit");
			Clause clause = Expr.equal(
					database.getProperty(bean, "name"),
					bean.getField("name"));
			if (orgunit != null) {
				clause = Where.and(Expr.equal(orgunit, ((PersonalConfigAA)(cntx.configImpl())).getOrgUnitId()), clause);
			}
			
			Integer exist =
					database.getCount(
					database.getCount(bean).
					where(clause));
			if (exist.intValue() != 0) {
				errors.add("Es existiert bereits ein Stammdaten-Eintrag unter dem Namen '" + bean.getField("name") + "'.");
			} else {
				count += super.insertBean(cntx, errors, bean);
			}
		}
		return count;
	}
}
