package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

@Data
public class CrossRefType {
    private String refType;
    private String refId;
    private String source;
}
