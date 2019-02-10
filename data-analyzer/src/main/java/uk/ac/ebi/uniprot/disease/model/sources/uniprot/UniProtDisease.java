package uk.ac.ebi.uniprot.disease.model.sources.uniprot;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UniProtDisease {
    private String identifier;
    private String accession;
    private String acronym;
    private String definition;
    private List<AlternativeName> synonyms;
    private List<CrossRef> crossRefs;
    private List<Keyword> keywords;
    private List<String> alternativeNames;
}
