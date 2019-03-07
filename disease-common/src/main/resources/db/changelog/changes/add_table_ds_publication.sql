CREATE TABLE ds_publication (
	id bigserial NOT NULL,
	pub_type varchar(63) NOT NULL,
	pub_id   varchar(255) NOT NULL,
	ds_protein_id int8 NULL,
	ds_disease_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_pub_pk PRIMARY KEY (id),
	CONSTRAINT pub_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES ds_disease(id),
	CONSTRAINT pub_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES ds_protein(id)
);
