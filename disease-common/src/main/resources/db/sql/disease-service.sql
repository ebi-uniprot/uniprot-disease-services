--
-- PostgreSQL database dump
-- Command to take db dump
-- pg_dump -O -s -c --if-exists -s -U postgres -d postgres -n disease_service > disease-service.sql
--

-- Dumped from database version 11.1
-- Dumped by pg_dump version 11.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY disease_service.ds_variant DROP CONSTRAINT IF EXISTS variant_protein_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_variant DROP CONSTRAINT IF EXISTS variant_feature_location_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_variant DROP CONSTRAINT IF EXISTS variant_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_publication DROP CONSTRAINT IF EXISTS pub_protein_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_publication DROP CONSTRAINT IF EXISTS pub_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_protein_cross_ref DROP CONSTRAINT IF EXISTS pathway_protein_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_interaction DROP CONSTRAINT IF EXISTS interaction_protein_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_synonym DROP CONSTRAINT IF EXISTS ds_synonyms_ds_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_keyword DROP CONSTRAINT IF EXISTS ds_keyword_ds_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_gene_coordinate DROP CONSTRAINT IF EXISTS ds_gene_coordinate_ds_protein_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_evidence DROP CONSTRAINT IF EXISTS ds_evidence_ds_variant_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_drug_evidence DROP CONSTRAINT IF EXISTS ds_evidence_drug_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_drug DROP CONSTRAINT IF EXISTS ds_drug_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_relation DROP CONSTRAINT IF EXISTS ds_disease_relation_ds_disease_fk2;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_relation DROP CONSTRAINT IF EXISTS ds_disease_relation_ds_disease_fk1;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_protein DROP CONSTRAINT IF EXISTS ds_disease_protein_ds_protein_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_protein DROP CONSTRAINT IF EXISTS ds_disease_protein_ds_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_descendent DROP CONSTRAINT IF EXISTS ds_disease_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_descendent DROP CONSTRAINT IF EXISTS ds_disease_disease_descendent_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_cross_ref DROP CONSTRAINT IF EXISTS ds_cross_ref_ds_disease_fk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_drug DROP CONSTRAINT IF EXISTS drug_prot_cross_ref_fk;
DROP TRIGGER IF EXISTS detect_cycle_after_insert_update ON disease_service.ds_disease_relation;
DROP INDEX IF EXISTS disease_service.ds_site_mapping_accession_idx;
DROP INDEX IF EXISTS disease_service.ds_keyword_key_value_idx;
DROP INDEX IF EXISTS disease_service.ds_drug_name_idx;
DROP INDEX IF EXISTS disease_service.ds_drug_ds_protein_cross_ref_id_idx;
DROP INDEX IF EXISTS disease_service.ds_drug_ds_disease_id_idx;
DROP INDEX IF EXISTS disease_service.ds_disease_descendent_ds_disease_id_idx;
DROP INDEX IF EXISTS disease_service.ds_disease_descendent_ds_disease_descendent_idx;
ALTER TABLE IF EXISTS ONLY disease_service.databasechangeloglock DROP CONSTRAINT IF EXISTS pk_databasechangeloglock;
ALTER TABLE IF EXISTS ONLY disease_service.ds_variant DROP CONSTRAINT IF EXISTS ds_variant_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_synonym DROP CONSTRAINT IF EXISTS ds_synonyms_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_site_mapping DROP CONSTRAINT IF EXISTS ds_site_mapping_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_publication DROP CONSTRAINT IF EXISTS ds_pub_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_protein DROP CONSTRAINT IF EXISTS ds_protein_un2;
ALTER TABLE IF EXISTS ONLY disease_service.ds_protein DROP CONSTRAINT IF EXISTS ds_protein_un1;
ALTER TABLE IF EXISTS ONLY disease_service.ds_protein DROP CONSTRAINT IF EXISTS ds_protein_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_protein_cross_ref DROP CONSTRAINT IF EXISTS ds_pathway_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_interaction DROP CONSTRAINT IF EXISTS ds_interaction_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_gene_coordinate DROP CONSTRAINT IF EXISTS ds_gene_coordinate_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_feature_location DROP CONSTRAINT IF EXISTS ds_feature_location_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_evidence DROP CONSTRAINT IF EXISTS ds_evidence_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_drug DROP CONSTRAINT IF EXISTS ds_drug_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_drug_evidence DROP CONSTRAINT IF EXISTS ds_drug_evidence_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease DROP CONSTRAINT IF EXISTS ds_disease_un;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_relation DROP CONSTRAINT IF EXISTS ds_disease_relation_un;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_protein DROP CONSTRAINT IF EXISTS ds_disease_protein_un;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease DROP CONSTRAINT IF EXISTS ds_disease_pk;
ALTER TABLE IF EXISTS ONLY disease_service.ds_disease_descendent DROP CONSTRAINT IF EXISTS ds_disease_descendent_un;
ALTER TABLE IF EXISTS ONLY disease_service.ds_cross_ref DROP CONSTRAINT IF EXISTS ds_cross_ref_un;
ALTER TABLE IF EXISTS ONLY disease_service.ds_cross_ref DROP CONSTRAINT IF EXISTS ds_cross_ref_pk;
ALTER TABLE IF EXISTS disease_service.ds_variant ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_synonym ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_site_mapping ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_publication ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_protein_cross_ref ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_protein ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_keyword ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_interaction ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_gene_coordinate ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_feature_location ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_evidence ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_drug_evidence ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_drug ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_disease ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS disease_service.ds_cross_ref ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS disease_service.ds_variant_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_variant_id_seq;
DROP TABLE IF EXISTS disease_service.ds_variant;
DROP SEQUENCE IF EXISTS disease_service.ds_synonym_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_synonym_id_seq;
DROP TABLE IF EXISTS disease_service.ds_synonym;
DROP SEQUENCE IF EXISTS disease_service.ds_site_mapping_id_seq;
DROP TABLE IF EXISTS disease_service.ds_site_mapping;
DROP SEQUENCE IF EXISTS disease_service.ds_publication_id_seq;
DROP TABLE IF EXISTS disease_service.ds_publication;
DROP SEQUENCE IF EXISTS disease_service.ds_protein_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_protein_id_seq;
DROP TABLE IF EXISTS disease_service.ds_protein;
DROP SEQUENCE IF EXISTS disease_service.ds_pathway_id_seq1;
DROP TABLE IF EXISTS disease_service.ds_protein_cross_ref;
DROP SEQUENCE IF EXISTS disease_service.ds_pathway_id_seq;
DROP SEQUENCE IF EXISTS disease_service.ds_keyword_id_seq;
DROP TABLE IF EXISTS disease_service.ds_keyword;
DROP SEQUENCE IF EXISTS disease_service.ds_interaction_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_interaction_id_seq;
DROP TABLE IF EXISTS disease_service.ds_interaction;
DROP SEQUENCE IF EXISTS disease_service.ds_gene_coordinate_id_seq;
DROP TABLE IF EXISTS disease_service.ds_gene_coordinate;
DROP SEQUENCE IF EXISTS disease_service.ds_feature_location_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_feature_location_id_seq;
DROP TABLE IF EXISTS disease_service.ds_feature_location;
DROP SEQUENCE IF EXISTS disease_service.ds_evidence_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_evidence_id_seq;
DROP TABLE IF EXISTS disease_service.ds_evidence;
DROP SEQUENCE IF EXISTS disease_service.ds_drug_id_seq;
DROP SEQUENCE IF EXISTS disease_service.ds_drug_evidence_id_seq;
DROP TABLE IF EXISTS disease_service.ds_drug_evidence;
DROP TABLE IF EXISTS disease_service.ds_drug;
DROP TABLE IF EXISTS disease_service.ds_disease_relation;
DROP TABLE IF EXISTS disease_service.ds_disease_protein;
DROP SEQUENCE IF EXISTS disease_service.ds_disease_id_seq1;
DROP SEQUENCE IF EXISTS disease_service.ds_disease_id_seq;
DROP TABLE IF EXISTS disease_service.ds_disease_descendent;
DROP TABLE IF EXISTS disease_service.ds_disease;
DROP SEQUENCE IF EXISTS disease_service.ds_cross_ref_id_seq;
DROP TABLE IF EXISTS disease_service.ds_cross_ref;
DROP TABLE IF EXISTS disease_service.databasechangeloglock;
DROP TABLE IF EXISTS disease_service.databasechangelog;
DROP FUNCTION IF EXISTS disease_service.detect_cycle();
DROP SCHEMA IF EXISTS disease_service;
--
-- Name: disease_service; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA disease_service;


