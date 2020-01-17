package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.GraphQLName;
import lombok.Data;

import java.util.List;

@Data
@GraphQLName("ProteinCrossRef")
public class ProteinCrossRefType  {
    private String primaryId;
    private String description;
    private String dbType;
    private String isoformId;
    private String third;
    private String fourth;
    private List<DrugType> drugs;
}
