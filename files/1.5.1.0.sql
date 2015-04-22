/* ---------------------------------------------------------------------- */
/* Alter table "tdoctype"                                                 */
/* ---------------------------------------------------------------------- */

CREATE OR REPLACE FUNCTION veraweb.umlaut_fix(character varying) RETURNS character varying AS $BODY$
    BEGIN
        IF
        ($1 LIKE '%ä%') THEN RETURN replace($1,'ä','ae');
        END IF;
        IF
        ($1 LIKE '%ö%') THEN RETURN replace($1,'ö','oe');
        END IF;
        IF
        ($1 LIKE '%ü%') THEN RETURN replace($1,'ü','ue');
        END IF;
        IF
        ($1 LIKE '%ß%') THEN RETURN replace($1,'ß','ss');
        END IF;
        RETURN $1;
    END;
$BODY$ LANGUAGE plpgsql;

ALTER FUNCTION veraweb.umlaut_fix(character varying) OWNER TO veraweb;


/* ---------------------------------------------------------------------- */
/* Update schema version                                                  */
/* ---------------------------------------------------------------------- */

UPDATE veraweb.tconfig SET cvalue = '2015-02-24' WHERE cname = 'SCHEMA_VERSION';
