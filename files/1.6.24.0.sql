-- New column to identify Guest-Photo
ALTER TABLE veraweb.tguest ADD COLUMN image_uuid character varying(100);

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-06-29' WHERE cname = 'SCHEMA_VERSION';