package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

import java.util.List;

@Data
public class DiseaseType {
    private String diseaseId;
    private String name;
    private String desc;
    private String acronym;
    private String note;
    private String source;
    private List<ProteinType> proteins;
    private List<SynonymType> synonyms;
    private List<CrossRefType> crossRefs;
    private List<DiseaseType> children;
    private List<PublicationType> publications;
    private List<Variation> variations;
}
