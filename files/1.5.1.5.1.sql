-- Remove foreign key constraints (Hotfix)

ALTER TABLE veraweb.tcategorie DROP CONSTRAINT mandant_categ_unique;

-- Update schema version

UPDATE veraweb.tconfig SET cvalue = '2015-04-14' WHERE cname = 'SCHEMA_VERSION';
