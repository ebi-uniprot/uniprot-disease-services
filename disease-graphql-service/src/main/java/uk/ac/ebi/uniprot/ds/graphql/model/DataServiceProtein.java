package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;
import uk.ac.ebi.uniprot.ds.common.model.dataservice.Variation;

import java.util.List;

@Data
public class DataServiceProtein {
    private String accession;
    private String proteinName;
    List<Variation> features;
}
