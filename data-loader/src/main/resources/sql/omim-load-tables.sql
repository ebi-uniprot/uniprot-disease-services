-- Drop table

-- DROP TABLE disease.omim_phenotypic_series

CREATE TABLE disease.omim_phenotypic_series (
	phenotypic_series_number varchar NOT NULL,
	mim_number int4 NOT NULL,
	phenotype varchar NOT NULL
);

-- Drop table

-- DROP TABLE disease.omim_mim_gene

CREATE TABLE disease.omim_mim_gene (
	mim_id int4 NOT NULL,
	mim_type varchar NOT NULL,
	entrez_gene_id int4 NULL,
	gene_symbol varchar NULL,
	ensembl_gene_id varchar NULL
);

-- Drop table

-- DROP TABLE disease.omim_genemap

CREATE TABLE disease.omim_genemap (
	chromosome varchar NULL,
	genomic_start int8 NULL,
	genomic_end int8 NULL,
	cyto_location varchar NULL,
	computed_cyto_location varchar NULL,
	mim_id int4 NULL,
	gene_symbols varchar NULL,
	gene_name varchar NULL,
	approved_symbol varchar NULL,
	entrez_gene_id int4 NULL,
	ensembl_gene_id varchar NULL,
	"comments" text NULL,
	phenotypes text NULL,
	mouse_gene_id varchar NULL
);

-- Drop table

-- DROP TABLE disease.omim_mim_titles

CREATE TABLE disease.omim_mim_titles (
	prefix varchar NULL,
	mim_id int4 NOT NULL,
	preferred_title varchar NULL,
	alternative_title varchar NULL,
	included_title varchar NULL
);
