ALTER TABLE ds_variant DROP COLUMN ds_evidence_id;
-- ALTER TABLE disease_service.ds_variant DROP CONSTRAINT variant_evidence_fk;

ALTER TABLE ds_evidence ADD ds_variant_id int8 NULL;
ALTER TABLE ds_evidence ADD CONSTRAINT ds_evidence_ds_variant_fk FOREIGN KEY (ds_variant_id) REFERENCES disease_service.ds_variant(id);