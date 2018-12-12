package uk.ac.ebi.uniprot.disease.model.sources.uniprot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlternativeName {
    private String name;
    private Integer diseaseId;
}
