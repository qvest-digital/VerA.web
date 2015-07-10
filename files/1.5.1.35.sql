-- New flag to allow events without login
ALTER TABLE veraweb.tevent ADD COLUMN maxwaitlist int4 DEFAULT 0;

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-06-future' WHERE cname = 'SCHEMA_VERSION';