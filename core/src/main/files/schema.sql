--
-- Update Script fuer das VerA.web - Schema.
--
-- Kann auf eine beliebige Version des Schemas
-- oder eine leere Datenbank angewendet werden.
--
-- Die Datenbank innerhalb der dieses Script ausgefuehrt wird,
-- muss dem Benutzer entsprechen der es ausfuehrt.
--
-- Usage: SELECT serv_verawebschema(0); -- log changes only
-- Usage: SELECT serv_verawebschema(1); -- create or update schema
--

CREATE OR REPLACE FUNCTION serv_verawebschema(int4) RETURNS varchar AS
$serv_verawebschema$

DECLARE
	vmsg varchar;
	vdate varchar;
	vversion varchar;
	vint int4;
	vrecno int4;
	vstatus int4;

BEGIN
	--##### please set vversion to current date
	vversion := '2013-02-21';
	--#####

	vrecno := 0;
	vstatus := 0;
	vmsg := '';

	---------------------------<INIT>
	vint := 0;

	-- SCHEMA ANLEGEN
	SELECT count(*) INTO vint FROM information_schema.schemata
		WHERE schema_name = 'veraweb';
	IF vint = 0 THEN
		vstatus := 1;
		CREATE SCHEMA veraweb
		  AUTHORIZATION veraweb;
		GRANT ALL ON SCHEMA veraweb TO veraweb WITH GRANT OPTION;
		COMMENT ON SCHEMA veraweb IS 'The veraweb namespace.

		veraweb, platform independent webservice-based event management (Veranstaltungsmanagment VerA.web), is Copyright © tarent solutions GmbH
		';
	END IF;

	-- TUPDATE ANLEGEN
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tupdate';
	IF vint = 0 THEN
		CREATE TABLE veraweb.tupdate
		(
		  id serial NOT NULL,
		  date varchar(50),
		  value varchar(1000),
		  CONSTRAINT tupdate_pkey PRIMARY KEY (id)
		) WITH OIDS;
		COMMENT ON TABLE veraweb.tupdate IS 'VerA.web: System-Tabelle';
	ELSE
		-- TUPDATE LEEREN
		DELETE FROM veraweb.tupdate;
	END IF;
	-- TRESULT ANLEGEN
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tresult';
	IF vint = 0 THEN
		CREATE TABLE veraweb.tresult
		(
		  id serial NOT NULL,
		  value varchar(1000),
		  CONSTRAINT tresult_pkey PRIMARY KEY (id)
		) WITH OIDS;
		COMMENT ON TABLE veraweb.tresult IS 'VerA.web: System-Tabelle';
	ELSE
		-- TRESULT LEEREN
		DELETE FROM veraweb.tresult;
	END IF;

	SELECT CURRENT_TIMESTAMP INTO vdate;

	IF $1 = 1 THEN
		vmsg := 'begin.SCHEMA UPDATE TO VERSION ' || vversion;
	ELSE
		vmsg := 'begin.SCHEMA CHECK TO VERSION (NO ACTION) ' || vversion;
	END IF;
	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

	IF vstatus = 1 THEN
		vmsg := 'begin.createschema.veraweb ';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		vmsg := 'end.createschema.veraweb ';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	SELECT max(id) INTO vrecno FROM veraweb.tupdate;

	---------------------------</INIT>

	---------------------------<PROCEDURES>

	vmsg := 'begin.createprocedure.all';
	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	IF $1 = 1 THEN

	---------------------------<PROCEDURE>
	CREATE OR REPLACE FUNCTION veraweb.serv_build_sequences() RETURNS int4 AS
	$serv_build_sequences$
	DECLARE
		vpos1 int4;
		vpos2 int4;
		vcount int4;
		vseq RECORD;
		vtable varchar;
		vpk varchar;
		vresult varchar;
		vmaxid int4;
		vstmt varchar;
	BEGIN
		vcount := 0;
		vpos2 := 0;
		vpos1 := 0;
		vresult := '';
		vpk := '';

		PERFORM SETVAL('veraweb.tresult_id_seq', (SELECT MAX(id) FROM veraweb.tresult));
		PERFORM SETVAL('veraweb.tupdate_id_seq', (SELECT MAX(id) FROM veraweb.tupdate));

		FOR vseq IN SELECT * FROM pg_catalog.pg_statio_user_sequences WHERE schemaname = 'veraweb' LOOP
			SELECT position('pk_'  in vseq.relname) into vpos1;
			SELECT position('_seq' in vseq.relname) into vpos2;
			IF vpos2 > vpos1 AND (vpos1 - 2) > 0 THEN
				SELECT substring(vseq.relname from 1 for (vpos1 - 2)) into vtable;
				SELECT substring(vseq.relname from vpos1 for (vpos2 - vpos1)) into vpk;
				vcount := vcount + 1;
				vstmt := 'SELECT SETVAL(''veraweb.' || vseq.relname || ''', ' || '(SELECT MAX(' || vpk || ') FROM veraweb.' || vtable || '));';
				--INSERT INTO colibri.tresult(value) VALUES (vstmt);
				EXECUTE vstmt;
			ELSE
				INSERT INTO veraweb.tresult(value) VALUES ('ERROR FINDING SEQUENCE NAME ' || vseq.relname);
		END IF;
	END LOOP;

	RETURN vcount;
END;
$serv_build_sequences$
  LANGUAGE 'plpgsql' VOLATILE;

	CREATE OR REPLACE FUNCTION veraweb.serv_transform_column(varchar, varchar, varchar) RETURNS int4 AS $serv_transform_column$
		DECLARE
		BEGIN
			EXECUTE 'ALTER TABLE ' || $1 || ' RENAME ' || $2 || ' TO temp';
			EXECUTE 'ALTER TABLE ' || $1 || ' ADD COLUMN ' || $2 || ' ' || $3;
			EXECUTE 'UPDATE ' || $1 || ' SET ' || $2 || ' = temp';
			EXECUTE 'ALTER TABLE ' || $1 || ' DROP COLUMN temp';
			RETURN 0;
		END;
	$serv_transform_column$
	LANGUAGE 'plpgsql' VOLATILE;

	CREATE OR REPLACE FUNCTION veraweb.upper_fix(varchar) RETURNS varchar AS $upper_fix$
		DECLARE
		BEGIN
			RETURN replace(replace(replace(upper($1), 'ä', 'Ä'), 'ö', 'Ö'), 'ü', 'Ü');
		END;
	$upper_fix$
	LANGUAGE 'plpgsql' IMMUTABLE;

	CREATE OR REPLACE FUNCTION veraweb.lower_fix(varchar) RETURNS varchar AS $lower_fix$
		DECLARE
		BEGIN
			RETURN replace(replace(replace(lower($1), 'Ä', 'ä'), 'Ö', 'ö'), 'Ü', 'ü');
		END;
	$lower_fix$
	LANGUAGE 'plpgsql' IMMUTABLE;
	---------------------------</PROCEDURE>

	------------------<TRIGGER>
	------------------</TRIGGER>

	END IF; ---- gehört zu IF $1 = 1 (für alle Procedures)

	vmsg := 'end.createprocedure.all';
	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

	---------------------------</PROCEDURES>

	---------------------------<TABLES>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tcategorie';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tcategorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tcategorie
			(
			  pk serial NOT NULL,
			  fk_event int4 DEFAULT 0,
			  fk_orgunit int4 DEFAULT 0,
			  catname varchar(200) NOT NULL,
			  flags int4 DEFAULT 0,
			  rank int4 DEFAULT 0,
			  CONSTRAINT tcategorie_pkey PRIMARY KEY (pk)
			)
			WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tcategorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tcolor';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tcolor';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tcolor
			(
			  pk serial NOT NULL,
			  color varchar(100) NOT NULL,
			  addresstype int4 NOT NULL DEFAULT 0,
			  locale int4 NOT NULL DEFAULT 0,
			  rgb int4 NOT NULL,
			  CONSTRAINT tcolor_pkey PRIMARY KEY (pk)
			)
			WITH OIDS;

		END IF;
		vmsg := 'end.createTABLE.tcolor';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tconfig';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tconfig';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tconfig
			(
			  pk serial NOT NULL,
			  cname varchar(100) NOT NULL,
			  cvalue varchar(300) NOT NULL,
			  CONSTRAINT tconfig_pkey PRIMARY KEY (pk)
			)
			WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tconfig';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	-- Datenbankbereinigen abhaengig von der Version
	-- <= 2005-08-12 -- fix: Addresstype Nummerierung (1=G, 2=P, 3=W)
	SELECT count(*) INTO vint FROM veraweb.tconfig WHERE cname = 'SCHEMA_VERSION' AND cvalue <= '2005-08-12';
	IF vint > 0 THEN
		vmsg := 'begin.update.addresstypes in tdoctype, tperson_doctype and tguest_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			UPDATE veraweb.tdoctype SET addresstype = 101 WHERE addresstype = 2;
			UPDATE veraweb.tdoctype SET addresstype = 102 WHERE addresstype = 1;
			UPDATE veraweb.tdoctype SET addresstype = 103 WHERE addresstype = 3;
			UPDATE veraweb.tdoctype SET addresstype = 1 WHERE addresstype = 101;
			UPDATE veraweb.tdoctype SET addresstype = 2 WHERE addresstype = 102;
			UPDATE veraweb.tdoctype SET addresstype = 3 WHERE addresstype = 103;
			UPDATE veraweb.tperson_doctype SET addresstype = 101 WHERE addresstype = 2;
			UPDATE veraweb.tperson_doctype SET addresstype = 102 WHERE addresstype = 1;
			UPDATE veraweb.tperson_doctype SET addresstype = 103 WHERE addresstype = 3;
			UPDATE veraweb.tperson_doctype SET addresstype = 1 WHERE addresstype = 101;
			UPDATE veraweb.tperson_doctype SET addresstype = 2 WHERE addresstype = 102;
			UPDATE veraweb.tperson_doctype SET addresstype = 3 WHERE addresstype = 103;
			UPDATE veraweb.tguest_doctype SET addresstype = 101 WHERE addresstype = 2;
			UPDATE veraweb.tguest_doctype SET addresstype = 102 WHERE addresstype = 1;
			UPDATE veraweb.tguest_doctype SET addresstype = 103 WHERE addresstype = 3;
			UPDATE veraweb.tguest_doctype SET addresstype = 1 WHERE addresstype = 101;
			UPDATE veraweb.tguest_doctype SET addresstype = 2 WHERE addresstype = 102;
			UPDATE veraweb.tguest_doctype SET addresstype = 3 WHERE addresstype = 103;
		END IF;
		vmsg := 'end.update.addresstypes in tdoctype, tperson_doctype and tguest_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables WHERE
		table_schema = 'veraweb' AND table_name = 'tcontact';
	IF vint = 1 THEN
		vmsg := 'begin.combineTABLE.tcontact & tperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			-- Spalten-Breiten von tcontact an tperson anpassen
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'ssalutation', 'varchar(50)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'stitle', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sfirstname', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'slastname', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'slanguages', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'snationality', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sstreetprivate', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'szippostalcodeprivate', 'varchar(50)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scityprivate', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scountryprivate', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sphoneprivate', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sfaxprivate', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'semailprivate', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'surlprivate', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sfunction', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scompany', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sstreet', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'szippostalcode', 'varchar(50)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scity', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scountry', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sphone', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sfax', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'smobilephone', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'semail', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'surl', 'varchar(250)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'sstreetdelivery', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'szippostalcodedelivery', 'varchar(50)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scitydelivery', 'varchar(100)');
			PERFORM veraweb.serv_transform_column('veraweb.tcontact', 'scountrydelivery', 'varchar(100)');

			-- Neue Tabelle tt2 erstellen.
			CREATE TABLE veraweb.tt2 AS SELECT
			  tcontact.iid::int4 AS pk,
			  tperson.fk_orgunit AS fk_orgunit,
			  tperson.created AS created,
			  tperson.createdby AS createdby,
			  tperson.changed AS changed,
			  tperson.changedby AS changedby,
			  'f'::character varying(1) AS deleted,
			  tperson.dateexpire AS dateexpire,
			  iscompany::character varying(1) AS iscompany,
			  tperson.importsource AS importsource,

			  -- Hauptperson, Latein
			  tcontact.ssalutation AS salutation_a_e1,
			  tperson.fk_salutation AS fk_salutation_a_e1,
			  tcontact.stitle AS title_a_e1,
			  tcontact.sfirstname AS firstname_a_e1,
			  tcontact.slastname AS lastname_a_e1,
			  tperson.domestic::varchar(1) AS domestic_a_e1,
			  tperson.gender::varchar(1) AS sex_a_e1,
			  tcontact.dbirthday::timestamptz AS birthday_a_e1,
			  NULL::varchar(100) as birthplace_a_e1,
			  tperson.diplodate AS diplodate_a_e1,
			  tcontact.slanguages AS languages_a_e1,
			  tcontact.snationality AS nationality_a_e1,
			  tcontact.sremarks AS note_a_e1,
			  tperson.noteorga AS noteorga_a_e1,
			  tperson.notehost AS notehost_a_e1,

			  -- Hauptperson, Zeichensatz 1
			  salutation_z1 AS salutation_a_e2,
			  NULL::int4 AS fk_salutation_a_e2,
			  NULL::varchar(100) as birthplace_a_e2,
			  titel_z1 AS title_a_e2,
			  firstname_z1 AS firstname_a_e2,
			  lastname_z1 AS lastname_a_e2,

			  -- Hauptperson, Zeichensatz 2
			  salutation_z2 AS salutation_a_e3,
			  NULL::int4 AS fk_salutation_a_e3,
			  NULL::varchar(100) as birthplace_a_e3,
			  titel_z2 AS title_a_e3,
			  firstname_z2 AS firstname_a_e3,
			  lastname_z2 AS lastname_a_e3,

			  -- Partner, Latein
			  salutation_p AS salutation_b_e1,
			  fk_salutation_p AS fk_salutation_b_e1,
			  titel_p AS title_b_e1,
			  firstname_p AS firstname_b_e1,
			  lastname_p AS lastname_b_e1,
			  domestic_p::character varying(1) AS domestic_b_e1,
			  gender_p::character varying(1) AS sex_b_e1,
			  birthday_p AS birthday_b_e1,
			  NULL::varchar(100) as birthplace_b_e1,
			  NULL::timestamptz AS diplodate_b_e1,
			  languages_p AS languages_b_e1,
			  nationality_p AS nationality_b_e1,
			  note_p AS note_b_e1,
			  noteorga_p AS noteorga_b_e1,
			  notehost_p AS notehost_b_e1,

			  -- Partner, Zeichensatz 1
			  salutation_p_z1 AS salutation_b_e2,
			  NULL::int4 AS fk_salutation_b_e2,
			  NULL::varchar(100) as birthplace_b_e2,
			  titel_p_z1 AS title_b_e2,
			  firstname_p_z1 AS firstname_b_e2,
			  lastname_p_z1 AS lastname_b_e2,

			  -- Partner, Zeichensatz 2
			  salutation_p_z2 AS salutation_b_e3,
			  NULL::int4 AS fk_salutation_b_e3,
			  NULL::varchar(100) as birthplace_b_e3,
			  titel_p_z2 AS title_b_e3,
			  firstname_p_z2 AS firstname_b_e3,
			  lastname_p_z2 AS lastname_b_e3,

			  -- Adressdaten Geschaeftlich, Latein
			  function_b AS function_a_e1,
			  company_b AS company_a_e1,
			  sstreetprivate AS street_a_e1,
			  szippostalcodeprivate AS zipcode_a_e1,
			  NULL::varchar(100) AS state_a_e1,
			  scityprivate AS city_a_e1,
			  scountryprivate AS country_a_e1,
			  pobox_b AS pobox_a_e1,
			  poboxzipcode_b AS poboxzipcode_a_e1,
			  suffix1_b AS suffix1_a_e1,
			  suffix2_b AS suffix2_a_e1,
			  sphoneprivate AS fon_a_e1,
			  sfaxprivate AS fax_a_e1,
			  mobil_b AS mobil_a_e1,
			  semailprivate AS mail_a_e1,
			  surlprivate AS url_a_e1,

			  -- Adressdaten Geschaeftlich, Zeichensatz 1
			  function_b_z1 AS function_a_e2,
			  company_b_z1 AS company_a_e2,
			  street_b_z1 AS street_a_e2,
			  zipcode_b_z1 AS zipcode_a_e2,
			  NULL::varchar(100) AS state_a_e2,
			  city_b_z1 AS city_a_e2,
			  country_b_z1 AS country_a_e2,
			  pobox_b_z1 AS pobox_a_e2,
			  poboxzipcode_b_z1 AS poboxzipcode_a_e2,
			  suffix1_b_z1 AS suffix1_a_e2,
			  suffix2_b_z1 AS suffix2_a_e2,
			  fon_b_z1 AS fon_a_e2,
			  fax_b_z1 AS fax_a_e2,
			  mobil_b_z1 AS mobil_a_e2,
			  mail_b_z1 AS mail_a_e2,
			  www_b_z1 AS url_a_e2,

			  -- Adressdaten Geschaeftlich, Zeichensatz 2
			  function_b_z2 AS function_a_e3,
			  company_b_z2 AS company_a_e3,
			  street_b_z2 AS street_a_e3,
			  zipcode_b_z2 AS zipcode_a_e3,
			  NULL::varchar(100) AS state_a_e3,
			  city_b_z2 AS city_a_e3,
			  country_b_z2 AS country_a_e3,
			  pobox_b_z2 AS pobox_a_e3,
			  poboxzipcode_b_z2 AS poboxzipcode_a_e3,
			  suffix1_b_z2 AS suffix1_a_e3,
			  suffix2_b_z2 AS suffix2_a_e3,
			  fon_b_z2 AS fon_a_e3,
			  fax_b_z2 AS fax_a_e3,
			  mobil_b_z2 AS mobil_a_e3,
			  mail_b_z2 AS mail_a_e3,
			  www_b_z2 AS url_a_e3,

			  -- Adressdaten Privat, Latein
			  sfunction AS function_b_e1,
			  scompany AS company_b_e1,
			  sstreet AS street_b_e1,
			  szippostalcode AS zipcode_b_e1,
			  NULL::varchar(100) AS state_b_e1,
			  scity AS city_b_e1,
			  scountry AS country_b_e1,
			  pobox_a AS pobox_b_e1,
			  poboxzipcode_a AS poboxzipcode_b_e1,
			  suffix1_a AS suffix1_b_e1,
			  suffix1_b AS suffix2_b_e1,
			  sphone AS fon_b_e1,
			  sfax AS fax_b_e1,
			  smobilephone AS mobil_b_e1,
			  semail AS mail_b_e1,
			  surl AS url_b_e1,

			  -- Adressdaten Privat, Zeichensatz 1
			  function_a_z1 AS function_b_e2,
			  company_a_z1 AS company_b_e2,
			  street_a_z1 AS street_b_e2,
			  zipcode_a_z1 AS zipcode_b_e2,
			  NULL::varchar(100) AS state_b_e2,
			  city_a_z1 AS city_b_e2,
			  country_a_z1 AS country_b_e2,
			  pobox_a_z1 AS pobox_b_e2,
			  poboxzipcode_a_z1 AS poboxzipcode_b_e2,
			  suffix1_a_z1 AS suffix1_b_e2,
			  suffix2_a_z1 AS suffix2_b_e2,
			  fon_a_z1 AS fon_b_e2,
			  fax_a_z1 AS fax_b_e2,
			  mobil_a_z1 AS mobil_b_e2,
			  mail_a_z1 AS mail_b_e2,
			  www_a_z1 AS url_b_e2,

			  -- Adressdaten Privat, Zeichensatz 2
			  function_a_z2 AS function_b_e3,
			  company_a_z2 AS company_b_e3,
			  street_a_z2 AS street_b_e3,
			  zipcode_a_z2 AS zipcode_b_e3,
			  NULL::varchar(100) AS state_b_e3,
			  city_a_z2 AS city_b_e3,
			  country_a_z2 AS country_b_e3,
			  pobox_a_z2 AS pobox_b_e3,
			  poboxzipcode_a_z2 AS poboxzipcode_b_e3,
			  suffix1_a_z2 AS suffix1_b_e3,
			  suffix2_a_z2 AS suffix2_b_e3,
			  fon_a_z2 AS fon_b_e3,
			  fax_a_z2 AS fax_b_e3,
			  mobil_a_z2 AS mobil_b_e3,
			  mail_a_z2 AS mail_b_e3,
			  www_a_z2 AS url_b_e3,

			  -- Adressdaten Weitere, Latein
			  function_c AS function_c_e1,
			  company_c AS company_c_e1,
			  sstreetdelivery AS street_c_e1,
			  szippostalcodedelivery AS zipcode_c_e1,
			  NULL::varchar(100) AS state_c_e1,
			  scitydelivery AS city_c_e1,
			  scountrydelivery AS country_c_e1,
			  pobox_c AS pobox_c_e1,
			  poboxzipcode_c AS poboxzipcode_c_e1,
			  suffix1_c AS suffix1_c_e1,
			  suffix2_c AS suffix2_c_e1,
			  fon_c AS fon_c_e1,
			  fax_c AS fax_c_e1,
			  mobil_c AS mobil_c_e1,
			  mail_c AS mail_c_e1,
			  www_c AS url_c_e1,

			  -- Adressdaten Weitere, Zeichensatz 1
			  function_c_z1 AS function_c_e2,
			  company_c_z1 AS company_c_e2,
			  street_c_z1 AS street_c_e2,
			  zipcode_c_z1 AS zipcode_c_e2,
			  NULL::varchar(100) AS state_c_e2,
			  city_c_z1 AS city_c_e2,
			  country_c_z1 AS country_c_e2,
			  pobox_c_z1 AS pobox_c_e2,
			  poboxzipcode_c_z1 AS poboxzipcode_c_e2,
			  suffix1_c_z1 AS suffix1_c_e2,
			  suffix2_c_z1 AS suffix2_c_e2,
			  fon_c_z1 AS fon_c_e2,
			  fax_c_z1 AS fax_c_e2,
			  mobil_c_z1 AS mobil_c_e2,
			  mail_c_z1 AS mail_c_e2,
			  www_c_z1 AS url_c_e2,

			  -- Adressdaten Weitere, Zeichensatz 2
			  function_c_z2 AS function_c_e3,
			  company_c_z2 AS company_c_e3,
			  street_c_z2 AS street_c_e3,
			  zipcode_c_z2 AS zipcode_c_e3,
			  NULL::varchar(100) AS state_c_e3,
			  city_c_z2 AS city_c_e3,
			  country_c_z2 AS country_c_e3,
			  pobox_c_z2 AS pobox_c_e3,
			  poboxzipcode_c_z2 AS poboxzipcode_c_e3,
			  suffix1_c_z2 AS suffix1_c_e3,
			  suffix2_c_z2 AS suffix2_c_e3,
			  fon_c_z2 AS fon_c_e3,
			  fax_c_z2 AS fax_c_e3,
			  mobil_c_z2 AS mobil_c_e3,
			  mail_c_z2 AS mail_c_e3,
			  www_c_z2 AS url_c_e3

			  FROM veraweb.tcontact
			  LEFT OUTER JOIN veraweb.tperson ON (tcontact.iid = tperson.fk_contact)
			;

			-- Anpassen von tt2.
			UPDATE veraweb.tt2 SET sex_a_e1 = 'f' WHERE sex_a_e1 IN ('F', 'f', 'W', 'w');
			UPDATE veraweb.tt2 SET sex_b_e1 = 'f' WHERE sex_b_e1 IN ('F', 'f', 'W', 'w');
			UPDATE veraweb.tt2 SET sex_a_e1 = 'm' WHERE sex_a_e1 IS NULL OR sex_a_e1 NOT IN ('m', 'f');
			UPDATE veraweb.tt2 SET sex_b_e1 = 'm' WHERE sex_b_e1 IS NULL OR sex_b_e1 NOT IN ('m', 'f');
			UPDATE veraweb.tt2 SET domestic_a_e1 = 'f' WHERE domestic_a_e1 IN ('F', 'f', '0');
			UPDATE veraweb.tt2 SET domestic_b_e1 = 'f' WHERE domestic_b_e1 IN ('F', 'f', '0');
			UPDATE veraweb.tt2 SET domestic_a_e1 = 't' WHERE domestic_a_e1 IS NULL OR domestic_a_e1 NOT IN ('t', 'f');
			UPDATE veraweb.tt2 SET domestic_b_e1 = 't' WHERE domestic_b_e1 IS NULL OR domestic_b_e1 NOT IN ('t', 'f');
			UPDATE veraweb.tt2 SET iscompany = 't' WHERE iscompany IN ('T', 't', '1');
			UPDATE veraweb.tt2 SET iscompany = 'f' WHERE iscompany IS NULL OR iscompany NOT IN ('t', 'f');

			ALTER TABLE veraweb.tt2 ALTER COLUMN deleted SET DEFAULT 'f';
			ALTER TABLE veraweb.tt2 ALTER COLUMN deleted SET NOT NULL;
			ALTER TABLE veraweb.tt2 ALTER COLUMN dateexpire SET DEFAULT (now() + '3 years'::interval);
			ALTER TABLE veraweb.tt2 ALTER COLUMN sex_a_e1 SET DEFAULT 'm';
			ALTER TABLE veraweb.tt2 ALTER COLUMN sex_a_e1 SET NOT NULL;
			ALTER TABLE veraweb.tt2 ALTER COLUMN sex_b_e1 SET DEFAULT 'm';
			ALTER TABLE veraweb.tt2 ALTER COLUMN sex_b_e1 SET NOT NULL;
			ALTER TABLE veraweb.tt2 ALTER COLUMN domestic_a_e1 SET DEFAULT 't';
			ALTER TABLE veraweb.tt2 ALTER COLUMN domestic_a_e1 SET NOT NULL;
			ALTER TABLE veraweb.tt2 ALTER COLUMN domestic_b_e1 SET DEFAULT 't';
			ALTER TABLE veraweb.tt2 ALTER COLUMN domestic_b_e1 SET NOT NULL;
			ALTER TABLE veraweb.tt2 ALTER COLUMN iscompany SET DEFAULT 'f';
			ALTER TABLE veraweb.tt2 ALTER COLUMN iscompany SET NOT NULL;

			-- tt2.id auf serial umstellen.
			CREATE SEQUENCE veraweb.tt2_pk_seq;
			ALTER TABLE veraweb.tt2 ALTER COLUMN pk SET STORAGE PLAIN;
			ALTER TABLE veraweb.tt2 ALTER COLUMN pk SET NOT NULL;
			ALTER TABLE veraweb.tt2 ALTER COLUMN pk SET DEFAULT nextval('veraweb.tt2_pk_seq'::text);
			PERFORM SETVAL('veraweb.tt2_pk_seq', (SELECT MAX(pk) FROM veraweb.tt2));

			-- drop tcontact und tperson
			DROP TABLE veraweb.tcontact;
			DROP TABLE veraweb.tperson;

			-- rename tt2 zu tperson
			ALTER TABLE veraweb.tt2 RENAME TO tperson;
			ALTER TABLE veraweb.tt2_pk_seq RENAME TO tperson_pk_seq;
			ALTER TABLE veraweb.tperson ALTER COLUMN pk SET DEFAULT nextval('veraweb.tperson_pk_seq'::text);
			ALTER TABLE veraweb.tperson ADD CONSTRAINT tperson_pkey PRIMARY KEY (pk);
		END IF;
		vmsg := 'end.combineTABLE.tcontact & tperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tdoctype';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tdoctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tdoctype
			(
			  pk serial NOT NULL,
			  docname varchar(200) NOT NULL,
			  addresstype int4 NOT NULL DEFAULT 1,
			  locale int4 NOT NULL DEFAULT 0,
			  sortorder int4,
			  flags int4,
			  isdefault int4 NOT NULL DEFAULT 0,
			  partner int4 NOT NULL DEFAULT 0,
			  host int4 NOT NULL DEFAULT 1,
			  format varchar(20) NOT NULL,
			  CONSTRAINT tdoctype_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tdoctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tdoctype ADD COLUMN format varchar(20) NOT NULL;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tdoctype' AND column_name = 'format';
	IF vint = 0 THEN
		vmsg := 'begin.createcolumn.tdoctype.format';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tdoctype ADD COLUMN format varchar(20);
			UPDATE veraweb.tdoctype SET format = 'ods-document' WHERE format IS NULL OR format = '';
			ALTER TABLE veraweb.tdoctype ALTER COLUMN format SET NOT NULL;
		END IF;
		vmsg := 'end.createcolumn.tdoctype.format';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tevent';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tevent';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tevent
			(
			  pk serial NOT NULL,
			  fk_orgunit int4 DEFAULT 0,
			  fk_host int4,
			  invitationtype int4 NOT NULL DEFAULT 1,
			  shortname varchar(50) NOT NULL,
			  eventname text,
			  datebegin timestamptz NOT NULL,
			  dateend timestamptz,
			  invitehostpartner int4 DEFAULT 0,
			  hostname varchar(300),
			  maxguest int4 DEFAULT 0,
			  location varchar(300) NOT NULL,
			  note text,
			  createdby varchar(50),
			  changedby varchar(50),
			  created timestamptz,
			  changed timestamptz,
			  CONSTRAINT tevent_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tevent';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tevent DROP COLUMN fk_personhost;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tevent' AND column_name = 'fk_personhost';
	IF vint > 0 THEN
		vmsg := 'begin.dropcolumn.tevent.fk_personhost';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tevent DROP COLUMN fk_personhost;
		END IF;
		vmsg := 'end.dropcolumn.tevent.fk_personhost';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tevent DROP COLUMN fk_contacthost;
	--ALTER TABLE veraweb.tevent ADD COLUMN fk_host int4;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tevent' AND column_name = 'fk_contacthost';
	IF vint = 1 THEN
		vmsg := 'begin.changecolumn.tevent.fk_contacthost to fk_host';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tevent ADD COLUMN fk_host int4;
			UPDATE veraweb.tevent SET fk_host = fk_contacthost;
			ALTER TABLE veraweb.tevent DROP COLUMN fk_contacthost;
		END IF;
		vmsg := 'end.changecolumn.tevent.fk_contacthost to fk_host';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tevent ADD COLUMN fk_host int4;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tevent' AND column_name = 'fk_host';
	IF vint = 0 THEN
		vmsg := 'begin.createcolumn.tevent.fk_host';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tevent ADD COLUMN fk_host int4;
		END IF;
		vmsg := 'end.createcolumn.tevent.fk_host';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tevent_doctype';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tevent_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tevent_doctype
			(
			  pk serial NOT NULL,
			  fk_event int4 NOT NULL DEFAULT 0,
			  fk_doctype int4 NOT NULL DEFAULT 0,
			  CONSTRAINT tevent_doctype_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tevent_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tfunction';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tfunction';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tfunction
			(
			  pk serial NOT NULL,
			  functionname varchar(300) NOT NULL,
			  CONSTRAINT tfunction_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tfunction';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tguest';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tguest';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tguest
			(
			  pk serial NOT NULL,
			  fk_person int4 NOT NULL,
			  fk_event int4 NOT NULL,
			  fk_category int4 DEFAULT 0,
			  fk_color int4 DEFAULT 0,
			  invitationtype int4 DEFAULT 0,
			  invitationstatus int4 DEFAULT 0,
			  ishost int4 DEFAULT 0,
			  diplodate timestamptz,
			  rank int4 DEFAULT 0,
			  reserve int4 DEFAULT 0,
			  tableno int4 DEFAULT 0,
			  seatno int4 DEFAULT 0,
			  orderno int4 DEFAULT 0,
			  notehost text,
			  noteorga text,
			  language varchar(250),
			  gender varchar(1) NOT NULL,
			  nationality varchar(100),
			  domestic_a varchar(1) NOT NULL DEFAULT 't'::character varying,
			  invitationstatus_p int4 DEFAULT 0,
			  tableno_p int4 DEFAULT 0,
			  seatno_p int4 DEFAULT 0,
			  orderno_p int4 DEFAULT 0,
			  notehost_p text,
			  noteorga_p text,
			  language_p varchar(250),
			  gender_p varchar(1) NOT NULL,
			  nationality_p varchar(100),
			  domestic_b varchar(1) NOT NULL DEFAULT 't'::character varying,
			  fk_color_p int4 DEFAULT 0,
			  createdby varchar(50),
			  changedby varchar(50),
			  created timestamptz,
			  changed timestamptz,
			  CONSTRAINT tguest_pkey PRIMARY KEY (pk)
			) WITH OIDS;
			CREATE INDEX tguest_fk_person_index ON veraweb.tguest
			  USING btree (fk_person);
			CREATE INDEX tguest_fk_event_index ON veraweb.tguest
			  USING btree (fk_event);
			CREATE INDEX tguest_fk_category_index ON veraweb.tguest
			  USING btree (fk_category);
		END IF;
		vmsg := 'end.createTABLE.tguest';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tguest DROP COLUMN fk_contact;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		(table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'fk_contact' AND udt_name = 'int8') OR
		(table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'fk_person' AND udt_name = 'int4');
	IF vint = 2 THEN
		vmsg := 'begin.combine.tguest.fk_contact and fk_person';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			UPDATE veraweb.tguest SET fk_person = fk_contact;
			DELETE FROM veraweb.tguest WHERE fk_person IS NULL;
			ALTER TABLE veraweb.tguest DROP COLUMN fk_contact;
			ALTER TABLE veraweb.tguest ALTER COLUMN fk_person SET NOT NULL;
		END IF;
		vmsg := 'end.combine.tguest.fk_contact and fk_person';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest DROP COLUMN saveunder;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'saveunder';
	IF vint = 1 THEN
		vmsg := 'begin.dropcolumn.tguest.saveunder';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest DROP COLUMN saveunder;
		END IF;
		vmsg := 'end.dropcolumn.tguest.saveunder';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest DROP COLUMN firstname;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'firstname';
	IF vint = 1 THEN
		vmsg := 'begin.dropcolumn.tguest.firstname';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest DROP COLUMN firstname;
		END IF;
		vmsg := 'end.dropcolumn.tguest.firstname';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest DROP COLUMN lastname;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'lastname';
	IF vint = 1 THEN
		vmsg := 'begin.dropcolumn.tguest.lastname';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest DROP COLUMN lastname;
		END IF;
		vmsg := 'end.dropcolumn.tguest.lastname';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest DROP COLUMN mail;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'mail';
	IF vint = 1 THEN
		vmsg := 'begin.dropcolumn.tguest.mail';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest DROP COLUMN mail;
		END IF;
		vmsg := 'end.dropcolumn.tguest.mail';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest ADD COLUMN domestic_a varchar(1);
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'domestic' AND udt_name = 'int4';
	IF vint = 1 THEN
		vmsg := 'begin.change.tguest.domestic_a from int4 to varchar(1)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest ADD COLUMN domestic_a varchar(1);
			UPDATE veraweb.tguest SET domestic_a = domestic;
			ALTER TABLE veraweb.tguest ALTER COLUMN domestic_a SET NOT NULL;
			ALTER TABLE veraweb.tguest DROP COLUMN domestic;
			UPDATE veraweb.tguest SET domestic_a = 'f' WHERE domestic_a IN ('F', 'f', '0');
			UPDATE veraweb.tguest SET domestic_a = 't' WHERE domestic_a IS NULL OR domestic_a NOT IN ('t', 'f');
		END IF;
		vmsg := 'end.change.tguest.domestic_a from int4 to varchar(1)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest ALTER COLUMN language varchar(100) to varchar(250);
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'language' AND udt_name = 'varchar';
	IF vint = 1 THEN
		vmsg := 'begin.change.tguest.language from varchar(100) to varchar(250)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest ALTER COLUMN language type varchar(250);
		END IF;
		vmsg := 'end.change.tguest.language from varchar(100) to varchar(250)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest ALTER COLUMN language_p varchar(100) to varchar(250);
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'language_p' AND udt_name = 'varchar';
	IF vint = 1 THEN
		vmsg := 'begin.change.tguest.language_p from varchar(100) to varchar(250)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest ALTER COLUMN language_p type varchar(250);
		END IF;
		vmsg := 'end.change.tguest.language_p from varchar(100) to varchar(250)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tguest ADD COLUMN domestic_b varchar(1);
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns WHERE
		table_schema = 'veraweb' AND table_name = 'tguest' AND column_name = 'domestic_p' AND udt_name = 'int4';
	IF vint = 1 THEN
		vmsg := 'begin.change.tguest.domestic_b from int4 to varchar(1)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest ADD COLUMN domestic_b varchar(1);
			UPDATE veraweb.tguest SET domestic_b = domestic_p;
			ALTER TABLE veraweb.tguest ALTER COLUMN domestic_b SET NOT NULL;
			ALTER TABLE veraweb.tguest DROP COLUMN domestic_p;
			UPDATE veraweb.tguest SET domestic_b = 'f' WHERE domestic_b IN ('F', 'f', '0');
			UPDATE veraweb.tguest SET domestic_b = 't' WHERE domestic_b IS NULL OR domestic_b NOT IN ('t', 'f');
		END IF;
		vmsg := 'end.change.tguest.domestic_b from int4 to varchar(1)';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tguest_doctype';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tguest_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tguest_doctype
			(
			  pk serial NOT NULL,
			  fk_guest int4 NOT NULL DEFAULT 0,
			  fk_doctype int4 NOT NULL DEFAULT 0,
			  addresstype int4 NOT NULL DEFAULT 0,
			  locale int4 NOT NULL DEFAULT 0,
			  textfield text,
			  textfield_p text,
			  textjoin varchar(50),
			  salutation varchar(50),
			  function varchar(250),
			  titel varchar(250),
			  firstname varchar(100),
			  lastname varchar(100),
			  zipcode varchar(50),
			  state varchar(100),
			  city varchar(100),
			  street varchar(100),
			  country varchar(100),
			  suffix1 varchar(100),
			  suffix2 varchar(100),
			  salutation_p varchar(50),
			  titel_p varchar(250),
			  firstname_p varchar(100),
			  lastname_p varchar(100),
			  fon varchar(100),
			  fax varchar(100),
			  mail varchar(250),
			  www varchar(250),
			  mobil varchar(100),
			  company varchar(250),
			  pobox varchar(50),
			  poboxzipcode varchar(50),
			  CONSTRAINT tguest_doctype_pkey PRIMARY KEY (pk)
			) WITH OIDS;
			CREATE INDEX tguest_doctype_fk_index ON veraweb.tguest_doctype
			  USING btree (fk_guest, fk_doctype);
			CREATE INDEX tguest_doctype_fk_guest_index ON veraweb.tguest_doctype
			  USING btree (fk_guest);
		END IF;
		vmsg := 'end.createTABLE.tguest_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tguest_doctype RENAME fk_person TO fk_guest;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tguest_doctype' AND column_name = 'fk_person';
	IF vint > 0 THEN
		vmsg := 'begin.renamecolumn.tguest_doctype.fk_person to fk_guest';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest_doctype RENAME fk_person TO fk_guest;
		END IF;
		vmsg := 'end.renamecolumn.tguest_doctype.fk_person to fk_guest';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>

	-------- added state attribute as per the change request for the next version 1.2.2
	-------- 2008-12-23
	--------<COLUMN>
    --ALTER TABLE veraweb.tguest_doctype ADD COLUMN state varchar(100)
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tguest_doctype' AND column_name = 'state';
	IF vint = 0 THEN
		vmsg := 'begin.addcolumn.tguest_doctype.state';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tguest_doctype ADD COLUMN state varchar(100);
		END IF;
		vmsg := 'end.addcolumn.tguest_doctype.state';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tlocation';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tlocation';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tlocation
			(
			  pk serial NOT NULL,
			  fk_orgunit int4 DEFAULT 0,
			  locationname varchar(200) NOT NULL,
			  CONSTRAINT tlocation_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tlocation';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tmailinglist';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tmailinglist';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tmailinglist
			(
			  pk serial NOT NULL,
			  fk_orgunit int4 NOT NULL,
			  listname varchar(200) NOT NULL,
			  fk_vera int4 DEFAULT 0,
			  created timestamptz,
			  createdby varchar(50),
			  fk_user int4,
			  CONSTRAINT tmailinglist_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tmailinglist';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tmailinglist ADD COLUMN fk_orgunit int4 NOT NULL;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tmailinglist' AND column_name = 'fk_orgunit';
	IF vint = 0 THEN
		vmsg := 'begin.createcolumn.tmailinglist.fk_orgunit';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tmailinglist ADD COLUMN fk_orgunit int4;
		END IF;
		vmsg := 'end.createcolumn.tmailinglist.fk_orgunit';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tmaildraft';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tmaildraft';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tmaildraft
			(
			  pk serial NOT NULL,
			  name varchar(200) NOT NULL,
			  subject varchar(200) NOT NULL,
			  content text NOT NULL,
			  createdby varchar(50),
			  created timestamptz,
			  changedby varchar(50),
			  changed timestamptz,
			  PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tmaildraft';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tmailoutbox';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tmailoutbox';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tmailoutbox
			(
			  pk serial NOT NULL,
			  status int4 NOT NULL,
			  addrfrom varchar(200) NOT NULL,
			  addrto varchar(200) NOT NULL,
			  subject varchar(200) NOT NULL,
			  content text NOT NULL,
			  lastupdate timestamptz,
			  errortext varchar(200),
			  PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tmailoutbox';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'torgunit';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.torgunit';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.torgunit
			(
			  pk serial NOT NULL,
			  unitname varchar(100) NOT NULL,
			  folderxman varchar(100),
			  fk_folderxman int4,
			  CONSTRAINT torgunit_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.torgunit';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	-------- added table as per the change request for the next version 1.2.0
	-------- cklein 2008-02-12
	-------- added foreign key constraint fk_orgunit
	-------- cklein 2008-02-14
	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tworkarea';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tworkare';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE SEQUENCE veraweb.tworkarea_pk_seq INCREMENT 1 MINVALUE 0;
			CREATE TABLE veraweb.tworkarea
			(
			  pk int4 NOT NULL,
			  name varchar(250) NOT NULL,
			  fk_orgunit int4 NULL, -- must allow null as there is no default orgunit, see the default workarea below corresponding to no workarea
			  CONSTRAINT tworkarea_pkey PRIMARY KEY (pk),
			  CONSTRAINT tworkarea_fkey_orgunit FOREIGN KEY (fk_orgunit) REFERENCES veraweb.torgunit (pk) ON UPDATE RESTRICT ON DELETE RESTRICT
			) WITH OIDS;
			ALTER TABLE veraweb.tworkarea ALTER COLUMN pk SET DEFAULT nextval('veraweb.tworkarea_pk_seq'::text);
		END IF;
		vmsg := 'end.createTABLE.tworkarea';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		vmsg := 'begin.insertDEFAULTS.tworkarea';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			INSERT INTO veraweb.tworkarea (name) VALUES('Kein');
		END IF;
		vmsg := 'end.insertDEFAULTS.tworkarea';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables WHERE
		table_schema = 'veraweb' AND table_name = 'tperson';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tperson
			(
			  pk serial NOT NULL,
			  fk_orgunit int4,
			  created timestamptz,
			  createdby varchar(100),
			  changed timestamptz,
			  changedby varchar(100),
			  deleted varchar(1) NOT NULL DEFAULT 'f'::character varying,
			  dateexpire timestamptz DEFAULT (now() + '3 years'::interval),
			  iscompany varchar(1) NOT NULL DEFAULT 'f'::character varying,
			  importsource varchar(250),
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- cklein 2008-02-20
	-------- added NOT NULL constraint
	-------- cklein 2008-03-14
	-------- added default value
                          fk_workarea int4 NOT NULL DEFAULT 0,

			  -- Hauptperson, Latein
			  salutation_a_e1 varchar(50),
			  fk_salutation_a_e1 int4,
			  title_a_e1 varchar(250),
			  firstname_a_e1 varchar(100),
			  lastname_a_e1 varchar(100),
			  domestic_a_e1 varchar(1) NOT NULL DEFAULT 't'::character varying,
			  sex_a_e1 varchar(1) NOT NULL DEFAULT 'm'::character varying,
			  birthday_a_e1 timestamptz,
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- cklein 2008-02-11
			  birthplace_a_e1 varchar(100),
			  diplodate_a_e1 timestamptz,
			  languages_a_e1 varchar(250),
			  nationality_a_e1 varchar(100),
			  note_a_e1 text,
			  noteorga_a_e1 text,
			  notehost_a_e1 text,

			  -- Hauptperson, Zeichensatz 1
			  salutation_a_e2 varchar(50),
			  fk_salutation_a_e2 int4,
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- cklein 2008-02-11
			  birthplace_a_e2 varchar(100),
			  title_a_e2 varchar(250),
			  firstname_a_e2 varchar(100),
			  lastname_a_e2 varchar(100),

			  -- Hauptperson, Zeichensatz 2
			  salutation_a_e3 varchar(50),
			  fk_salutation_a_e3 int4,
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- cklein 2008-02-11
			  birthplace_a_e3 varchar(100),
			  title_a_e3 varchar(250),
			  firstname_a_e3 varchar(100),
			  lastname_a_e3 varchar(100),

			  -- Partner, Latein
			  salutation_b_e1 varchar(50),
			  fk_salutation_b_e1 int4,
			  title_b_e1 varchar(250),
			  firstname_b_e1 varchar(100),
			  lastname_b_e1 varchar(100),
			  domestic_b_e1 varchar(1) NOT NULL DEFAULT 't'::character varying,
			  sex_b_e1 varchar(1) NOT NULL DEFAULT 'm'::character varying,
			  birthday_b_e1 timestamptz,
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- outcommented since it is no longer required
	-------- cklein 2008-02-11
			  --birthplace_b_e1 varchar(100),
			  diplodate_b_e1 timestamptz,
			  languages_b_e1 varchar(250),
			  nationality_b_e1 varchar(100),
			  note_b_e1 text,
			  noteorga_b_e1 text,
			  notehost_b_e1 text,

			  -- Partner, Zeichensatz 1
			  salutation_b_e2 varchar(50),
			  fk_salutation_b_e2 int4,
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- outcommented since it is no longer required
	-------- cklein 2008-02-11
			  --birthplace_b_e2 varchar(100),
			  title_b_e2 varchar(250),
			  firstname_b_e2 varchar(100),
			  lastname_b_e2 varchar(100),

			  -- Partner, Zeichensatz 2
			  salutation_b_e3 varchar(50),
			  fk_salutation_b_e3 int4,
	-------- added new attribute as per the change request for the next version 1.2.0
	-------- outcommented since it is no longer required
	-------- cklein 2008-02-11
			  --birthplace_b_e3 varchar(100),
			  title_b_e3 varchar(250),
			  firstname_b_e3 varchar(100),
			  lastname_b_e3 varchar(100),

			  -- Adressdaten Geschäftlich, Latein
			  function_a_e1 varchar(250),
			  company_a_e1 varchar(250),
			  street_a_e1 varchar(100),
			  zipcode_a_e1 varchar(50),
			  state_a_e1 varchar(100),
			  city_a_e1 varchar(100),
			  country_a_e1 varchar(100),
			  pobox_a_e1 varchar(50),
			  poboxzipcode_a_e1 varchar(50),
			  suffix1_a_e1 varchar(100),
			  suffix2_a_e1 varchar(100),
			  fon_a_e1 varchar(100),
			  fax_a_e1 varchar(100),
			  mobil_a_e1 varchar(100),
			  mail_a_e1 varchar(250),
			  url_a_e1 varchar(250),

			  -- Adressdaten Geschäftlich, Zeichensatz 1
			  function_a_e2 varchar(250),
			  company_a_e2 varchar(250),
			  street_a_e2 varchar(100),
			  zipcode_a_e2 varchar(50),
			  state_a_e2 varchar(100),
			  city_a_e2 varchar(100),
			  country_a_e2 varchar(100),
			  pobox_a_e2 varchar(50),
			  poboxzipcode_a_e2 varchar(50),
			  suffix1_a_e2 varchar(100),
			  suffix2_a_e2 varchar(100),
			  fon_a_e2 varchar(100),
			  fax_a_e2 varchar(100),
			  mobil_a_e2 varchar(100),
			  mail_a_e2 varchar(250),
			  url_a_e2 varchar(250),

			  -- Adressdaten Geschäftlich, Zeichensatz 2
			  function_a_e3 varchar(250),
			  company_a_e3 varchar(250),
			  street_a_e3 varchar(100),
			  zipcode_a_e3 varchar(50),
			  state_a_e3 varchar(100),
			  city_a_e3 varchar(100),
			  country_a_e3 varchar(100),
			  pobox_a_e3 varchar(50),
			  poboxzipcode_a_e3 varchar(50),
			  suffix1_a_e3 varchar(100),
			  suffix2_a_e3 varchar(100),
			  fon_a_e3 varchar(100),
			  fax_a_e3 varchar(100),
			  mobil_a_e3 varchar(100),
			  mail_a_e3 varchar(250),
			  url_a_e3 varchar(250),

			  -- Adressdaten Privat, Latein
			  function_b_e1 varchar(250),
			  company_b_e1 varchar(250),
			  street_b_e1 varchar(100),
			  zipcode_b_e1 varchar(50),
			  state_b_e1 varchar(100),
			  city_b_e1 varchar(100),
			  country_b_e1 varchar(100),
			  pobox_b_e1 varchar(50),
			  poboxzipcode_b_e1 varchar(50),
			  suffix1_b_e1 varchar(100),
			  suffix2_b_e1 varchar(100),
			  fon_b_e1 varchar(100),
			  fax_b_e1 varchar(100),
			  mobil_b_e1 varchar(100),
			  mail_b_e1 varchar(250),
			  url_b_e1 varchar(250),

			  -- Adressdaten Privat, Zeichensatz 1
			  function_b_e2 varchar(250),
			  company_b_e2 varchar(250),
			  street_b_e2 varchar(100),
			  zipcode_b_e2 varchar(50),
			  state_b_e2 varchar(100),
			  city_b_e2 varchar(100),
			  country_b_e2 varchar(100),
			  pobox_b_e2 varchar(50),
			  poboxzipcode_b_e2 varchar(50),
			  suffix1_b_e2 varchar(100),
			  suffix2_b_e2 varchar(100),
			  fon_b_e2 varchar(100),
			  fax_b_e2 varchar(100),
			  mobil_b_e2 varchar(100),
			  mail_b_e2 varchar(250),
			  url_b_e2 varchar(250),

			  -- Adressdaten Privat, Zeichensatz 2
			  function_b_e3 varchar(250),
			  company_b_e3 varchar(250),
			  street_b_e3 varchar(100),
			  zipcode_b_e3 varchar(50),
			  state_b_e3 varchar(100),
			  city_b_e3 varchar(100),
			  country_b_e3 varchar(100),
			  pobox_b_e3 varchar(50),
			  poboxzipcode_b_e3 varchar(50),
			  suffix1_b_e3 varchar(100),
			  suffix2_b_e3 varchar(100),
			  fon_b_e3 varchar(100),
			  fax_b_e3 varchar(100),
			  mobil_b_e3 varchar(100),
			  mail_b_e3 varchar(250),
			  url_b_e3 varchar(250),

			  -- Adressdaten Weitere, Latein
			  function_c_e1 varchar(250),
			  company_c_e1 varchar(250),
			  street_c_e1 varchar(100),
			  zipcode_c_e1 varchar(50),
			  state_c_e1 varchar(100),
			  city_c_e1 varchar(100),
			  country_c_e1 varchar(100),
			  pobox_c_e1 varchar(50),
			  poboxzipcode_c_e1 varchar(50),
			  suffix1_c_e1 varchar(100),
			  suffix2_c_e1 varchar(100),
			  fon_c_e1 varchar(100),
			  fax_c_e1 varchar(100),
			  mobil_c_e1 varchar(100),
			  mail_c_e1 varchar(250),
			  url_c_e1 varchar(250),

			  -- Adressdaten Weitere, Zeichensatz 1
			  function_c_e2 varchar(250),
			  company_c_e2 varchar(250),
			  street_c_e2 varchar(100),
			  zipcode_c_e2 varchar(50),
			  state_c_e2 varchar(100),
			  city_c_e2 varchar(100),
			  country_c_e2 varchar(100),
			  pobox_c_e2 varchar(50),
			  poboxzipcode_c_e2 varchar(50),
			  suffix1_c_e2 varchar(100),
			  suffix2_c_e2 varchar(100),
			  fon_c_e2 varchar(100),
			  fax_c_e2 varchar(100),
			  mobil_c_e2 varchar(100),
			  mail_c_e2 varchar(250),
			  url_c_e2 varchar(250),

			  -- Adressdaten Weitere, Zeichensatz 2
			  function_c_e3 varchar(250),
			  company_c_e3 varchar(250),
			  street_c_e3 varchar(100),
			  zipcode_c_e3 varchar(50),
			  state_c_e3 varchar(100),
			  city_c_e3 varchar(100),
			  country_c_e3 varchar(100),
			  pobox_c_e3 varchar(50),
			  poboxzipcode_c_e3 varchar(50),
			  suffix1_c_e3 varchar(100),
			  suffix2_c_e3 varchar(100),
			  fon_c_e3 varchar(100),
			  fax_c_e3 varchar(100),
			  mobil_c_e3 varchar(100),
			  mail_c_e3 varchar(250),
			  url_c_e3 varchar(250),
			  CONSTRAINT tperson_pkey PRIMARY KEY (pk),
			  CONSTRAINT tperson_fkey_workarea FOREIGN KEY (fk_workarea) REFERENCES veraweb.tworkarea (pk) ON UPDATE RESTRICT ON DELETE RESTRICT
			)
			WITH OIDS;
			CREATE INDEX tperson_bothnames_a_e1_index ON veraweb.tperson
			  USING btree (lastname_a_e1, firstname_a_e1);
			CREATE INDEX tperson_firstname_a_e1_index ON veraweb.tperson
			  USING btree (firstname_a_e1);
			CREATE INDEX tperson_lastname_a_e1_index ON veraweb.tperson
			  USING btree (lastname_a_e1);
			CREATE INDEX tperson_firstname_a_e2_index ON veraweb.tperson
			  USING btree (firstname_a_e2);
			CREATE INDEX tperson_lastname_a_e2_index ON veraweb.tperson
			  USING btree (lastname_a_e2);
			CREATE INDEX tperson_firstname_a_e3_index ON veraweb.tperson
			  USING btree (firstname_a_e3);
			CREATE INDEX tperson_lastname_a_e3_index ON veraweb.tperson
			  USING btree (lastname_a_e3);
			CREATE INDEX tperson_firstname_b_e1_index ON veraweb.tperson
			  USING btree (firstname_b_e1);
			CREATE INDEX tperson_lastname_b_e1_index ON veraweb.tperson
			  USING btree (lastname_b_e1);
			CREATE INDEX tperson_firstname_b_e2_index ON veraweb.tperson
			  USING btree (firstname_b_e2);
			CREATE INDEX tperson_lastname_b_e2_index ON veraweb.tperson
			  USING btree (lastname_b_e2);
			CREATE INDEX tperson_firstname_b_e3_index ON veraweb.tperson
			  USING btree (firstname_b_e3);
			CREATE INDEX tperson_lastname_b_e3_index ON veraweb.tperson
			  USING btree (lastname_b_e3);
		END IF;
		vmsg := 'end.createTABLE.tperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
		--------<COLUMN>
		--ALTER TABLE veraweb.tperson ADD CONSTRAINT tperson_pkey PRIMARY KEY (pk);
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.constraint_column_usage
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'pk' AND constraint_name = 'tperson_pkey';
		IF vint = 0 THEN
			vmsg := 'begin.addconstraint.tperson.pk';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD CONSTRAINT tperson_pkey PRIMARY KEY (pk);
			END IF;
			vmsg := 'end.addconstraint.tperson.pk';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added additional upgrade path as per the change request for the next version 1.2.0
		-------- cklein 2008-02-20
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN fk_workarea int4
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'fk_workarea';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.fk_workarea';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN fk_workarea int4 NOT NULL DEFAULT 0;
			END IF;
			vmsg := 'end.addcolumn.tperson.fk_workarea';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added additional upgrade path as per the change request for the next version 1.2.0
		-------- cklein 2008-02-20
		--------<COLUMN>
		--ALTER TABLE veraweb.tperson ADD CONSTRAINT tperson_fkey_workarea FOREIGN KEY (fk_workarea) REFERENCES veraweb.tworkarea (pk) ON UPDATE RESTRICT ON DELETE RESTRICT
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.constraint_column_usage
			WHERE table_schema = 'veraweb' AND table_name = 'tworkarea' AND column_name = 'pk' AND constraint_name = 'tperson_fkey_workarea';
		IF vint = 0 THEN
			vmsg := 'begin.addconstraint.tperson.fk_workarea';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD CONSTRAINT tperson_fkey_workarea FOREIGN KEY (fk_workarea) REFERENCES veraweb.tworkarea (pk) ON UPDATE RESTRICT ON DELETE RESTRICT;
			END IF;
			vmsg := 'end.addconstraint.tperson.fk_workarea';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_a_e1 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_a_e1';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_a_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_a_e1 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_a_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_a_e2 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_a_e2';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_a_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_a_e2 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_a_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_a_e3 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_a_e3';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_a_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_a_e3 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_a_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_b_e1 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_b_e1';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_b_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_b_e1 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_b_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_b_e2 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_b_e2';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_b_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_b_e2 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_b_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_b_e3 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_b_e3';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_b_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_b_e3 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_b_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_c_e1 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_c_e1';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_c_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_c_e1 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_c_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_c_e2 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_c_e2';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_c_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_c_e2 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_c_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added state attribute as per the change request for the next version 1.2.2
		-------- 2008-12-23
		--------<COLUMN>
        --ALTER TABLE veraweb.tperson ADD COLUMN state_c_e3 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'state_c_e3';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.state_c_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN state_c_e3 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.state_c_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMN/>

		-------- added additional upgrade path as per the change request for the next version 1.2.0
		-------- cklein 2008-02-11
		--------<COLUMNS>
        --ALTER TABLE veraweb.tperson ADD COLUMN birthplace_a_e1 varchar(100)
		vint := 0;
		SELECT count(*) INTO vint FROM information_schema.columns
			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'birthplace_a_e1';
		IF vint = 0 THEN
			vmsg := 'begin.addcolumn.tperson.birthplace_a_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN birthplace_a_e1 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.birthplace_a_e1';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			vmsg := 'begin.addcolumn.tperson.birthplace_a_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN birthplace_a_e2 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.birthplace_a_e2';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			vmsg := 'begin.addcolumn.tperson.birthplace_a_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
			IF $1 = 1 THEN
				ALTER TABLE veraweb.tperson ADD COLUMN birthplace_a_e3 varchar(100);
			END IF;
			vmsg := 'end.addcolumn.tperson.birthplace_a_e3';
			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		END IF;
		--------<COLUMNS/>

-- Provable future change: add birthplace attribute to partner
		--------<COLUMNS>
--		vint := 0;
--		SELECT count(*) INTO vint FROM information_schema.columns
--			WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'birthplace_b_e1';
--		IF vint = 0 THEN
--			vmsg := 'begin.addcolumn.tperson.birthplace_b_e1';
--			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
--			IF $1 = 1 THEN
--				ALTER TABLE veraweb.tperson ADD COLUMN birthplace_b_e1 varchar(100);
--			END IF;
--			vmsg := 'end.addcolumn.tperson.birthplace_b_e1';
--			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
--			vmsg := 'begin.addcolumn.tperson.birthplace_b_e2';
--			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
--			IF $1 = 1 THEN
--				ALTER TABLE veraweb.tperson ADD COLUMN birthplace_b_e2 varchar(100);
--			END IF;
--			vmsg := 'end.addcolumn.tperson.birthplace_b_e2';
--			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
--			vmsg := 'begin.addcolumn.tperson.birthplace_b_e3';
--			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
--			IF $1 = 1 THEN
--				ALTER TABLE veraweb.tperson ADD COLUMN birthplace_b_e3 varchar(100);
--			END IF;
--			vmsg := 'end.addcolumn.tperson.birthplace_b_e3';
--			INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
--		END IF;
		--------<COLUMNS/>

	END IF;

	--ALTER TABLE veraweb.tperson ALTER COLUMN gender SET NOT NULL;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tperson_categorie' AND column_name = 'fk_vera';
	IF vint > 0 THEN
		vmsg := 'begin.changecolumn.tperson.gender add not null';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			UPDATE veraweb.tperson SET gender = 'F' WHERE gender =  'W';
			UPDATE veraweb.tperson SET gender = 'M' WHERE gender <> 'F' OR gender IS NULL;
			ALTER TABLE veraweb.tperson ALTER COLUMN gender SET NOT NULL;
		END IF;
		vmsg := 'end.changecolumn.tperson.gender add not null';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tperson ALTER COLUMN gender_p SET NOT NULL;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tperson' AND column_name = 'gender_p' AND is_nullable = 'YES';
	IF vint > 0 THEN
		vmsg := 'begin.changecolumn.tperson.gender_p add not null';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			UPDATE veraweb.tperson SET gender_p = 'F' WHERE gender_p =  'W';
			UPDATE veraweb.tperson SET gender_p = 'M' WHERE gender_p <> 'F' OR gender_p IS NULL;
			ALTER TABLE veraweb.tperson ALTER COLUMN gender_p SET NOT NULL;
		END IF;
		vmsg := 'end.changecolumn.tperson.gender_p add not null';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'timport';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.timport';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.timport
			(
			  pk serial NOT NULL,
			  fk_orgunit int4,
			  created timestamptz,
			  createdby varchar(100),
			  changed timestamptz,
			  changedby varchar(100),
			  importsource varchar(250),
			  importformat varchar(250),
			  CONSTRAINT timport_pkey PRIMARY KEY (pk)
			)
			WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.timport';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	--DROP falls veraltete Variante erkannt an Spalte state_a_e1
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson' AND column_name = 'state_a_e1';
	IF vint = 0 THEN
		vmsg := 'begin.dropTABLE.timportperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			-- BUGFIX:
			-- CAN DROP ONLY IF EXISTS (PostgreSQL 8.2 added this feature)
			-- INSTALLATION CHOKES HERE IN NEW/VANILLA SETUPS...
			SELECT count(*) INTO vint FROM pg_class WHERE relname = 'timportperson';
			IF vint = 1 THEN
				vmsg := 'really dropping table timportperson - because table is present';
				DROP TABLE veraweb.timportperson CASCADE;
			ELSE
				vmsg := 'not dropping table timportperson - because table is not even present';
			END IF;
			-- BUGFIX... same procedure with the associated sequence...
			vint := 0;
			SELECT count(*) INTO vint FROM pg_class WHERE relname = 'timportperson_pk_seq';
			IF vint = 1 THEN
				vmsg := 'really dropping sequence timportperson_pk_seq - because sequence is present';
				DROP SEQUENCE veraweb.timportperson_pk_seq;
			ELSE
				vmsg := 'not dropping sequence timportperson_pk_seq - because sequence is not even present';
			END IF;
		END IF;
		vmsg := 'end.dropTABLE.timportperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	-- CREATE falls nicht vorhanden
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.timportperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.timportperson (
			  LIKE veraweb.tperson INCLUDING DEFAULTS,
			  fk_import int4,
			  fk_externalid int4,
			  duplicates varchar(500),
			  dupcheckaction int4,
			  dupcheckstatus int4,
			  category varchar,
			  occasion varchar,
			  textfield_1 text,
			  textfield_2 text,
			  textfield_3 text
			)
			WITH OIDS;
			ALTER TABLE veraweb.timportperson DROP COLUMN pk;
			CREATE SEQUENCE veraweb.timportperson_pk_seq;
			ALTER TABLE veraweb.timportperson ADD COLUMN pk int4;
			ALTER TABLE veraweb.timportperson ALTER COLUMN pk SET NOT NULL;
			ALTER TABLE veraweb.timportperson ALTER COLUMN pk SET DEFAULT nextval('veraweb.timportperson_pk_seq'::text);
			ALTER TABLE veraweb.timportperson ADD CONSTRAINT timportperson_pkey PRIMARY KEY (pk);
		END IF;
		vmsg := 'end.createTABLE.timportperson';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.timportperson ALTER category TYPE varchar;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson' AND column_name = 'category' AND character_maximum_length = 200;
	IF vint > 0 THEN
		vmsg := 'begin.changecolumn.timportperson.category alter type varchar';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			--ALTER TABLE veraweb.timportperson ALTER category TYPE varchar;
			UPDATE pg_attribute SET atttypmod=-1
			 WHERE attrelid in (SELECT oid from pg_class where relname='timportperson') AND attname='category';
		END IF;
		vmsg := 'end.changecolumn.timportperson.category alter type varchar';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	--------<COLUMN>
	--ALTER TABLE veraweb.timportperson ALTER occasion TYPE varchar;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson' AND column_name = 'occasion' AND character_maximum_length = 200;
	IF vint > 0 THEN
		vmsg := 'begin.changecolumn.timportperson.occasion alter type varchar';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			--ALTER TABLE veraweb.timportperson ALTER occasion TYPE varchar;
			UPDATE pg_attribute SET atttypmod=-1
			 WHERE attrelid in (SELECT oid from pg_class where relname='timportperson') AND attname='occasion';
		END IF;
		vmsg := 'end.changecolumn.timportperson.occasion alter type varchar';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson' AND column_name = 'state_a_e1';
	IF vint = 0 THEN
		vmsg := 'begin.dropTABLE.timportperson_categorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			-- CAN DROP ONLY IF EXISTS (PostgreSQL 8.2 added this feature)
			-- INSTALLATION CHOKES HERE IN NEW/VANILLA SETUPS...
			SELECT count(*) INTO vint FROM pg_class WHERE relname = 'timportperson_categorie';
			IF vint = 1 THEN
				vmsg := 'really dropping table timportperson_categorie - because table is present';
				DROP TABLE veraweb.timportperson_categorie CASCADE;
			ELSE
				vmsg := 'not dropping table timportperson_categorie - because table is not even present';
			END IF;
		END IF;
		vmsg := 'end.dropTABLE.timportperson_categorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson_categorie';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.timportperson_categorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.timportperson_categorie
			(
			  pk serial NOT NULL,
			  fk_importperson int4 NOT NULL,
			  catname varchar(200) NOT NULL,
			  flags int4 DEFAULT 0,
			  rank int4 DEFAULT 0,
			  CONSTRAINT timportperson_categorie_pkey PRIMARY KEY (pk)
			)
			WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.timportperson_categorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson' AND column_name = 'state_a_e1';
	IF vint = 0 THEN
		vmsg := 'begin.dropTABLE.timportperson_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			-- CAN DROP ONLY IF EXISTS (PostgreSQL 8.2 added this feature)
			-- INSTALLATION CHOKES HERE IN NEW/VANILLA SETUPS...
			SELECT count(*) INTO vint FROM pg_class WHERE relname = 'timportperson_doctype';
			IF vint = 1 THEN
				vmsg := 'really dropping table timportperson_doctype - because table is present';
				DROP TABLE veraweb.timportperson_doctype CASCADE;
			ELSE
				vmsg := 'not dropping table timportperson_doctype - because table is not even present';
			END IF;
		END IF;
		vmsg := 'end.dropTABLE.timportperson_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'timportperson_doctype';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.timportperson_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.timportperson_doctype
			(
			  pk serial NOT NULL,
			  fk_importperson int4 NOT NULL,
			  docname varchar(200) NOT NULL,
			  textfield text,
			  textfield_p text,
			  textjoin varchar(50),
			  CONSTRAINT timportperson_doctype_pkey PRIMARY KEY (pk)
			)
			WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.timportperson_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tperson_categorie';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tperson_categorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tperson_categorie
			(
			  pk serial NOT NULL,
			  fk_person int4 NOT NULL DEFAULT 0,
			  fk_categorie int4 NOT NULL DEFAULT 0,
			  rank int4 DEFAULT 0,
			  CONSTRAINT tperson_categorie_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tperson_categorie';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tperson_categorie DROP COLUMN fk_vera;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tperson_categorie' AND column_name = 'fk_vera';
	IF vint > 0 THEN
		vmsg := 'begin.dropcolumn.tperson_categorie.fk_vera';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tperson_categorie DROP COLUMN fk_vera;
		END IF;
		vmsg := 'end.dropcolumn.tperson_categorie.fk_vera';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tperson_doctype';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tperson_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tperson_doctype
			(
			  pk serial NOT NULL,
			  fk_person int4 NOT NULL DEFAULT 0,
			  fk_doctype int4 NOT NULL DEFAULT 0,
			  addresstype int4 NOT NULL DEFAULT 0,
			  locale int4 NOT NULL DEFAULT 0,
			  textfield text,
			  textfield_p text,
			  textjoin varchar(50),
			  CONSTRAINT tperson_doctype_pkey PRIMARY KEY (pk)
			) WITH OIDS;
			CREATE INDEX tperson_doctype_fk_person_index ON veraweb.tperson_doctype
			  USING btree (fk_person);
		END IF;
		vmsg := 'end.createTABLE.tperson_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tperson_mailinglist';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tperson_mailinglist';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tperson_mailinglist
			(
			  pk serial NOT NULL,
			  fk_person int4 NOT NULL DEFAULT 0,
			  fk_mailinglist int4 NOT NULL DEFAULT 0,
			  address varchar(250),
			  CONSTRAINT tperson_mailinglist_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tperson_mailinglist';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tsalutation';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tsalutation';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tsalutation
			(
			  pk serial NOT NULL,
			  salutation varchar(100) NOT NULL,
			  gender varchar(1),
			  CONSTRAINT tsalutation_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tsalutation';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tsalutation DROP COLUMN gender;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tsalutation' AND column_name = 'gender' AND udt_name = 'int4';
	IF vint > 0 THEN
		vmsg := 'begin.dropcolumn.tsalutation.gender';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tsalutation DROP COLUMN gender;
		END IF;
		vmsg := 'end.dropcolumn.tsalutation.gender';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tsalutation ADD COLUMN gender varchar(1);
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tsalutation' AND column_name = 'gender';
	IF vint = 0 THEN
		vmsg := 'begin.createcolumn.tsalutation.gender';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tsalutation ADD COLUMN gender varchar(1);
		END IF;
		vmsg := 'end.createcolumn.tsalutation.gender';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;

	--ALTER TABLE veraweb.tsalutation ALTER COLUMN gender DROP NOT NULL;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tsalutation' AND column_name = 'gender' AND is_nullable = 'NO';
	IF vint > 0 THEN
		vmsg := 'begin.changecolumn.tsalutation.gender drop not null';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			UPDATE veraweb.tsalutation SET gender = 'F' WHERE gender =  'W';
			UPDATE veraweb.tsalutation SET gender = 'M' WHERE gender <> 'M' OR gender IS NULL;
			ALTER TABLE veraweb.tsalutation ALTER COLUMN gender DROP NOT NULL;
		END IF;
		vmsg := 'end.changecolumn.tsalutation.gender drop not null';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tsalutation_doctype';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tsalutation_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tsalutation_doctype
			(
			  pk serial NOT NULL,
			  fk_salutation int4 NOT NULL DEFAULT 0,
			  fk_doctype int4 NOT NULL DEFAULT 0,
			  salutation varchar(100),
			  CONSTRAINT tsalutation_doctype_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tsalutation_doctype';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--ALTER TABLE veraweb.tsalutation_doctype RENAME fk_event TO fk_salutation;
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.columns
		WHERE table_schema = 'veraweb' AND table_name = 'tsalutation_doctype' AND column_name = 'fk_event';
	IF vint > 0 THEN
		vmsg := 'begin.renamecolumn.tsalutation_doctype.fk_event to fk_salutation';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			ALTER TABLE veraweb.tsalutation_doctype RENAME fk_event TO fk_salutation;
		END IF;
		vmsg := 'end.renamecolumn.tsalutation_doctype.fk_event to fk_salutation';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tuser';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tuser';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tuser
			(
			  pk serial NOT NULL,
			  fk_orgunit int4 DEFAULT 0,
			  username varchar(100) NOT NULL,
			  role int4,
			  CONSTRAINT tuser_pkey PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tuser';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tuser_config';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tuser_config';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tuser_config
			(
			  pk serial NOT NULL,
			  fk_user int4 NOT NULL,
			  name varchar(100) NOT NULL,
			  value varchar(300) NOT NULL,
			  PRIMARY KEY (pk)
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tuser_config';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tproxy';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tproxy';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tproxy
			(
			  pk serial NOT NULL,
			  fk_user int4 NOT NULL,
			  proxy varchar(100) NOT NULL,
			  validfrom timestamptz,
			  validtill timestamptz,
			  CONSTRAINT tproxy_pkey PRIMARY KEY (pk),
			  CONSTRAINT tproxy_fkey_user FOREIGN KEY (fk_user) REFERENCES veraweb.tuser (pk) ON UPDATE RESTRICT ON DELETE RESTRICT
			) WITH OIDS;
		END IF;
		vmsg := 'end.createTABLE.tproxy';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	-------- added table as per the change request for the next version 1.2.0
	-------- cklein 2008-02-12
	---------------------------<TABLE>
	vint := 0;
	SELECT count(*) INTO vint FROM information_schema.tables
		WHERE table_schema = 'veraweb' AND table_name = 'tchangelog';
	IF vint = 0 THEN
		vmsg := 'begin.createTABLE.tchangelog';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE TABLE veraweb.tchangelog
			(
			  pk serial NOT NULL,
			  username varchar(100) NOT NULL,
			  objname varchar(300) NOT NULL,	-- the name of the object (tperson:firstname/lastname,tevent:name,tguest:/tperson:firstname/lastname)
			  objtype varchar(250) NOT NULL,	-- the type of the object (fully-qualified-classname)
			  objid int4 NOT NULL,				-- the id of the object
			  op varchar(6) NOT NULL,			-- the operation logged, one of delete, insert, or update
			  attributes text NOT NULL,			-- a list of comma separated attribute names
			  date timestamptz,
			  CONSTRAINT tchangelog_pkey PRIMARY KEY (pk)
			) WITH OIDS;
			CREATE INDEX tchangelog_date_index ON veraweb.tchangelog
			  USING btree (date);
			CREATE INDEX tchangelog_username_index ON veraweb.tchangelog
			  USING btree (username);
		END IF;
		vmsg := 'end.createTABLE.tchangelog';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	ELSE
	--------<COLUMN>
	--------<COLUMN/>
	END IF;
	---------------------------</TABLE>

	---------------------------</TABLES>

	---------------------------<VIEWS>

	------------------<VIEW>
	-- vint := 0;
	-- SELECT count(*) INTO vint FROM information_schema.views
	--	WHERE table_schema = 'veraweb' AND table_name = 'v_user_folder';
	-- IF vint = 0 THEN
	--	vmsg := 'begin.createview.v_user_folder';
	--	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	--	IF $1 = 1 THEN
	--		CREATE OR REPLACE VIEW veraweb.v_user_folder AS
	--		SELECT tgroup_user.fk_user AS userid, tgroup_folder.fk_folder
	--		FROM veraweb.tfolderrole, veraweb.tgroup_folder, veraweb.tgroup_user
	--		WHERE tgroup_folder.fk_folderrole = tfolderrole.pk
	--		AND tgroup_user.fk_group = tgroup_folder.fk_group
	--		AND tfolderrole.auth_read = 1;
	--	END IF;
	--	vmsg := 'end.createview.v_user_folder';
	--	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	-- ELSE
	--	vmsg := 'begin.drop_and_createview.v_user_folder';
	--	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	--	IF $1 = 1 THEN
	--
	--		DROP VIEW veraweb.v_user_folder CASCADE;
	--
	--		CREATE OR REPLACE VIEW veraweb.v_user_folder AS
	--		SELECT tgroup_user.fk_user AS userid, tgroup_folder.fk_folder
	--		FROM veraweb.tfolderrole, veraweb.tgroup_folder, veraweb.tgroup_user
	--		WHERE tgroup_folder.fk_folderrole = tfolderrole.pk
	--		AND tgroup_user.fk_group = tgroup_folder.fk_group
	--		AND tfolderrole.auth_read = 1;
	--	END IF;
	--	vmsg := 'end.drop_and_createview.v_user_folder';
	--	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	-- END IF;
	------------------</VIEW>

	---------------------------</VIEWS>

	---------------------------<INDEXES>

	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_bothnames_a_e1_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_bothnames_a_e1_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_bothnames_a_e1_index ON veraweb.tperson
			  USING btree (lastname_a_e1, firstname_a_e1);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_firstname_a_e1_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_firstname_a_e1_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_firstname_a_e1_index ON veraweb.tperson
			  USING btree (firstname_a_e1);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_lastname_a_e1_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_lastname_a_e1_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_lastname_a_e1_index ON veraweb.tperson
			  USING btree (lastname_a_e1);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_firstname_a_e2_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_firstname_a_e2_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_firstname_a_e2_index ON veraweb.tperson
			  USING btree (firstname_a_e2);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_lastname_a_e2_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_lastname_a_e2_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_lastname_a_e2_index ON veraweb.tperson
			  USING btree (lastname_a_e2);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_firstname_a_e3_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_firstname_a_e3_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_firstname_a_e3_index ON veraweb.tperson
			  USING btree (firstname_a_e3);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_lastname_a_e3_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_lastname_a_e3_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_lastname_a_e3_index ON veraweb.tperson
			  USING btree (lastname_a_e3);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_firstname_b_e1_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_firstname_b_e1_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_firstname_b_e1_index ON veraweb.tperson
			  USING btree (firstname_b_e1);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_lastname_b_e1_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_lastname_b_e1_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_lastname_b_e1_index ON veraweb.tperson
			  USING btree (lastname_b_e1);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_firstname_b_e2_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_firstname_b_e2_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_firstname_b_e2_index ON veraweb.tperson
			  USING btree (firstname_b_e2);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_lastname_b_e2_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_lastname_b_e2_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_lastname_b_e2_index ON veraweb.tperson
			  USING btree (lastname_b_e2);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_firstname_b_e3_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_firstname_b_e3_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_firstname_b_e3_index ON veraweb.tperson
			  USING btree (firstname_b_e3);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_lastname_b_e3_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_lastname_b_e3_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_lastname_b_e3_index ON veraweb.tperson
			  USING btree (lastname_b_e3);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tperson_doctype_fk_person_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tperson_doctype_fk_person_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tperson_doctype_fk_person_index ON veraweb.tperson_doctype
			  USING btree (fk_person);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tguest_fk_person_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tguest_fk_person_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tguest_fk_person_index ON veraweb.tguest
			  USING btree (fk_person);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tguest_fk_event_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tguest_fk_event_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tguest_fk_event_index ON veraweb.tguest
			  USING btree (fk_event);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tguest_fk_category_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tguest_fk_category_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tguest_fk_category_index ON veraweb.tguest
			  USING btree (fk_category);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tguest_doctype_fk_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tguest_doctype_fk_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tguest_doctype_fk_index ON veraweb.tguest_doctype
			  USING btree (fk_guest, fk_doctype);
		END IF;
	END IF;
	---------------------------</INDEX>
	---------------------------<INDEX>
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_indexes WHERE
		schemaname = 'veraweb' AND indexname = 'tguest_doctype_fk_guest_index';
	IF vint = 0 THEN
		vmsg := 'createindex.tguest_doctype_fk_guest_index';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE INDEX tguest_doctype_fk_guest_index ON veraweb.tguest_doctype
			  USING btree (fk_guest);
		END IF;
	END IF;
	---------------------------</INDEX>

	---------------------------</INDEXES>

	---------------------------<SEQUENCES>

	---------------------------<SEQUENCE>
	--CREATE SEQUENCE veraweb.import_id_seq...
	vint := 0;
	SELECT count(*) INTO vint FROM pg_catalog.pg_statio_user_sequences
		WHERE schemaname = 'veraweb' AND relname = 'import_id_seq';
	IF vint = 0 THEN
		vmsg := 'begin.createsequence.import_id_seq';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
		IF $1 = 1 THEN
			CREATE SEQUENCE veraweb.import_id_seq
			  INCREMENT 1
			  MINVALUE 1
			  MAXVALUE 9223372036854775807
			  START 1000
			  CACHE 1;
		END IF;
		vmsg := 'end.createsequence.import_id_seq';
		INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);
	END IF;
	---------------------------</SEQUENCE>

	---------------------------</SEQUENCES>

	-- Update veraweb.tconfig: SCHEMA_VERSION = vversion
	IF $1 = 1 THEN
		vmsg := 'end.SCHEMA UPDATE TO VERSION ' || vversion;
		vint := 0;
		SELECT count(*) INTO vint FROM veraweb.tconfig WHERE cname = 'SCHEMA_VERSION';
		IF vint = 0 THEN
			INSERT INTO veraweb.tconfig (cvalue, cname) VALUES (vversion, 'SCHEMA_VERSION');
		ELSE
			UPDATE veraweb.tconfig SET cvalue = vversion WHERE cname = 'SCHEMA_VERSION';
		END IF;
	ELSE
		vmsg := 'end.SCHEMA CHECK TO VERSION (NO ACTION) ' || vversion;
	END IF;
	INSERT INTO veraweb.tupdate(date, value) VALUES (vdate, vmsg);

	vmsg := vdate;

	RETURN vmsg;

END;
$serv_verawebschema$
  LANGUAGE 'plpgsql' VOLATILE;
