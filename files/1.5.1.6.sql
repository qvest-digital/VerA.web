-- Restore mandant_categ_unique constraint removed in 1.5.1.5.1

ALTER TABLE veraweb.tcategorie ADD CONSTRAINT mandant_categ_unique UNIQUE(fk_orgunit,catname);

-- Update schema version

UPDATE veraweb.tconfig SET cvalue = '2015-04-15' WHERE cname = 'SCHEMA_VERSION';
