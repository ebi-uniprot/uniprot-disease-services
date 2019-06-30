package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class DrugDTO {
    private String name;
    private String sourceType;
    private String sourceId;
    private String moleculeType;
    private Integer clinicalTrialPhase;
    private String mechanismOfAction;
    private String clinicalTrialLink;
    private List<String> evidences;
    private Set<String> proteins;
    private Set<String> diseases;
}
