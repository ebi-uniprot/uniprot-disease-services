CREATE TABLE ds_disease_descendent (
	ds_disease_id int8 NOT NULL,
	ds_descendent_id int8 NOT NULL,
	CONSTRAINT ds_disease_descendent_un UNIQUE (ds_disease_id, ds_descendent_id)
);
CREATE INDEX ds_disease_descendent_ds_disease_id_idx ON ds_disease_descendent USING btree (ds_disease_id);
CREATE INDEX ds_disease_descendent_ds_disease_descendent_idx ON ds_disease_descendent USING btree (ds_descendent_id);
ALTER TABLE ds_disease_descendent ADD CONSTRAINT ds_disease_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES ds_disease(id) on delete cascade;
ALTER TABLE ds_disease_descendent ADD CONSTRAINT ds_disease_disease_descendent_fk FOREIGN KEY (ds_descendent_id) REFERENCES ds_disease(id) on delete cascade;