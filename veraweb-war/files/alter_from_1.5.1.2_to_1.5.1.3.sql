/* ---------------------------------------------------------------------- */
/* Create sequence "link_uuid_pk_seq"			                          */
/* ---------------------------------------------------------------------- */
CREATE SEQUENCE veraweb.link_uuid_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

/* ---------------------------------------------------------------------- */
/* Create table "link_uuid"						                          */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.link_uuid (
    pk INTEGER DEFAULT nextval('link_uuid_pk_seq') NOT NULL,
    uuid character varying(100),
    linktype character varying(100),
    personid INTEGER DEFAULT 0,
    CONSTRAINT link_uuid_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Foreign key constraints                                                */
/* ---------------------------------------------------------------------- */

ALTER TABLE veraweb.link_uuid ADD CONSTRAINT link_uuid_fkey_person
    FOREIGN KEY (personid) REFERENCES veraweb.tperson (pk) ON DELETE RESTRICT ON UPDATE RESTRICT;

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2015-03-24' WHERE cname = 'SCHEMA_VERSION';
