-- Drop table

CREATE TABLE ds_disease_relation (
	ds_disease_id int8 NOT NULL,
	ds_disease_parent_id int8 NOT NULL,
	CONSTRAINT ds_disease_relation_un UNIQUE (ds_disease_id, ds_disease_parent_id),
	CONSTRAINT ds_disease_relation_ds_disease_fk1 FOREIGN KEY (ds_disease_id) REFERENCES ds_disease(id),
	CONSTRAINT ds_disease_relation_ds_disease_fk2 FOREIGN KEY (ds_disease_parent_id) REFERENCES ds_disease(id)
);
