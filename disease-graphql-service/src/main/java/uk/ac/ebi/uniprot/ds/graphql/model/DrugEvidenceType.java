package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.GraphQLName;
import lombok.Data;

@Data
@GraphQLName("DrugEvidence")
public class DrugEvidenceType {
    private String refType;
    private String refUrl;
}
