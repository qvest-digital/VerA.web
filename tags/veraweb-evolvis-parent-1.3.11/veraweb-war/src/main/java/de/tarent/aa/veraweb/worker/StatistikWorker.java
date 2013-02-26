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
package de.tarent.aa.veraweb.worker;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;

import de.tarent.aa.veraweb.beans.facade.EventConstants;
import de.tarent.aa.veraweb.utils.ExportHelper;
import de.tarent.aa.veraweb.utils.OctopusHelper;
import de.tarent.commons.spreadsheet.export.SpreadSheet;
import de.tarent.commons.spreadsheet.export.SpreadSheetFactory;
import de.tarent.dblayer.helper.ResultList;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.clause.Clause;
import de.tarent.dblayer.sql.clause.Expr;
import de.tarent.dblayer.sql.clause.GroupBy;
import de.tarent.dblayer.sql.clause.Order;
import de.tarent.dblayer.sql.clause.Where;
import de.tarent.dblayer.sql.statement.Select;
import de.tarent.octopus.PersonalConfigAA;
import de.tarent.octopus.beans.BeanException;
import de.tarent.octopus.beans.BeanFactory;
import de.tarent.octopus.beans.Database;
import de.tarent.octopus.beans.veraweb.DatabaseVeraWeb;
import de.tarent.octopus.response.TcBinaryResponseEngine;
import de.tarent.octopus.server.OctopusContext;

/**
 * Dieser Octopus-Worker stellt Aktionen zur Erstellung
 * von Statistiken zur Verf�gung.
 *  
 * @author Christoph
 */
public class StatistikWorker {
    /** Logger dieser Klasse */
	private final Logger logger = Logger.getLogger(getClass());

	/** Octopus-Eingabeparameter f�r die Aktion {@link #getFirstDayInMonth()} */
	public static final String INPUT_getFirstDayInMonth[] = {};
	/** Octopus-Ausgabeparameter f�r die Aktion {@link #getFirstDayInMonth()} */
	public static final String OUTPUT_getFirstDayInMonth = "firstDayInMonth";
	/**
	 * @return Gibt den ersten Tag des aktuellen Monats zur�ck.
	 */
	public Date getFirstDayInMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/** Octopus-Eingabeparameter f�r die Aktion {@link #getLastDayInMonth()} */
	public static final String INPUT_getLastDayInMonth[] = {};
	/** Octopus-Ausgabeparameter f�r die Aktion {@link #getLastDayInMonth()} */
	public static final String OUTPUT_getLastDayInMonth = "lastDayInMonth";
	/**
	 * @return Gibt den letzten Tag des aktuellen Monats zur�ck.
	 */
	public Date getLastDayInMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	/** Octopus-Eingabeparameter f�r die Aktion {@link #loadStatistik(OctopusContext, String, String, String)} */
	public static final String INPUT_loadStatistik[] = { "statistik", "begin", "end" };
	/** Octopus-Eingabeparameter f�r die Aktion {@link #loadStatistik(OctopusContext, String, String, String)} */
	public static final boolean MANDATORY_loadStatistik[] = { false, false, false };
	/**
	 * Speichert die Einstellungen f�r die n�chste Statistik in der Session.
	 * 
	 * @param cntx Octopus-Context
	 * @param statistik Statistikname
	 * @param begin Zeitrahmen-Beginn
	 * @param end Zeitrahmen-Ende
	 */
	public void loadStatistik(OctopusContext cntx, String statistik, String begin, String end) {
		Map map = (Map)cntx.sessionAsObject("statistikSettings");
		if (map == null) {
			map = new HashMap();
			cntx.setSession("statistikSettings", map);
		}
		
		if (statistik != null && statistik.length() != 0) {
			map.put("statistik", statistik);
			map.put("begin", begin);
			map.put("end", end);
		}
		cntx.setContent("statistik", map.get("statistik"));
		cntx.setContent("begin", map.get("begin"));
		cntx.setContent("end", map.get("end"));
	}

