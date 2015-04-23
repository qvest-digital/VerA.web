-- Alter table "tevent"

alter table veraweb.tevent add column hash varchar(100);

-- Update schema version

UPDATE veraweb.tconfig SET cvalue = '2015-01-26' WHERE cname = 'SCHEMA_VERSION';
