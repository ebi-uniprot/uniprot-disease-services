-- DROP SCHEMA disease_service;

CREATE SCHEMA disease_service AUTHORIZATION variant;

-- Drop table

-- DROP TABLE disease_service.batch_job_execution;

CREATE TABLE disease_service.batch_job_execution (
	job_execution_id int8 NOT NULL,
	"version" int8 NULL,
	job_instance_id int8 NOT NULL,
	create_time timestamp NOT NULL,
	start_time timestamp NULL,
	end_time timestamp NULL,
	status varchar(10) NULL,
	exit_code varchar(2500) NULL,
	exit_message varchar(2500) NULL,
	last_updated timestamp NULL,
	job_configuration_location varchar(2500) NULL,
	CONSTRAINT batch_job_execution_pkey PRIMARY KEY (job_execution_id),
	CONSTRAINT job_inst_exec_fk FOREIGN KEY (job_instance_id) REFERENCES disease_service.batch_job_instance(job_instance_id)
);

-- Drop table

-- DROP TABLE disease_service.batch_job_execution_context;

CREATE TABLE disease_service.batch_job_execution_context (
	job_execution_id int8 NOT NULL,
	short_context varchar(2500) NOT NULL,
	serialized_context text NULL,
	CONSTRAINT batch_job_execution_context_pkey PRIMARY KEY (job_execution_id),
	CONSTRAINT job_exec_ctx_fk FOREIGN KEY (job_execution_id) REFERENCES disease_service.batch_job_execution(job_execution_id)
);

-- Drop table

-- DROP TABLE disease_service.batch_job_execution_params;

CREATE TABLE disease_service.batch_job_execution_params (
	job_execution_id int8 NOT NULL,
	type_cd varchar(6) NOT NULL,
	key_name varchar(100) NOT NULL,
	string_val varchar(250) NULL,
	date_val timestamp NULL,
	long_val int8 NULL,
	double_val float8 NULL,
	identifying bpchar(1) NOT NULL,
	CONSTRAINT job_exec_params_fk FOREIGN KEY (job_execution_id) REFERENCES disease_service.batch_job_execution(job_execution_id)
);

-- Drop table

-- DROP TABLE disease_service.batch_job_instance;

CREATE TABLE disease_service.batch_job_instance (
	job_instance_id int8 NOT NULL,
	"version" int8 NULL,
	job_name varchar(100) NOT NULL,
	job_key varchar(32) NOT NULL,
	CONSTRAINT batch_job_instance_pkey PRIMARY KEY (job_instance_id),
	CONSTRAINT job_inst_un UNIQUE (job_name, job_key)
);

-- Drop table

-- DROP TABLE disease_service.batch_step_execution;

CREATE TABLE disease_service.batch_step_execution (
	step_execution_id int8 NOT NULL,
	"version" int8 NOT NULL,
	step_name varchar(100) NOT NULL,
	job_execution_id int8 NOT NULL,
	start_time timestamp NOT NULL,
	end_time timestamp NULL,
	status varchar(10) NULL,
	commit_count int8 NULL,
	read_count int8 NULL,
	filter_count int8 NULL,
	write_count int8 NULL,
	read_skip_count int8 NULL,
	write_skip_count int8 NULL,
	process_skip_count int8 NULL,
	rollback_count int8 NULL,
	exit_code varchar(2500) NULL,
	exit_message varchar(2500) NULL,
	last_updated timestamp NULL,
	CONSTRAINT batch_step_execution_pkey PRIMARY KEY (step_execution_id),
	CONSTRAINT job_exec_step_fk FOREIGN KEY (job_execution_id) REFERENCES disease_service.batch_job_execution(job_execution_id)
);

-- Drop table

-- DROP TABLE disease_service.batch_step_execution_context;

CREATE TABLE disease_service.batch_step_execution_context (
	step_execution_id int8 NOT NULL,
	short_context varchar(2500) NOT NULL,
	serialized_context text NULL,
	CONSTRAINT batch_step_execution_context_pkey PRIMARY KEY (step_execution_id),
	CONSTRAINT step_exec_ctx_fk FOREIGN KEY (step_execution_id) REFERENCES disease_service.batch_step_execution(step_execution_id)
);

-- Drop table

-- DROP TABLE disease_service.databasechangelog;

