package de.tarent.dblayer;

/*-
 * VerA.web-Middleware, newly MIT licenced, is comprised of:
 * tarent-commons, a set of common components and solutions
 * tarent-contact, platform-independent webservice-based contact management
 * tarent-database, jdbc database library
 * tarent-doctor, Document Generation Platform
 * tarent-octopus, Webservice Data Integrator and Application Server
 *  © 2005, 2006, 2007 asteban <s.mancke@tarent.de>
 *  © 2007 David Goemans <d.goemans@tarent.de>
 *  © 2006, 2007, 2010 Hendrik Helwich <h.helwich@tarent.de>
 *  © 2005, 2006, 2007 Christoph Jerolimov <c.jerolimov@tarent.de>
 *  © 2006 Philipp Kirchner <p.kirchner@tarent.de>
 *  © 2010 Carsten Klein <c.klein@tarent.de>
 *  © 2006 Michael Kleinhenz <m.kleinhenz@tarent.de>
 *  © 2006 Michael Klink <m.klink@tarent.de>
 *  © 2007 Fabian Köster <f.koester@tarent.de>
 *  © 2006 Martin Ley <m.ley@tarent.de>
 *  © 2007 Alex Maier <a.maier@tarent.de>
 *  © 2007, 2015, 2017, 2018 mirabilos <t.glaser@tarent.de>
 *  © 2006, 2007 Jens Neumaier <j.neumaier@tarent.de>
 *  © 2006 Nils Neumaier <n.neumaier@tarent.de>
 *  © 2007, 2008 Martin Pelzer <m.pelzer@tarent.de>
 *  © 2008, 2009 Christian Preilowski <c.thiel@tarent.de>
 *  © 2006, 2008, 2009 Thomas Schmitz <t.schmitz@tarent.de>
 *  © 2007 Robert Schuster <r.schuster@tarent.de>
 * and older code, Copyright © 2001–2007 ⮡ tarent GmbH and contributors.
 * Licensor is tarent solutions GmbH, http://www.tarent.de/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
