/*
 * tarent-database,
 * jdbc database library
 * Copyright (c) 2005-2006 tarent GmbH
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
 * interest in the program 'tarent-database'
 * Signature of Elmar Geese, 14 June 2007
 * Elmar Geese, CEO tarent GmbH.
 */

/**
 * @author  peet@tarent.de
 */
package de.tarent.dblayer.octopus;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;

import de.tarent.commons.logging.LogFactory;
import de.tarent.dblayer.engine.DB;
import de.tarent.dblayer.engine.DBContext;
import de.tarent.dblayer.engine.Pool;
import de.tarent.dblayer.sql.SQL;
import de.tarent.dblayer.sql.statement.Insert;
/*
 * Copyright (c) tarent GmbH Heilsbachstrasse 24 . 53123 Bonn www.tarent.de . info@tarent.de
 * 
 * Created on 11.10.2006
 */
public class CopyDBWorker 
{
	public static final String	TABLE_NAME_PATTERN	= "t%";
	private static final List ignoreList = new ArrayList();
	private static Log logger = LogFactory.getLog(CopyDBWorker.class);


	/** Param Definition */
	public static String[]			INPUT_copy					= {"poolNameFrom", "schemeFrom", "poolNameTo", "schemeTo", "tablesToIgnore"};

	/**
	 * copying table-data (from all tables in the given scheme which name starts with TABLE_NAME_PATTERN) from the db in
	 * poolNameFrom into the db in poolNameTo. Array-type data like Byte[] (e.g. Pictures) is not supported.
	 * 
	 * Both db's must be equivalent in terms of tables, table names and column names. They may differ in column types
	 * while dblayer can handle these differences.
	 * 
	 * //TODO should be augmented to let you specify the involved tables (e.g. by param list).
	 * 
	 * @param poolNameFrom
	 *          pool name of the database where the data derived from
	 * @param schemeFrom
	 *          scheme name of the data donating tables
	 * @param poolNameTo
	 *          pool name of the database where the data should go to
	 * @param schemeTo
	 *          scheme name of the data recieving tables
	 * @param tablesToIgnore
	 * 			names of tables (space seperated), that should be ignored
	 */
	public void copy(String poolNameFrom, String schemeFrom, String poolNameTo, String schemeTo , String tablesToIgnore)
	{
		assert poolNameFrom != null;
		assert poolNameTo != null;

		logger.info("Starting to copy database scheme " + schemeFrom + " from pool " + poolNameFrom + " to scheme " + schemeTo + " in pool " + poolNameTo + " ignoring tables " + tablesToIgnore + ".");
		StringTokenizer strTok = new StringTokenizer(tablesToIgnore != null?tablesToIgnore:"");
		while (strTok.hasMoreTokens())
			ignoreList.add(strTok.nextToken());
		
		try
		{
			Pool from = DB.getPool(poolNameFrom);

			DBContext contextTo = DB.getDefaultContext(poolNameTo);
			Connection fromConnection = from.getConnection();
			DatabaseMetaData metaDataFrom = fromConnection.getMetaData();

			String[] types = {"TABLE"};
			ResultSet tables = metaDataFrom.getTables(null, schemeFrom, TABLE_NAME_PATTERN, types);
			Object value;
			Insert insert;
			String tableName, tableNameFrom, tablenameTo, sel;
			Statement stm;
			ResultSet selectResult;
			ResultSetMetaData meta;
			while (tables.next())
			{
				tableName = tables.getString("TABLE_NAME");
				
				if (ignoreList.contains(tableName.toLowerCase())){
					logger.info("skipping table " + tableName);
					continue;
				}
				
				logger.info("transferring table " + tableName);
				tableNameFrom = schemeFrom + "." + tableName;
				tablenameTo = schemeTo + "." + tableName;

				sel = "SELECT * FROM " + tableNameFrom;
				stm = fromConnection.createStatement();
				stm.execute(sel);
				selectResult = stm.getResultSet();
				meta = selectResult.getMetaData();

				// Inserts ueber dblayer -> Abstraktion vom DB-Typ

				preInsertTable(tablenameTo, contextTo);

				while (selectResult.next())
				{
					insert = SQL.Insert(contextTo).table(tablenameTo);
					for (int i = 1; i <= meta.getColumnCount(); i++)
					{
						value = selectResult.getObject(i);
						// we don't support blobs
						// Insert doesn't support null values
						if (value != null && !value.getClass().isArray())
						{
							insert.insert(meta.getColumnName(i), value);
						}
					}
					try
					{
						insert.execute();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				selectResult.close();
				afterInsertTable(tablenameTo, contextTo);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.info("CopyDB finished.");
	}

	/**
	 * Actions to be done before inserting data-dump
	 * 
	 * @param tableName
	 *          full qualified name (scheme.tabelName)
	 * @param context
	 */
	private void preInsertTable(String tableName, DBContext context)
	{
		try
		{
			// Ziel-Tabelle leeren
			SQL.Delete(context).from(tableName).execute();

			if (SQL.isPostgres(context))
			{
				// Ziel-Tabelle Trigger aus
				DB.getStatement(context).execute("alter table " + tableName + " disable trigger all");
			} else if (SQL.isMSSQL(context))
			{
				try
				{
					// die pk Spalte für den Insert freischalten
					DB.getStatement(context).execute("SET IDENTITY_INSERT " + tableName + " ON");
				} catch (Exception e)
				{
					// nicht alle Tabellen haben einen IDENTITY_INSERT. Dann klappt das Statement nicht.
					// ist dann aber egal
				}
				try
				{
					// TODO noch nicht getestet. Laut doku geht es mit Triggername. Auch mit all?
					DB.getStatement(context).execute("alter table " + tableName + " disable trigger all");
				} catch (Exception e)
				{

				}
			} else if (SQL.isOracle(context))
			{

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Actions to be done after inserting data-dump
	 * 
	 * @param tableName
	 *          full qualified name (scheme.tabelName)
	 * @param context
	 */
	private void afterInsertTable(String tableName, DBContext context)
	{
		try
		{
			if (SQL.isPostgres(context))
			{
				// Ziel-Tabelle Trigger an
				DB.getStatement(context).execute("alter table " + tableName + " enable trigger all");
			} else if (SQL.isMSSQL(context))
			{
				try
				{
					DB.getStatement(context).execute("SET IDENTITY_INSERT " + tableName + " OFF");
				} catch (Exception e)
				{
					// nicht alle Tabellen haben einen IDENTITY_INSERT. Dann klappt das Statement nicht.
					// ist dann aber egal
				}
				try
				{
					// TODO noch nicht getestet. Laut doku geht es mit Triggername. Auch mit all?
					DB.getStatement(context).execute("alter table " + tableName + " enable trigger all");
				} catch (Exception e)
				{

				}
			} else if (SQL.isOracle(context))
			{

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
