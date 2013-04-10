CREATE TABLE veraweb.ttask (
	pk serial NOT NULL,
	title varchar(100) NOT NULL,
	description varchar(1000),
	startdate timestamptz,
	enddate timestamptz,
	degree_of_completion integer DEFAULT 0,
	priority integer,
	fk_person integer references tperson (pk),
	createdby varchar(50),
	changedby varchar(50),
	created timestamptz,
	changed timestamptz,
	CONSTRAINT ttask_pkey PRIMARY KEY (pk)
) WITH OIDS;