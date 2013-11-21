/* ---------------------------------------------------------------------- */
/* Add table "ttask" with related sequence                                */
/* ---------------------------------------------------------------------- */

CREATE SEQUENCE veraweb.ttask_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE veraweb.ttask (
	pk INTEGER DEFAULT nextval('ttask_pk_seq') NOT NULL,
	title VARCHAR(100) NOT NULL,
	description VARCHAR(1000),
	startdate TIMESTAMP WITH TIME ZONE,
	enddate TIMESTAMP WITH TIME ZONE,
	degree_of_completion INTEGER DEFAULT 0,
	priority INTEGER,
	fk_event INTEGER NOT NULL REFERENCES tevent (pk),
	fk_person INTEGER REFERENCES tperson (pk),
	createdby VARCHAR(50),
	changedby VARCHAR(50),
	created TIMESTAMP WITH TIME ZONE,
	changed TIMESTAMP WITH TIME ZONE,
	CONSTRAINT ttask_pkey PRIMARY KEY (pk)
) WITH OIDS;


/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2013-04-12' WHERE cname = 'SCHEMA_VERSION';