	/** Octopus-Eingabeparameter f�r die Aktion {@link #getStatistik(OctopusContext, String, String, String, Integer)} */
	public static final String INPUT_getStatistik[] = { "statistik", "begin", "end", "id" };
	/** Octopus-Eingabeparameter f�r die Aktion {@link #getStatistik(OctopusContext, String, String, String, Integer)} */
	public static final boolean MANDATORY_getStatistik[] = { true, false, false, false };
	/**
	 * Exportiert Statistiken f�r folgende F�lle:<br>
	 * 
	 * <ul>
	 * <li><em>EventsPerYear</em> - �bersicht �ber die Anzahl der Veranstaltungen pro Jahr.</li>
	 * <li><em>EventsPerMonth</em> - �bersicht �ber die Anzahl der Veranstaltungen pro Monat.</li>
	 * <li><em>EventsGroupByHost</em> - �bersicht �ber die Verstanstaltungen eines Gastgebers.</li>
	 * <li><em>EventsGroupByGuest</em> - �bersicht �ber die Veranstaltungen eines Gastes.</li>
	 * </ul>
	 * 
	 * @param cntx Octopus-Context
	 * @param statistik Statistikname
	 * @param begin Zeitrahmen-Beginn
	 * @param end Zeitrahmen-Ende
	 * @param id Abh�ngig vom Statistiknamen
	 */
	public void getStatistik(OctopusContext cntx, String statistik, String begin, String end, Integer id) throws BeanException, IOException {
		Database database = new DatabaseVeraWeb(cntx);
		
		Date filterBegin = (Date)BeanFactory.transform(begin, Date.class);
		Date filterEnd = (Date)BeanFactory.transform(end, Date.class);
		
		Select select;
		Clause clause = getEventFilter(cntx, filterBegin, filterEnd);
		if (statistik.equals("EventsPerYear")) {
			select = getEventsPerYear( database );
			select.where(clause);
		} else if (statistik.equals("EventsPerMonth")) {
			select = getEventsPerMonth( database );
			select.where(clause);
		} else if (statistik.equals("EventsGroupByHost")) {
			select = getEventsGroupByHost( database );
			if (id != null) {
				clause = Where.and(Expr.equal("tguest.fk_person", id), clause);
				cntx.setContent("person", database.getBean("Person", id));
			}
			select.where(clause);
		} else if (statistik.equals("EventsGroupByGuest")) {
			select = getEventsGroupByGuest( database );
			if (id != null) {
				clause = Where.and(Expr.equal("tguest.fk_person", id), clause);
				cntx.setContent("person", database.getBean("Person", id));
			}
			select.where(clause);
		} else if (statistik.equals("EventsGroupByLocation")) {
			select = getEventsGroupByLocation( database );
			select.where(clause);
		} else {
			throw new BeanException("Es wurde versucht eine unbekannte Statistik zu exportieren: " + statistik);
		}
		
		
		// EXPORT IN EINE ODS-DATEI
		//ResultList resultList = (ResultList)database.getList(select);
		//cntx.setContent("stream", getExport(cntx, resultList.getResultSet()));
		
		// EXPORT �BER EIN VELOCITY SCRIPT
		cntx.setContent("begin", filterBegin);
		cntx.setContent("end", filterEnd);
		ResultList resultList = (ResultList)database.getList(select, database);
		cntx.setContent("result", resultList);
	}

	/**
	 * Erstellt ein SQL-Statement das f�r folgende Statistik verwendet wird:
	 * <ul>
	 * <li><em>"Gesamt�bersicht �ber die Anzahl der Veranstaltungen pro Jahr."</em></li>
	 * </ul>
	 * 
	 * @return SQL-Statement, nie null.
	 */
	protected Select getEventsPerYear( Database db ) {
		return SQL.Select( db ).
				from("veraweb.tevent").
				selectAs("date(to_char(datebegin, 'YYYY-01-01'))", "year").
				selectAs("count(*)", "events").
				groupBy(GroupBy.groupBy("year")).
				orderBy(Order.asc("year"));
	}

	/**
	 * Erstellt ein SQL-Statement das f�r folgende Statistik verwendet wird:
	 * <ul>
	 * <li><em>"Gesamt�bersicht �ber die Anzahl der Veranstaltungen pro Monat."</em></li>
	 * </ul>
	 * 
	 * @return SQL-Statement, nie null.
	 */
	protected Select getEventsPerMonth( Database db ) {
		return SQL.Select( db ).
				from("veraweb.tevent").
				selectAs("date(to_char(datebegin, 'YYYY-MM-01'))", "month").
				selectAs("count(*)", "events").
				groupBy(GroupBy.groupBy("month")).
				orderBy(Order.asc("month"));
	}

