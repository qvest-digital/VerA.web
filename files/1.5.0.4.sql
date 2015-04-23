/* ---------------------------------------------------------------------- */
/* Create table "tevent_function"                                         */
/* ---------------------------------------------------------------------- */

ALTER TABLE veraweb.tdoctype ADD CONSTRAINT docname_unique UNIQUE(docname);

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2015-02-24' WHERE cname = 'SCHEMA_VERSION';
