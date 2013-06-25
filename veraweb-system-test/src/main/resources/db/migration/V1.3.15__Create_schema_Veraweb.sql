/* ---------------------------------------------------------------------- */
/* Sequences                                                              */
/* ---------------------------------------------------------------------- */
CREATE SEQUENCE veraweb.tcategorie_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tcolor_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tconfig_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tupdate_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tresult_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tdoctype_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tevent_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tevent_doctype_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tfunction_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tguest_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tguest_doctype_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tlocation_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tmailinglist_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tmaildraft_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tmailoutbox_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.torgunit_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tworkarea_pk_seq INCREMENT 1 MINVALUE 0 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tperson_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.timport_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.timportperson_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.timportperson_categorie_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.timportperson_doctype_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tperson_categorie_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tperson_doctype_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tperson_mailinglist_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tsalutation_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tsalutation_doctype_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tuser_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tuser_config_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tproxy_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.tchangelog_pk_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;
CREATE SEQUENCE veraweb.import_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1;

/* ---------------------------------------------------------------------- */
/* Tables                                                                 */
/* ---------------------------------------------------------------------- */

/* ---------------------------------------------------------------------- */
/* Add table "tcategorie"                                                 */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tcategorie (
    pk INTEGER DEFAULT nextval('tcategorie_pk_seq')  NOT NULL,
    fk_event INTEGER DEFAULT 0,
    fk_orgunit INTEGER DEFAULT 0,
    catname VARCHAR(200)  NOT NULL,
    flags INTEGER DEFAULT 0,
    rank INTEGER DEFAULT 0,
    CONSTRAINT tcategorie_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tchangelog"                                                 */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tchangelog (
    pk INTEGER DEFAULT nextval('tchangelog_pk_seq')  NOT NULL,
    username VARCHAR(100)  NOT NULL,
    objname VARCHAR(300)  NOT NULL,
    objtype VARCHAR(250)  NOT NULL,
    objid INTEGER  NOT NULL,
    op VARCHAR(6)  NOT NULL,
    attributes TEXT  NOT NULL,
    date TIMESTAMP WITH TIME ZONE,
    CONSTRAINT tchangelog_pkey PRIMARY KEY (pk)
) WITH OIDS;

CREATE INDEX tchangelog_date_index ON veraweb.tchangelog (date);
CREATE INDEX tchangelog_username_index ON veraweb.tchangelog (username);

/* ---------------------------------------------------------------------- */
/* Add table "tcolor"                                                     */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tcolor (
    pk INTEGER DEFAULT nextval('tcolor_pk_seq')  NOT NULL,
    color VARCHAR(100)  NOT NULL,
    addresstype INTEGER DEFAULT 0  NOT NULL,
    locale INTEGER DEFAULT 0  NOT NULL,
    rgb INTEGER  NOT NULL,
    CONSTRAINT tcolor_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tconfig"                                                    */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tconfig (
    pk INTEGER DEFAULT nextval('tconfig_pk_seq')  NOT NULL,
    cname VARCHAR(100)  NOT NULL,
    cvalue VARCHAR(300)  NOT NULL,
    CONSTRAINT tconfig_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tdoctype"                                                   */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tdoctype (
    pk INTEGER DEFAULT nextval('tdoctype_pk_seq')  NOT NULL,
    docname VARCHAR(200)  NOT NULL,
    addresstype INTEGER DEFAULT 1  NOT NULL,
    locale INTEGER DEFAULT 0  NOT NULL,
    sortorder INTEGER,
    flags INTEGER,
    isdefault INTEGER DEFAULT 0  NOT NULL,
    partner INTEGER DEFAULT 0  NOT NULL,
    host INTEGER DEFAULT 1  NOT NULL,
    format VARCHAR(20)  NOT NULL,
    CONSTRAINT tdoctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tevent"                                                     */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tevent (
    pk INTEGER DEFAULT nextval('tevent_pk_seq')  NOT NULL,
    fk_orgunit INTEGER DEFAULT 0,
    fk_host INTEGER,
    invitationtype INTEGER DEFAULT 1  NOT NULL,
    shortname VARCHAR(50)  NOT NULL,
    eventname TEXT,
    datebegin TIMESTAMP WITH TIME ZONE  NOT NULL,
    dateend TIMESTAMP WITH TIME ZONE,
    invitehostpartner INTEGER DEFAULT 0,
    hostname VARCHAR(300),
    maxguest INTEGER DEFAULT 0,
    location VARCHAR(300)  NOT NULL,
    note TEXT,
    createdby VARCHAR(50),
    changedby VARCHAR(50),
    created TIMESTAMP WITH TIME ZONE,
    changed TIMESTAMP WITH TIME ZONE,
    CONSTRAINT tevent_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tevent_doctype"                                             */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tevent_doctype (
    pk INTEGER DEFAULT nextval('tevent_doctype_pk_seq') NOT NULL,
    fk_event INTEGER DEFAULT 0  NOT NULL,
    fk_doctype INTEGER DEFAULT 0  NOT NULL,
    CONSTRAINT tevent_doctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tfunction"                                                  */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tfunction (
    pk INTEGER DEFAULT nextval('tfunction_pk_seq')  NOT NULL,
    functionname VARCHAR(300)  NOT NULL,
    CONSTRAINT tfunction_pkey PRIMARY KEY (pk)
);

