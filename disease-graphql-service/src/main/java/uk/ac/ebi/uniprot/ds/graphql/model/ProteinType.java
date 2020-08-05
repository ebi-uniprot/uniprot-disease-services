package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.annotationTypes.GraphQLName;
import lombok.Data;

import java.util.List;

@Data
@GraphQLName(("Protein"))
public class ProteinType  {
    private String proteinId;
    private String proteinName;
    private String accession;
    private String gene;
    private String description;
    private List<DiseaseType> diseases;
    private List<ProteinCrossRefType> proteinCrossRefs;
    private List<InteractionType> interactions;
    private List<Variation> variants;
    private List<GeneCoordinateType> geneCoordinates;
    private List<PublicationType> publications;
    private Boolean isExternallyMapped;
}
