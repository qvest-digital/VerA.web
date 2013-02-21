/*
 * veraweb,
 * Veranstaltungsmanagment veraweb
 * Copyright (c) 2005-2007 tarent GmbH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License,version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * tarent GmbH., hereby disclaims all copyright
 * interest in the program 'veraweb'
 * Signature of Elmar Geese, 21 November 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/* $Id$ */
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
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.beans.veraweb.ListWorkerVeraWeb;
import de.tarent.octopus.server.OctopusContext;

/**
 * Testet ob bereits ein Stammdaten-Eintrag mit dem selben Namen existiert.
 * Bietet zus�tzlich einen Task f�r Direkteinsprungsmarken an.
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
    protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		if (bean.isModified() && bean.isCorrect()) {
			Database database = context.getDatabase();
			
			String orgunit = database.getProperty(bean, "orgunit");
			Clause clause = Expr.equal(
					database.getProperty(bean, "name"),
					bean.getField("name"));
			if (orgunit != null) {
				clause = Where.and(Expr.equal(orgunit, ((PersonalConfigAA)(cntx.personalConfig())).getOrgUnitId()), clause);
			}
			
			Integer exist =
					database.getCount(
					database.getCount(bean).
					where(clause),context);
			if (exist.intValue() != 0) {
				errors.add("Es existiert bereits ein Stammdaten-Eintrag unter dem Namen '" + bean.getField("name") + "'.");
			} else {
				count += super.insertBean(cntx, errors, bean, context);
			}
		}
		return count;
	}
}