--
-- Name: detect_cycle(); Type: FUNCTION; Schema: disease_service; Owner: -
--

CREATE FUNCTION disease_service.detect_cycle() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
  loops INTEGER;
BEGIN
   EXECUTE 'WITH RECURSIVE search_graph(ds_disease_id, ds_disease_parent_id, depth, path, cycle) AS (
        SELECT dr.ds_disease_id, dr.ds_disease_parent_id, 1,
          ARRAY[dr.ds_disease_id],
          false
        FROM ds_disease_relation dr
      UNION ALL
        SELECT dr.ds_disease_id, dr.ds_disease_parent_id, sg.depth + 1,
          path || dr.ds_disease_id,
          dr.ds_disease_id = ANY(path)
        FROM ds_disease_relation dr, search_graph sg
        WHERE dr.ds_disease_id = sg.ds_disease_parent_id AND NOT cycle
)
select count(*) FROM search_graph where cycle = TRUE' INTO loops;
IF loops > 0 THEN
  RAISE EXCEPTION 'Error - Cycle detected in ds_disease_relation table! Fix it and then retry.';
ELSE
  RETURN NEW;
END IF;
END
$$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: databasechangelog; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255)
);


--
-- Name: databasechangeloglock; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


--
-- Name: ds_cross_ref; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_cross_ref (
    id bigint NOT NULL,
    ref_type character varying NOT NULL,
    ref_id character varying NOT NULL,
    ds_disease_id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_name character varying(255) NOT NULL
);


