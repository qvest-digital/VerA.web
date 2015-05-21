-- Create new table for storing field types
CREATE TABLE veraweb.toptional_field_type (
  pk serial NOT NULL,
  description text,

  CONSTRAINT toptional_field_type_pk PRIMARY KEY (pk)
) WITH OIDS;

-- Inserting data for types

INSERT INTO veraweb.toptional_field_type values (1, 'Eingabefeld');
INSERT INTO veraweb.toptional_field_type values (2, 'Einfaches Auswahfeld');
INSERT INTO veraweb.toptional_field_type values (3, 'Mehrfaches Auswahlfeld');

-- Modify toptional_fields to allow field types

ALTER TABLE veraweb.toptional_fields ADD COLUMN fk_type INTEGER REFERENCES veraweb.toptional_field_type(pk);

-- Create new table for storing field type's content
CREATE SEQUENCE veraweb.toptional_field_type_content_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

CREATE TABLE veraweb.toptional_field_type_content (
  pk INTEGER DEFAULT nextval('toptional_field_type_content_seq') NOT NULL,
  fk_optional_field INTEGER NOT NULL REFERENCES veraweb.toptional_fields(pk),
  content text NOT NULL,

  CONSTRAINT toptional_field_type_content_pkey PRIMARY KEY (pk)
);

-- New column to store foreign key from toptional_field_type_content
ALTER TABLE veraweb.toptional_fields_delegation_content ADD COLUMN fk_type_content INTEGER REFERENCES veraweb.toptional_field_type_content(pk);

-- Migration function to allow new DB model for Optional Fields Types
CREATE OR REPLACE FUNCTION veraweb.migrate_optional_fields() RETURNS character varying AS $BODY$
    DECLARE 
        query varchar;
        pk_type_content varchar;
        delegation_content RECORD;

    BEGIN
        EXECUTE 'UPDATE veraweb.toptional_fields SET fk_type=1';

        FOR delegation_content IN SELECT * FROM veraweb.toptional_fields_delegation_content LOOP
            EXECUTE 'INSERT INTO veraweb.toptional_field_type_content (content, fk_optional_field)
            values (''' || delegation_content.value || ''','|| delegation_content.fk_delegation_field||')';

            SELECT pk INTO pk_type_content FROM veraweb.toptional_field_type_content ORDER BY pk DESC LIMIT 1;

            EXECUTE 'UPDATE veraweb.toptional_fields_delegation_content SET fk_type_content='''|| pk_type_content ||'''
            WHERE fk_guest='|| delegation_content.fk_guest ||' AND fk_delegation_field='|| delegation_content.fk_delegation_field ||'';
        END LOOP;

        RETURN 1;
    END;
$BODY$ LANGUAGE plpgsql;

-- Call the migration script
select veraweb.migrate_optional_fields();

-- Delete old value column
alter table veraweb.toptional_fields_delegation_content DROP COLUMN value;

-- ON DELETE CASCADE Between toptional_field_type_content and toptional_fields_delegation_content
alter table veraweb.toptional_fields_delegation_content drop constraint toptional_fields_delegation_content_fk_type_content_fkey,
 add constraint toptional_fields_delegation_content_fk_type_content_fkey 
 foreign key (fk_type_content) references toptional_field_type_content(pk) on delete cascade;
 
-- Update schema version
UPDATE veraweb.tconfig SET cvalue = '2015-05-20' WHERE cname = 'SCHEMA_VERSION';

