package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.annotationTypes.GraphQLName;
import lombok.Data;

@Data
@GraphQLName("Publication")
public class PublicationType {
    private String pubType;
    private String pubId;
}
