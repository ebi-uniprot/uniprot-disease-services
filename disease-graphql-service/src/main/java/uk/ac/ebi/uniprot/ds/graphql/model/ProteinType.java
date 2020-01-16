package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

import java.util.List;

@Data
public class ProteinType  {
    private String proteinId;
    private String name;
    private String accession;
    private String gene;
    private String desc;
    private List<DiseaseType> diseases;
    private List<ProteinCrossRefType> proteinCrossRefs;
    private List<InteractionType> interactions;
    private List<GeneCoordinateType> geneCoordinates;
    private List<PublicationType> publications;
    private List<Variation> variations;
    private Boolean isExternallyMapped; // to keep manually mapped protein flag
}
