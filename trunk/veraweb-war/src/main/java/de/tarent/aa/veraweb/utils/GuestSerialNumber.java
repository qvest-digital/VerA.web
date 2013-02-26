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
package de.tarent.aa.veraweb.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.tarent.aa.veraweb.beans.Categorie;
import de.tarent.aa.veraweb.beans.Event;
import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.RawClause;
import de.tarent.dblayer.sql.clause.WhereList;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.dblayer.sql.statement.Update;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.ExecutionContext;

/**
 * Diese Klasse sammelt Hilfsklassen zum Ermitteln laufender Nummern f�r G�ste. 
 * 
 * @author christoph
 */
public class GuestSerialNumber {
    /**
     * Diese Klasse ist Basisklasse f�r Hilfsklassen zum Ermitteln laufender
     * Nummern f�r G�ste.
     */
	static public abstract class CalcSerialNumber {
		protected int orderNo = 0;
		protected ExecutionContext context;
		protected Event event;
		
        /**
         * Dieser Konstruktor �bernimmt Datenbank und Veranstaltung zur
         * Benutzung bei der sp�teren Berechnung laufender G�stenummern. 
         */
		public CalcSerialNumber(ExecutionContext context, Event event) {
			this.context = context;
			this.event = event;
		}

        /**
         * Diese abstrakte Methode berechnet die tats�chlichen laufenden
         * Nummern der G�ste der im Konstruktor �bergebenen Veranstaltung
         * in der ebenda �bergebenen Datenbank.  
         */
		public abstract void calcSerialNumber() throws BeanException, IOException;
		
		protected void clearSerialNumber() throws BeanException {
			Update update = SQL.Update( context ).
					table("veraweb.tguest").
					update("orderno", null).
					update("orderno_p", null).
					where(Expr.equal("fk_event", event.id));
			context.execute(update);
		}
		
		protected void setSerialNumber(Map guest) throws BeanException, IOException {
			Integer invitationtype = (Integer)guest.get("invitationtype");
			Integer invitationstatus_a = (Integer)guest.get("invitationstatus_a");
			Integer invitationstatus_b = (Integer)guest.get("invitationstatus_b");
			Integer orderno_a;
			Integer orderno_b;
			
			if (invitationtype == null || invitationtype.intValue() == EventConstants.TYPE_MITPARTNER) {
				if (invitationstatus_a != null && invitationstatus_a.intValue() == 2)
					orderno_a = null;
				else
					orderno_a = new Integer(++orderNo);
				if (invitationstatus_b != null && invitationstatus_b.intValue() == 2)
					orderno_b = null;
				else
					orderno_b = new Integer(++orderNo);
			} else if (invitationtype.intValue() == EventConstants.TYPE_OHNEPARTNER) {
				if (invitationstatus_a != null && invitationstatus_a.intValue() == 2)
					orderno_a = null;
				else
					orderno_a = new Integer(++orderNo);
				orderno_b =  null;
			} else if (invitationtype.intValue() == EventConstants.TYPE_NURPARTNER) {
				orderno_a = null;
				if (invitationstatus_b != null && invitationstatus_b.intValue() == 2)
					orderno_b = null;
				else
					orderno_b = new Integer(++orderNo);
			} else {
				throw new IOException("wrong invitationtype");
			}
			
			context.execute(SQL.Update( context ).
					table("veraweb.tguest").
					update("tguest.orderno", orderno_a).
					update("tguest.orderno_p", orderno_b).
					where(Expr.equal("pk", guest.get("id"))));
		}
		
		protected void setSerialNumber(Select select) throws BeanException, IOException {
			for (Iterator it = context.getDatabase().getList(select, context).iterator(); it.hasNext(); ) {
				setSerialNumber((Map)it.next());
			}
		}
		
