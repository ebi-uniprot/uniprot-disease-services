package uk.ac.ebi.uniprot.ds.importer.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author sahmad
 * @created 22/06/2021
 */
@Data
@Builder
public class Mechanism {
    private String mechanismOfAction;
    private String targetChemblId;
    private String moleculeChemblId;
    private String parentMoleculeChemblId;
    private List<String> evidences;
}
