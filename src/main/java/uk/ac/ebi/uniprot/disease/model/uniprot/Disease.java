package uk.ac.ebi.uniprot.disease.model.uniprot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Disease {
    private String identifier;
    private String accession;
    private String acronym;
    private String definition;
}
