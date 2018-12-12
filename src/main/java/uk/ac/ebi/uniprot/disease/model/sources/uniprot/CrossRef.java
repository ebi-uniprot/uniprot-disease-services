package uk.ac.ebi.uniprot.disease.model.sources.uniprot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrossRef {
    private String refType;
    private String refId;
    private Integer diseaseId;
    private String refMeta;
}