	/**
	 * Erstellt ein SQL-Statement das f�r folgende Statistik verwendet wird:
	 * <ul>
	 * <li><em>"Gesamt�bersicht �ber alle Veranstaltungen, gruppiert nach Gastgeber."</em></li>
	 * <li><em>"�bersicht �ber alle Veranstaltungen eines Gastgebers (mit Suche)."</em></li>
	 * </ul>
	 * <p>
	 * Die Filterung auf einen bestimmten Gastgeber muss dabei auf einer
	 * h�her liegenden Ebene geschehen. Theoretisch lie�e sich mit dieser
	 * Abfrage auch eine (gruppierte) Liste aller G�stgeber realisieren.
	 * </p>
	 * 
	 * @return SQL-Statement, nie null.
	 */
	protected Select getEventsGroupByHost( Database db ) {
		String zusagen = "(SELECT SUM(" +
				"(CASE WHEN (invitationtype != 3 AND invitationstatus = 1) THEN 1 ELSE 0 END) +" +
				"(CASE WHEN (invitationtype != 2 AND invitationstatus_p = 1) THEN 1 ELSE 0 END))" +
				" FROM veraweb.tguest g WHERE" +
				" g.fk_event = tevent.pk)";
		
		return SQL.Select( db ).
				from("veraweb.tevent").
				joinLeftOuter("veraweb.tguest", "tevent.pk", "tguest.fk_event AND tguest.ishost = 1").
				joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
				selectAs("tguest.pk", "subhead").
				selectAs("tguest.fk_person", "person").
				selectAs("tperson.lastname_a_e1", "lastname").
				selectAs("tperson.firstname_a_e1", "firstname").
				selectAs("tperson.function_a_e1", "function").
				selectAs("tevent.shortname", "shortname").
				selectAs("tevent.location", "location").
				selectAs("tevent.datebegin", "datebegin").
				selectAs(zusagen, "zusagen").
				orderBy(Order.asc("tperson.lastname_a_e1").andAsc("tperson.firstname_a_e1").andAsc("tevent.datebegin"));
	}

	/**
	 * Erstellt ein SQL-Statement das f�r folgende Statistik verwendet wird:
	 * <ul>
	 * <li><em>"�bersicht �ber alle Veranstaltungen eines Gastes (mit Suche)."</em></li>
	 * </ul>
	 * <p>
	 * Die Filterung auf einen bestimmten Gast muss dabei auf einer
	 * h�her liegenden Ebene geschehen. Theoretisch lie�e sich mit dieser
	 * Abfrage auch eine (gruppierte) Liste alles G�sten realisieren.
	 * </p>
	 * 
	 * @return SQL-Statement, nie null.
	 */
	protected Select getEventsGroupByGuest( Database db ) {
		return SQL.Select( db ).
				from("veraweb.tguest").
				joinLeftOuter("veraweb.tevent", "fk_event", "tevent.pk").
				joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
				selectAs("tguest.fk_person", "subhead").
				selectAs("tguest.fk_person", "person").
				selectAs("tperson.lastname_a_e1", "lastname").
				selectAs("tperson.firstname_a_e1", "firstname").
				selectAs("tperson.function_a_e1", "function").
				selectAs("tevent.shortname", "shortname").
				selectAs("tevent.location", "shortname").
				selectAs("tevent.datebegin", "datebegin").
				selectAs("tguest.invitationtype", "invitationtype").
				selectAs("tguest.invitationstatus", "invitationstatus").
				selectAs("tguest.invitationstatus_p", "invitationstatus_p").
				orderBy(Order.asc("tperson.lastname_a_e1").andAsc("tperson.firstname_a_e1").andAsc("tevent.shortname"));
	}

	/**
	 * Erstellt ein SQL-Statement das f�r folgende Statistik verwendet wird:
	 * <ul>
	 * <li><em>"�bersicht �ber alle Veranstaltungen sortiert nach Veranstaltungsort."</em></li>
	 * </ul>
	 * <p>
	 * Die Filterung auf einen bestimmten Veranstaltungsort muss dabei auf einer
	 * h�her liegenden Ebene geschehen. Theoretisch lie�e sich mit dieser
	 * Abfrage auch eine (gruppierte) Liste aller Veranstaltungsorte realisieren.
	 * </p>
	 * 
	 * @return SQL-Statement, nie null.
	 */
	protected Select getEventsGroupByLocation( Database db ) {
		return SQL.Select( db ).
				from("veraweb.tevent").
				joinLeftOuter("veraweb.tguest", "tevent.pk", "tguest.fk_event AND tguest.ishost = 1").
				joinLeftOuter("veraweb.tperson", "tguest.fk_person", "tperson.pk").
				selectAs("tevent.location", "location").
				selectAs("tevent.shortname", "shortname").
				selectAs("tperson.lastname_a_e1", "lastname").
				selectAs("tperson.firstname_a_e1", "firstname").
				selectAs("tevent.datebegin", "datebegin").
				orderBy(Order.asc("location").andAsc("datebegin"));
	}

	/**
	 * Gibt einen SQL-Filter zur�ck der eine Einschr�nkung auf den
	 * Starttermin einer Veranstaltung legt.
	 * 
	 * @param cntx
	 * @param filterBegin
	 * @param filterEnd
	 * @return Filter
	 */
	protected Clause getEventFilter(OctopusContext cntx, Date filterBegin, Date filterEnd) {
		Clause clause = Expr.equal("tevent.fk_orgunit", ((PersonalConfigAA)cntx.personalConfig()).getOrgUnitId());
		
		if (filterBegin != null && filterEnd != null) {
			clause = Where.and(clause, Where.and(
					Expr.greaterOrEqual("datebegin", filterBegin),
					Expr.lessOrEqual("datebegin", filterEnd)));
		} else if (filterBegin != null) {
			clause = Where.and(clause,
					Expr.greaterOrEqual("datebegin", filterBegin));
		} else if (filterEnd != null) {
			clause = Where.and(clause,
					Expr.lessOrEqual("datebegin", filterEnd));
		}
		return clause;
	}