/* ---------------------------------------------------------------------- */
/* Add table "tguest"                                                     */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tguest (
    pk INTEGER DEFAULT nextval('tguest_pk_seq')  NOT NULL,
    fk_person INTEGER  NOT NULL,
    fk_event INTEGER  NOT NULL,
    fk_category INTEGER DEFAULT 0,
    fk_color INTEGER DEFAULT 0,
    invitationtype INTEGER DEFAULT 0,
    invitationstatus INTEGER DEFAULT 0,
    ishost INTEGER DEFAULT 0,
    diplodate TIMESTAMP WITH TIME ZONE,
    rank INTEGER DEFAULT 0,
    reserve INTEGER DEFAULT 0,
    tableno INTEGER DEFAULT 0,
    seatno INTEGER DEFAULT 0,
    orderno INTEGER DEFAULT 0,
    notehost TEXT,
    noteorga TEXT,
    language VARCHAR(250),
    gender VARCHAR(1)  NOT NULL,
    nationality VARCHAR(100),
    domestic_a VARCHAR(1) DEFAULT 't'  NOT NULL,
    invitationstatus_p INTEGER DEFAULT 0,
    tableno_p INTEGER DEFAULT 0,
    seatno_p INTEGER DEFAULT 0,
    orderno_p INTEGER DEFAULT 0,
    notehost_p TEXT,
    noteorga_p TEXT,
    language_p VARCHAR(250),
    gender_p VARCHAR(1)  NOT NULL,
    nationality_p VARCHAR(100),
    domestic_b VARCHAR(1) DEFAULT 't'  NOT NULL,
    fk_color_p INTEGER DEFAULT 0,
    createdby VARCHAR(50),
    changedby VARCHAR(50),
    created TIMESTAMP WITH TIME ZONE,
    changed TIMESTAMP WITH TIME ZONE,
    CONSTRAINT tguest_pkey PRIMARY KEY (pk)
) WITH OIDS;

CREATE INDEX tguest_fk_person_index ON veraweb.tguest (fk_person);

CREATE INDEX tguest_fk_event_index ON veraweb.tguest (fk_event);

CREATE INDEX tguest_fk_category_index ON veraweb.tguest (fk_category);

