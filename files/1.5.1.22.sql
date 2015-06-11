-- New flag to allow events without login
ALTER TABLE veraweb.tevent ADD COLUMN login_required BOOLEAN;

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-06-11' WHERE cname = 'SCHEMA_VERSION';