﻿--
-- $Id: veraweb-stammdaten-only-colors.sql,v 1.1 2005/11/16 13:57:37 christoph Exp $
--
-- FORMAT: UTF-8
--
-- Stammdaten-Script das Beispielhafte Farben in das
-- VerA.Web - Schema einspielt und ggf. überschreibt.
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
