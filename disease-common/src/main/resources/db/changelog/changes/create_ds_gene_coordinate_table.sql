
CREATE TABLE ds_gene_coordinate (
	id bigserial NOT NULL,
	chromosome_number varchar(63) NULL,
	gene_start int8 NOT NULL,
	gene_end int8 NOT NULL,
	ensembl_gene_id varchar(127) NOT NULL,
	ensembl_transcript_id varchar(127) NOT NULL,
	ensembl_translation_id varchar(127) NOT NULL,
	ds_protein_id int8 NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp NOT NULL,
	CONSTRAINT ds_gene_coordinate_pk PRIMARY KEY (id),
	CONSTRAINT ds_gene_coordinate_ds_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES ds_protein(id)
);
