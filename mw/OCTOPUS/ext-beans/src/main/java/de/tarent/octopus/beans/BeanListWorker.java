package de.tarent.octopus.beans;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.Statement;
import de.tarent.dblayer.sql.clause.Limit;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.server.OctopusContext;

/**
 * Abstrakter Worker der Seitenweise Listen von Datenbankinhalten darstallen
 * und speichern kann. Stellt mehrere Methoden zum erweitern zur Verfügung
 * um die Datensichten einzuschränken und/oder zu erweitern.
 * Merkt sich zusätzlich die Benutzer-Selektion um seitenübergreifende
 * Aktionen durchzuführen.
 *
 * @author Michael Klink, Alex Steeg, Christoph Jerolimov
 * @version 1.3
 */
public abstract class BeanListWorker {
    /** logger of this class. */
	private static final Logger logger = Logger.getLogger(BeanListWorker.class.getName());

	/** Name des Beans, das der jeweils abgeleitete Worker verwaltet. */
	protected final String BEANNAME;

	/** Gibt das Prefix eines neu hinzugefügten Beans an. */
	protected String INPUT_ADD = "add";
	/** Input-Parameter: Wenn dieser true ist, können dieser Liste neue Elemente hinzugefügt werden. */
	protected String INPUT_INSERT = "doInsert";
	/** Input-Parameter: Wenn dieser true ist, können Elemente dieser Liste aktuallisiert werden. */
	protected String INPUT_UPDATE = "doUpdate";
	/** Input-Parameter: Wenn dieser true ist, können Elemente dieser Liste gelöscht werden. */
	protected String INPUT_REMOVE = "doRemove";
	/** Input-Parameter: Wenn dieser Parameter übergeben wird, können Elemente gespeichert werden. */
	protected String INPUT_BUTTON_SAVE = "save";
	/** Input-Parameter: Wenn dieser Parameter übergeben wird, können Elemente gelöscht werden. */
	protected String INPUT_BUTTON_REMOVE = "remove";

	/** Input-Parameter: Beinhaltet eine Liste von IDs die in übergeben werden sollen. */
	protected String INPUT_LIST = "list";
	/** Input-Parameter: Wenn dieser Parameter true ist, wird die komplett verfügbare Liste markiert. */
	protected String INPUT_SELECTALL = "selectAll";
	/** Input-Parameter: Wenn dieser Parameter true ist, wird die komplette markierte Liste geleert. */
	protected String INPUT_SELECTNONE = "selectNone";

    /** Octopus-Eingabe-Parameter für {@link #getAll(OctopusContext)} */
	public static final String INPUT_getAll[] = {};

    /** Octopus-Eingabe-Parameter für {@link #showList(OctopusContext)} */
	public static final String INPUT_showList[] = {};
    /** Octopus-Ausgabe-Parameter für {@link #showList(OctopusContext)} */
	public static final String OUTPUT_showList = "list";
    /** Octopus-Ausgabe-Parameter für {@link #showList(OctopusContext)} */
	public static final String OUTPUT_showListParams = "listparam";

    /** Octopus-Eingabe-Parameter für {@link #saveList(OctopusContext)} */
	public static final String INPUT_saveList[] = {};
    /** Octopus-Ausgabe-Parameter für {@link #saveList(OctopusContext)} */
	public static final String OUTPUT_saveListErrors = "listerrors";

