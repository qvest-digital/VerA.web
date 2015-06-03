-- Dropping old Primary Key constraint
ALTER TABLE veraweb.toptional_fields_delegation_content DROP CONSTRAINT toptional_fields_delegation_content_pkey;

-- Create new sequence to allow unique PK in toptional_fields_delegation_content
CREATE SEQUENCE veraweb.toptional_fields_delegation_content_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

-- Including new PK column to work with unique values
ALTER TABLE veraweb.toptional_fields_delegation_content ADD COLUMN pk INTEGER DEFAULT nextval('toptional_fields_delegation_content_seq') NOT NULL;
ALTER TABLE veraweb.toptional_fields_delegation_content ADD CONSTRAINT toptional_fields_delegation_content_pkey PRIMARY KEY (pk);

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-06-02' WHERE cname = 'SCHEMA_VERSION';
