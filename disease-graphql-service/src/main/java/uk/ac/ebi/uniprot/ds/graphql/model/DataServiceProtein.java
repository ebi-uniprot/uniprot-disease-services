package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

import java.util.List;

@Data
public class DataServiceProtein {
    private String accession;
    private String proteinName;
    List<Variation> features;
}
