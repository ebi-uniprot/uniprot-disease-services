package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

@Data
public class InteractionType {
    private String type;
    private String accession;
    private String gene;
    private Integer experimentCount;
    private String firstInteractor;
    private String secondInteractor;
}
