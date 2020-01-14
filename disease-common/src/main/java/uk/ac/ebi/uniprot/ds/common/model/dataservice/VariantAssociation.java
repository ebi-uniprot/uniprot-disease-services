package uk.ac.ebi.uniprot.ds.common.model.dataservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VariantAssociation {
    private String name;
    private String description;
    private List<DbReferenceObject> xrefs;
    private List<VariationEvidence> evidences;
    private boolean disease;
}

