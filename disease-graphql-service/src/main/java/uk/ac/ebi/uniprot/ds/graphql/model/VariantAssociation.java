package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

import java.util.List;

@Data
public class VariantAssociation {
    private String name;
    private String description;
    private List<DbReferenceObject> xrefs;
    private List<VariationEvidence> evidences;
    private boolean disease;
}

