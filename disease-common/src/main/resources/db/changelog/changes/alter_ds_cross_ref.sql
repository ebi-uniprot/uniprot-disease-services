-- make the field not null
ALTER TABLE ds_cross_ref ALTER COLUMN ds_disease_id SET NOT NULL;

-- add a unique key constraint
ALTER TABLE ds_cross_ref ADD CONSTRAINT ds_cross_ref_un UNIQUE (ref_type, ref_id, ds_disease_id);