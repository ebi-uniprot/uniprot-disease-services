package uk.ac.ebi.uniprot.ds.importer.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author sahmad
 * @created 22/06/2021
 */
@Builder
@Data
public class DrugIndication {
    private String efoId;
    private Integer maxPhase;
    private List<String> clinicalTrialLinks;
}