--
-- Name: ds_cross_ref_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_cross_ref_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_cross_ref_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_cross_ref_id_seq OWNED BY disease_service.ds_cross_ref.id;


--
-- Name: ds_disease; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_disease (
    id bigint NOT NULL,
    disease_id character varying(255) NOT NULL,
    disease_name character varying(255) NOT NULL,
    description text,
    acronym character varying(255),
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    source_name character varying(255) NOT NULL,
    note character varying(4000)
);


--
-- Name: ds_disease_descendent; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_disease_descendent (
    ds_disease_id bigint NOT NULL,
    ds_descendent_id bigint NOT NULL
);


--
-- Name: ds_disease_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_disease_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_disease_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_disease_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_disease_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_disease_id_seq1 OWNED BY disease_service.ds_disease.id;


--
-- Name: ds_disease_protein; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_disease_protein (
    ds_disease_id bigint NOT NULL,
    ds_protein_id bigint NOT NULL,
    is_mapped boolean DEFAULT false NOT NULL
);


--
-- Name: ds_disease_relation; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_disease_relation (
    ds_disease_id bigint NOT NULL,
    ds_disease_parent_id bigint NOT NULL
);


--
-- Name: ds_drug; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_drug (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    source_type character varying(63) NOT NULL,
    source_id character varying(123),
    molecule_type character varying(63),
    ds_protein_cross_ref_id bigint,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    clinical_trial_phase smallint,
    mechanism_of_action character varying(255),
    clinical_trial_link character varying(255),
    chembl_disease_id character varying(255),
    ds_disease_id bigint
);


