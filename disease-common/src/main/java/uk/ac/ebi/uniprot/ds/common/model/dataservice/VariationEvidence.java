package uk.ac.ebi.uniprot.ds.common.model.dataservice;

import lombok.Data;

@Data
public class VariationEvidence {
    private String code;
    private String label;
    private DbReferenceObject source;
}
