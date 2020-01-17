package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.GraphQLName;
import lombok.Data;

import java.util.List;

@Data
@GraphQLName("Variant")
public class Variation {
    private String type;
    private String cvId;
    private String ftId;
    private String description;
    private String alternativeSequence;
    private String begin;
    private String end;
    private String molecule;
    private List<DbReferenceObject> xrefs;
    private List<VariationEvidence> evidences;
    private String wildType;
    private Double frequency;
    private String polyphenPrediction;
    private Double polyphenScore;
    private String siftPrediction;
    private Double siftScore;
    private int somaticStatus;
    private String cytogeneticBand;
    private String consequenceType;
    private String genomicLocation;
    private List<VariantAssociation> association;
    private String clinicalSignificances;
    private VariantSourceTypeEnum sourceType;
}