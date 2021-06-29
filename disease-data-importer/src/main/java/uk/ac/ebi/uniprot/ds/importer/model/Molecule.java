package uk.ac.ebi.uniprot.ds.importer.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author sahmad
 * @created 22/06/2021
 */
@Data
@Builder
public class Molecule {
    private String name;
    private String chemblId;
    private String moleculeType;
    private String sourceType;
}
