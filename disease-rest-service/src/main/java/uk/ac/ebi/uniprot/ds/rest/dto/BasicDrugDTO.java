package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicDrugDTO {
    private String name;
    private Long xrefId;
}
