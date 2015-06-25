-- Create new table for storing field types
CREATE TABLE veraweb.toptional_field_type (
  pk serial NOT NULL,
  description text,

  CONSTRAINT toptional_field_type_pk PRIMARY KEY (pk)
) WITH OIDS;

-- Inserting data for types

INSERT INTO veraweb.toptional_field_type values (1, 'Eingabefeld');
INSERT INTO veraweb.toptional_field_type values (2, 'Einfaches Auswahlfeld');
INSERT INTO veraweb.toptional_field_type values (3, 'Mehrfaches Auswahlfeld');

-- Modify toptional_fields to allow field types

ALTER TABLE veraweb.toptional_fields ADD COLUMN fk_type INTEGER REFERENCES veraweb.toptional_field_type(pk);

-- Create new table for storing field type's content
CREATE SEQUENCE veraweb.toptional_field_type_content_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE veraweb.toptional_field_type_content (
  pk INTEGER DEFAULT nextval('veraweb.toptional_field_type_content_seq') NOT NULL,
  fk_optional_field INTEGER NOT NULL REFERENCES veraweb.toptional_fields(pk) ON DELETE CASCADE,
  content text NOT NULL,

  CONSTRAINT toptional_field_type_content_pkey PRIMARY KEY (pk)
);

-- Update the orgunit for default categories

UPDATE tcategorie set fk_orgunit=-1 where fk_orgunit IS NULL;

-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-05-20' WHERE cname = 'SCHEMA_VERSION';