    /** Octopus-Eingabe-Parameter für {@link #getSelection(OctopusContext, Integer)} */
	public static final String INPUT_getSelection[] = { "listsize" };
    /** Octopus-Eingabe-Parameter für {@link #getSelection(OctopusContext, Integer)} */
	public static final boolean MANDATORY_getSelection[] = { false };
    /** Octopus-Ausgabe-Parameter für {@link #getSelection(OctopusContext, Integer)} */
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
     * This octopus action creates a {@link List} wrapping a database
     * {@link ResultSet} selecting all entries (subject to the filters
     * set in {@link #extendAll(OctopusContext, Select)}) in the backing
     * table. This list renders them accessible by use of a {@link Map}
     * implementation wrapping the current row of the {@link ResultSet}.<br>
     * If additional result columns are required you can extend the
     * {@link Select} {@link Statement} used here by overriding
     * {@link #extendColumns(OctopusContext, Select)}. If there are some
     * filters to be taken into account (e.g. 'only those entries the
     * user is privileged to access') you can extend the {@link Where}
     * clause of the {@link Select Select} {@link Statement} by overriding
     * {@link #extendAll(OctopusContext, Select)}. Please don't mix this
     * up with {@link #extendWhere(OctopusContext, Select)} which is
     * <em>not</em> used here.<br>
     * The list created here is registered with octopus to be closed at the
     * end of the request processing. If you require a list for long term
     * use (e.g. stored in the session) this is <em>not</em> the right
     * method for you.<br>
     * Beware! There are some warnings about the {@link List} implementation
     * used here. Read {@link Database#getList(Select, ExecutionContext) this}
     * for more. Even though objections concerning database connection pool
     * drainage are attenuated by the registration for closing after the
     * request is processed you should keep the number of these lists used
     * in a task as low as sensibly possible.
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
        ResultList resultList = database.getList(select, database);
        cntx.addCleanupCode(resultList);
		cntx.setContent("all" + BEANNAME, resultList);
	}

    /** Octopus-Ausgabe-Parameter für {@link #getMap(OctopusContext)} */
	public static final String INPUT_getMap[] = {};
	/**
	 * Octopus-Aktion die alle Einträge aus der Datenbank zurückliefert
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
				logger.warning("Schlüsselfeld " + idField + " von " + BEANNAME + " nicht verfügbar.");
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
	 * Octopus-Aktion die eine <strong>blätterbare</strong> Liste
	 * mit Beans aus der Datenbank in den Content stellt. Kann durch
	 * {@link #extendColumns(OctopusContext, Select)} erweitert bzw.
	 * {@link #extendWhere(OctopusContext, Select)} eingeschränkt werden.
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
	 * zurück zugeben. In der Standardimplementierung ein vollständiges
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
	 * aus dem übergebenem Select-Statement zurückzugeben.
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
	 * Speichert eine übergebene Liste von Beans in der Datenbank. Verwendet
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
		TransactionContext context = getDatabase( cntx ).getTransactionContext();
		try
		{
			Request request = getRequest(cntx);
			if (doInsert) {
				int count = insertBean(cntx, errors, request.getBean(BEANNAME, INPUT_ADD), context);
				cntx.setContent("countInsert", new Integer(count));
			}
			if (doUpdate) {
				int count = updateBeanList(cntx, errors, request.getBeanList(BEANNAME, INPUT_LIST), context);
				cntx.setContent("countUpdate", new Integer(count));
			}
			if (doRemove) {
				int count = removeSelection(cntx, errors, getSelection(cntx, null), context);
				cntx.setContent("countRemove", new Integer(count));
			}
			if (!errors.isEmpty()) {
				cntx.setContent(OUTPUT_saveListErrors, errors);
			}
			context.commit();
		}
		catch( Throwable e )
		{
			context.rollBack();
			throw new BeanException( "Die Änderungen an der Datenliste konnten nicht gespeichert werden.", e );
		}
	}

	/**
	 * Wird von saveList aufgerufen und soll das übergebene Bean speichern.
	 * Ruft in der Standard-Implementierung <code>saveBean(cntx, bean);</code>
	 * auf, wenn sowohl das isModified als auch das isCorrect-Flag true ist.<br><br>
	 *
	 * Kann überladen werden falls zusätzliche Sicherheitsabfragen oder
	 * sonstige Kontrollen (z.B. existiert bereit) ausgeführt werden sollen.
	 *
	 * @see #saveBean(OctopusContext, Bean)
	 *
	 * @param cntx
	 * @param errors
	 * @param bean
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int insertBean(OctopusContext cntx, List errors, Bean bean, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		if (bean.isModified() && bean.isCorrect()) {
			saveBean(cntx, bean, context);
			count++;
		}
		return count;
	}

	/**
	 * Wird von saveList aufgerufen und soll die übergebene Liste von Beans
	 * aktuallisieren. Ruft in der Standard-Implementierung für jedes Bean
	 * <code>saveBean(cntx, bean);</code> auf, wenn sowohl das isModified-
	 * als auch das isCorrect-Flag true ist.<br><br>
	 *
	 * Kann überladen werden falls zusätzliche Sicherheitsabfragen oder
	 * sonstige Kontrollen auf die vollständige Liste ausgeführt werden müssen.
	 *
	 * @see #saveBean(OctopusContext, Bean)
	 *
	 * @param cntx
	 * @param errors Liste in die Warnungen als Strings hinzugefügt werden können.
	 * @param beanlist
	 * @param context
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int updateBeanList(OctopusContext cntx, List errors, List beanlist, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		for (Iterator it = beanlist.iterator(); it.hasNext(); ) {
			Bean bean = (Bean)it.next();
			if (bean.isModified() && bean.isCorrect()) {
				saveBean(cntx, bean, context);
				count++;
			} else if (!bean.isModified()) {
				errors.addAll(bean.getErrors());
			}
		}
		return count;
	}

	/**
	 * Wird von saveList aufgerufen und soll die übergebene Liste von Bean-IDs
	 * löschen. Ruft in der Standard-Implementierung für jede ID
	 * <code>removeBean(cntx, bean);</code> auf.<br><br>
	 *
	 * Kann überladen werden falls zusätzliche Sicherheitsabfragen und
	 * sonstige Kontrollen auf die vollständige Liste ausgeführt werden müssen.
	 *
	 * @see #removeBean(OctopusContext, Bean)
	 *
	 * @param cntx
	 * @param errors Liste in die Warnungen als Strings hinzugefügt werden können.
	 * @param selection
	 * @param context
	 * @throws BeanException
	 * @throws IOException
	 */
	protected int removeSelection(OctopusContext cntx, List errors, List selection, TransactionContext context) throws BeanException, IOException {
		int count = 0;
		Bean bean = getRequest(cntx).createBean(BEANNAME);
		for (Iterator it = selection.iterator(); it.hasNext(); ) {
			bean.setField("id", it.next());
			if (removeBean(cntx, bean, context)) {
				it.remove();
				count++;
			}
		}
		return count;
	}

	/**
	 * Methode die von abgeleiteten Klassen überschrieben werden kann,
	 * um das Select-Statement um Spalten zu erweitern.
	 *
	 * @param cntx
	 * @param select
	 * @throws BeanException
	 */
	protected void extendColumns(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	/**
	 * Methode die von abgeleiteten Klassen überschrieben werden kann,
	 * um das Select-Statement um Bedingungen zu erweitern.
	 *
	 * @param cntx
	 * @param select
	 * @throws BeanException
	 */
	protected void extendWhere(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	/**
	 * Methode die von abgeleiteten Klassen überschrieben werden kann,
	 * um das Select-Statement um Bedingungen zu erweitern.
	 *
	 * @param cntx
	 * @param select
	 * @throws BeanException
	 */
	protected void extendAll(OctopusContext cntx, Select select) throws BeanException, IOException {
	}

	/**
	 * Methode die von abgeleiteten Klassen überschrieben werden kann,
	 * um ggf. abhängige Datenbankeinträge ebenfalls zu löschen.<br><br>
	 *
	 * Standardimplemtierung: <code>getDatabase(cntx).removeBean(bean, context);</code>
	 *
	 * @param cntx
	 * @param bean
	 * @param context
	 * @throws BeanException
	 * @throws IOException
	 */
	protected boolean removeBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		context.getDatabase().removeBean(bean,context);
		return true;
	}

	/**
	 * Methode die von abgeleiteten Klassen überschrieben werden kann,
	 * um abhängige Datenbankeinträge ebenfalls zu aktuallisieren.<br><br>
	 *
	 * Die Implementierung wurde dahingehend geändert, dass nun ein ExecutionContext
	 * übergeben werden muss, in dessen Kontext die Anfrage an die Datenbank gestellt wird.
	 * Dies kann z.B. eine Database sein, oder aber ein TransactionContext.
	 *
	 * @param cntx
	 * @param bean
	 * @param context
	 * @throws BeanException
	 * @throws IOException
	 */
	protected void saveBean(OctopusContext cntx, Bean bean, TransactionContext context) throws BeanException, IOException {
		context.getDatabase().saveBean( bean, context, bean.isModified() );
	}

	/**
	 * Mergt die aktuelle Selekion von markierten Einträgen mit der neuen
	 * vom Benutzer getroffenen Auswahl und gibt eine Liste der nun aktuellen
	 * Einträge zurück.
	 *
	 * @param cntx
	 * @param count Gibt die erwartete Größe der Liste an.
	 * @return neue Liste mit selektierten Listeneinträgen (ID's als Integer).
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
            ResultList resultList = database.getList(select, database);
			for (Iterator it = resultList.iterator(); it.hasNext(); ) {
				selection.add(((Map)it.next()).get("id"));
			}
            resultList.close();
		} else {
			// IDs zusammenführen.
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
	 * Gibt eine Map mit Parametern zurück die für das Blättern in einer
	 * Weboberfläche benötigt werden.
	 *
	 * @param start Offset ab welchen die Datensätze angezeigt werden
	 * @param limit Anzahl Datensätze pro Seite (maximal)
	 * @param count Anzahl DAtensätze auf der aktuellen Seite
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
					(  count.intValue() != 0 && count.intValue() % limit.intValue()  == 0 ? limit.intValue() : 0);
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

	/** @return Die Anzahl von Datensätze mit dem aktuellen Filter. */
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
	 * Gibt den Index zurück, ab welchem Datensatz die aktuelle Seite angezeigt
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
	 * Gibt den Index anhand eines übergebenen Start-Parameters zurück.
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
	 * Gibt das Limit zurück, wieviele Datensätze auf der aktuellen Seite
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
