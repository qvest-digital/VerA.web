--
-- $Id: veraweb-stammdaten.sql,v 1.9 2005/11/16 13:57:37 christoph Exp $
--
-- FORMAT: UTF-8
--
-- Stammdaten-Script das Beispielhafte Farben, Dokumenttypen,
-- Anreden und Veranstaltungsdaten in das VerA.Web - Schema einspielt.
--

SET client_encoding = 'UNICODE';

DELETE FROM veraweb.tcolor;
-- Inland: Ja - Geschlecht: Weiblich
INSERT INTO veraweb.tcolor (pk, color, addresstype, locale, rgb) VALUES (1, 'Rot',      1, 1, 16724787);
-- Inland: Ja - Geschlecht: Männlich
INSERT INTO veraweb.tcolor (pk, color, addresstype, locale, rgb) VALUES (2, 'Weiß',     1, 1, 16777215);
-- Inland: Nein - Geschlecht: Weiblich
INSERT INTO veraweb.tcolor (pk, color, addresstype, locale, rgb) VALUES (3, 'Hellgrün', 1, 1, 6750003);
-- Inland: Nein - Geschlecht: Männlich
INSERT INTO veraweb.tcolor (pk, color, addresstype, locale, rgb) VALUES (4, 'Hellblau', 1, 1, 3381759);

-- DELETE FROM veraweb.tcategorie;
INSERT INTO veraweb.tcategorie (pk, fk_event, fk_orgunit, catname, flags, rank) VALUES (1, NULL, NULL, 'Raucher', 0, 20);
INSERT INTO veraweb.tcategorie (pk, fk_event, fk_orgunit, catname, flags, rank) VALUES (2, NULL, NULL, 'Weihnachten', 1, NULL);
INSERT INTO veraweb.tcategorie (pk, fk_event, fk_orgunit, catname, flags, rank) VALUES (3, NULL, NULL, 'Erste Klasse (gleicher Rang, sortiert nach Name)', 0, 10);
INSERT INTO veraweb.tcategorie (pk, fk_event, fk_orgunit, catname, flags, rank) VALUES (4, NULL, NULL, 'Zweite Klasse (gleicher Rang, sortiert nach Name)', 0, 10);
INSERT INTO veraweb.tcategorie (pk, fk_event, fk_orgunit, catname, flags, rank) VALUES (5, NULL, NULL, 'Nicht Raucher', 0, 30);

-- DELETE FROM veraweb.tdoctype;
INSERT INTO veraweb.tdoctype (pk, docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES (1, 'Gästeliste', 0, 0, 0, 99, 0, 0, 0, 'ods-document');
INSERT INTO veraweb.tdoctype (pk, docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES (2, 'Etikett', 2, 1, 1, 99, 0, 0, 0, 'ods-document');
INSERT INTO veraweb.tdoctype (pk, docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES (3, 'Ausweis2', 1, 2, NULL, NULL, 0, 0, 0, 'ods-document');
INSERT INTO veraweb.tdoctype (pk, docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES (4, 'Tischkarte', 1, 1, NULL, NULL, 0, 0, 0, 'ods-document');

-- DELETE FROM veraweb.tsalutation;
INSERT INTO veraweb.tsalutation (pk, salutation, gender) VALUES (1, 'Sir',  'M');
INSERT INTO veraweb.tsalutation (pk, salutation, gender) VALUES (2, 'Frau', 'F');
INSERT INTO veraweb.tsalutation (pk, salutation, gender) VALUES (3, 'Herr', 'M');
INSERT INTO veraweb.tsalutation (pk, salutation, gender) VALUES (4, 'Lady', 'F');

-- DELETE FROM veraweb.tsalutation_doctype;
INSERT INTO veraweb.tsalutation_doctype (pk, fk_salutation, fk_doctype, salutation) VALUES (1, 2, 1, 'Frau');
INSERT INTO veraweb.tsalutation_doctype (pk, fk_salutation, fk_doctype, salutation) VALUES (2, 3, 1, 'Herr');
INSERT INTO veraweb.tsalutation_doctype (pk, fk_salutation, fk_doctype, salutation) VALUES (3, 3, 2, 'Herrn');
INSERT INTO veraweb.tsalutation_doctype (pk, fk_salutation, fk_doctype, salutation) VALUES (4, 3, 3, 'Herr');
INSERT INTO veraweb.tsalutation_doctype (pk, fk_salutation, fk_doctype, salutation) VALUES (5, 3, 4, 'Herrn');

-- DELETE FROM veraweb.tlocation;
INSERT INTO veraweb.tlocation (pk, fk_orgunit, locationname) VALUES (1, 0, 'Bundeskanzleramt, großer Saal');
INSERT INTO veraweb.tlocation (pk, fk_orgunit, locationname) VALUES (2, 0, 'Hotel Adler');
INSERT INTO veraweb.tlocation (pk, fk_orgunit, locationname) VALUES (3, 0, 'Zum Hirsch');
