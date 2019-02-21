-- make the field not null
ALTER TABLE ds_synonym ALTER COLUMN ds_disease_id SET NOT NULL;

-- add a unique key constraint
ALTER TABLE ds_synonym ADD CONSTRAINT ds_synonym_un UNIQUE (disease_name,ds_disease_id);