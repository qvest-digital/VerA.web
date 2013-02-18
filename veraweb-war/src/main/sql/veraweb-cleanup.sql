--
-- $Id$
--
-- FORMAT: UTF-8
--
-- Update Script fuer das VerA.Web - Schema.
--
-- Kann auf eine beliebige Version des Schemas
-- oder eine leere Datenbank angewendet werden.
--
-- Die Datenbank innerhalb der dieses Script ausgefuehrt wird,
-- muss dem Benutzer entsprechen der es ausfuehrt.
--

SET client_encoding = 'UNICODE';

-- Aktuallisiert die Sequenzen
SELECT veraweb.serv_build_sequences();

-- Loescht fehlerhafte Event-Dokumenttypen
DELETE FROM veraweb.tevent_doctype WHERE
  (fk_event NOT IN (SELECT pk FROM veraweb.tevent));

-- Loescht fehlerhafte Gaeste
DELETE FROM veraweb.tguest WHERE
  (fk_person IS NOT NULL AND fk_person NOT IN (SELECT pk FROM veraweb.tperson)) OR
  (fk_event NOT IN (SELECT pk FROM veraweb.tevent));
DELETE FROM veraweb.tguest_doctype WHERE
  (fk_guest NOT IN (SELECT pk FROM veraweb.tguest)) OR
  (fk_doctype NOT IN (SELECT pk FROM veraweb.tdoctype));

-- Loescht doppelte Gaeste
DELETE FROM veraweb.tguest WHERE pk IN (
  SELECT g1.pk FROM veraweb.tguest g1
  JOIN veraweb.tguest g2 ON (g1.fk_person = g2.fk_person AND g1.fk_event = g2.fk_event)
  WHERE g1.pk > g2.pk);

-- Loescht fehlerhafte Personen
DELETE FROM veraweb.tperson_categorie WHERE
  (fk_person NOT IN (SELECT pk FROM veraweb.tperson));
DELETE FROM veraweb.tperson_doctype WHERE
  (fk_person NOT IN (SELECT pk FROM veraweb.tperson));
DELETE FROM veraweb.tperson_mailinglist WHERE
  (fk_person NOT IN (SELECT pk FROM veraweb.tperson));

-- Loescht nicht benoetigte Farbeintraege
DELETE FROM veraweb.tcolor WHERE pk NOT IN (1, 2, 3, 4);

-- Updated Gastgeber Status von Gaesten
UPDATE veraweb.tguest SET ishost = 0 WHERE ishost IS NULL;
UPDATE veraweb.tguest SET reserve = 0 WHERE reserve IS NULL;

-- Updatet Farbwahl von Gaesten entsprechend ihrer 'Inland' und 'Geschlecht' Angabe
UPDATE veraweb.tguest SET fk_color = 1 WHERE domestic_a != '0' AND gender = 'F';
UPDATE veraweb.tguest SET fk_color = 2 WHERE domestic_a != '0' AND gender != 'F';
UPDATE veraweb.tguest SET fk_color = 3 WHERE domestic_a = '0' AND gender = 'F';
UPDATE veraweb.tguest SET fk_color = 4 WHERE domestic_a = '0' AND gender != 'F';
UPDATE veraweb.tguest SET fk_color_p = 1 WHERE domestic_b != '0' AND gender_p = 'F';
UPDATE veraweb.tguest SET fk_color_p = 2 WHERE domestic_b != '0' AND gender_p != 'F';
UPDATE veraweb.tguest SET fk_color_p = 3 WHERE domestic_b = '0' AND gender_p = 'F';
UPDATE veraweb.tguest SET fk_color_p = 4 WHERE domestic_b = '0' AND gender_p != 'F';

-- Updatet Person-Dokumenttypen und setzt 'Anschrift' und 'Zeichensatz' aus dem Dokumenttyp
UPDATE veraweb.tperson_doctype SET locale = (SELECT locale FROM veraweb.tdoctype WHERE pk = fk_doctype) WHERE locale NOT IN (1, 2, 3);
UPDATE veraweb.tperson_doctype SET addresstype = (SELECT addresstype FROM veraweb.tdoctype WHERE pk = fk_doctype) WHERE addresstype NOT IN (1, 2, 3);
