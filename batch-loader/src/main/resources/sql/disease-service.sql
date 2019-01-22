-- Drop table

-- DROP TABLE disease_service.ds_disease

CREATE TABLE disease_service.ds_disease (
	id bigserial NOT NULL,
	disease_id varchar NULL,
	disease_name varchar NULL,
	description varchar NULL,
	acronym varchar NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_disease_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE disease_service.ds_evidence

CREATE TABLE disease_service.ds_evidence (
	id bigserial NOT NULL,
	evidence_id varchar NULL,
	evidence_type varchar NULL,
	evidence_attribute varchar NULL,
	evidence_code varchar NULL,
	use_eco_code bool NULL,
	type_value varchar NULL,
	has_type_value bool NULL,
	CONSTRAINT ds_evidence_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE disease_service.ds_feature_location

CREATE TABLE disease_service.ds_feature_location (
	id bigserial NOT NULL,
	start_modifier varchar NULL,
	end_modifier varchar NULL,
	"start" int4 NULL,
	"end" int4 NULL,
	CONSTRAINT ds_feature_location_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE disease_service.ds_interaction

CREATE TABLE disease_service.ds_interaction (
	id bigserial NOT NULL,
	interaction_type varchar NULL,
	accession varchar NULL,
	gene varchar NULL,
	experiment_count int4 NULL,
	first_interactor varchar NULL,
	second_interactor varchar NULL,
	ds_protein_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_interaction_pk PRIMARY KEY (id),
	CONSTRAINT interaction_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_pathway

CREATE TABLE disease_service.ds_pathway (
	id bigserial NOT NULL,
	primary_id varchar NULL,
	description varchar NULL,
	db_type varchar NULL,
	isoform_id varchar NULL,
	third varchar NULL,
	fourth varchar NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	ds_protein_id int8 NULL,
	CONSTRAINT ds_pathway_pk PRIMARY KEY (id),
	CONSTRAINT pathway_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_protein

CREATE TABLE disease_service.ds_protein (
	id bigserial NOT NULL,
	protein_id varchar NULL,
	protein_name varchar NULL,
	accession varchar NULL,
	gene varchar NULL,
	description varchar NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_protein_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE disease_service.ds_synonyms

CREATE TABLE disease_service.ds_synonyms (
	id bigserial NOT NULL,
	disease_name varchar NULL,
	ds_disease_id int8 NULL,
	CONSTRAINT ds_synonyms_pk PRIMARY KEY (id),
	CONSTRAINT ds_synonyms_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_variant

CREATE TABLE disease_service.ds_variant (
	id bigserial NOT NULL,
	original_sequence varchar NULL,
	alternate_sequence varchar NULL,
	feature_id varchar NULL,
	variant_report varchar NULL,
	ds_feature_location_id int8 NULL,
	feature_status varchar NULL,
	ds_evidence_id int8 NULL,
	ds_protein_id int8 NULL,
	ds_disease_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_variant_pk PRIMARY KEY (id),
	CONSTRAINT variant_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id),
	CONSTRAINT variant_evidence_fk FOREIGN KEY (ds_evidence_id) REFERENCES disease_service.ds_evidence(id),
	CONSTRAINT variant_feature_location_fk FOREIGN KEY (ds_feature_location_id) REFERENCES disease_service.ds_feature_location(id),
	CONSTRAINT variant_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_disease_protein

CREATE TABLE disease_service.ds_disease_protein (
	ds_disease_id int8 NOT NULL,
	ds_protein_id int8 NOT NULL,
	CONSTRAINT ds_disease_protein_un UNIQUE (ds_disease_id, ds_protein_id),
	CONSTRAINT ds_disease_protein_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id),
	CONSTRAINT ds_disease_protein_ds_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);
