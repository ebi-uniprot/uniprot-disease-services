
ALTER TABLE ds_disease ADD parent_id int8 NULL;

ALTER TABLE ds_disease ADD CONSTRAINT ds_disease_ds_disease_fk FOREIGN KEY (parent_id) REFERENCES ds_disease(id);
