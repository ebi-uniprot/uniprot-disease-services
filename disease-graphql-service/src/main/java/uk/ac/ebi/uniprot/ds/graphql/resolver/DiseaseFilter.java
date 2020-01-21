package uk.ac.ebi.uniprot.ds.graphql.resolver;

import lombok.Data;

@Data
public class DiseaseFilter {
    private String nameContains;
    private String descriptionContains;
}
