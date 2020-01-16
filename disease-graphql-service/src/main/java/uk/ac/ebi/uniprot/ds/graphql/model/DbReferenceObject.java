package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

@Data
public class DbReferenceObject {
    private String name;
    private String id;
    private String url;
    private String alternativeUrl;
    private Boolean reviewed;
}

