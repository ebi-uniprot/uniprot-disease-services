package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.annotationTypes.GraphQLName;
import lombok.Data;

@Data
@GraphQLName("Interaction")
public class InteractionType {
    private String type;
    private String accession;
    private String gene;
    private Integer experimentCount;
    private String firstInteractor;
    private String secondInteractor;
}
