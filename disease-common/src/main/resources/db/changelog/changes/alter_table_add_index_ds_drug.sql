CREATE INDEX ds_drug_name_idx ON ds_drug ("name");
CREATE INDEX ds_drug_ds_protein_cross_ref_id_idx ON ds_drug USING btree (ds_protein_cross_ref_id);
