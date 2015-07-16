-- New column to allow max number of reserves
ALTER TABLE veraweb.tevent ADD COLUMN maxreserve int4 DEFAULT 0;

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-07-10' WHERE cname = 'SCHEMA_VERSION';
