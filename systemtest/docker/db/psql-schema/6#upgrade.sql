--
-- Veranstaltungsmanagement VerA.web (platform-independent
-- webservice-based event management) is Copyright
--  © 2014, 2015, 2016, 2017 Атанас Александров (a.alexandrov@tarent.de)
--  © 2018 Атанас Александров (sirakov@gmail.com)
--  © 2013 Иванка Александрова (i.alexandrova@tarent.de)
--  © 2013 Patrick Apel (p.apel@tarent.de)
--  © 2016 Eugen Auschew (e.auschew@tarent.de)
--  © 2013 Andrei Boulgakov (a.boulgakov@tarent.de)
--  © 2013 Valentin But (v.but@tarent.de)
--  © 2016 Lukas Degener (l.degener@tarent.de)
--  © 2017 Axel Dirla (a.dirla@tarent.de)
--  © 2015 Julian Drawe (j.drawe@tarent.de)
--  © 2014 Dominik George (d.george@tarent.de)
--  © 2013 Sascha Girrulat (s.girrulat@tarent.de)
--  © 2008 David Goemans (d.goemans@tarent.de)
--  © 2018 Christian Gorski (c.gorski@tarent.de)
--  © 2015 Viktor Hamm (v.hamm@tarent.de)
--  © 2013 Katja Hapke (k.hapke@tarent.de)
--  © 2013 Hendrik Helwich (h.helwich@tarent.de)
--  © 2018 Thomas Hensel (t.hensel@tarent.de)
--  © 2018 Benedict Hoeger (b.hoeger@tarent.de)
--  © 2018 Titian Horvath (t.horvath@tarent.de)
--  © 2005, 2006, 2007, 2008 Christoph Jerolimov (jerolimov@gmx.de)
--  © 2018 Timo Kanera (t.kanera@tarent.de)
--  © 2008, 2009, 2010 Carsten Klein (c.klein@tarent.de)
--  © 2014 Martin Ley (m.ley@tarent.de)
--  © 2014, 2015 Max Marche (m.marche@tarent.de)
--  © 2007 Jan Meyer (jan@evolvis.org)
--  © 2013, 2014, 2015, 2016, 2017, 2018 mirabilos (t.glaser@tarent.de)
--  © 2016 Cristian Molina (c.molina@tarent.de)
--  © 2018 Yorka Neumann (y.neumann@tarent.de)
--  © 2017 Michael Nienhaus (m.nienhaus@tarent.de)
--  © 2013 Claudia Nuessle (c.nuessle@tarent.de)
--  © 2014, 2015 Jon Nuñez Alvarez (j.nunez-alvarez@tarent.de)
--  © 2016 Jens Oberender (j.oberender@tarent.de)
--  © 2016, 2017 Miluška Pech (m.pech@tarent.de)
--  © 2009 Martin Pelzer (m.pelzer@tarent.de)
--  © 2013 Marc Radel (m.radel@tarent.de)
--  © 2013 Sebastian Reimers (s.reimers@tarent.de)
--  © 2015 Charbel Saliba (c.saliba@tarent.de)
--  © 2008, 2009, 2010 Thomas Schmitz (t.schmitz@tarent.de)
--  © 2013 Volker Schmitz (v.schmitz@tarent.de)
--  © 2014 Sven Schumann (s.schumann@tarent.de)
--  © 2014 Sevilay Temiz (s.temiz@tarent.de)
--  © 2013 Kevin Viola Schmitz (k.schmitz@tarent.de)
--  © 2015 Stefan Walenda (s.walenda@tarent.de)
--  © 2015, 2016, 2017 Max Weierstall (m.weierstall@tarent.de)
--  © 2013 Rebecca Weinz (r.weinz@tarent.de)
--  © 2015, 2016 Stefan Weiz (s.weiz@tarent.de)
--  © 2015, 2016 Tim Zimmer (t.zimmer@tarent.de)
-- and older code, Copyright © 2004–2008 ⮡ tarent GmbH and contributors.
-- Licensor is tarent solutions GmbH, http://www.tarent.de/
--
-- This program is free software; you can redistribute it and/or modify
-- it under the terms of the GNU General Public License as published by
-- the Free Software Foundation; either version 2 of the License, or
-- (at your option) any later version.
--
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Public License for more details.
--
-- You should have received a copy of the GNU General Public License along
-- with this program; if not, see: http://www.gnu.org/licenses/
--

