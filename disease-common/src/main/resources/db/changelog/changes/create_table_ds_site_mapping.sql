CREATE TABLE ds_site_mapping (
	id bigserial NOT NULL,
	accession varchar NOT NULL,
	protein_id varchar NOT NULL,
	site_position int8 NOT NULL,
	position_in_alignment int8 NOT NULL,
	site_type varchar NULL,
	uniref_id varchar NOT NULL,
	mapped_site text NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_site_mapping_pk PRIMARY KEY (id)
);
CREATE INDEX ds_site_mapping_accession_idx ON ds_site_mapping USING btree (accession);