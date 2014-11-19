/* ---------------------------------------------------------------------- */
/* Add column "eventtype" to mark public events                           */
/* ---------------------------------------------------------------------- */

alter table tevent add eventtype varchar(100);

/* ---------------------------------------------------------------------- */
/* Alter table "tguest"                                                   */
/* ---------------------------------------------------------------------- */
alter table tguest add column delegation varchar(255);
alter table tguest add column companyname varchar(255);

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2014-11-17' WHERE cname = 'SCHEMA_VERSION';