--
-- $Id$
--
-- FORMAT: UTF-8
--
-- Stammdaten-Script das Beispielhafte Farben, Dokumenttypen,
-- Anreden und Veranstaltungsdaten in das VerA.Web - Schema einspielt.
--

SET client_encoding = 'UNICODE';

DELETE FROM veraweb.tcolor;
-- Inland: Ja - Geschlecht: Weiblich
INSERT INTO veraweb.tcolor (color, addresstype, locale, rgb) VALUES ('Rot',      1, 1, 16724787);
-- Inland: Ja - Geschlecht: Männlich
INSERT INTO veraweb.tcolor (color, addresstype, locale, rgb) VALUES ('Weiß',     1, 1, 16777215);
-- Inland: Nein - Geschlecht: Weiblich
INSERT INTO veraweb.tcolor (color, addresstype, locale, rgb) VALUES ('Hellgrün', 1, 1, 6750003);
-- Inland: Nein - Geschlecht: Männlich
INSERT INTO veraweb.tcolor (color, addresstype, locale, rgb) VALUES ('Hellblau', 1, 1, 3381759);

-- DELETE FROM veraweb.tcategorie;
INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NULL, 'Raucher', 0, 20);
INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NULL, 'Weihnachten', 1, NULL);
INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NULL, 'Erste Klasse (gleicher Rang, sortiert nach Name)', 0, 10);
INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NULL, 'Zweite Klasse (gleicher Rang, sortiert nach Name)', 0, 10);
INSERT INTO veraweb.tcategorie (fk_event, fk_orgunit, catname, flags, rank) VALUES (NULL, NULL, 'Nicht Raucher', 0, 30);

-- DELETE FROM veraweb.tdoctype;
INSERT INTO veraweb.tdoctype (docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES ('Gästeliste', 0, 0, 0, 99, 0, 0, 0, 'ods-document');
INSERT INTO veraweb.tdoctype (docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES ('Etikett', 2, 1, 1, 99, 0, 0, 0, 'ods-document');
INSERT INTO veraweb.tdoctype (docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES ('Ausweis2', 1, 2, NULL, NULL, 0, 0, 0, 'ods-document');
INSERT INTO veraweb.tdoctype (docname, addresstype, locale, sortorder, flags, isdefault, partner, host, format) VALUES ('Tischkarte', 1, 1, NULL, NULL, 0, 0, 0, 'ods-document');

-- DELETE FROM veraweb.tsalutation;
INSERT INTO veraweb.tsalutation (salutation, gender) VALUES ('Sir',  'M');
INSERT INTO veraweb.tsalutation (salutation, gender) VALUES ('Frau', 'F');
INSERT INTO veraweb.tsalutation (salutation, gender) VALUES ('Herr', 'M');
INSERT INTO veraweb.tsalutation (salutation, gender) VALUES ('Lady', 'F');

-- DELETE FROM veraweb.tsalutation_doctype;
INSERT INTO veraweb.tsalutation_doctype (fk_salutation, fk_doctype, salutation) VALUES (2, 1, 'Frau');
INSERT INTO veraweb.tsalutation_doctype (fk_salutation, fk_doctype, salutation) VALUES (3, 1, 'Herr');
INSERT INTO veraweb.tsalutation_doctype (fk_salutation, fk_doctype, salutation) VALUES (3, 2, 'Herrn');
INSERT INTO veraweb.tsalutation_doctype (fk_salutation, fk_doctype, salutation) VALUES (3, 3, 'Herr');
INSERT INTO veraweb.tsalutation_doctype (fk_salutation, fk_doctype, salutation) VALUES (3, 4, 'Herrn');

-- DELETE FROM veraweb.tlocation;
INSERT INTO veraweb.tlocation (fk_orgunit, locationname) VALUES (0, 'Bundeskanzleramt, großer Saal');
INSERT INTO veraweb.tlocation (fk_orgunit, locationname) VALUES (0, 'Hotel Adler');
INSERT INTO veraweb.tlocation (fk_orgunit, locationname) VALUES (0, 'Zum Hirsch');