		protected Select getSelect() {
			return SQL.Select( context ).
					from("veraweb.tguest").
					selectAs("tguest.pk", "id").
					selectAs("tguest.invitationtype", "invitationtype").
					selectAs("invitationstatus", "invitationstatus_a").
					selectAs("invitationstatus_p", "invitationstatus_b").
					selectAs("CASE WHEN tcategorie.flags = 99 THEN diplodate_a_e1 ELSE NULL END", "diplodate").
					joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
					joinLeftOuter("veraweb.tcategorie", "tguest.fk_category", "tcategorie.pk");
		}
	}

    /**
     * Berechnet die 'Laufende Nummer' einer G�steliste nach folgendem Schema:
     * <ul>
     * <li>Sortiert G�ste mit Rang ein.</li>
     * <li>Sortiert G�ste anhand ihrer Kategorie ein.</li>
     * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
     * </ul>
     */
	static public class CalcSerialNumberImpl2 extends CalcSerialNumber {
        /**
         * Dieser Konstruktor �bernimmt Datenbank und Veranstaltung zur
         * Benutzung bei der sp�teren Berechnung laufender G�stenummern. 
         */
		public CalcSerialNumberImpl2(ExecutionContext context, Event event) {
			super(context, event);
		}
		
        /**
         * Diese Methode berechnet die tats�chlichen laufenden Nummern der
         * G�ste der im Konstruktor �bergebenen Veranstaltung in der ebenda
         * �bergebenen Datenbank nach folgendem Schema:
         * <ul>
         * <li>Sortiert G�ste mit Rang ein.</li>
         * <li>Sortiert G�ste anhand ihrer Kategorie ein.</li>
         * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
         * </ul>
         */
		@Override
        public void calcSerialNumber() throws BeanException, IOException {
			clearSerialNumber();
			calcSerialNumberForGuestRank();
			
			Select select = SQL.Select( context ).
					from("veraweb.tcategorie").
					selectAs("pk", "id").
					selectAs("flags", "flag").
					orderBy(Order.asc("rank").andAsc("catname"));
			for (Iterator it = context.getDatabase().getList(select, context).iterator(); it.hasNext(); ) {
				Map map = (Map)it.next();
				if (((Integer)map.get("flag")).intValue() == Categorie.FLAG_DIPLO_CORPS) {
					calcSerialNumberForDiploCorp((Integer)map.get("id"));
				} else {
					calcSerialNumberForCategorie((Integer)map.get("id"));
				}
			}
			
			calcSerialNumberForGuestName();
		}
		
		protected void calcSerialNumberForGuestRank() throws BeanException, IOException {
			WhereList where = getGuestSerialNumberWhere(event);
			where.addAnd(Expr.isNotNull("tguest.rank"));
			
			Select select = getSelect();
			select.where(where);
			select.orderBy(Order.asc("tguest.rank").andAsc("lastname_a_e1").andAsc("firstname_a_e1"));
			
			setSerialNumber(select);
		}
		
		protected void calcSerialNumberForGuestName() throws BeanException, IOException {
			WhereList where = getGuestSerialNumberWhere(event);
			
			Select select = getSelect();
			select.where(where);
			select.orderBy(Order.asc("lastname_a_e1").andAsc("firstname_a_e1"));
			
			setSerialNumber(select);
		}
		
		protected void calcSerialNumberForCategorie(Integer categorieId) throws BeanException, IOException {
			WhereList where = getGuestSerialNumberWhere(event);
			where.addAnd(Expr.equal("tperson_categorie.fk_categorie", categorieId));
			
			Select select = getSelect();
			select.join("veraweb.tperson_categorie", "tperson_categorie.fk_person", "tguest.fk_person");
			select.where(where);
			select.orderBy(Order.asc("tperson_categorie.rank").andAsc("lastname_a_e1").andAsc("firstname_a_e1"));
			
			setSerialNumber(select);
		}
		
