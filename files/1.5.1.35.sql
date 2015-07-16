-- New flag to allow events without login
ALTER TABLE veraweb.tevent ADD COLUMN maxreserve int4 DEFAULT 0;

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-07-xx' WHERE cname = 'SCHEMA_VERSION';