CREATE TABLE ds_drug (
	id bigserial NOT NULL,
	name varchar(255) NOT NULL,
	source_type   varchar(63) NOT NULL,
	source_id varchar(123) NULL,
	molecule_type varchar(63) NULL,
	ds_protein_cross_ref_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_drug_pk PRIMARY KEY (id),
	CONSTRAINT drug_prot_cross_ref_fk FOREIGN KEY (ds_protein_cross_ref_id) REFERENCES ds_protein_cross_ref(id)
);
