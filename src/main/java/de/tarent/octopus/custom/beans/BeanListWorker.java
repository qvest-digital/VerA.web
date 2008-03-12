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

/*
 * Created on 23.02.2005
 */
package de.tarent.octopus.custom.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.server.OctopusContext;

/**
 * Abstrakter Worker der Seitenweise Listen von Datenbankinhalten darstallen
 * und speichern kann. Stellt mehrere Methoden zum erweitern zur Verf�gung
 * um die Datensichten einzuschr�nken und/oder zu erweitern.
 * Merkt sich zus�tzlich die Benutzer-Selektion um seiten�bergreifende
 * Aktionen durchzuf�hren.
 * 
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class BeanListWorker {
	/** Instanz eines Log4J Loggers f�r die aktuelle Klasse */
	private static final Logger logger = Logger.getLogger(BeanListWorker.class);

	/** Name des Beans, das der jeweils abgeleitete Worker verwaltet. */
	protected final String BEANNAME;

	/** Gibt das Prefix eines neu hinzugef�gten Beans an. */
	protected String INPUT_ADD = "add";
	/** Input-Parameter: Wenn dieser true ist, k�nnen dieser Liste neue Elemente hinzugef�gt werden. */
	protected String INPUT_INSERT = "doInsert";
	/** Input-Parameter: Wenn dieser true ist, k�nnen Elemente dieser Liste aktuallisiert werden. */
	protected String INPUT_UPDATE = "doUpdate";
	/** Input-Parameter: Wenn dieser true ist, k�nnen Elemente dieser Liste gel�scht werden. */
	protected String INPUT_REMOVE = "doRemove";
	/** Input-Parameter: Wenn dieser Parameter �bergeben wird, k�nnen Elemente gespeichert werden. */
	protected String INPUT_BUTTON_SAVE = "save";
	/** Input-Parameter: Wenn dieser Parameter �bergeben wird, k�nnen Elemente gel�scht werden. */
	protected String INPUT_BUTTON_REMOVE = "remove";

	/** Input-Parameter: Beinhaltet eine Liste von IDs die in �bergeben werden sollen. */
	protected String INPUT_LIST = "list";
	/** Input-Parameter: Wenn dieser Parameter true ist, wird die komplett verf�gbare Liste markiert. */
	protected String INPUT_SELECTALL = "selectAll";
	/** Input-Parameter: Wenn dieser Parameter true ist, wird die komplette markierte Liste geleert. */
	protected String INPUT_SELECTNONE = "selectNone";

    /** Octopus-Eingabe-Parameter f�r {@link #getAll(OctopusContext)} */
	public static final String INPUT_getAll[] = {};

    /** Octopus-Eingabe-Parameter f�r {@link #showList(OctopusContext)} */
	public static final String INPUT_showList[] = {};
    /** Octopus-Ausgabe-Parameter f�r {@link #showList(OctopusContext)} */
	public static final String OUTPUT_showList = "list";
    /** Octopus-Ausgabe-Parameter f�r {@link #showList(OctopusContext)} */
	public static final String OUTPUT_showListParams = "listparam";

    /** Octopus-Eingabe-Parameter f�r {@link #saveList(OctopusContext)} */
	public static final String INPUT_saveList[] = {};
    /** Octopus-Ausgabe-Parameter f�r {@link #saveList(OctopusContext)} */
	public static final String OUTPUT_saveListErrors = "listerrors";

    /** Octopus-Eingabe-Parameter f�r {@link #getSelection(OctopusContext, Integer)} */
	public static final String INPUT_getSelection[] = { "listsize" };
    /** Octopus-Eingabe-Parameter f�r {@link #getSelection(OctopusContext, Integer)} */
	public static final boolean MANDATORY_getSelection[] = { false };
    /** Octopus-Ausgabe-Parameter f�r {@link #getSelection(OctopusContext, Integer)} */
	public static final String OUTPUT_getSelection = "listselection";

    /**
     * Dieser Konstruktor setzt den Namen der zugrunde liegenden Bean.
     * 
     * @param beanName
     */
    protected BeanListWorker(String beanName) {
        assert beanName != null;
        BEANNAME = beanName;
    }
    
	/**
	 * Octopus-Aktion die alle Eintr�ge aus der Datenbank zur�ckliefert
	 * und als Liste in den Content stellt. Kann durch
	 * {@link #extendColumns(OctopusContext, Select)} erweitert bzw.
	 * {@link #extendAll(OctopusContext, Select)} eingeschr�nkt werden.
	 * 
	 * @param cntx Octopus-Context
	 * @throws BeanException
	 * @throws IOException
	 */
	public void getAll(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Select select = database.getSelect(BEANNAME);
		extendColumns(cntx, select);
		extendAll(cntx, select);
		cntx.setContent("all" + BEANNAME, database.getList(select));
	}

    /** Octopus-Ausgabe-Parameter f�r {@link #getMap(OctopusContext)} */
	public static final String INPUT_getMap[] = {};
	/**
	 * Octopus-Aktion die alle Eintr�ge aus der Datenbank zur�ckliefert
	 * und diese als Map in den Content stellt.
	 * 
	 * @param cntx
	 * @throws BeanException
	 * @throws IOException
	 */
	public void getMap(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		Bean sample = database.createBean(BEANNAME);
		
		Select select = database.getSelect(sample);
		extendColumns(cntx, select);
		extendWhere(cntx, select);
		
		List beans = database.getBeanList(BEANNAME, select);
		
		Map beanMap = Collections.EMPTY_MAP;
		try {
			if (beans == null)
				return;
			
			String idField = database.getProperty(sample, "pk");
			if (idField == null)
				idField = "id";
			if (!sample.getFields().contains(idField)) {
				logger.warn("Schl�sselfeld " + idField + " von " + BEANNAME + " nicht verf�gbar.");
				return;
			}
			
			beanMap = new HashMap();
			for (Iterator itBeans = beans.iterator(); itBeans.hasNext();) {
				Bean bean = (Bean)itBeans.next();
				beanMap.put(bean.getField(idField), bean);
			}
		} finally {
			cntx.setContent("map" + BEANNAME, beanMap);
		}
	}

	/**
	 * Octopus-Aktion die eine <strong>bl�tterbare</strong> Liste
	 * mit Beans aus der Datenbank in den Content stellt. Kann durch
	 * {@link #extendColumns(OctopusContext, Select)} erweitert bzw.
	 * {@link #extendWhere(OctopusContext, Select)} eingeschr�nkt werden.
	 * 
	 * @see #getSelection(OctopusContext, Integer)
	 * 
	 * @param cntx Octopus-Context
	 * @return Liste mit Beans, nie null.
	 * @throws BeanException
	 * @throws IOException
	 */
	public List showList(OctopusContext cntx) throws BeanException, IOException {
		Database database = getDatabase(cntx);
		
		Integer start = getStart(cntx);
		Integer limit = getLimit(cntx);
		Integer count = getCount(cntx, database);
		Map param = getParamMap(start, limit, count);
		
		Select select = getSelect(database);
		extendColumns(cntx, select);
		extendWhere(cntx, select);
		select.Limit(new Limit((Integer)param.get("limit"), (Integer)param.get("start")));
		
		cntx.setContent(OUTPUT_showListParams, param);
		cntx.setContent(OUTPUT_getSelection, getSelection(cntx, count));
		return getResultList(database, select);
	}

	/**
	 * Wird von showList verwendet um ein entsprechendes Select-Statement
	 * zur�ck zugeben. In der Standardimplementierung ein vollst�ndiges
	 * Bean-Select.
	 * 
	 * @see #getResultList(Database, Select)
	 * 
	 * @param database
	 * @return Select-Statement
	 * @throws BeanException
	 * @throws IOException
	 */
	protected Select getSelect(Database database) throws BeanException, IOException {
		return database.getSelect(BEANNAME);
	}

	/**
	 * Wird von showList verwendet um eine entsprechende Ergebnisliste
	 * aus dem �bergebenem Select-Statement zur�ckzugeben.
	 * In der Standardimplemtierung wird eine einfache Bean-Liste erstellt.
	 * 
	 * @see #getSelect(Database)
	 * 
	 * @param database Database-Instanz
	 * @param select Select-Statement
	 * @return Ergebnisliste
	 * @throws BeanException
	 * @throws IOException
	 */
	protected List getResultList(Database database, Select select) throws BeanException, IOException {
		return database.getBeanList(BEANNAME, select);
	}

	/**
	 * Speichert eine �bergebene Liste von Beans in der Datenbank. Verwendet
	 * insertBean, updateBeanList und removeSelection.
	 * 
	 * @param cntx Octopus-Context
	 * @throws BeanException
	 * @throws IOException
	 */
	public void saveList(OctopusContext cntx) throws BeanException, IOException {
		List errors = new ArrayList();
		boolean doInsert = cntx.requestAsBoolean(INPUT_INSERT).booleanValue();
		boolean doUpdate = cntx.requestAsBoolean(INPUT_UPDATE).booleanValue();
		boolean doRemove = cntx.requestAsBoolean(INPUT_REMOVE).booleanValue();
		if (!cntx.requestContains(INPUT_BUTTON_SAVE)) {
			doInsert = false;
			doUpdate = false;
		}
		if (!cntx.requestContains(INPUT_BUTTON_REMOVE)) {
			doRemove = false;
		}
		
		Request request = getRequest(cntx);
		if (doInsert) {
			int count = insertBean(cntx, errors, request.getBean(BEANNAME, INPUT_ADD));
			cntx.setContent("countInsert", new Integer(count));
		}
		if (doUpdate) {
			int count = updateBeanList(cntx, errors, request.getBeanList(BEANNAME, INPUT_LIST));
			cntx.setContent("countUpdate", new Integer(count));
		}
		if (doRemove) {
			int count = removeSelection(cntx, errors, getSelection(cntx, null));
			cntx.setContent("countRemove", new Integer(count));
		}
		if (!errors.isEmpty()) {
			cntx.setContent(OUTPUT_saveListErrors, errors);
		}
	}

	/**
	 * Wird von saveList aufgerufen und soll das �bergebene Bean speichern.
	 * Ruft in der Standard-Implementierung <code>saveBean(cntx, bean);</code>
	 * auf, wenn sowohl das isModified als auch das isCorrect-Flag true ist.<br><br>
	 * 
	 * Kann �berladen werden falls zus�tzliche Sicherheitsabfragen oder
	 * sonstige Kontrollen (z.B. existiert bereit) ausgef�hrt werden sollen.
	 * 
	 * @see #saveBean(OctopusContext, Bean)
	 * 
	 * @param cntx
	 * @param errors
	 * @param bean
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int insertBean(OctopusContext cntx, List errors, Bean bean) throws BeanException, IOException {
		int count = 0;
		if (bean.isModified() && bean.isCorrect()) {
			saveBean(cntx, bean);
			count++;
		} else if ( bean.isModified() && !bean.isCorrect()) 
		{
			//mindestens ein Attribut wurde vom User eingegeben. Bean ist aber nicht ok.
			//Fehlermeldung anzeigen.
			errors.addAll(bean.getErrors());
		}
		return count;
	}

	/**
	 * Wird von saveList aufgerufen und soll die �bergebene Liste von Beans
	 * aktuallisieren. Ruft in der Standard-Implementierung f�r jedes Bean
	 * <code>saveBean(cntx, bean);</code> auf, wenn sowohl das isModified-
	 * als auch das isCorrect-Flag true ist.<br><br>
	 * 
	 * Kann �berladen werden falls zus�tzliche Sicherheitsabfragen oder
	 * sonstige Kontrollen auf die vollst�ndige Liste ausgef�hrt werden m�ssen.
	 * 
	 * @see #saveBean(OctopusContext, Bean)
	 * 
	 * @param cntx
	 * @param errors Liste in die Warnungen als Strings hinzugef�gt werden k�nnen.
	 * @param beanlist
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int updateBeanList(OctopusContext cntx, List errors, List beanlist) throws BeanException, IOException {
		int count = 0;
		for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
			Bean bean = (Bean)it.next();
			if (bean.isModified() && bean.isCorrect()) {
				saveBean(cntx, bean);
				count++;
			} else if (bean.isModified()) {
				errors.addAll(bean.getErrors());
			}
		}
		return count;
	}

	/**
	 * Wird von saveList aufgerufen und soll die �bergebene Liste von Bean-IDs
	 * l�schen. Ruft in der Standard-Implementierung f�r jede ID
	 * <code>removeBean(cntx, bean);</code> auf.<br><br>
	 * 
	 * Kann �berladen werden falls zus�tzliche Sicherheitsabfragen und
	 * sonstige Kontrollen auf die vollst�ndige Liste ausgef�hrt werden m�ssen.
	 * 
	 * @see #removeBean(OctopusContext, Bean)
	 * 
	 * @param cntx
	 * @param errors Liste in die Warnungen als Strings hinzugef�gt werden k�nnen.
	 * @param selection
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int removeSelection(OctopusContext cntx, List errors, List selection) throws BeanException, IOException {
		int count = 0;
		Bean bean = getRequest(cntx).createBean(BEANNAME);
		for (Iterator it = selection.iterator(); it.hasNext(); ) {
			bean.setField("id", it.next());
			if (removeBean(cntx, bean)) {
				it.remove();
				count++;
			}
		}
		return count;
	}

	/**
	 * Methode die von abgeleiteten Klassen �berschrieben werden kann,
	 * um das Select-Statement um Spalten zu erweitern.
	 * 
	 * @param cntx
	 * @param select
	 * @throws BeanException
	 */
	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	/**
	 * Methode die von abgeleiteten Klassen �berschrieben werden kann,
	 * um das Select-Statement um Bedingungen zu erweitern.
	 * 
	 * @param cntx
	 * @param select
	 * @throws BeanException
	 */
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	/**
	 * Methode die von abgeleiteten Klassen �berschrieben werden kann,
	 * um das Select-Statement um Bedingungen zu erweitern.
	 * 
	 * @param cntx
	 * @param select
	 * @throws BeanException
	 */
	protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	/**
	 * Methode die von abgeleiteten Klassen �berschrieben werden kann,
	 * um ggf. abh�ngige Datenbankeintr�ge ebenfalls zu l�schen.<br><br>
	 * 
	 * Standardimplemtierung: <code>getDatabase(cntx).removeBean(bean);</code>
	 * 
	 * @param cntx
	 * @param bean
	 * @throws BeanException
	 * @throws IOException
	 */
	protected boolean removeBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		getDatabase(cntx).removeBean(bean);
		return true;
	}

	/**
	 * Methode die von abgeleiteten Klassen �berschrieben werden kann,
	 * um abh�ngige Datenbank eintr�ge ebenfalls zu aktuallisieren.<br><br>
	 * 
	 * Standardimplemtierung: <code>getDatabase(cntx).saveBean(bean);</code>
	 * 
	 * @param cntx
	 * @param bean
	 * @throws BeanException
	 * @throws IOException
	 */
	protected void saveBean(OctopusContext cntx, Bean bean) throws BeanException, IOException {
		getDatabase(cntx).saveBean(bean);
	}

	/**
	 * Mergt die aktuelle Selekion von markierten Eintr�gen mit der neuen
	 * vom Benutzer getroffenen Auswahl und gibt eine Liste der nun aktuellen
	 * Eintr�ge zur�ck.
	 * 
	 * @param cntx
	 * @param count Gibt die erwartete Gr��e der Liste an.
	 * @return neue Liste mit selektierten Listeneintr�gen (ID's als Integer).
	 * @throws BeanException
	 * @throws IOException
	 */
	public List getSelection(OctopusContext cntx, Integer count) throws BeanException, IOException {
		// Alte auswahl
		List selection = (List)cntx.sessionAsObject("selection" + BEANNAME);
		if (cntx.contentContains("getSelection")) {
			return (List)cntx.sessionAsObject("selection" + BEANNAME);
		} else {
			cntx.setContent("getSelection", Boolean.TRUE);
		}
		
		if (cntx.requestAsBoolean(INPUT_SELECTNONE).booleanValue()) {
			// Leere Liste anlegen.
			selection = new ArrayList(count != null ? count.intValue() : 10);
			List list = (List)BeanFactory.transform(cntx.requestAsObject(INPUT_LIST), List.class);
			for (Iterator it = list.iterator(); it.hasNext(); ) {
				Integer id = new Integer((String)it.next());
				if (cntx.requestAsBoolean(id + "-select").booleanValue()) {
					selection.add(id);
				}
			}
		} else if (cntx.requestAsBoolean(INPUT_SELECTALL).booleanValue()) {
			// Alle IDs aus der Datenbank in die Liste kopieren. 
			selection = new ArrayList(count != null ? count.intValue() : 10);
			Database database = getDatabase(cntx);
			Select select = database.getSelectIds(database.createBean(BEANNAME));
			extendWhere(cntx, select);
			for (Iterator it = database.getList(select).iterator(); it.hasNext(); ) {
				selection.add(((Map)it.next()).get("id"));
			}
		} else {
			// IDs zusammenf�hren.
			if (selection == null) selection = new ArrayList(count != null ? count.intValue() : 10);
			List list = (List)BeanFactory.transform(cntx.requestAsObject(INPUT_LIST), List.class);
			for (Iterator it = list.iterator(); it.hasNext(); ) {
				Integer id = new Integer((String)it.next());
				if (cntx.requestAsBoolean(id + "-select").booleanValue()) {
					if (selection.indexOf(id) == -1)
						selection.add(id);
				} else {
					selection.remove(id);
				}
			}
		}
		cntx.setSession("selection" + BEANNAME, selection);
		return selection;
	}

	/**
	 * Gibt eine Map mit Parametern zur�ck die f�r das Bl�ttern in einer
	 * Weboberfl�che ben�tigt werden.
	 * 
	 * @param start Offset ab welchen die Datens�tze angezeigt werden
	 * @param limit Anzahl Datens�tze pro Seite (maximal)
	 * @param count Anzahl DAtens�tze auf der aktuellen Seite
	 * @return Map mit Parametern
	 */
	protected Map getParamMap(Integer start, Integer limit, Integer count) {
		int pages;
		int first;
		int prev;
		int next;
		int last;
		
		if (limit.intValue() == 0) {
			pages = 1;
			first = 0;
			prev = 0;
			next = 0;
			last = 0;
			start = new Integer(0);
		} else {
			pages = (count.intValue() - (count.intValue() % limit.intValue())) / limit.intValue() +
					(count.intValue() % limit.intValue() == 0 ? 0 : 1);
			first = 0;
			last = count.intValue() - (count.intValue() % limit.intValue()) -
					(count.intValue() % limit.intValue() == 0 ? limit.intValue() : 0);
			if (start.intValue() > last)
				start = new Integer(last);
			prev = start.intValue() - limit.intValue();
			next = start.intValue() + limit.intValue();
			if (prev < first) prev = first;
			if (next > last) next = last;
		}
		
		Map param = new HashMap();
		param.put("start", start);
		param.put("limit", limit);
		param.put("count", count);
		param.put("pages", new Integer(pages));
		param.put("first", new Integer(first));
		param.put("prev", new Integer(prev));
		param.put("next", new Integer(next));
		param.put("last", new Integer(last));
		return param;
	}

	/** @return Die Anzahl von Datens�tze mit dem aktuellen Filter. */
	protected Integer getCount(OctopusContext cntx, Database database) throws BeanException, IOException {
		Select select = database.getCount(BEANNAME);
		extendWhere(cntx, select);
		return database.getCount(select);
	}

	/** @return Instanz eines Datenbank-Objektes. */
	protected abstract Database getDatabase(OctopusContext cntx);

	/** @return Instanz eines Request-Objektes. */
	protected abstract Request getRequest(OctopusContext cntx);

	/**
	 * Gibt den Index zur�ck, ab welchem Datensatz die aktuelle Seite angezeigt
	 * werden soll, vgl. {@link #getLimit(OctopusContext)}.
	 * 
	 * @param cntx Octopus-Context
	 * @return Index, nie null.
	 * @throws IOException
	 * @throws BeanException
	 */
	protected Integer getStart(OctopusContext cntx) throws BeanException, IOException {
		String s = cntx.requestAsString("start");
		Integer i = null;
		
		if (s != null && s.length() != 0) {
			try {
				i = new Integer(s);
			} catch (NumberFormatException e) {
				i = getAlphaStart(cntx, s);
			}
		}
		if (i == null)
			i = (Integer)cntx.sessionAsObject("start" + BEANNAME);
		if (i == null)
			i = new Integer(0);
		cntx.setSession("start" + BEANNAME, i);
		return i;
	}

	/**
	 * Gibt den Index anhand eines �bergebenen Start-Parameters zur�ck.
	 * Muss von dem entsprechenden ListWorker implementiert werden. 
	 * 
	 * @param cntx Octopus-Context
	 * @param start Start-Zeichenkette, niemals null.
	 * @return Index, darf null sein.
	 * @throws BeanException
	 * @throws IOException
	 */
	protected Integer getAlphaStart(OctopusContext cntx, String start) throws BeanException, IOException {
		return null;
	}

	/**
	 * Gibt das Limit zur�ck, wieviele Datens�tze auf der aktuellen Seite
	 * angezeigt werden sollen, vgl. {@link #getStart(OctopusContext)}.
	 * 
	 * @param cntx Octopus-Context
	 * @return Limit, nie null.
	 */
	protected Integer getLimit(OctopusContext cntx) {
		Integer l = null;
		try {
			l = new Integer(cntx.requestAsString("limit"));
		} catch (NumberFormatException e) {
		}
		if (l == null)
			l = (Integer)cntx.sessionAsObject("limit" + BEANNAME);
		if (l == null)
			l = new Integer(10);
		cntx.setSession("limit" + BEANNAME, l);
		return l;
	}
}