/* ---------------------------------------------------------------------- */
/* Add table "tguest_doctype"                                             */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tguest_doctype (
    pk INTEGER DEFAULT nextval('tguest_doctype_pk_seq')  NOT NULL,
    fk_guest INTEGER DEFAULT 0  NOT NULL,
    fk_doctype INTEGER DEFAULT 0  NOT NULL,
    addresstype INTEGER DEFAULT 0  NOT NULL,
    locale INTEGER DEFAULT 0  NOT NULL,
    textfield TEXT,
    textfield_p TEXT,
    textjoin VARCHAR(50),
    salutation VARCHAR(50),
    function VARCHAR(250),
    titel VARCHAR(250),
    firstname VARCHAR(100),
    lastname VARCHAR(100),
    zipcode VARCHAR(50),
    state VARCHAR(100),
    city VARCHAR(100),
    street VARCHAR(100),
    country VARCHAR(100),
    suffix1 VARCHAR(100),
    suffix2 VARCHAR(100),
    salutation_p VARCHAR(50),
    titel_p VARCHAR(250),
    firstname_p VARCHAR(100),
    lastname_p VARCHAR(100),
    fon VARCHAR(100),
    fax VARCHAR(100),
    mail VARCHAR(250),
    www VARCHAR(250),
    mobil VARCHAR(100),
    company VARCHAR(250),
    pobox VARCHAR(50),
    poboxzipcode VARCHAR(50),
    CONSTRAINT tguest_doctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

CREATE INDEX tguest_doctype_fk_index ON veraweb.tguest_doctype (fk_guest,fk_doctype);

CREATE INDEX tguest_doctype_fk_guest_index ON veraweb.tguest_doctype (fk_guest);

/* ---------------------------------------------------------------------- */
/* Add table "timport"                                                    */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.timport (
    pk INTEGER DEFAULT nextval('timport_pk_seq')  NOT NULL,
    fk_orgunit INTEGER,
    created TIMESTAMP WITH TIME ZONE,
    createdby VARCHAR(100),
    changed TIMESTAMP WITH TIME ZONE,
    changedby VARCHAR(100),
    importsource VARCHAR(250),
    importformat VARCHAR(250),
    CONSTRAINT timport_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "timportperson"                                              */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.timportperson (
    fk_orgunit INTEGER,
    created TIMESTAMP WITH TIME ZONE,
    createdby VARCHAR(100),
    changed TIMESTAMP WITH TIME ZONE,
    changedby VARCHAR(100),
    deleted VARCHAR(1) DEFAULT 'f'  NOT NULL,
    dateexpire TIMESTAMP WITH TIME ZONE DEFAULT now() + '3 years'::interval,
    iscompany VARCHAR(1) DEFAULT 'f'  NOT NULL,
    importsource VARCHAR(250),
    fk_workarea INTEGER DEFAULT 0  NOT NULL,
    salutation_a_e1 VARCHAR(50),
    fk_salutation_a_e1 INTEGER,
    title_a_e1 VARCHAR(250),
    firstname_a_e1 VARCHAR(100),
    lastname_a_e1 VARCHAR(100),
    domestic_a_e1 VARCHAR(1) DEFAULT 't'  NOT NULL,
    sex_a_e1 VARCHAR(1) DEFAULT 'm'  NOT NULL,
    birthday_a_e1 TIMESTAMP WITH TIME ZONE,
    birthplace_a_e1 VARCHAR(100),
    diplodate_a_e1 TIMESTAMP WITH TIME ZONE,
    languages_a_e1 VARCHAR(250),
    nationality_a_e1 VARCHAR(100),
    note_a_e1 TEXT,
    noteorga_a_e1 TEXT,
    notehost_a_e1 TEXT,
    salutation_a_e2 VARCHAR(50),
    fk_salutation_a_e2 INTEGER,
    birthplace_a_e2 VARCHAR(100),
    title_a_e2 VARCHAR(250),
    firstname_a_e2 VARCHAR(100),
    lastname_a_e2 VARCHAR(100),
    salutation_a_e3 VARCHAR(50),
    fk_salutation_a_e3 INTEGER,
    birthplace_a_e3 VARCHAR(100),
    title_a_e3 VARCHAR(250),
    firstname_a_e3 VARCHAR(100),
    lastname_a_e3 VARCHAR(100),
    salutation_b_e1 VARCHAR(50),
    fk_salutation_b_e1 INTEGER,
    title_b_e1 VARCHAR(250),
    firstname_b_e1 VARCHAR(100),
    lastname_b_e1 VARCHAR(100),
    domestic_b_e1 VARCHAR(1) DEFAULT 't'  NOT NULL,
    sex_b_e1 VARCHAR(1) DEFAULT 'm'  NOT NULL,
    birthday_b_e1 TIMESTAMP WITH TIME ZONE,
    diplodate_b_e1 TIMESTAMP WITH TIME ZONE,
    languages_b_e1 VARCHAR(250),
    nationality_b_e1 VARCHAR(100),
    note_b_e1 TEXT,
    noteorga_b_e1 TEXT,
    notehost_b_e1 TEXT,
    salutation_b_e2 VARCHAR(50),
    fk_salutation_b_e2 INTEGER,
    title_b_e2 VARCHAR(250),
    firstname_b_e2 VARCHAR(100),
    lastname_b_e2 VARCHAR(100),
    salutation_b_e3 VARCHAR(50),
    fk_salutation_b_e3 INTEGER,
    title_b_e3 VARCHAR(250),
    firstname_b_e3 VARCHAR(100),
    lastname_b_e3 VARCHAR(100),
    function_a_e1 VARCHAR(250),
    company_a_e1 VARCHAR(250),
    street_a_e1 VARCHAR(100),
    zipcode_a_e1 VARCHAR(50),
    state_a_e1 VARCHAR(100),
    city_a_e1 VARCHAR(100),
    country_a_e1 VARCHAR(100),
    pobox_a_e1 VARCHAR(50),
    poboxzipcode_a_e1 VARCHAR(50),
    suffix1_a_e1 VARCHAR(100),
    suffix2_a_e1 VARCHAR(100),
    fon_a_e1 VARCHAR(100),
    fax_a_e1 VARCHAR(100),
    mobil_a_e1 VARCHAR(100),
    mail_a_e1 VARCHAR(250),
    url_a_e1 VARCHAR(250),
    function_a_e2 VARCHAR(250),
    company_a_e2 VARCHAR(250),
    street_a_e2 VARCHAR(100),
    zipcode_a_e2 VARCHAR(50),
    state_a_e2 VARCHAR(100),
    city_a_e2 VARCHAR(100),
    country_a_e2 VARCHAR(100),
    pobox_a_e2 VARCHAR(50),
    poboxzipcode_a_e2 VARCHAR(50),
    suffix1_a_e2 VARCHAR(100),
    suffix2_a_e2 VARCHAR(100),
    fon_a_e2 VARCHAR(100),
    fax_a_e2 VARCHAR(100),
    mobil_a_e2 VARCHAR(100),
    mail_a_e2 VARCHAR(250),
    url_a_e2 VARCHAR(250),
    function_a_e3 VARCHAR(250),
    company_a_e3 VARCHAR(250),
    street_a_e3 VARCHAR(100),
    zipcode_a_e3 VARCHAR(50),
    state_a_e3 VARCHAR(100),
    city_a_e3 VARCHAR(100),
    country_a_e3 VARCHAR(100),
    pobox_a_e3 VARCHAR(50),
    poboxzipcode_a_e3 VARCHAR(50),
    suffix1_a_e3 VARCHAR(100),
    suffix2_a_e3 VARCHAR(100),
    fon_a_e3 VARCHAR(100),
    fax_a_e3 VARCHAR(100),
    mobil_a_e3 VARCHAR(100),
    mail_a_e3 VARCHAR(250),
    url_a_e3 VARCHAR(250),
    function_b_e1 VARCHAR(250),
    company_b_e1 VARCHAR(250),
    street_b_e1 VARCHAR(100),
    zipcode_b_e1 VARCHAR(50),
    state_b_e1 VARCHAR(100),
    city_b_e1 VARCHAR(100),
    country_b_e1 VARCHAR(100),
    pobox_b_e1 VARCHAR(50),
    poboxzipcode_b_e1 VARCHAR(50),
    suffix1_b_e1 VARCHAR(100),
    suffix2_b_e1 VARCHAR(100),
    fon_b_e1 VARCHAR(100),
    fax_b_e1 VARCHAR(100),
    mobil_b_e1 VARCHAR(100),
    mail_b_e1 VARCHAR(250),
    url_b_e1 VARCHAR(250),
    function_b_e2 VARCHAR(250),
    company_b_e2 VARCHAR(250),
    street_b_e2 VARCHAR(100),
    zipcode_b_e2 VARCHAR(50),
    state_b_e2 VARCHAR(100),
    city_b_e2 VARCHAR(100),
    country_b_e2 VARCHAR(100),
    pobox_b_e2 VARCHAR(50),
    poboxzipcode_b_e2 VARCHAR(50),
    suffix1_b_e2 VARCHAR(100),
    suffix2_b_e2 VARCHAR(100),
    fon_b_e2 VARCHAR(100),
    fax_b_e2 VARCHAR(100),
    mobil_b_e2 VARCHAR(100),
    mail_b_e2 VARCHAR(250),
    url_b_e2 VARCHAR(250),
    function_b_e3 VARCHAR(250),
    company_b_e3 VARCHAR(250),
    street_b_e3 VARCHAR(100),
    zipcode_b_e3 VARCHAR(50),
    state_b_e3 VARCHAR(100),
    city_b_e3 VARCHAR(100),
    country_b_e3 VARCHAR(100),
    pobox_b_e3 VARCHAR(50),
    poboxzipcode_b_e3 VARCHAR(50),
    suffix1_b_e3 VARCHAR(100),
    suffix2_b_e3 VARCHAR(100),
    fon_b_e3 VARCHAR(100),
    fax_b_e3 VARCHAR(100),
    mobil_b_e3 VARCHAR(100),
    mail_b_e3 VARCHAR(250),
    url_b_e3 VARCHAR(250),
    function_c_e1 VARCHAR(250),
    company_c_e1 VARCHAR(250),
    street_c_e1 VARCHAR(100),
    zipcode_c_e1 VARCHAR(50),
    state_c_e1 VARCHAR(100),
    city_c_e1 VARCHAR(100),
    country_c_e1 VARCHAR(100),
    pobox_c_e1 VARCHAR(50),
    poboxzipcode_c_e1 VARCHAR(50),
    suffix1_c_e1 VARCHAR(100),
    suffix2_c_e1 VARCHAR(100),
    fon_c_e1 VARCHAR(100),
    fax_c_e1 VARCHAR(100),
    mobil_c_e1 VARCHAR(100),
    mail_c_e1 VARCHAR(250),
    url_c_e1 VARCHAR(250),
    function_c_e2 VARCHAR(250),
    company_c_e2 VARCHAR(250),
    street_c_e2 VARCHAR(100),
    zipcode_c_e2 VARCHAR(50),
    state_c_e2 VARCHAR(100),
    city_c_e2 VARCHAR(100),
    country_c_e2 VARCHAR(100),
    pobox_c_e2 VARCHAR(50),
    poboxzipcode_c_e2 VARCHAR(50),
    suffix1_c_e2 VARCHAR(100),
    suffix2_c_e2 VARCHAR(100),
    fon_c_e2 VARCHAR(100),
    fax_c_e2 VARCHAR(100),
    mobil_c_e2 VARCHAR(100),
    mail_c_e2 VARCHAR(250),
    url_c_e2 VARCHAR(250),
    function_c_e3 VARCHAR(250),
    company_c_e3 VARCHAR(250),
    street_c_e3 VARCHAR(100),
    zipcode_c_e3 VARCHAR(50),
    state_c_e3 VARCHAR(100),
    city_c_e3 VARCHAR(100),
    country_c_e3 VARCHAR(100),
    pobox_c_e3 VARCHAR(50),
    poboxzipcode_c_e3 VARCHAR(50),
    suffix1_c_e3 VARCHAR(100),
    suffix2_c_e3 VARCHAR(100),
    fon_c_e3 VARCHAR(100),
    fax_c_e3 VARCHAR(100),
    mobil_c_e3 VARCHAR(100),
    mail_c_e3 VARCHAR(250),
    url_c_e3 VARCHAR(250),
    fk_import INTEGER,
    fk_externalid INTEGER,
    duplicates VARCHAR(500),
    dupcheckaction INTEGER,
    dupcheckstatus INTEGER,
    category VARCHAR,
    occasion VARCHAR,
    textfield_1 TEXT,
    textfield_2 TEXT,
    textfield_3 TEXT,
    pk INTEGER DEFAULT nextval(('veraweb.timportperson_pk_seq'))  NOT NULL,
    CONSTRAINT timportperson_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "timportperson_categorie"                                    */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.timportperson_categorie (
    pk INTEGER DEFAULT nextval('timportperson_categorie_pk_seq')  NOT NULL,
    fk_importperson INTEGER  NOT NULL,
    catname VARCHAR(200)  NOT NULL,
    flags INTEGER DEFAULT 0,
    rank INTEGER DEFAULT 0,
    CONSTRAINT timportperson_categorie_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "timportperson_doctype"                                      */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.timportperson_doctype (
    pk INTEGER DEFAULT nextval('timportperson_doctype_pk_seq')  NOT NULL,
    fk_importperson INTEGER  NOT NULL,
    docname VARCHAR(200)  NOT NULL,
    textfield TEXT,
    textfield_p TEXT,
    textjoin VARCHAR(50),
    CONSTRAINT timportperson_doctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tlocation"                                                  */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tlocation (
    pk INTEGER DEFAULT nextval('tlocation_pk_seq')  NOT NULL,
    fk_orgunit INTEGER DEFAULT 0,
    locationname VARCHAR(200)  NOT NULL,
    CONSTRAINT tlocation_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tmaildraft"                                                 */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tmaildraft (
    pk INTEGER DEFAULT nextval('tmaildraft_pk_seq')  NOT NULL,
    name VARCHAR(200)  NOT NULL,
    subject VARCHAR(200)  NOT NULL,
    content TEXT  NOT NULL,
    createdby VARCHAR(50),
    created TIMESTAMP WITH TIME ZONE,
    changedby VARCHAR(50),
    changed TIMESTAMP WITH TIME ZONE,
    CONSTRAINT tmaildraft_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tmailinglist"                                               */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tmailinglist (
    pk INTEGER DEFAULT nextval('tmailinglist_pk_seq')  NOT NULL,
    fk_orgunit INTEGER  NOT NULL,
    listname VARCHAR(200)  NOT NULL,
    fk_vera INTEGER DEFAULT 0,
    created TIMESTAMP WITH TIME ZONE,
    createdby VARCHAR(50),
    fk_user INTEGER,
    CONSTRAINT tmailinglist_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tmailoutbox"                                                */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tmailoutbox (
    pk INTEGER DEFAULT nextval('tmailoutbox_pk_seq')  NOT NULL,
    status INTEGER  NOT NULL,
    addrfrom VARCHAR(200)  NOT NULL,
    addrto VARCHAR(200)  NOT NULL,
    subject VARCHAR(200)  NOT NULL,
    content TEXT  NOT NULL,
    lastupdate TIMESTAMP WITH TIME ZONE,
    errortext VARCHAR(200),
    CONSTRAINT tmailoutbox_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "torgunit"                                                   */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.torgunit (
    pk INTEGER DEFAULT nextval('torgunit_pk_seq')  NOT NULL,
    unitname VARCHAR(100)  NOT NULL,
    folderxman VARCHAR(100),
    fk_folderxman INTEGER,
    CONSTRAINT torgunit_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tperson_categorie"                                          */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tperson_categorie (
    pk INTEGER DEFAULT nextval('tperson_categorie_pk_seq')  NOT NULL,
    fk_person INTEGER DEFAULT 0  NOT NULL,
    fk_categorie INTEGER DEFAULT 0  NOT NULL,
    rank INTEGER DEFAULT 0,
    CONSTRAINT tperson_categorie_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tperson_doctype"                                            */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tperson_doctype (
    pk INTEGER DEFAULT nextval('tperson_doctype_pk_seq')  NOT NULL,
    fk_person INTEGER DEFAULT 0  NOT NULL,
    fk_doctype INTEGER DEFAULT 0  NOT NULL,
    addresstype INTEGER DEFAULT 0  NOT NULL,
    locale INTEGER DEFAULT 0  NOT NULL,
    textfield TEXT,
    textfield_p TEXT,
    textjoin VARCHAR(50),
    CONSTRAINT tperson_doctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

CREATE INDEX tperson_doctype_fk_person_index ON veraweb.tperson_doctype (fk_person);

/* ---------------------------------------------------------------------- */
/* Add table "tperson_mailinglist"                                        */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tperson_mailinglist (
    pk INTEGER DEFAULT nextval('tperson_mailinglist_pk_seq')  NOT NULL,
    fk_person INTEGER DEFAULT 0  NOT NULL,
    fk_mailinglist INTEGER DEFAULT 0  NOT NULL,
    address VARCHAR(250),
    CONSTRAINT tperson_mailinglist_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tresult"                                                    */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tresult (
    id INTEGER DEFAULT nextval('tresult_id_seq')  NOT NULL,
    value VARCHAR(1000),
    CONSTRAINT tresult_pkey PRIMARY KEY (id)
) WITH OIDS;

COMMENT ON TABLE veraweb.tresult IS 'VerA.Web: System-Tabelle';

/* ---------------------------------------------------------------------- */
/* Add table "tsalutation"                                                */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tsalutation (
    pk INTEGER DEFAULT nextval('tsalutation_pk_seq')  NOT NULL,
    salutation VARCHAR(100)  NOT NULL,
    gender VARCHAR(1),
    CONSTRAINT tsalutation_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tsalutation_doctype"                                        */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tsalutation_doctype (
    pk INTEGER DEFAULT nextval('tsalutation_doctype_pk_seq')  NOT NULL,
    fk_salutation INTEGER DEFAULT 0  NOT NULL,
    fk_doctype INTEGER DEFAULT 0  NOT NULL,
    salutation VARCHAR(100),
    CONSTRAINT tsalutation_doctype_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tupdate"                                                    */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tupdate (
    id INTEGER DEFAULT nextval('tupdate_id_seq')  NOT NULL,
    date VARCHAR(50),
    value VARCHAR(1000),
    CONSTRAINT tupdate_pkey PRIMARY KEY (id)
) WITH OIDS;

COMMENT ON TABLE veraweb.tupdate IS 'VerA.Web: System-Tabelle';

/* ---------------------------------------------------------------------- */
/* Add table "tuser"                                                      */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tuser (
    pk INTEGER DEFAULT nextval('tuser_pk_seq')  NOT NULL,
    fk_orgunit INTEGER DEFAULT 0,
    username VARCHAR(100)  NOT NULL,
    role INTEGER,
    CONSTRAINT tuser_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tuser_config"                                               */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tuser_config (
    pk INTEGER DEFAULT nextval('tuser_config_pk_seq')  NOT NULL,
    fk_user INTEGER  NOT NULL,
    name VARCHAR(100)  NOT NULL,
    value VARCHAR(300)  NOT NULL,
    CONSTRAINT tuser_config_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tworkarea"                                                  */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tworkarea (
    pk INTEGER DEFAULT nextval(('veraweb.tworkarea_pk_seq'))  NOT NULL,
    name VARCHAR(250)  NOT NULL,
    fk_orgunit INTEGER,
    CONSTRAINT tworkarea_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Add table "tperson"                                                    */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tperson (
    pk INTEGER DEFAULT nextval('tperson_pk_seq')  NOT NULL,
    fk_orgunit INTEGER,
    created TIMESTAMP WITH TIME ZONE,
    createdby VARCHAR(100),
    changed TIMESTAMP WITH TIME ZONE,
    changedby VARCHAR(100),
    deleted VARCHAR(1) DEFAULT 'f'  NOT NULL,
    dateexpire TIMESTAMP WITH TIME ZONE DEFAULT now() + '3 years'::interval,
    iscompany VARCHAR(1) DEFAULT 'f'  NOT NULL,
    importsource VARCHAR(250),
    fk_workarea INTEGER DEFAULT 0  NOT NULL,
    salutation_a_e1 VARCHAR(50),
    fk_salutation_a_e1 INTEGER,
    title_a_e1 VARCHAR(250),
    firstname_a_e1 VARCHAR(100),
    lastname_a_e1 VARCHAR(100),
    domestic_a_e1 VARCHAR(1) DEFAULT 't'  NOT NULL,
    sex_a_e1 VARCHAR(1) DEFAULT 'm'  NOT NULL,
    birthday_a_e1 TIMESTAMP WITH TIME ZONE,
    birthplace_a_e1 VARCHAR(100),
    diplodate_a_e1 TIMESTAMP WITH TIME ZONE,
    languages_a_e1 VARCHAR(250),
    nationality_a_e1 VARCHAR(100),
    note_a_e1 TEXT,
    noteorga_a_e1 TEXT,
    notehost_a_e1 TEXT,
    salutation_a_e2 VARCHAR(50),
    fk_salutation_a_e2 INTEGER,
    birthplace_a_e2 VARCHAR(100),
    title_a_e2 VARCHAR(250),
    firstname_a_e2 VARCHAR(100),
    lastname_a_e2 VARCHAR(100),
    salutation_a_e3 VARCHAR(50),
    fk_salutation_a_e3 INTEGER,
    birthplace_a_e3 VARCHAR(100),
    title_a_e3 VARCHAR(250),
    firstname_a_e3 VARCHAR(100),
    lastname_a_e3 VARCHAR(100),
    salutation_b_e1 VARCHAR(50),
    fk_salutation_b_e1 INTEGER,
    title_b_e1 VARCHAR(250),
    firstname_b_e1 VARCHAR(100),
    lastname_b_e1 VARCHAR(100),
    domestic_b_e1 VARCHAR(1) DEFAULT 't'  NOT NULL,
    sex_b_e1 VARCHAR(1) DEFAULT 'm'  NOT NULL,
    birthday_b_e1 TIMESTAMP WITH TIME ZONE,
    diplodate_b_e1 TIMESTAMP WITH TIME ZONE,
    languages_b_e1 VARCHAR(250),
    nationality_b_e1 VARCHAR(100),
    note_b_e1 TEXT,
    noteorga_b_e1 TEXT,
    notehost_b_e1 TEXT,
    salutation_b_e2 VARCHAR(50),
    fk_salutation_b_e2 INTEGER,
    title_b_e2 VARCHAR(250),
    firstname_b_e2 VARCHAR(100),
    lastname_b_e2 VARCHAR(100),
    salutation_b_e3 VARCHAR(50),
    fk_salutation_b_e3 INTEGER,
    title_b_e3 VARCHAR(250),
    firstname_b_e3 VARCHAR(100),
    lastname_b_e3 VARCHAR(100),
    function_a_e1 VARCHAR(250),
    company_a_e1 VARCHAR(250),
    street_a_e1 VARCHAR(100),
    zipcode_a_e1 VARCHAR(50),
    state_a_e1 VARCHAR(100),
    city_a_e1 VARCHAR(100),
    country_a_e1 VARCHAR(100),
    pobox_a_e1 VARCHAR(50),
    poboxzipcode_a_e1 VARCHAR(50),
    suffix1_a_e1 VARCHAR(100),
    suffix2_a_e1 VARCHAR(100),
    fon_a_e1 VARCHAR(100),
    fax_a_e1 VARCHAR(100),
    mobil_a_e1 VARCHAR(100),
    mail_a_e1 VARCHAR(250),
    url_a_e1 VARCHAR(250),
    function_a_e2 VARCHAR(250),
    company_a_e2 VARCHAR(250),
    street_a_e2 VARCHAR(100),
    zipcode_a_e2 VARCHAR(50),
    state_a_e2 VARCHAR(100),
    city_a_e2 VARCHAR(100),
    country_a_e2 VARCHAR(100),
    pobox_a_e2 VARCHAR(50),
    poboxzipcode_a_e2 VARCHAR(50),
    suffix1_a_e2 VARCHAR(100),
    suffix2_a_e2 VARCHAR(100),
    fon_a_e2 VARCHAR(100),
    fax_a_e2 VARCHAR(100),
    mobil_a_e2 VARCHAR(100),
    mail_a_e2 VARCHAR(250),
    url_a_e2 VARCHAR(250),
    function_a_e3 VARCHAR(250),
    company_a_e3 VARCHAR(250),
    street_a_e3 VARCHAR(100),
    zipcode_a_e3 VARCHAR(50),
    state_a_e3 VARCHAR(100),
    city_a_e3 VARCHAR(100),
    country_a_e3 VARCHAR(100),
    pobox_a_e3 VARCHAR(50),
    poboxzipcode_a_e3 VARCHAR(50),
    suffix1_a_e3 VARCHAR(100),
    suffix2_a_e3 VARCHAR(100),
    fon_a_e3 VARCHAR(100),
    fax_a_e3 VARCHAR(100),
    mobil_a_e3 VARCHAR(100),
    mail_a_e3 VARCHAR(250),
    url_a_e3 VARCHAR(250),
    function_b_e1 VARCHAR(250),
    company_b_e1 VARCHAR(250),
    street_b_e1 VARCHAR(100),
    zipcode_b_e1 VARCHAR(50),
    state_b_e1 VARCHAR(100),
    city_b_e1 VARCHAR(100),
    country_b_e1 VARCHAR(100),
    pobox_b_e1 VARCHAR(50),
    poboxzipcode_b_e1 VARCHAR(50),
    suffix1_b_e1 VARCHAR(100),
    suffix2_b_e1 VARCHAR(100),
    fon_b_e1 VARCHAR(100),
    fax_b_e1 VARCHAR(100),
    mobil_b_e1 VARCHAR(100),
    mail_b_e1 VARCHAR(250),
    url_b_e1 VARCHAR(250),
    function_b_e2 VARCHAR(250),
    company_b_e2 VARCHAR(250),
    street_b_e2 VARCHAR(100),
    zipcode_b_e2 VARCHAR(50),
    state_b_e2 VARCHAR(100),
    city_b_e2 VARCHAR(100),
    country_b_e2 VARCHAR(100),
    pobox_b_e2 VARCHAR(50),
    poboxzipcode_b_e2 VARCHAR(50),
    suffix1_b_e2 VARCHAR(100),
    suffix2_b_e2 VARCHAR(100),
    fon_b_e2 VARCHAR(100),
    fax_b_e2 VARCHAR(100),
    mobil_b_e2 VARCHAR(100),
    mail_b_e2 VARCHAR(250),
    url_b_e2 VARCHAR(250),
    function_b_e3 VARCHAR(250),
    company_b_e3 VARCHAR(250),
    street_b_e3 VARCHAR(100),
    zipcode_b_e3 VARCHAR(50),
    state_b_e3 VARCHAR(100),
    city_b_e3 VARCHAR(100),
    country_b_e3 VARCHAR(100),
    pobox_b_e3 VARCHAR(50),
    poboxzipcode_b_e3 VARCHAR(50),
    suffix1_b_e3 VARCHAR(100),
    suffix2_b_e3 VARCHAR(100),
    fon_b_e3 VARCHAR(100),
    fax_b_e3 VARCHAR(100),
    mobil_b_e3 VARCHAR(100),
    mail_b_e3 VARCHAR(250),
    url_b_e3 VARCHAR(250),
    function_c_e1 VARCHAR(250),
    company_c_e1 VARCHAR(250),
    street_c_e1 VARCHAR(100),
    zipcode_c_e1 VARCHAR(50),
    state_c_e1 VARCHAR(100),
    city_c_e1 VARCHAR(100),
    country_c_e1 VARCHAR(100),
    pobox_c_e1 VARCHAR(50),
    poboxzipcode_c_e1 VARCHAR(50),
    suffix1_c_e1 VARCHAR(100),
    suffix2_c_e1 VARCHAR(100),
    fon_c_e1 VARCHAR(100),
    fax_c_e1 VARCHAR(100),
    mobil_c_e1 VARCHAR(100),
    mail_c_e1 VARCHAR(250),
    url_c_e1 VARCHAR(250),
    function_c_e2 VARCHAR(250),
    company_c_e2 VARCHAR(250),
    street_c_e2 VARCHAR(100),
    zipcode_c_e2 VARCHAR(50),
    state_c_e2 VARCHAR(100),
    city_c_e2 VARCHAR(100),
    country_c_e2 VARCHAR(100),
    pobox_c_e2 VARCHAR(50),
    poboxzipcode_c_e2 VARCHAR(50),
    suffix1_c_e2 VARCHAR(100),
    suffix2_c_e2 VARCHAR(100),
    fon_c_e2 VARCHAR(100),
    fax_c_e2 VARCHAR(100),
    mobil_c_e2 VARCHAR(100),
    mail_c_e2 VARCHAR(250),
    url_c_e2 VARCHAR(250),
    function_c_e3 VARCHAR(250),
    company_c_e3 VARCHAR(250),
    street_c_e3 VARCHAR(100),
    zipcode_c_e3 VARCHAR(50),
    state_c_e3 VARCHAR(100),
    city_c_e3 VARCHAR(100),
    country_c_e3 VARCHAR(100),
    pobox_c_e3 VARCHAR(50),
    poboxzipcode_c_e3 VARCHAR(50),
    suffix1_c_e3 VARCHAR(100),
    suffix2_c_e3 VARCHAR(100),
    fon_c_e3 VARCHAR(100),
    fax_c_e3 VARCHAR(100),
    mobil_c_e3 VARCHAR(100),
    mail_c_e3 VARCHAR(250),
    url_c_e3 VARCHAR(250),
    CONSTRAINT tperson_pkey PRIMARY KEY (pk)
) WITH OIDS;

CREATE INDEX tperson_bothnames_a_e1_index ON veraweb.tperson (lastname_a_e1,firstname_a_e1);

CREATE INDEX tperson_firstname_a_e1_index ON veraweb.tperson (firstname_a_e1);

CREATE INDEX tperson_lastname_a_e1_index ON veraweb.tperson (lastname_a_e1);

CREATE INDEX tperson_firstname_a_e2_index ON veraweb.tperson (firstname_a_e2);

CREATE INDEX tperson_lastname_a_e2_index ON veraweb.tperson (lastname_a_e2);

CREATE INDEX tperson_firstname_a_e3_index ON veraweb.tperson (firstname_a_e3);

CREATE INDEX tperson_lastname_a_e3_index ON veraweb.tperson (lastname_a_e3);

CREATE INDEX tperson_firstname_b_e1_index ON veraweb.tperson (firstname_b_e1);

CREATE INDEX tperson_lastname_b_e1_index ON veraweb.tperson (lastname_b_e1);

CREATE INDEX tperson_firstname_b_e2_index ON veraweb.tperson (firstname_b_e2);

CREATE INDEX tperson_lastname_b_e2_index ON veraweb.tperson (lastname_b_e2);

CREATE INDEX tperson_firstname_b_e3_index ON veraweb.tperson (firstname_b_e3);

CREATE INDEX tperson_lastname_b_e3_index ON veraweb.tperson (lastname_b_e3);

/* ---------------------------------------------------------------------- */
/* Add table "tproxy"                                                     */
/* ---------------------------------------------------------------------- */

CREATE TABLE veraweb.tproxy (
    pk INTEGER DEFAULT nextval('tproxy_pk_seq')  NOT NULL,
    fk_user INTEGER  NOT NULL,
    proxy VARCHAR(100)  NOT NULL,
    validfrom TIMESTAMP WITH TIME ZONE,
    validtill TIMESTAMP WITH TIME ZONE,
    CONSTRAINT tproxy_pkey PRIMARY KEY (pk)
) WITH OIDS;

/* ---------------------------------------------------------------------- */
/* Foreign key constraints                                                */
/* ---------------------------------------------------------------------- */

ALTER TABLE veraweb.tperson ADD CONSTRAINT tperson_fkey_workarea 
    FOREIGN KEY (fk_workarea) REFERENCES veraweb.tworkarea (pk) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE veraweb.tproxy ADD CONSTRAINT tproxy_fkey_user 
    FOREIGN KEY (fk_user) REFERENCES veraweb.tuser (pk) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE veraweb.tworkarea ADD CONSTRAINT tworkarea_fkey_orgunit 
    FOREIGN KEY (fk_orgunit) REFERENCES veraweb.torgunit (pk) ON DELETE RESTRICT ON UPDATE RESTRICT;

/* ---------------------------------------------------------------------- */
/* Procedures                                                             */
/* ---------------------------------------------------------------------- */

CREATE OR REPLACE FUNCTION veraweb.serv_build_sequences () RETURNS integer AS
$BODY$
	DECLARE
		vpos1 int4;
		vpos2 int4;
		vcount int4;
		vseq RECORD;
		vtable varchar;
		vpk varchar;
		vresult varchar;
		vmaxid int4;
		vstmt varchar;
	BEGIN
		vcount := 0;
		vpos2 := 0;
		vpos1 := 0;
		vresult := '';
		vpk := '';

		PERFORM SETVAL('veraweb.tresult_id_seq', (SELECT MAX(id) FROM veraweb.tresult));
		PERFORM SETVAL('veraweb.tupdate_id_seq', (SELECT MAX(id) FROM veraweb.tupdate));

		FOR vseq IN SELECT * FROM pg_catalog.pg_statio_user_sequences WHERE schemaname = 'veraweb' LOOP
			SELECT position('pk_'  in vseq.relname) into vpos1;
			SELECT position('_seq' in vseq.relname) into vpos2;
			IF vpos2 > vpos1 AND (vpos1 - 2) > 0 THEN
				SELECT substring(vseq.relname from 1 for (vpos1 - 2)) into vtable;
				SELECT substring(vseq.relname from vpos1 for (vpos2 - vpos1)) into vpk;
				vcount := vcount + 1;
				vstmt := 'SELECT SETVAL(''veraweb.' || vseq.relname || ''', ' || '(SELECT MAX(' || vpk || ') FROM veraweb.' || vtable || '));';
				--INSERT INTO colibri.tresult(value) VALUES (vstmt);
				EXECUTE vstmt;
			ELSE
				INSERT INTO veraweb.tresult(value) VALUES ('ERROR FINDING SEQUENCE NAME ' || vseq.relname);
		END IF;
	END LOOP;

	RETURN vcount;
END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION veraweb.serv_transform_column (VARCHAR, VARCHAR, VARCHAR) RETURNS integer AS
$BODY$
		DECLARE
		BEGIN
			EXECUTE 'ALTER TABLE ' || $1 || ' RENAME ' || $2 || ' TO temp';
			EXECUTE 'ALTER TABLE ' || $1 || ' ADD COLUMN ' || $2 || ' ' || $3;
			EXECUTE 'UPDATE ' || $1 || ' SET ' || $2 || ' = temp';
			EXECUTE 'ALTER TABLE ' || $1 || ' DROP COLUMN temp';
			RETURN 0;
		END;$BODY$
LANGUAGE 'plpgsql' VOLATILE;

CREATE OR REPLACE FUNCTION veraweb.upper_fix (VARCHAR) RETURNS VARCHAR AS
$BODY$
		DECLARE
		BEGIN
			RETURN replace(replace(replace(upper($1), 'ä', 'Ä'), 'ö', 'Ö'), 'ü', 'Ü');
		END;$BODY$
LANGUAGE 'plpgsql' IMMUTABLE;

CREATE OR REPLACE FUNCTION veraweb.lower_fix (VARCHAR) RETURNS VARCHAR AS
$BODY$
		DECLARE
		BEGIN
			RETURN replace(replace(replace(lower($1), 'Ä', 'ä'), 'Ö', 'ö'), 'Ü', 'ü');
		END;$BODY$
LANGUAGE 'plpgsql' IMMUTABLE;


/* ---------------------------------------------------------------------- */
/* Set schema version                                                     */
/* ---------------------------------------------------------------------- */

INSERT INTO veraweb.tconfig (cvalue, cname) VALUES ('2013-02-21', 'SCHEMA_VERSION');


/* ---------------------------------------------------------------------- */
/* Set default workarea                                                   */
/* ---------------------------------------------------------------------- */

INSERT INTO veraweb.tworkarea (name) VALUES('Kein');
UPDATE veraweb.tworkarea SET pk = 0 WHERE name = 'Kein';
