package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.GraphQLName;
import lombok.Data;

@Data
@GraphQLName("DiseaseCrossRef")
public class CrossRefType {
    private String refType;
    private String refId;
    private String source;
}
