--
-- $Id$
--
-- FORMAT: UTF-8
--
-- Dieses Script ändert den Besitzer einer VerA.Web Datenbank auf "veraweb".
-- Es wird erwartet das die Datenbank ebenfalls "veraweb" heißt,
-- sollte dies nicht der Fall sein beachten Sie bitte folgenden Hinweis.
--

--
-- HINWEIS zum umbennen einer Datenbank:
--
--   Um eine Datenbank vollständig umzubennen (dabei werden auch weitere
--   Schemas übertragen) darf keine Verbindung zu dieser Datenbank bestehen.
--   VerA.Web sollte dafür herruntergefahren werden. Sollten Verbindungen
--   anderer Anwendungen weiterhin zu dieser Datenbank bestehen empfielt es
--   sich den Postgres Service neu zu starten (restart).
--
--   Anschließend können sie als superadmin die Datenbank umbennen.
--   Bitte wechseln sie nach dem umbennen der Datenbank in diese
--   und führen sie dort dieses Script aus.
--
--     ALTER Database xmanage RENAME TO veraweb;
--     GRANT ALL ON SCHEMA veraweb TO veraweb;
--

SET client_encoding = 'UNICODE';

-- Dieser Abschnitt ändert den Besitzer der Datenbank "veraweb".

UPDATE pg_database SET datdba =
  (SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
  WHERE datname = 'veraweb';

-- Dieser Abschnitt ändert den Besitzer des Schemas "veraweb".
--

UPDATE pg_namespace SET nspowner =
  (SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
  WHERE nspname = 'veraweb';

UPDATE pg_proc SET proowner =
  (SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
  WHERE pronamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'veraweb');

UPDATE pg_class SET relowner =
  (SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
  WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'veraweb');

UPDATE pg_type SET typowner =
  (SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
  WHERE typnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'veraweb');

UPDATE pg_proc SET proowner =
  (SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
  WHERE proname = 'serv_verawebschema';

--UPDATE pg_namespace SET nspowner = (
--  SELECT usesysid FROM pg_user WHERE usename = 'veraweb')
--  WHERE nspname = 'public';



-- Dieser Abschnitt ändert den Besitzer alle VerA.Web - Tabellen der
-- aktuellen Datenbank. Bitte kontrollieren sie nach dem umbennen
-- ob sie sich auf der richtigen (neuen) Datenbanken befinden.

ALTER TABLE veraweb.tcategorie OWNER TO veraweb;
ALTER TABLE veraweb.tcolor OWNER TO veraweb;
ALTER TABLE veraweb.tconfig OWNER TO veraweb;
ALTER TABLE veraweb.tdoctype OWNER TO veraweb;
ALTER TABLE veraweb.tevent OWNER TO veraweb;
ALTER TABLE veraweb.tevent_doctype OWNER TO veraweb;
ALTER TABLE veraweb.tfunction OWNER TO veraweb;
ALTER TABLE veraweb.tguest OWNER TO veraweb;
ALTER TABLE veraweb.tguest_doctype OWNER TO veraweb;
ALTER TABLE veraweb.timport OWNER TO veraweb;
ALTER TABLE veraweb.timportperson_categorie OWNER TO veraweb;
ALTER TABLE veraweb.timportperson_doctype OWNER TO veraweb;
ALTER TABLE veraweb.tlocation OWNER TO veraweb;
ALTER TABLE veraweb.tmaildraft OWNER TO veraweb;
ALTER TABLE veraweb.tmailinglist OWNER TO veraweb;
ALTER TABLE veraweb.tmailoutbox OWNER TO veraweb;
ALTER TABLE veraweb.torgunit OWNER TO veraweb;
ALTER TABLE veraweb.tperson OWNER TO veraweb;
ALTER TABLE veraweb.tperson_categorie OWNER TO veraweb;
ALTER TABLE veraweb.tperson_doctype OWNER TO veraweb;
ALTER TABLE veraweb.tperson_mailinglist OWNER TO veraweb;
ALTER TABLE veraweb.tproxy OWNER TO veraweb;
ALTER TABLE veraweb.tresult OWNER TO veraweb;
ALTER TABLE veraweb.tsalutation OWNER TO veraweb;
ALTER TABLE veraweb.tsalutation_doctype OWNER TO veraweb;
ALTER TABLE veraweb.tupdate OWNER TO veraweb;
ALTER TABLE veraweb.tuser OWNER TO veraweb;
ALTER TABLE veraweb.tuser_config OWNER TO veraweb;
