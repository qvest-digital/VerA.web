/* ---------------------------------------------------------------------- */
/* Create table "tevent_functions                                         */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tevent_function (
  pk serial NOT NULL,
  fk_event int4 NOT NULL DEFAULT 0,
  fk_function int4 NOT NULL DEFAULT 0,
  CONSTRAINT tevent_doctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2015-04-22' WHERE cname = 'SCHEMA_VERSION';
