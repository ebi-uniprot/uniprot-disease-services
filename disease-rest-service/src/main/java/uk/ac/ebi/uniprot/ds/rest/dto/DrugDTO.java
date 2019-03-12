package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DrugDTO extends BasicDrugDTO {
    private String sourceType;
    private String sourceId;
    private String moleculeType;
}
