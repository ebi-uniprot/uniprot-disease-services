-- Drop table

DROP TABLE disease.up_disease

CREATE TABLE disease.up_disease (
	id serial NOT NULL,
	identifier varchar NOT NULL,
	acronym varchar NULL,
	definition text NULL,
	accession varchar NOT NULL,
	CONSTRAINT up_disease_pk PRIMARY KEY (id)
);
CREATE INDEX up_disease_accession_idx ON disease.up_disease USING btree (accession);
CREATE INDEX up_disease_identifier_idx ON disease.up_disease USING btree (identifier);

-- Drop table

DROP TABLE disease.up_cross_reference

CREATE TABLE disease.up_cross_reference (
	id serial NOT NULL,
	ref_type varchar NOT NULL,
	ref_id varchar NOT NULL,
	disease_id int4 NOT NULL,
	ref_meta varchar NULL,
	CONSTRAINT sp_cross_reference_up_disease_fk FOREIGN KEY (disease_id) REFERENCES disease.up_disease(id)
);

-- Drop table

DROP TABLE disease.up_alternative_name

CREATE TABLE disease.up_alternative_name (
	id serial NOT NULL,
	synonym varchar NOT NULL,
	disease_id int4 NOT NULL,
	CONSTRAINT up_other_name_up_disease_fk FOREIGN KEY (disease_id) REFERENCES disease.up_disease(id)
);

-- Drop table

DROP TABLE disease.up_keyword

CREATE TABLE disease.up_keyword (
	id serial NOT NULL,
	key_id varchar NOT NULL,
	key_value varchar NOT NULL,
	disease_id int4 NOT NULL,
	CONSTRAINT up_keyword_up_disease_fk FOREIGN KEY (disease_id) REFERENCES disease.up_disease(id)
);
CREATE INDEX up_keyword_key_id_idx ON disease.up_keyword USING btree (key_id);
