--
-- Stammdaten-Script das Beispielhafte Farben in das
-- VerA.web - Schema einspielt und ggf. überschreibt.
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
