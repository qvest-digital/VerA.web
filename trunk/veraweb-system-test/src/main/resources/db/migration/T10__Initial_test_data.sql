/* ---------------------------------------------------------------------- */
/* Intitialize test data                                                  */
/* ---------------------------------------------------------------------- */

INSERT INTO veraweb.torgunit (unitname) VALUES ('Spielwiese');

INSERT INTO veraweb.tuser (username, role, fk_orgunit) VALUES ('LesenEingeschraenkt', 0, (SELECT pk FROM veraweb.torgunit WHERE unitname = 'Spielwiese'));
INSERT INTO veraweb.tuser (username, role, fk_orgunit) VALUES ('LesenUneingeschraenkt', 1, (SELECT pk FROM veraweb.torgunit WHERE unitname = 'Spielwiese'));
INSERT INTO veraweb.tuser (username, role, fk_orgunit) VALUES ('LesenUndSchreibenEingeschraenkt', 2, (SELECT pk FROM veraweb.torgunit WHERE unitname = 'Spielwiese'));
INSERT INTO veraweb.tuser (username, role, fk_orgunit) VALUES ('LesenUndSchreiben', 3, (SELECT pk FROM veraweb.torgunit WHERE unitname = 'Spielwiese'));
INSERT INTO veraweb.tuser (username, role, fk_orgunit) VALUES ('mandantAdministrieren', 4, (SELECT pk FROM veraweb.torgunit WHERE unitname = 'Spielwiese'));
INSERT INTO veraweb.tuser (username, role, fk_orgunit) VALUES ('administrator', 5, (SELECT pk FROM veraweb.torgunit WHERE unitname = 'Spielwiese'));