package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DataServiceProtein {
    private String accession;
    private String proteinName;
    List<Variation> features;
}
