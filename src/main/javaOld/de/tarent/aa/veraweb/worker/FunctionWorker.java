/**
 * veraweb, platform independent webservice-based event management
 * (Veranstaltungsmanagment VerA.web), is
 * Copyright © 2004–2008 tarent GmbH
 * Copyright © 2013–2016 tarent solutions GmbH
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
package de.tarent.aa.veraweb.worker;

import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.Function;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.beans.Bean;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.TransactionContext;
import de.tarent.octopus.server.OctopusContext;

import java.io.IOException;
import java.util.List;

/**
 * Diese Liste zeigt eine Liste von Funktionen an.
 *
 * @author Christoph
 * @version $Revision: 1.1 $
 */
public class FunctionWorker extends StammdatenWorker {
    /**
     * Der Konstruktor legt den Bean-Namen fest.
     */
    public FunctionWorker() {
        super("Function");
    }


    @Override
    protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
        Event event = (Event) cntx.contentAsObject("event");
        if (event != null) {
            select.where(getClause(event));
        }
    }

    private RawClause getClause(Event event) {
        String clause = "pk NOT IN (SELECT fk_function FROM veraweb.tevent_function WHERE fk_event = " + event.id + ")";
        return new RawClause(clause);
    }

    @Override
    protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
    	final Function function = (Function) bean;
    	if(!function.equals("")){
    		super.saveBean(cntx, bean, context);
    	} else {
    		return ;
    	}
    }

    @Override
    public void saveList(OctopusContext octopusContext) throws BeanException, IOException {
    	super.saveList(octopusContext);
    }

    @Override
    public List showList(OctopusContext octopusContext) throws BeanException, IOException {
    	return super.showList(octopusContext);
    }

}
