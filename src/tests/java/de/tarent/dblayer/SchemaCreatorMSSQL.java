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

package de.tarent.dblayer;

import java.sql.SQLException;
import java.sql.Statement;

import de.tarent.dblayer.engine.DB;



/**
 * Creates the initial Schema for the tests.
 */
public class SchemaCreatorMSSQL extends SchemaCreator {

    protected void dropSchema() throws SQLException {
    	Statement st = DB.getStatement(TEST_POOL);
    	try{
	    	try{
	    		st.execute("IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[person]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)\n"
    					+"DROP TABLE [dbo].[person]");
	    		st.execute("IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[firma]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)\n"
	    				+"DROP TABLE [dbo].[firma]");
	    		st.execute("IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[dbo].[insert_test]') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)\n"
	    					+"DROP TABLE [dbo].[insert_test]");
	    		st.execute("if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[unit_test]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)\n"
	    					+"drop procedure [dbo].[unit_test]");
	    		st.execute("if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[unit_test2]') and OBJECTPROPERTY(id, N'IsProcedure') = 1)\n"
    					+"drop procedure [dbo].[unit_test2]");
	    	}catch(SQLException e){
	    		logger.error(e.getMessage(), e);
	    	}
    	}finally{
    		DB.close(st.getConnection());
    	}
    }

    protected void createSchema() throws SQLException {
    	Statement st = DB.getStatement(TEST_POOL);
    	try{
	    	try{
	    		st.execute("CREATE TABLE firma ("
	                    +" pk_firma int identity(1,1) PRIMARY KEY,"
	                    +" name varchar(50),"
	                    +" umsatz integer"
	                    +")");
	          
	            st.execute("CREATE TABLE person ("
	                    +" pk_person int identity(1,1) PRIMARY KEY,"
	                    +" fk_firma integer,"
	                    +" vorname varchar(50),"
	                    +" nachname varchar(50),"
	                    +" geburtstag datetime"
	                    +")");        

	    		st.execute("CREATE TABLE [dbo].[insert_test]" +
	    						"([pk] [int] IDENTITY(1,1) NOT NULL," +
	    						"[daten] [varchar](50) COLLATE Latin1_General_CI_AS NULL," +
	    						"CONSTRAINT [PK_insert_test] PRIMARY KEY CLUSTERED(" +
	    							"[pk] ASC) ON [PRIMARY])");
	    		st.execute("CREATE PROCEDURE [dbo].[unit_test]" + 
	    					"@param1 varchar(500) AS "+
	    					"SELECT @param1 AS param1; " +
	    					"RETURN ");
	    		st.execute("CREATE PROCEDURE [dbo].[unit_test2]" + 
    					" AS "+
    					"RETURN ");
	    	}catch(SQLException e){
	    		logger.error(e.getMessage(), e);
	    	}
    	}finally{
    		DB.close(st.getConnection());
    	}
    }
    
    protected void doInserts() throws SQLException {
    	Statement st = DB.getStatement(TEST_POOL);
    	try{
    		st.execute("INSERT INTO firma (name, umsatz) VALUES('Dagoberts Geldspeicher', 100000)");
    		st.execute("INSERT INTO firma (name, umsatz) VALUES('Donalds Frittenbute', 30)");
    		st.execute("INSERT INTO firma (name, umsatz) VALUES('Duesentriebs Wertkstatt', 3000)");
    		st.execute("INSERT INTO person (fk_firma, vorname, nachname, geburtstag) " +
    				"VALUES(1, 'Dagobert', 'Duck', convert(datetime, '1980.03.31', 120))");
    		st.execute("INSERT INTO person (fk_firma, vorname, nachname, geburtstag) " +
    			"VALUES(1, 'Daisy', 'Duck', convert(datetime, '1980.03.11', 120))");
    		st.execute("INSERT INTO person (fk_firma, vorname, nachname, geburtstag) " +
				"VALUES(2, 'Donald', 'Duck', convert(datetime, '1980.01.28', 120))");
	st.execute("INSERT INTO person (vorname, nachname, geburtstag) " +
    				"VALUES('Gustav', 'Gans', convert(datetime, '1980.11.26', 120))");
    	}finally{
    		DB.close(st.getConnection());
    	}


    }
}

