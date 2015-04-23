-- Create table "tevent_function"

CREATE TABLE veraweb.tevent_function (
  pk serial NOT NULL,
  fk_event int4 NOT NULL DEFAULT 0,
  fk_function int4 NOT NULL DEFAULT 0,
  CONSTRAINT tevent_function_pkey PRIMARY KEY (pk)
) WITH OIDS;

-- Create table "tevent_category"

CREATE TABLE veraweb.tevent_category (
  pk serial NOT NULL,
  fk_event int4 NOT NULL DEFAULT 0,
  fk_category int4 NOT NULL DEFAULT 0,
  CONSTRAINT tevent_category_pkey PRIMARY KEY (pk)
) WITH OIDS;

-- Update schema version

UPDATE veraweb.tconfig SET cvalue = '2015-04-23' WHERE cname = 'SCHEMA_VERSION';
