--
-- WARNING! 1.5.0.4 and 1.5.1.0 have the same SCHEMA_VERSION!
--

/* ---------------------------------------------------------------------- */
/* Alter table "tdoctype"						  */
/* ---------------------------------------------------------------------- */

ALTER TABLE veraweb.tdoctype ADD CONSTRAINT docname_unique UNIQUE(docname);

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2015-02-24' WHERE cname = 'SCHEMA_VERSION';
