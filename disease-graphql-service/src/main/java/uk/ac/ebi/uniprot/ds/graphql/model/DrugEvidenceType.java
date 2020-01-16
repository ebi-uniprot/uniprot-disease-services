package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

@Data
public class DrugEvidenceType {
    private String refType;
    private String refUrl;
}
