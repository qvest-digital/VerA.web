-- Alter table "tcategorie"

ALTER TABLE veraweb.tcategorie ADD CONSTRAINT mandant_categ_unique UNIQUE(fk_orgunit,catname);

-- Update schema version

UPDATE veraweb.tconfig SET cvalue = '2015-03-12' WHERE cname = 'SCHEMA_VERSION';
