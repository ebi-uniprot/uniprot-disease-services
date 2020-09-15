ALTER TABLE ds_drug ADD chembl_disease_id varchar(255) NOT NULL;
ALTER TABLE ds_drug ADD ds_disease_id int8 NULL;
ALTER TABLE ds_drug ADD CONSTRAINT ds_drug_fk FOREIGN KEY (ds_disease_id) REFERENCES ds_disease(id);