--
-- Name: ds_drug_evidence; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_drug_evidence (
    id bigint NOT NULL,
    ref_type character varying(64),
    ref_url character varying(255),
    ds_drug_id bigint,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: TABLE ds_drug_evidence; Type: COMMENT; Schema: disease_service; Owner: -
--

COMMENT ON TABLE disease_service.ds_drug_evidence IS 'table to store evidence of the drug e.g. pubmed urls';


--
-- Name: ds_drug_evidence_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_drug_evidence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_drug_evidence_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_drug_evidence_id_seq OWNED BY disease_service.ds_drug_evidence.id;


--
-- Name: ds_drug_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_drug_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_drug_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_drug_id_seq OWNED BY disease_service.ds_drug.id;


--
-- Name: ds_evidence; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_evidence (
    id bigint NOT NULL,
    evidence_id character varying(255),
    evidence_type character varying(255),
    evidence_attribute character varying(255),
    evidence_code character varying(255),
    use_eco_code boolean,
    type_value character varying(255),
    has_type_value boolean,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    ds_variant_id bigint
);


--
-- Name: ds_evidence_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_evidence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_evidence_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_evidence_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_evidence_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_evidence_id_seq1 OWNED BY disease_service.ds_evidence.id;


--
-- Name: ds_feature_location; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_feature_location (
    id bigint NOT NULL,
    start_modifier character varying(255),
    end_modifier character varying(255),
    start_id integer,
    end_id integer,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);


--
-- Name: ds_feature_location_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_feature_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_feature_location_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_feature_location_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_feature_location_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_feature_location_id_seq1 OWNED BY disease_service.ds_feature_location.id;


--
-- Name: ds_gene_coordinate; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_gene_coordinate (
    id bigint NOT NULL,
    chromosome_number character varying(63),
    gene_start bigint NOT NULL,
    gene_end bigint NOT NULL,
    ensembl_gene_id character varying(127) NOT NULL,
    ensembl_transcript_id character varying(127) NOT NULL,
    ensembl_translation_id character varying(127) NOT NULL,
    ds_protein_id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: ds_gene_coordinate_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_gene_coordinate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_gene_coordinate_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_gene_coordinate_id_seq OWNED BY disease_service.ds_gene_coordinate.id;


--
-- Name: ds_interaction; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_interaction (
    id bigint NOT NULL,
    interaction_type character varying(255) NOT NULL,
    accession character varying(255),
    gene character varying(255),
    experiment_count integer,
    first_interactor character varying(255),
    second_interactor character varying(255),
    ds_protein_id bigint,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);


--
-- Name: ds_interaction_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_interaction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_interaction_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_interaction_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_interaction_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_interaction_id_seq1 OWNED BY disease_service.ds_interaction.id;


--
-- Name: ds_keyword; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_keyword (
    id bigint NOT NULL,
    key_id character varying(63) NOT NULL,
    key_value character varying(255) NOT NULL,
    ds_disease_id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: ds_keyword_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_keyword_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_keyword_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_keyword_id_seq OWNED BY disease_service.ds_keyword.id;


--
-- Name: ds_pathway_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_pathway_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_protein_cross_ref; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_protein_cross_ref (
    id bigint NOT NULL,
    primary_id character varying(255),
    description character varying(255),
    db_type character varying(255),
    isoform_id character varying(255),
    third character varying(255),
    fourth character varying(255),
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    ds_protein_id bigint
);


--
-- Name: ds_pathway_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_pathway_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_pathway_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_pathway_id_seq1 OWNED BY disease_service.ds_protein_cross_ref.id;


--
-- Name: ds_protein; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_protein (
    id bigint NOT NULL,
    protein_id character varying(255) NOT NULL,
    protein_name character varying(255) NOT NULL,
    accession character varying(255) NOT NULL,
    gene character varying(255),
    description text,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);


--
-- Name: ds_protein_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_protein_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_protein_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_protein_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_protein_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_protein_id_seq1 OWNED BY disease_service.ds_protein.id;


--
-- Name: ds_publication; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_publication (
    id bigint NOT NULL,
    pub_type character varying(63) NOT NULL,
    pub_id character varying(255) NOT NULL,
    ds_protein_id bigint,
    ds_disease_id bigint,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: ds_publication_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_publication_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_publication_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_publication_id_seq OWNED BY disease_service.ds_publication.id;


--
-- Name: ds_site_mapping; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_site_mapping (
    id bigint NOT NULL,
    accession character varying NOT NULL,
    protein_id character varying NOT NULL,
    site_position bigint NOT NULL,
    position_in_alignment bigint NOT NULL,
    site_type character varying,
    uniref_id character varying NOT NULL,
    mapped_site text,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: ds_site_mapping_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_site_mapping_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_site_mapping_id_seq; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_site_mapping_id_seq OWNED BY disease_service.ds_site_mapping.id;


--
-- Name: ds_synonym; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_synonym (
    id bigint NOT NULL,
    disease_name character varying(255) NOT NULL,
    ds_disease_id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    source_name character varying(255) NOT NULL
);


--
-- Name: ds_synonym_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_synonym_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_synonym_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_synonym_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_synonym_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_synonym_id_seq1 OWNED BY disease_service.ds_synonym.id;


--
-- Name: ds_variant; Type: TABLE; Schema: disease_service; Owner: -
--

CREATE TABLE disease_service.ds_variant (
    id bigint NOT NULL,
    original_sequence character varying(255),
    alternate_sequence character varying(255),
    feature_id character varying(255),
    variant_report text,
    ds_feature_location_id bigint,
    feature_status character varying(255),
    ds_protein_id bigint,
    ds_disease_id bigint,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL
);


--
-- Name: ds_variant_id_seq; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_variant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_variant_id_seq1; Type: SEQUENCE; Schema: disease_service; Owner: -
--

CREATE SEQUENCE disease_service.ds_variant_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: ds_variant_id_seq1; Type: SEQUENCE OWNED BY; Schema: disease_service; Owner: -
--

ALTER SEQUENCE disease_service.ds_variant_id_seq1 OWNED BY disease_service.ds_variant.id;


--
-- Name: ds_cross_ref id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_cross_ref ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_cross_ref_id_seq'::regclass);


--
-- Name: ds_disease id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_disease_id_seq1'::regclass);


--
-- Name: ds_drug id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_drug_id_seq'::regclass);


--
-- Name: ds_drug_evidence id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug_evidence ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_drug_evidence_id_seq'::regclass);


--
-- Name: ds_evidence id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_evidence ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_evidence_id_seq1'::regclass);


--
-- Name: ds_feature_location id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_feature_location ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_feature_location_id_seq1'::regclass);


--
-- Name: ds_gene_coordinate id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_gene_coordinate ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_gene_coordinate_id_seq'::regclass);


--
-- Name: ds_interaction id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_interaction ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_interaction_id_seq1'::regclass);


--
-- Name: ds_keyword id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_keyword ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_keyword_id_seq'::regclass);


--
-- Name: ds_protein id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_protein_id_seq1'::regclass);


--
-- Name: ds_protein_cross_ref id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein_cross_ref ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_pathway_id_seq1'::regclass);


--
-- Name: ds_publication id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_publication ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_publication_id_seq'::regclass);


--
-- Name: ds_site_mapping id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_site_mapping ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_site_mapping_id_seq'::regclass);


--
-- Name: ds_synonym id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_synonym ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_synonym_id_seq1'::regclass);


--
-- Name: ds_variant id; Type: DEFAULT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_variant ALTER COLUMN id SET DEFAULT nextval('disease_service.ds_variant_id_seq1'::regclass);


--
-- Name: ds_cross_ref ds_cross_ref_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_cross_ref
    ADD CONSTRAINT ds_cross_ref_pk PRIMARY KEY (id);


--
-- Name: ds_cross_ref ds_cross_ref_un; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_cross_ref
    ADD CONSTRAINT ds_cross_ref_un UNIQUE (ref_type, ref_id, ds_disease_id);


--
-- Name: ds_disease_descendent ds_disease_descendent_un; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_descendent
    ADD CONSTRAINT ds_disease_descendent_un UNIQUE (ds_disease_id, ds_descendent_id);


--
-- Name: ds_disease ds_disease_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease
    ADD CONSTRAINT ds_disease_pk PRIMARY KEY (id);


--
-- Name: ds_disease_protein ds_disease_protein_un; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_protein
    ADD CONSTRAINT ds_disease_protein_un UNIQUE (ds_disease_id, ds_protein_id);


--
-- Name: ds_disease_relation ds_disease_relation_un; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_relation
    ADD CONSTRAINT ds_disease_relation_un UNIQUE (ds_disease_id, ds_disease_parent_id);


--
-- Name: ds_disease ds_disease_un; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease
    ADD CONSTRAINT ds_disease_un UNIQUE (disease_id);


--
-- Name: ds_drug_evidence ds_drug_evidence_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug_evidence
    ADD CONSTRAINT ds_drug_evidence_pk PRIMARY KEY (id);


--
-- Name: ds_drug ds_drug_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug
    ADD CONSTRAINT ds_drug_pk PRIMARY KEY (id);


--
-- Name: ds_evidence ds_evidence_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_evidence
    ADD CONSTRAINT ds_evidence_pk PRIMARY KEY (id);


--
-- Name: ds_feature_location ds_feature_location_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_feature_location
    ADD CONSTRAINT ds_feature_location_pk PRIMARY KEY (id);


--
-- Name: ds_gene_coordinate ds_gene_coordinate_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_gene_coordinate
    ADD CONSTRAINT ds_gene_coordinate_pk PRIMARY KEY (id);


--
-- Name: ds_interaction ds_interaction_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_interaction
    ADD CONSTRAINT ds_interaction_pk PRIMARY KEY (id);


--
-- Name: ds_protein_cross_ref ds_pathway_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein_cross_ref
    ADD CONSTRAINT ds_pathway_pk PRIMARY KEY (id);


--
-- Name: ds_protein ds_protein_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein
    ADD CONSTRAINT ds_protein_pk PRIMARY KEY (id);


--
-- Name: ds_protein ds_protein_un1; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein
    ADD CONSTRAINT ds_protein_un1 UNIQUE (protein_id);


--
-- Name: ds_protein ds_protein_un2; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein
    ADD CONSTRAINT ds_protein_un2 UNIQUE (accession);


--
-- Name: ds_publication ds_pub_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_publication
    ADD CONSTRAINT ds_pub_pk PRIMARY KEY (id);


--
-- Name: ds_site_mapping ds_site_mapping_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_site_mapping
    ADD CONSTRAINT ds_site_mapping_pk PRIMARY KEY (id);


--
-- Name: ds_synonym ds_synonyms_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_synonym
    ADD CONSTRAINT ds_synonyms_pk PRIMARY KEY (id);


--
-- Name: ds_variant ds_variant_pk; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_variant
    ADD CONSTRAINT ds_variant_pk PRIMARY KEY (id);


--
-- Name: databasechangeloglock pk_databasechangeloglock; Type: CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.databasechangeloglock
    ADD CONSTRAINT pk_databasechangeloglock PRIMARY KEY (id);


--
-- Name: ds_disease_descendent_ds_disease_descendent_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_disease_descendent_ds_disease_descendent_idx ON disease_service.ds_disease_descendent USING btree (ds_descendent_id);


--
-- Name: ds_disease_descendent_ds_disease_id_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_disease_descendent_ds_disease_id_idx ON disease_service.ds_disease_descendent USING btree (ds_disease_id);


--
-- Name: ds_drug_ds_disease_id_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_drug_ds_disease_id_idx ON disease_service.ds_drug USING btree (ds_disease_id);


--
-- Name: ds_drug_ds_protein_cross_ref_id_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_drug_ds_protein_cross_ref_id_idx ON disease_service.ds_drug USING btree (ds_protein_cross_ref_id);


--
-- Name: ds_drug_name_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_drug_name_idx ON disease_service.ds_drug USING btree (name);


--
-- Name: ds_keyword_key_value_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_keyword_key_value_idx ON disease_service.ds_keyword USING btree (key_value);


--
-- Name: ds_site_mapping_accession_idx; Type: INDEX; Schema: disease_service; Owner: -
--

CREATE INDEX ds_site_mapping_accession_idx ON disease_service.ds_site_mapping USING btree (accession);


--
-- Name: ds_disease_relation detect_cycle_after_insert_update; Type: TRIGGER; Schema: disease_service; Owner: -
--

CREATE TRIGGER detect_cycle_after_insert_update AFTER INSERT OR UPDATE ON disease_service.ds_disease_relation FOR EACH ROW EXECUTE PROCEDURE disease_service.detect_cycle();


--
-- Name: ds_drug drug_prot_cross_ref_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug
    ADD CONSTRAINT drug_prot_cross_ref_fk FOREIGN KEY (ds_protein_cross_ref_id) REFERENCES disease_service.ds_protein_cross_ref(id);


--
-- Name: ds_cross_ref ds_cross_ref_ds_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_cross_ref
    ADD CONSTRAINT ds_cross_ref_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_disease_descendent ds_disease_disease_descendent_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_descendent
    ADD CONSTRAINT ds_disease_disease_descendent_fk FOREIGN KEY (ds_descendent_id) REFERENCES disease_service.ds_disease(id) ON DELETE CASCADE;


--
-- Name: ds_disease_descendent ds_disease_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_descendent
    ADD CONSTRAINT ds_disease_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id) ON DELETE CASCADE;


--
-- Name: ds_disease_protein ds_disease_protein_ds_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_protein
    ADD CONSTRAINT ds_disease_protein_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_disease_protein ds_disease_protein_ds_protein_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_protein
    ADD CONSTRAINT ds_disease_protein_ds_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id);


--
-- Name: ds_disease_relation ds_disease_relation_ds_disease_fk1; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_relation
    ADD CONSTRAINT ds_disease_relation_ds_disease_fk1 FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_disease_relation ds_disease_relation_ds_disease_fk2; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_disease_relation
    ADD CONSTRAINT ds_disease_relation_ds_disease_fk2 FOREIGN KEY (ds_disease_parent_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_drug ds_drug_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug
    ADD CONSTRAINT ds_drug_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_drug_evidence ds_evidence_drug_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_drug_evidence
    ADD CONSTRAINT ds_evidence_drug_fk FOREIGN KEY (ds_drug_id) REFERENCES disease_service.ds_drug(id);


--
-- Name: ds_evidence ds_evidence_ds_variant_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_evidence
    ADD CONSTRAINT ds_evidence_ds_variant_fk FOREIGN KEY (ds_variant_id) REFERENCES disease_service.ds_variant(id);


--
-- Name: ds_gene_coordinate ds_gene_coordinate_ds_protein_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_gene_coordinate
    ADD CONSTRAINT ds_gene_coordinate_ds_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id);


--
-- Name: ds_keyword ds_keyword_ds_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_keyword
    ADD CONSTRAINT ds_keyword_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_synonym ds_synonyms_ds_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_synonym
    ADD CONSTRAINT ds_synonyms_ds_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_interaction interaction_protein_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_interaction
    ADD CONSTRAINT interaction_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id);


--
-- Name: ds_protein_cross_ref pathway_protein_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_protein_cross_ref
    ADD CONSTRAINT pathway_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id);


--
-- Name: ds_publication pub_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_publication
    ADD CONSTRAINT pub_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_publication pub_protein_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_publication
    ADD CONSTRAINT pub_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id);


--
-- Name: ds_variant variant_disease_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_variant
    ADD CONSTRAINT variant_disease_fk FOREIGN KEY (ds_disease_id) REFERENCES disease_service.ds_disease(id);


--
-- Name: ds_variant variant_feature_location_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_variant
    ADD CONSTRAINT variant_feature_location_fk FOREIGN KEY (ds_feature_location_id) REFERENCES disease_service.ds_feature_location(id);


--
-- Name: ds_variant variant_protein_fk; Type: FK CONSTRAINT; Schema: disease_service; Owner: -
--

ALTER TABLE ONLY disease_service.ds_variant
    ADD CONSTRAINT variant_protein_fk FOREIGN KEY (ds_protein_id) REFERENCES disease_service.ds_protein(id);


--
-- PostgreSQL database dump complete
--

