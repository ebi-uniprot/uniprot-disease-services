-- Drop table

-- DROP TABLE disease_service.ds_cross_ref

CREATE TABLE ds_cross_ref (
	id bigserial NOT NULL,
	ref_type varchar NOT NULL,
	ref_id varchar NOT NULL,
	ds_disease_id int8 NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_cross_ref_pk PRIMARY KEY (id),
	CONSTRAINT ds_cross_ref_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES ds_disease(id)
);