	/**
	 * Exportiert eine Veranstaltung und gibt das Spreadsheet Ergebnis
	 * in einer Ocotpus-ResultMap zur�ck, die von der Binary-Response
	 * ausgegeben werden kann.
	 * 
	 * @see #getColumnName(String)
	 * @see #getColumnValue(String, ResultSet, Object)
	 * 
	 * @param cntx
	 * @param resultSet
	 * @return Map mit Stream Informationen
	 * @throws BeanException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParserConfigurationException
	 * @throws FactoryConfigurationError
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	protected Map getExport(OctopusContext cntx, ResultSet resultSet) throws IOException, SQLException, FactoryConfigurationError, TransformerFactoryConfigurationError {
		String filename = OctopusHelper.getFilename(cntx, "ods", "export.ods");
		final SpreadSheet spreadSheet = SpreadSheetFactory.getSpreadSheet(SpreadSheetFactory.TYPE_ODS_DOCUMENT);
		spreadSheet.init();
		
		// TABLE START
		int size = resultSet.getMetaData().getColumnCount();
		String column[] = new String[size + 1];
		for (int i = 1; i <= size; i++) {
			column[i] = resultSet.getMetaData().getColumnName(i);
		}
		spreadSheet.openTable("Statistik", size);
		
		// header
		spreadSheet.openRow();
		for (int i = 1; i <= size; i++) {
			spreadSheet.addCell(getColumnName(column[i]));
		}
		spreadSheet.closeRow();
		
		// data
		while (resultSet.next()) {
			spreadSheet.openRow();
			for (int i = 1; i <= size; i++) {
				spreadSheet.addCell(getColumnValue(column[i], resultSet, resultSet.getObject(i)));
			}
			spreadSheet.closeRow();
		}
		
		spreadSheet.closeTable();
		// TABLE END
		
		// SpreadSheet speichern
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);
        new Thread(new Runnable() {
        	public void run() {
        		try {
					spreadSheet.save(pos);
				} catch (Throwable t) {
					logger.error("Fehler beim Erstellen des Exports aufgetreten.", t);
        			// This will force a log output.
        			t.printStackTrace(System.out);
        			t.printStackTrace(System.err);
				} finally {
					try {
						pos.close();
					} catch (IOException e) {
					}
				}
        	}
        }).start();
		
		Map stream = new HashMap();
		stream.put(TcBinaryResponseEngine.PARAM_TYPE, TcBinaryResponseEngine.BINARY_RESPONSE_TYPE_STREAM);
		stream.put(TcBinaryResponseEngine.PARAM_FILENAME, ExportHelper.getFilename(filename));
		stream.put(TcBinaryResponseEngine.PARAM_MIMETYPE, ExportHelper.getContentType(spreadSheet.getContentType()));
		stream.put(TcBinaryResponseEngine.PARAM_STREAM, pis);
		stream.put(TcBinaryResponseEngine.PARAM_IS_ATTACHMENT, Boolean.TRUE);
		return stream;
	}

	protected String getColumnName(String name) {
		if (name.equals("shortname")) {
			return "Name der Veranstaltung";
		} else if (name.equals("datebegin")) {
			return "Beginn";
		} else if (name.equals("invitationtype")) {
			return "Einladungsart";
		} else if (name.equals("invitationstatus")) {
			return "Status";
		} else if (name.equals("invitationstatus_p")) {
			return "Status Partner";
		} else if (name.equals("year")) {
			return "Jahr";
		} else if (name.equals("month")) {
			return "Monat";
		} else if (name.equals("events")) {
			return "Veranstaltungen";
		}
		
		return "##" + name + "##";
	}

	protected Object getColumnValue(String name, ResultSet resultSet, Object content) throws SQLException {
		if (name.equals("invitationtype")) {
			return GuestExportWorker.getType((Integer)content);
		} else if (name.equals("invitationstatus")) {
			int type = resultSet.getInt("invitationtype");
			if (type != EventConstants.TYPE_NURPARTNER) {
				return GuestExportWorker.getStatus((Integer)content);
			} else {
				return "nicht eingeladen";
			}
		} else if (name.equals("invitationstatus_p")) {
			int type = resultSet.getInt("invitationtype");
			if (type != EventConstants.TYPE_OHNEPARTNER) {
				return GuestExportWorker.getStatus((Integer)content);
			} else {
				return "nicht eingeladen";
			}
		}
		
		return content;
	}
}
