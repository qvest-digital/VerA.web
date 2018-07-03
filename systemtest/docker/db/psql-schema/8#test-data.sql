--
-- Test data for system and ui tests.
--

-- user config for user 'admin'
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personListState','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personID','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personWorkarea','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personFirstname','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personLastname','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personFunction','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personCompany','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personStreet','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personPostCode','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personCategory','true');
INSERT INTO tuser_config (fk_user,name,value) VALUES (1,'personInternalId','true');

-- test persons
INSERT INTO tperson (fk_orgunit, iscompany, firstname_a_e1, lastname_a_e1, company_b_e1, street_b_e1, zipcode_b_e1, city_b_e1, state_b_e1, country_b_e1, fon_b_e1, fax_b_e1, mobil_b_e1, mail_b_e1, url_b_e1)
VALUES (-1, 'f', 'Max', 'Mustermann', 'Ich AG', 'Mustergasse 1', '53123', 'Bonn', 'NRW', 'Germany', '02281', '02282', '0179', 'mustermann@gmail.com', 'www.mustermann.de');

-- test company
INSERT INTO tperson (fk_orgunit, iscompany, lastname_a_e1, company_a_e1, street_a_e1, zipcode_a_e1, city_a_e1, state_a_e1, country_a_e1, fon_a_e1, fax_a_e1, mobil_a_e1, mail_a_e1, url_a_e1)
VALUES (-1, 't', 'tarent AG', 'tarent AG', 'Rochusstr. 2-4', '53123', 'Bonn', 'NRW', 'Germany', '02281', '02282', '0170', 'info@tarent.de', 'www.tarent.de');