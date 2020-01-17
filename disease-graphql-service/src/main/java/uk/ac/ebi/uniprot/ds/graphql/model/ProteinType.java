package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

import java.util.List;

@Data
public class ProteinType  {
    private String proteinId;
    private String proteinName;
    private String accession;
    private String gene;
    private String description;
    private List<DiseaseType> diseases;
    private List<ProteinCrossRefType> pathways;
    private List<InteractionType> interactions;
    private List<Variation> variants;
    private List<GeneCoordinateType> geneCoordinates;
    private List<PublicationType> publications;
    private Boolean isExternallyMapped;
}