-- Schema-Upgrade VerA.web Datenbank

-- Entwicklernotiz: bei Hinzufügen einer neuen Version muß an drei
-- Stellen was geändert werden:
-- ① vversion in Zeile 92
-- ② recht nah am Ende der Datei (vor „-- end“)
-- ③ in ../src/main/resources/de/tarent/aa/veraweb/veraweb.properties

DROP FUNCTION IF EXISTS serv_vwdbupgrade();
CREATE OR REPLACE FUNCTION veraweb.serv_vwdbupgrade() RETURNS VARCHAR AS $$

DECLARE
	vmsg VARCHAR;
	vdate VARCHAR;
	vversion VARCHAR;
	vcurvsn VARCHAR;
	vnewvsn VARCHAR;
	vint INT4;
	psqlvsn INTEGER;

BEGIN

	-- set this to the current DB schema version (date)
	vversion := '2017-01-19';

	-- initialisation
	vint := 0;
	SELECT CURRENT_TIMESTAMP INTO vdate;
	SELECT current_setting('server_version_num') INTO psqlvsn;
	SELECT COUNT(*) INTO vint FROM veraweb.tconfig WHERE cname = 'SCHEMA_VERSION';
	IF vint = 0 THEN
		RAISE EXCEPTION 'Not a VerA.web database'
		    USING HINT = 'veraweb.tconfig SCHEMA_VERSION does not exist';
		RETURN 'error';
	ELSEIF vint <> 1 THEN
		RAISE EXCEPTION 'Not a VerA.web database'
		    USING HINT = 'veraweb.tconfig SCHEMA_VERSION is not unique';
		RETURN 'error';
	END IF;

	SELECT cvalue INTO vcurvsn FROM veraweb.tconfig WHERE cname = 'SCHEMA_VERSION';
	vmsg := 'begin.SCHEMA UPDATE FROM VERSION ' || vcurvsn || ' TO VERSION ' || vversion;
	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	IF vcurvsn > vversion THEN
		RAISE EXCEPTION 'VerA.web current database version (%) newer than target version (%)',
		    vcurvsn, vversion
		    USING HINT = 'veraweb.tconfig SCHEMA_VERSION';
		RETURN 'error';
	ELSEIF vcurvsn < '2013-02-21' THEN
		vmsg := 'end.SCHEMA UPDATE (DB schema too old)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		RAISE WARNING 'VerA.web current database version (%) too old', vcurvsn
		    USING HINT = 'Must be at least VerA.web 1.3.15 (2013-02-21)';
		RETURN 'error (DB too old)';
	ELSEIF vcurvsn = vversion THEN
		vmsg := 'end.SCHEMA UPDATE (nothing to do)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		RETURN 'ok ' || vversion || ' (nothing to do)';
	END IF;

	-- 1.4
	vnewvsn := '2013-06-12';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.4)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Add table "ttask"
		CREATE TABLE veraweb.ttask (
			pk SERIAL,
			title VARCHAR(100) NOT NULL,
			description VARCHAR(1000),
			startdate TIMESTAMP WITH TIME ZONE,
			enddate TIMESTAMP WITH TIME ZONE,
			degree_of_completion INTEGER DEFAULT 0,
			priority INTEGER,
			fk_event INTEGER NOT NULL REFERENCES veraweb.tevent (pk),
			fk_person INTEGER REFERENCES veraweb.tperson (pk),
			createdby VARCHAR(50),
			changedby VARCHAR(50),
			created TIMESTAMP WITH TIME ZONE,
			changed TIMESTAMP WITH TIME ZONE,
			CONSTRAINT ttask_pkey PRIMARY KEY (pk)
		) WITH OIDS;

		-- Alter table "tlocation"
		ALTER TABLE veraweb.tlocation ADD COLUMN
			contactperson VARCHAR(250),
			ADD COLUMN address VARCHAR(250),
			ADD COLUMN zip VARCHAR(50),
			ADD COLUMN location VARCHAR(100),
			ADD COLUMN callnumber VARCHAR(50),
			ADD COLUMN faxnumber VARCHAR(50),
			ADD COLUMN email VARCHAR(100),
			ADD COLUMN comment VARCHAR(1000),
			ADD COLUMN url VARCHAR(250),
			ADD COLUMN gpsdata VARCHAR(1000),
			ADD COLUMN roomnumber VARCHAR(250);

		-- Alter table "tevent"
		ALTER TABLE veraweb.tevent DROP COLUMN location;
		ALTER TABLE veraweb.tevent ADD COLUMN fk_location INTEGER REFERENCES veraweb.tlocation(pk);

		-- post-upgrade 1.4
		vmsg := 'end.update(1.4)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.0.1
	vnewvsn := '2014-11-17';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.0.1)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Alter table "tevent"
		alter table veraweb.tevent add eventtype varchar(100);
		alter table veraweb.tevent add column mediarepresentatives varchar(100);

		-- Alter table "tguest"
		alter table veraweb.tguest add column delegation varchar(255);
		alter table veraweb.tguest add column osiam_login varchar(255);

		-- Trigger for link mandant with categories
		CREATE FUNCTION veraweb.linkOrgUnitWithCategorie()
			RETURNS TRIGGER AS $BODY$
			BEGIN
				INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NEW.PK, 'Pressevertreter', NULL, NULL);
				RETURN NEW;
			END;
			$BODY$ LANGUAGE plpgsql;

		CREATE TRIGGER createCategorieOnUnitInsert
			AFTER INSERT ON veraweb.torgunit
			FOR EACH ROW EXECUTE PROCEDURE veraweb.linkOrgUnitWithCategorie();

		-- Migrate old OrgUnits
		CREATE FUNCTION migrateOrgUnits() RETURNS integer AS $BODY$
			DECLARE
			    unLinked RECORD;
			BEGIN

			FOR unLinked IN SELECT pk FROM veraweb.torgunit WHERE pk NOT IN( SELECT fk_orgunit FROM veraweb.tcategorie ) LOOP

			execute format('INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, %s, %s, NULL, NULL);', unlinked, quote_literal(E'Pressevertreter'));

		    END LOOP;

		    RETURN 1;
		END;
		$BODY$ LANGUAGE plpgsql;

		PERFORM migrateOrgUnits();
		DROP FUNCTION migrateOrgUnits();

		-- Create new tables for the optional delegation fields
		CREATE TABLE veraweb.toptional_fields (
			pk serial NOT NULL,
			fk_event serial NOT NULL,
			label text,

			FOREIGN KEY (fk_event) REFERENCES veraweb.tevent(pk),
			PRIMARY KEY (pk)
		);

		CREATE TABLE veraweb.toptional_fields_delegation_content (
			fk_guest serial NOT NULL,
			fk_delegation_field serial NOT NULL,
			value text,

			FOREIGN KEY (fk_guest) REFERENCES veraweb.tguest(pk),
			FOREIGN KEY (fk_delegation_field) REFERENCES veraweb.toptional_fields(pk),
			PRIMARY KEY (fk_guest, fk_delegation_field)
		);

		-- post-upgrade 1.5.0.1
		vmsg := 'end.update(1.5.0.1)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.0.2
	vnewvsn := '2015-01-26';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.0.2)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Alter table "tevent"
		alter table veraweb.tevent add column hash varchar(100);

		-- post-upgrade 1.5.0.2
		vmsg := 'end.update(1.5.0.2)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.0.3
	vnewvsn := '2015-02-05';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.0.3)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Alter table "tperson"
		alter table veraweb.tperson add column username varchar(100);

		-- post-upgrade 1.5.0.3
		vmsg := 'end.update(1.5.0.3)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.0.4
	vnewvsn := '2015-02-24';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.0.4)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Alter table "tdoctype"
		ALTER TABLE veraweb.tdoctype ADD CONSTRAINT docname_unique UNIQUE(docname);

		-- post-upgrade 1.5.0.4
		vmsg := 'end.update(1.5.0.4)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.0
	vnewvsn := '2015-02-25';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.0)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Alter table "tdoctype"
		CREATE OR REPLACE FUNCTION veraweb.umlaut_fix(character varying) RETURNS character varying AS $BODY$
		    BEGIN
			IF
			($1 LIKE '%ä%') THEN RETURN replace($1,'ä','ae');
			END IF;
			IF
			($1 LIKE '%ö%') THEN RETURN replace($1,'ö','oe');
			END IF;
			IF
			($1 LIKE '%ü%') THEN RETURN replace($1,'ü','ue');
			END IF;
			IF
			($1 LIKE '%ß%') THEN RETURN replace($1,'ß','ss');
			END IF;
			RETURN $1;
		    END;
		$BODY$ LANGUAGE plpgsql;

		-- post-upgrade 1.5.1.0
		vmsg := 'end.update(1.5.1.0)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.2
	vnewvsn := '2015-03-12';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.2)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Alter table "tcategorie"
		ALTER TABLE veraweb.tcategorie ADD CONSTRAINT mandant_categ_unique UNIQUE(fk_orgunit,catname);

		-- post-upgrade 1.5.1.2
		vmsg := 'end.update(1.5.1.2)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.3
	vnewvsn := '2015-03-24';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.3)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Create sequence "link_uuid_pk_seq"
		CREATE SEQUENCE veraweb.link_uuid_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

		-- Create table "link_uuid"
		CREATE TABLE veraweb.link_uuid (
		    pk INTEGER DEFAULT nextval('veraweb.link_uuid_pk_seq') NOT NULL,
		    uuid character varying(100),
		    linktype character varying(100),
		    personid INTEGER DEFAULT 0,
		    CONSTRAINT link_uuid_pkey PRIMARY KEY (pk)
		) WITH OIDS;

		-- Add foreign key constraints
		ALTER TABLE veraweb.link_uuid ADD CONSTRAINT link_uuid_fkey_person
		    FOREIGN KEY (personid) REFERENCES veraweb.tperson (pk) ON DELETE RESTRICT ON UPDATE RESTRICT;

		-- post-upgrade 1.5.1.3
		vmsg := 'end.update(1.5.1.3)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.5.1
	vnewvsn := '2015-04-14';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.5.1)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Remove foreign key constraints (Hotfix)
		ALTER TABLE veraweb.tcategorie DROP CONSTRAINT mandant_categ_unique;

		-- post-upgrade 1.5.1.5.1
		vmsg := 'end.update(1.5.1.5.1)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.6
	vnewvsn := '2015-04-15';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.6)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Restore mandant_categ_unique constraint removed in 1.5.1.5.1
		ALTER TABLE veraweb.tcategorie ADD CONSTRAINT mandant_categ_unique UNIQUE(fk_orgunit,catname);

		-- post-upgrade 1.5.1.6
		vmsg := 'end.update(1.5.1.6)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.9
	vnewvsn := '2015-04-23';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.9)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Create table "tevent_function"
		CREATE TABLE veraweb.tevent_function (
		  pk serial NOT NULL,
		  fk_event int4 NOT NULL DEFAULT 0,
		  fk_function int4 NOT NULL DEFAULT 0,
		  CONSTRAINT tevent_function_pkey PRIMARY KEY (pk)
		) WITH OIDS;

		-- Create table "tevent_category"
		CREATE TABLE veraweb.tevent_category (
		  pk serial NOT NULL,
		  fk_event int4 NOT NULL DEFAULT 0,
		  fk_category int4 NOT NULL DEFAULT 0,
		  CONSTRAINT tevent_category_pkey PRIMARY KEY (pk)
		) WITH OIDS;

		-- post-upgrade 1.5.1.9
		vmsg := 'end.update(1.5.1.9)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.17
	vnewvsn := '2015-05-20';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.17)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Create new table for storing field types
		CREATE TABLE veraweb.toptional_field_type (
		  pk serial NOT NULL,
		  description text,

		  CONSTRAINT toptional_field_type_pk PRIMARY KEY (pk)
		) WITH OIDS;

		-- Inserting data for types
		INSERT INTO veraweb.toptional_field_type values (1, 'Eingabefeld');
		INSERT INTO veraweb.toptional_field_type values (2, 'Einfaches Auswahlfeld');
		INSERT INTO veraweb.toptional_field_type values (3, 'Mehrfaches Auswahlfeld');

		-- Modify toptional_fields to allow field types
		ALTER TABLE veraweb.toptional_fields ADD COLUMN fk_type INTEGER REFERENCES veraweb.toptional_field_type(pk);

		-- Create new table for storing field type's content
		CREATE SEQUENCE veraweb.toptional_field_type_content_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
		CREATE TABLE veraweb.toptional_field_type_content (
		  pk INTEGER DEFAULT nextval('veraweb.toptional_field_type_content_seq') NOT NULL,
		  fk_optional_field INTEGER NOT NULL REFERENCES veraweb.toptional_fields(pk) ON DELETE CASCADE,
		  content text NOT NULL,

		  CONSTRAINT toptional_field_type_content_pkey PRIMARY KEY (pk)
		);

		-- Update the orgunit for default categories
		UPDATE veraweb.tcategorie set fk_orgunit=-1 where fk_orgunit IS NULL;

		-- post-upgrade 1.5.1.17
		vmsg := 'end.update(1.5.1.17)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.21
	vnewvsn := '2015-06-02';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.21)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Dropping old Primary Key constraint
		ALTER TABLE veraweb.toptional_fields_delegation_content DROP CONSTRAINT toptional_fields_delegation_content_pkey;

		-- Create new sequence to allow unique PK in toptional_fields_delegation_content
		CREATE SEQUENCE veraweb.toptional_fields_delegation_content_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

		-- Including new PK column to work with unique values
		ALTER TABLE veraweb.toptional_fields_delegation_content ADD COLUMN pk INTEGER DEFAULT nextval('veraweb.toptional_fields_delegation_content_seq') NOT NULL;
		ALTER TABLE veraweb.toptional_fields_delegation_content ADD CONSTRAINT toptional_fields_delegation_content_pkey PRIMARY KEY (pk);

		-- post-upgrade 1.5.1.21
		vmsg := 'end.update(1.5.1.21)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.22
	vnewvsn := '2015-06-11';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.22)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- New flag to allow events without login
		ALTER TABLE veraweb.tevent ADD COLUMN login_required int4 DEFAULT 0;

		-- New UUID to identify created users to registration
		ALTER TABLE veraweb.tguest ADD COLUMN login_required_uuid CHARACTER VARYING(100);

		-- post-upgrade 1.5.1.22
		vmsg := 'end.update(1.5.1.22)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- 1.5.1.34
	vnewvsn := '2015-07-10';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(1.5.1.34)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- New flag to allow events without login
		ALTER TABLE veraweb.tevent ADD COLUMN maxreserve int4 DEFAULT 0;

		-- New column to identify Guest-Photo
		ALTER TABLE veraweb.tguest ADD COLUMN image_uuid character varying(100);

		-- post-upgrade 1.5.1.34
		vmsg := 'end.update(1.5.1.34)';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- anything after the old SQL files

	vnewvsn := '2015-10-07';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- New column to identify Guest-Partner-Photo
		ALTER TABLE veraweb.tguest ADD COLUMN image_uuid_p character varying(100);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2015-11-04';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- add a constraint which should have long existed
		UPDATE veraweb.tuser SET fk_orgunit=NULL
		    WHERE NOT EXISTS (SELECT pk FROM veraweb.torgunit WHERE pk=fk_orgunit);
		ALTER TABLE veraweb.tuser ADD CONSTRAINT tuser_fkey_orgunit
		    FOREIGN KEY (fk_orgunit) REFERENCES veraweb.torgunit(pk)
		    MATCH FULL ON DELETE RESTRICT ON UPDATE RESTRICT;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-01-19';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Create table "tosiam_user_activation"
		CREATE TABLE veraweb.tosiam_user_activation (
		    username character varying(100) NOT NULL,
		    expiration_date date NOT NULL,
		    activation_token character varying(100) NOT NULL,

		    CONSTRAINT tosiam_user_activation_pk PRIMARY KEY (activation_token)
		);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-03-02';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Create table "tmedia_representative_activation"
		CREATE TABLE veraweb.tmedia_representative_activation (
		    email character varying(100) NOT NULL,
		    fk_event  serial NOT NULL,
		    activation_token character varying(100) NOT NULL,

		    CONSTRAINT tmedia_representative_activation_pk PRIMARY KEY (email, fk_event)
		);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-03-08';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Drop table "tmedia_representative_activation"
		DROP TABLE veraweb.tmedia_representative_activation;

		-- Create table "tmedia_representative_activation"
		CREATE TABLE veraweb.tmedia_representative_activation (
		    email character varying(100) NOT NULL,
		    fk_event  serial NOT NULL,
		    activation_token character varying(100) NOT NULL,
		    gender character varying(10) NOT NULL,
		    address character varying(100) NOT NULL,
		    city character varying(100) NOT NULL,
		    country character varying(100) NOT NULL,
		    firstname character varying(100) NOT NULL,
		    lastname character varying(100) NOT NULL,
		    zip serial NOT NULL,

		    CONSTRAINT tmedia_representative_activation_pk PRIMARY KEY (email, fk_event)
		);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-03-14';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Add column to table "tmedia_representative_activation"
		ALTER TABLE veraweb.tmedia_representative_activation ADD COLUMN ACTIVATED int4 DEFAULT 0;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-03-16';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- Add column for keywords
		ALTER TABLE veraweb.tguest ADD COLUMN keywords character varying(1000);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-05-06';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';

		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		DROP TABLE veraweb.tdoctype;
		DROP TABLE veraweb.tevent_doctype;
		DROP TABLE veraweb.tguest_doctype;
		DROP TABLE veraweb.timportperson_doctype;
		DROP TABLE veraweb.tperson_doctype;
		DROP TABLE veraweb.tsalutation_doctype;

		CREATE TABLE veraweb.birthday_bak as (
			select pk, birthday_a_e1, birthday_b_e1 from veraweb.tperson
		);
		ALTER TABLE veraweb.tperson ALTER COLUMN birthday_a_e1 TYPE date;
		ALTER TABLE veraweb.tperson ALTER COLUMN birthday_b_e1 TYPE date;

		CREATE OR REPLACE VIEW veraweb.TPERSON_NORMALIZED AS (
			select tperson.*,
			    veraweb.umlaut_fix(firstname_a_e1) as firstname_normalized,
			    veraweb.umlaut_fix(lastname_a_e1) as lastname_normalized
			from veraweb.tperson
		);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-07-08';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';

		CREATE SEQUENCE veraweb.pdftemplate_seq
			INCREMENT 1 MINVALUE 1
			MAXVALUE 9223372036854775807
			START 1 CACHE 1;

		CREATE TABLE veraweb.pdftemplate (
		    pk INTEGER DEFAULT nextval('veraweb.pdftemplate_seq') NOT NULL,
		    name varchar(200) NOT NULL,
		    content bytea NOT NULL,
		    fk_orgunit int4 NOT NULL,
		    createdby varchar(50),
		    created timestamptz,
		    changedby varchar(50),
		    changed timestamptz
		) WITH OIDS;

		DROP VIEW IF EXISTS veraweb.TPERSON_NORMALIZED;

		ALTER TABLE veraweb.tperson ALTER COLUMN city_a_e1 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_b_e1 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_c_e1 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_a_e2 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_b_e2 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_c_e2 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_a_e3 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_b_e3 TYPE VARCHAR(300);
		ALTER TABLE veraweb.tperson ALTER COLUMN city_c_e3 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_a_e1 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_b_e1 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_c_e1 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_a_e2 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_b_e2 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_c_e2 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_a_e3 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_b_e3 TYPE VARCHAR(300);
		ALTER TABLE veraweb.timportperson ALTER COLUMN city_c_e3 TYPE VARCHAR(300);

		CREATE OR REPLACE VIEW veraweb.TPERSON_NORMALIZED AS (select tperson.*, veraweb.umlaut_fix(firstname_a_e1) as firstname_normalized, veraweb.umlaut_fix(lastname_a_e1) as lastname_normalized from veraweb.tperson);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-08-16';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';

		DROP TABLE veraweb.tevent_category;
		DROP TABLE veraweb.tevent_function;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-10-27';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';

		ALTER TABLE veraweb.pdftemplate ADD constraint pdftemplate_pkey PRIMARY KEY (pk);

		CREATE SEQUENCE veraweb.salutation_alternative_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

		CREATE TABLE veraweb.salutation_alternative (
		    pk INTEGER DEFAULT nextval('veraweb.salutation_alternative_seq') NOT NULL,
		    pdftemplate_id INTEGER NOT NULL REFERENCES veraweb.pdftemplate(pk) ON DELETE CASCADE,
		    salutation_id INTEGER NOT NULL REFERENCES veraweb.tsalutation(pk) ON DELETE CASCADE,
		    content varchar(100) NOT NULL,

		    CONSTRAINT salutation_alternative_pkey PRIMARY KEY (pk),
		    CONSTRAINT salutation_alternative_unique UNIQUE (pdftemplate_id, salutation_id)
		);
		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-12-05';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';

		ALTER TABLE veraweb.salutation_alternative
			ALTER COLUMN content
			TYPE VARCHAR(100);

		DROP VIEW IF EXISTS veraweb.TPERSON_NORMALIZED;

		ALTER TABLE veraweb.tperson ALTER COLUMN salutation_a_e1 TYPE varchar(100);
		ALTER TABLE veraweb.tperson ALTER COLUMN salutation_a_e2 TYPE varchar(100);
		ALTER TABLE veraweb.tperson ALTER COLUMN salutation_a_e3 TYPE varchar(100);
		ALTER TABLE veraweb.tperson ALTER COLUMN salutation_b_e1 TYPE varchar(100);
		ALTER TABLE veraweb.tperson ALTER COLUMN salutation_b_e2 TYPE varchar(100);
		ALTER TABLE veraweb.tperson ALTER COLUMN salutation_b_e3 TYPE varchar(100);

		ALTER TABLE veraweb.timportperson ALTER COLUMN salutation_a_e1 TYPE varchar(100);
		ALTER TABLE veraweb.timportperson ALTER COLUMN salutation_a_e2 TYPE varchar(100);
		ALTER TABLE veraweb.timportperson ALTER COLUMN salutation_a_e3 TYPE varchar(100);
		ALTER TABLE veraweb.timportperson ALTER COLUMN salutation_b_e1 TYPE varchar(100);
		ALTER TABLE veraweb.timportperson ALTER COLUMN salutation_b_e2 TYPE varchar(100);
		ALTER TABLE veraweb.timportperson ALTER COLUMN salutation_b_e3 TYPE varchar(100);

		CREATE OR REPLACE VIEW veraweb.TPERSON_NORMALIZED AS (
			select tperson.*,
			    veraweb.umlaut_fix(firstname_a_e1) as firstname_normalized,
			    veraweb.umlaut_fix(lastname_a_e1) as lastname_normalized
			from veraweb.tperson
		);

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2016-12-19';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		ALTER TABLE veraweb.tperson ADD COLUMN internal_id VARCHAR(45);
		ALTER TABLE veraweb.timportperson ADD COLUMN internal_id VARCHAR(45);

		DROP VIEW IF EXISTS veraweb.aggregated_field_content;
		CREATE OR REPLACE VIEW veraweb.aggregated_field_content as (
		    select c.fk_guest, c.fk_delegation_field, ';'::text as value
		      from veraweb.toptional_fields_delegation_content c
		      group by c.fk_guest,  c.fk_delegation_field
		);

		ALTER TABLE veraweb.toptional_fields_delegation_content
		DROP CONSTRAINT toptional_fields_delegation_content_fk_guest_fkey,
		ADD CONSTRAINT toptional_fields_delegation_content_fk_guest_fkey
		    FOREIGN KEY (fk_guest)
		    REFERENCES veraweb.tguest(pk)
		    ON DELETE CASCADE;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2017-01-12';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		ALTER TABLE veraweb.tguest DROP fk_color CASCADE;
		ALTER TABLE veraweb.tguest DROP fk_color_p CASCADE;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2017-01-16';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		ALTER TABLE veraweb.tmaildraft ADD COLUMN fk_orgunit int4 NOT NULL DEFAULT -1;
		ALTER TABLE veraweb.tmaildraft ALTER COLUMN fk_orgunit DROP DEFAULT;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	vnewvsn := '2017-01-19';
	IF vcurvsn < vnewvsn THEN
		vmsg := 'begin.update(' || vnewvsn || ')';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

		-- fixup the view so it works with PostgreSQL 8.4
		DROP VIEW IF EXISTS veraweb.aggregated_field_content;
		IF (psqlvsn < 90000) THEN
			CREATE OR REPLACE VIEW veraweb.aggregated_field_content AS (
				SELECT c.fk_guest, c.fk_delegation_field,
				    array_to_string(array_agg(c.value), ';') AS value
				FROM veraweb.toptional_fields_delegation_content c
				GROUP BY c.fk_guest, c.fk_delegation_field
			);
		ELSE
			CREATE OR REPLACE VIEW veraweb.aggregated_field_content AS (
				SELECT c.fk_guest, c.fk_delegation_field,
				    string_agg(c.value, ';') AS value
				FROM veraweb.toptional_fields_delegation_content c
				GROUP BY c.fk_guest, c.fk_delegation_field
			);
		END IF;

		-- post-upgrade
		vmsg := 'end.update(' || vnewvsn || ')';
		UPDATE veraweb.tconfig SET cvalue = vnewvsn WHERE cname = 'SCHEMA_VERSION';
		vcurvsn := vnewvsn;
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	-- end

	IF vcurvsn <> vversion THEN
		RAISE WARNING 'Database version after upgrade (%) does not match target (%)',
		    vcurvsn, vversion
		    USING HINT = 'vcurvsn in last if block vs. vversion at begin of upgrade.sql';
	END IF;
	vmsg := 'end.SCHEMA UPDATE TO VERSION ' || vcurvsn;
	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	IF vcurvsn <> vversion THEN
		RETURN 'error (schema version mismatch in upgrade.sql)';
	END IF;

	RETURN 'ok ' || vcurvsn || ' (success)';
END;
$$ LANGUAGE 'plpgsql' VOLATILE;

SELECT veraweb.serv_vwdbupgrade() AS "Status Datenbank-Upgrade";
