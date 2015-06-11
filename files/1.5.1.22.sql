-- New flag to allow events without login
ALTER TABLE veraweb.tevent ADD COLUMN login_required int4 DEFAULT 0;
-- New UUID to identify created users to registration
ALTER TABLE veraweb.tguest ADD COLUMN login_required_uuid CHARACTER VARYING(100);

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-06-11' WHERE cname = 'SCHEMA_VERSION';