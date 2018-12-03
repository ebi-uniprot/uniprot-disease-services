-- Create the schema
DROP SCHEMA IF EXISTS disease;
CREATE SCHEMA disease AUTHORIZATION variant;

-- Drop table

DROP TABLE IF EXISTS disease.dgn_disease_mapping

CREATE TABLE disease.dgn_disease_mapping (
	disease_id varchar NOT NULL,
	disease_name varchar NOT NULL,
	vocabulary varchar NOT NULL,
	code varchar NOT NULL,
	vocabulary_name varchar NOT NULL
);
CREATE INDEX dgn_disease_mapping_disease_id_idx ON disease.dgn_disease_mapping USING btree (disease_id);

-- Drop table

DROP TABLE IF EXISTS disease.dgn_gda

CREATE TABLE disease.dgn_gda (
	gene_id int8 NOT NULL,
	gene_symbol varchar NOT NULL,
	disease_id varchar NOT NULL,
	disease_name varchar NOT NULL,
	score float8 NOT NULL,
	no_of_pmids int4 NOT NULL,
	no_of_snps int4 NOT NULL,
	data_source varchar NOT NULL
);
CREATE INDEX dgn_gda_disease_id_idx ON disease.dgn_gda USING btree (disease_id);
CREATE INDEX dgn_gda_gene_id_idx ON disease.dgn_gda USING btree (gene_id);

-- Drop table

DROP TABLE IF EXISTS disease.dgn_gdpa

CREATE TABLE disease.dgn_gdpa (
	gene_id int8 NOT NULL,
	disease_id varchar NOT NULL,
	pmid int8 NULL,
	gene_symbol varchar NOT NULL,
	disease_name varchar NOT NULL,
	disease_type varchar NOT NULL,
	association_type varchar NOT NULL,
	sentence text NULL,
	score float8 NOT NULL,
	data_source varchar NOT NULL
);
CREATE INDEX dgn_gdpa_disease_id_idx ON disease.dgn_gdpa USING btree (disease_id);
CREATE INDEX dgn_gdpa_gene_id_idx ON disease.dgn_gdpa USING btree (gene_id);

-- Drop table

DROP TABLE IF EXISTS disease.dgn_uniprot_gene

CREATE TABLE disease.dgn_uniprot_gene (
	uniprot_id varchar NOT NULL,
	gene_id int8 NOT NULL
);
CREATE INDEX dgn_uniprot_gene_gene_id_idx ON disease.dgn_uniprot_gene USING btree (gene_id);
CREATE INDEX dgn_uniprot_gene_uniprot_id_idx ON disease.dgn_uniprot_gene USING btree (uniprot_id);

-- Drop table

DROP TABLE IF EXISTS disease.dgn_vda

CREATE TABLE disease.dgn_vda (
	snp_id varchar NOT NULL,
	disease_id varchar NOT NULL,
	disease_name varchar NOT NULL,
	score float8 NOT NULL,
	no_of_pmids int4 NOT NULL,
	data_source varchar NOT NULL
);
CREATE INDEX dgn_vda_disease_id_idx ON disease.dgn_vda USING btree (disease_id);
CREATE INDEX dgn_vda_snp_id_idx ON disease.dgn_vda USING btree (snp_id);

-- Drop table

DROP TABLE IF EXISTS disease.dgn_vdpa

CREATE TABLE disease.dgn_vdpa (
	snp_id varchar NOT NULL,
	disease_id varchar NOT NULL,
	pmid int8 NULL,
	disease_name varchar NOT NULL,
	disease_type varchar NOT NULL,
	sentence varchar NOT NULL,
	score float8 NOT NULL,
	data_source varchar NOT NULL,
	chromosome int4 NULL,
	chromosome_position int8 NULL
);
CREATE INDEX dgn_vdpa_disease_id_idx ON disease.dgn_vdpa USING btree (disease_id);
CREATE INDEX dgn_vdpa_snp_id_idx ON disease.dgn_vdpa USING btree (snp_id);
