package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.GraphQLType;
import lombok.Data;

import java.util.List;

@Data
public class DiseaseType {
    private String diseaseId;
    private String diseaseName;
    private String description;
    private String acronym;
    private String note;
    private String source;
    private List<ProteinType> proteins;
    private List<SynonymType> synonyms;
    private List<DiseaseType> children;
    private List<PublicationType> publications;
    private List<Variation> variants;
    private Boolean isGroup;
}