		protected void calcSerialNumberForDiploCorp(Integer categorieId) throws BeanException, IOException {
			WhereList where = getGuestSerialNumberWhere(event);
			where.addAnd(Expr.equal("tperson_categorie.fk_categorie", categorieId));
			
			Select select = getSelect();
			select.join("veraweb.tperson_categorie", "tperson_categorie.fk_person", "tguest.fk_person");
			select.where(where);
			select.orderBy(Order.asc("tperson.diplodate").andAsc("lastname_a_e1").andAsc("firstname_a_e1"));
			
			setSerialNumber(select);
		}
		
		/**
		 * @param event Event
		 * @return
		 * 		Where-Liste mit Einschr�nkung auf die �bergebene Veranstaltung
		 * 		und nur nocht nicht einsortierte G�ste.
		 */
		private WhereList getGuestSerialNumberWhere(Event event) {
			WhereList where = new WhereList();
			where.addAnd(Expr.equal("tguest.fk_event", event.id));
			where.addAnd(Expr.equal("ishost", new Integer(0)));
			where.addAnd(Expr.isNull("orderno"));
			where.addAnd(Expr.isNull("orderno_p"));
			return where;
		}
	}

    /**
     * Berechnet die 'Laufende Nummer' einer G�steliste nach folgendem Schema:
     * <ul>
     * <li>Sortiert G�ste anhand ihrer Kategorie ein.</li>
     * <li>Sortiert G�ste mit Rang ein.</li>
     * <li>Sortiert G�ste nach Akkreditierungsdatum ein.</li>
     * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
     * </ul>
     */
	static public class CalcSerialNumberImpl3 extends CalcSerialNumber {
        /**
         * Dieser Konstruktor �bernimmt Datenbank und Veranstaltung zur
         * Benutzung bei der sp�teren Berechnung laufender G�stenummern. 
         */
		public CalcSerialNumberImpl3(ExecutionContext context, Event event) {
			super(context, event);
		}
		
        /**
         * Diese Methode berechnet die tats�chlichen laufenden Nummern der
         * G�ste der im Konstruktor �bergebenen Veranstaltung in der ebenda
         * �bergebenen Datenbank nach folgendem Schema:
         * <ul>
         * <li>Sortiert G�ste anhand ihrer Kategorie ein.</li>
         * <li>Sortiert G�ste mit Rang ein.</li>
         * <li>Sortiert G�ste nach Akkreditierungsdatum ein.</li>
         * <li>Sortiert alle anderen anhand ihres Namens ein.</li>
         * </ul>
         */
		@Override
        public void calcSerialNumber() throws BeanException, IOException {
			clearSerialNumber();
			
			WhereList where = new WhereList();
			// Nur diese Veranstaltung
			where.addAnd(Expr.equal("tguest.fk_event", event.id));
			// Kein Gastgeber
			where.addAnd(Expr.equal("ishost", new Integer(0)));
			// Auf Platz
			where.addAnd(Expr.notEqual("reserve", new Integer(1)));
			where.addAnd(new RawClause("(NOT(" +
					"(invitationtype = 1" +
					" AND invitationstatus IS NOT NULL" +
					" AND invitationstatus = 2" +
					" AND invitationstatus_p IS NOT NULL" +
					" AND invitationstatus_p = 2) OR " +
					"(invitationtype = 2" +
					" AND invitationstatus IS NOT NULL" +
					" AND invitationstatus = 2) OR " +
					"(invitationtype = 3" +
					" AND invitationstatus_p IS NOT NULL" +
					" AND invitationstatus_p = 2)))"));
			
			List order = new ArrayList();
			order.add("tcategorie.rank");
			order.add("tcategorie.catname");
			order.add("tcategorie.pk");
			order.add("tguest.rank");
			order.add("diplodate");
			order.add("lastname_a_e1");
			order.add("firstname_a_e1");
			
			Select select = getSelect();
			select.where(where);
			select.orderBy(DatabaseHelper.getOrder(order));
			
			setSerialNumber(select);
		}
	}
}
