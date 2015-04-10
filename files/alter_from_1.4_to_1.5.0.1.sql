/* ---------------------------------------------------------------------- */
/* Alter table "tevent"                                                   */
/* ---------------------------------------------------------------------- */

alter table veraweb.tevent add eventtype varchar(100);
alter table veraweb.tevent add column mediarepresentatives varchar(100);

/* ---------------------------------------------------------------------- */
/* Alter table "tguest"                                                   */
/* ---------------------------------------------------------------------- */

alter table veraweb.tguest add column delegation varchar(255);
alter table veraweb.tguest add column osiam_login varchar(255);

/* ---------------------------------------------------------------------- */
/* Trigger for link mandant with categories                               */
/* ---------------------------------------------------------------------- */
CREATE FUNCTION linkOrgUnitWithCategorie()
        RETURNS TRIGGER AS $BODY$
        BEGIN
                INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NEW.PK, 'Pressevertreter', NULL, NULL);
                RETURN NEW;
        END;
        $BODY$ LANGUAGE plpgsql;

CREATE TRIGGER createCategorieOnUnitInsert
        AFTER INSERT ON veraweb.torgunit
        FOR EACH ROW EXECUTE PROCEDURE linkOrgUnitWithCategorie();

/* ---------------------------------------------------------------------- */
/* Mirgate old OrgUnits                                                   */
/* ---------------------------------------------------------------------- */
CREATE FUNCTION migrateOrgUnits() RETURNS integer AS $BODY$
	DECLARE
	    unLinked RECORD;
	BEGIN

	FOR unLinked IN SELECT pk FROM veraweb.torgunit WHERE pk NOT IN( SELECT fk_orgunit FROM veraweb.tcategorie ) LOOP

        execute format('INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, %s, %s, NULL, NULL);', unlinked, quote_literal(E'Pressevertreter'));

    END LOOP;

    RETURN 1;
END;
$BODY$ LANGUAGE plpgsql;

SELECT migrateOrgUnits();
DROP FUNCTION migrateOrgUnits();

/* ---------------------------------------------------------------------- */
/* Drop tables                                                            */
/* ---------------------------------------------------------------------- */
drop table veraweb.tdelegations;
drop table veraweb.tdelegation_fields;

/* ---------------------------------------------------------------------- */
/* Create new tables for the optional delegation fields                   */
/* ---------------------------------------------------------------------- */
CREATE TABLE veraweb.toptional_fields (
	pk serial NOT NULL,
	fk_event serial NOT NULL,
	label text,

	FOREIGN KEY (fk_event) REFERENCES veraweb.tevent(pk),
	PRIMARY KEY (pk)
);

CREATE TABLE veraweb.toptional_fields_delegation_content (
	fk_guest serial NOT NULL,
	fk_delegation_field serial NOT NULL,
	value text,

	FOREIGN KEY (fk_guest) REFERENCES veraweb.tguest(pk),
	FOREIGN KEY (fk_delegation_field) REFERENCES veraweb.toptional_fields(pk),
	PRIMARY KEY (fk_guest, fk_delegation_field)
);

/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2014-11-17' WHERE cname = 'SCHEMA_VERSION';
