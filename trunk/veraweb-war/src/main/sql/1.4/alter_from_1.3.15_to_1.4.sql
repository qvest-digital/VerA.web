/* ---------------------------------------------------------------------- */
/* Add table "ttask" with related sequence                                */
/* ---------------------------------------------------------------------- */

CREATE SEQUENCE ttask_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

ALTER TABLE ttask_pk_seq
  OWNER TO veraweb;

CREATE TABLE veraweb.ttask (
	pk INTEGER DEFAULT nextval('ttask_pk_seq') NOT NULL,
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

/* ---------------------------------------------------------------------- */
/* Alter table "tlocation"                                                */
/* ---------------------------------------------------------------------- */

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
	

/* ---------------------------------------------------------------------- */
/* Alter table "tevent"                                                   */
/* ---------------------------------------------------------------------- */

ALTER TABLE veraweb.tevent DROP COLUMN location;
ALTER TABLE veraweb.tevent ADD COLUMN fk_location INTEGER REFERENCES veraweb.tlocation(pk);

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2013-06-12' WHERE cname = 'SCHEMA_VERSION';
