package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

@Data
public class VariationEvidence {
    private String code;
    private String label;
    private DbReferenceObject source;
}