CREATE TABLE disease_service.databasechangelog (
	id varchar(255) NOT NULL,
	author varchar(255) NOT NULL,
	filename varchar(255) NOT NULL,
	dateexecuted timestamp NOT NULL,
	orderexecuted int4 NOT NULL,
	exectype varchar(10) NOT NULL,
	md5sum varchar(35) NULL,
	description varchar(255) NULL,
	"comments" varchar(255) NULL,
	tag varchar(255) NULL,
	liquibase varchar(20) NULL,
	contexts varchar(255) NULL,
	labels varchar(255) NULL
);

-- Drop table

-- DROP TABLE disease_service.databasechangeloglock;

CREATE TABLE disease_service.databasechangeloglock (
	id int4 NOT NULL,
	"locked" bool NOT NULL,
	lockgranted timestamp NULL,
	lockedby varchar(255) NULL,
	CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE disease_service.ds_cross_ref;

CREATE TABLE disease_service.ds_cross_ref (
	id bigserial NOT NULL,
	ref_type varchar NOT NULL,
	ref_id varchar NOT NULL,
	ds_disease_id int8 NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	source_name varchar(255) NOT NULL,
	CONSTRAINT ds_cross_ref_pk PRIMARY KEY (id),
	CONSTRAINT ds_cross_ref_un UNIQUE (ref_type, ref_id, ds_disease_id),
	CONSTRAINT ds_cross_ref_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_disease;

CREATE TABLE disease_service.ds_disease (
	id bigserial NOT NULL,
	disease_id varchar(255) NOT NULL,
	disease_name varchar(255) NOT NULL,
	description text NULL,
	acronym varchar(255) NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	source_name varchar(255) NOT NULL,
	note varchar(4000) NULL,
	CONSTRAINT ds_disease_pk PRIMARY KEY (id),
	CONSTRAINT ds_disease_un UNIQUE (disease_id)
);

-- Drop table

-- DROP TABLE disease_service.ds_disease_protein;

CREATE TABLE disease_service.ds_disease_protein (
	ds_disease_id int8 NOT NULL,
	ds_protein_id int8 NOT NULL,
	is_mapped bool NOT NULL DEFAULT false,
	CONSTRAINT ds_disease_protein_un UNIQUE (ds_disease_id, ds_protein_id),
	CONSTRAINT ds_disease_protein_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id),
	CONSTRAINT ds_disease_protein_ds_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_disease_relation;

CREATE TABLE disease_service.ds_disease_relation (
	ds_disease_id int8 NOT NULL,
	ds_disease_parent_id int8 NOT NULL,
	CONSTRAINT ds_disease_relation_un UNIQUE (ds_disease_id, ds_disease_parent_id),
	CONSTRAINT ds_disease_relation_ds_disease_fk1 FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id),
	CONSTRAINT ds_disease_relation_ds_disease_fk2 FOREIGN KEY (ds_disease_parent_id) REFERENCES disease_service.ds_disease(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_drug;

CREATE TABLE disease_service.ds_drug (
	id bigserial NOT NULL,
	"name" varchar(255) NOT NULL,
	source_type varchar(63) NOT NULL,
	source_id varchar(123) NULL,
	molecule_type varchar(63) NULL,
	ds_protein_cross_ref_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	clinical_trial_phase int2 NULL,
	mechanism_of_action varchar(255) NULL,
	clinical_trial_link varchar(255) NULL,
	CONSTRAINT ds_drug_pk PRIMARY KEY (id),
	CONSTRAINT drug_prot_cross_ref_fk FOREIGN KEY (ds_protein_cross_ref_id) REFERENCES disease_service.ds_protein_cross_ref(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_drug_evidence;

CREATE TABLE disease_service.ds_drug_evidence (
	id bigserial NOT NULL,
	ref_type varchar(64) NULL,
	ref_url varchar(255) NULL,
	ds_drug_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_drug_evidence_pk PRIMARY KEY (id),
	CONSTRAINT ds_evidence_drug_fk FOREIGN KEY (ds_drug_id) REFERENCES disease_service.ds_drug(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_evidence;

CREATE TABLE disease_service.ds_evidence (
	id bigserial NOT NULL,
	evidence_id varchar(255) NULL,
	evidence_type varchar(255) NULL,
	evidence_attribute varchar(255) NULL,
	evidence_code varchar(255) NULL,
	use_eco_code bool NULL,
	type_value varchar(255) NULL,
	has_type_value bool NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	ds_variant_id int8 NULL,
	CONSTRAINT ds_evidence_pk PRIMARY KEY (id),
	CONSTRAINT ds_evidence_ds_variant_fk FOREIGN KEY (ds_variant_id) REFERENCES disease_service.ds_variant(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_feature_location;

CREATE TABLE disease_service.ds_feature_location (
	id bigserial NOT NULL,
	start_modifier varchar(255) NULL,
	end_modifier varchar(255) NULL,
	start_id int4 NULL,
	end_id int4 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_feature_location_pk PRIMARY KEY (id)
);

-- Drop table

-- DROP TABLE disease_service.ds_gene_coordinate;

CREATE TABLE disease_service.ds_gene_coordinate (
	id bigserial NOT NULL,
	chromosome_number varchar(63) NULL,
	gene_start int8 NOT NULL,
	gene_end int8 NOT NULL,
	ensembl_gene_id varchar(127) NOT NULL,
	ensembl_transcript_id varchar(127) NOT NULL,
	ensembl_translation_id varchar(127) NOT NULL,
	ds_protein_id int8 NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_gene_coordinate_pk PRIMARY KEY (id),
	CONSTRAINT ds_gene_coordinate_ds_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_interaction;

CREATE TABLE disease_service.ds_interaction (
	id bigserial NOT NULL,
	interaction_type varchar(255) NOT NULL,
	accession varchar(255) NULL,
	gene varchar(255) NULL,
	experiment_count int4 NULL,
	first_interactor varchar(255) NULL,
	second_interactor varchar(255) NULL,
	ds_protein_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_interaction_pk PRIMARY KEY (id),
	CONSTRAINT interaction_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_keyword;

CREATE TABLE disease_service.ds_keyword (
	id bigserial NOT NULL,
	key_id varchar(63) NOT NULL,
	key_value varchar(255) NOT NULL,
	ds_disease_id int8 NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_keyword_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id)
);
CREATE INDEX ds_keyword_key_value_idx ON disease_service.ds_keyword USING btree (key_value);

-- Drop table

-- DROP TABLE disease_service.ds_protein;

CREATE TABLE disease_service.ds_protein (
	id bigserial NOT NULL,
	protein_id varchar(255) NOT NULL,
	protein_name varchar(255) NOT NULL,
	accession varchar(255) NOT NULL,
	gene varchar(255) NULL,
	description text NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_protein_pk PRIMARY KEY (id),
	CONSTRAINT ds_protein_un1 UNIQUE (protein_id),
	CONSTRAINT ds_protein_un2 UNIQUE (accession)
);

-- Drop table

-- DROP TABLE disease_service.ds_protein_cross_ref;

CREATE TABLE disease_service.ds_protein_cross_ref (
	id bigserial NOT NULL,
	primary_id varchar(255) NULL,
	description varchar(255) NULL,
	db_type varchar(255) NULL,
	isoform_id varchar(255) NULL,
	third varchar(255) NULL,
	fourth varchar(255) NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	ds_protein_id int8 NULL,
	CONSTRAINT ds_pathway_pk PRIMARY KEY (id),
	CONSTRAINT pathway_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_publication;

CREATE TABLE disease_service.ds_publication (
	id bigserial NOT NULL,
	pub_type varchar(63) NOT NULL,
	pub_id varchar(255) NOT NULL,
	ds_protein_id int8 NULL,
	ds_disease_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_pub_pk PRIMARY KEY (id),
	CONSTRAINT pub_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id),
	CONSTRAINT pub_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_synonym;

CREATE TABLE disease_service.ds_synonym (
	id bigserial NOT NULL,
	disease_name varchar(255) NOT NULL,
	ds_disease_id int8 NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	source_name varchar(255) NOT NULL,
	CONSTRAINT ds_synonym_un UNIQUE (disease_name, ds_disease_id),
	CONSTRAINT ds_synonyms_pk PRIMARY KEY (id),
	CONSTRAINT ds_synonyms_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id)
);

-- Drop table

-- DROP TABLE disease_service.ds_variant;

CREATE TABLE disease_service.ds_variant (
	id bigserial NOT NULL,
	original_sequence varchar(255) NULL,
	alternate_sequence varchar(255) NULL,
	feature_id varchar(255) NULL,
	variant_report text NULL,
	ds_feature_location_id int8 NULL,
	feature_status varchar(255) NULL,
	ds_protein_id int8 NULL,
	ds_disease_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_variant_pk PRIMARY KEY (id),
	CONSTRAINT variant_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id),
	CONSTRAINT variant_feature_location_fk FOREIGN KEY (ds_feature_location_id) REFERENCES disease_service.ds_feature_location(id),
	CONSTRAINT variant_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id)
);
