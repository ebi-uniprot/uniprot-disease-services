package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.annotationTypes.GraphQLName;
import lombok.Data;

@Data
@GraphQLName("Synonym")
public class SynonymType {
    private String name;
    private String source;
}
