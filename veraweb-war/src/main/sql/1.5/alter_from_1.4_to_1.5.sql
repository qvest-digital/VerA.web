/* ---------------------------------------------------------------------- */
/* Alter table "tevent"						                              */
/* ---------------------------------------------------------------------- */

alter table tevent add eventtype varchar(100);
alter table tevent add column mediarepresentatives varchar(1) DEFAULT 'f'::character varying;

/* ---------------------------------------------------------------------- */
/* Alter table "tguest"                                                   */
/* ---------------------------------------------------------------------- */

alter table tguest add column delegation varchar(255);
alter table tguest add column osiam_login varchar(255);

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
        FOR EACH ROW EXECUTE PROCEDURE test();
        
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
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2014-11-17' WHERE cname = 'SCHEMA_VERSION';
