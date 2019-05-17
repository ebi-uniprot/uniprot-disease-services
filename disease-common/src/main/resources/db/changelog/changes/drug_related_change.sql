ALTER TABLE ds_drug ADD clinical_trial_phase smallint NULL;
ALTER TABLE ds_drug ADD mechanism_of_action varchar(255) NULL;
ALTER TABLE ds_drug ADD clinical_trial_link varchar(255) NULL;

-- Add a new table
CREATE TABLE ds_drug_evidence (
	id bigserial NOT NULL,
	ref_type varchar(64) NULL,
	ref_url varchar(255) NULL,
	ds_drug_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_drug_evidence_pk PRIMARY KEY (id),
	CONSTRAINT ds_evidence_drug_fk FOREIGN KEY (ds_drug_id) REFERENCES ds_drug(id)

);
COMMENT ON TABLE ds_drug_evidence IS 'table to store evidence of the drug e.g. pubmed urls';
