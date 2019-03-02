CREATE TABLE ds_keyword (
	id bigserial NOT NULL,
	key_id varchar(63) NOT NULL,
	key_value varchar(255) NOT NULL,
	ds_disease_id int8 NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_keyword_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES ds_disease(id)
);
CREATE INDEX ds_keyword_key_value_idx ON ds_keyword USING btree (key_value);
