-- New flag to allow events without login
ALTER TABLE veraweb.tevent ADD COLUMN maxreserve int4 DEFAULT 0;

-- New column to identify Guest-Photo
ALTER TABLE veraweb.tguest ADD COLUMN image_uuid character varying(100);

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-07-10' WHERE cname = 'SCHEMA_VERSION';
