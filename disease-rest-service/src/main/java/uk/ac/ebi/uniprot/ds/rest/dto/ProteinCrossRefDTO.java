package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProteinCrossRefDTO {
    String primaryId;
    String dbType;
    String description;
    List<String> proteinAccessions;

    public ProteinCrossRefDTO(String primaryId, String dbType, String description, List<String> proteinAccessions){
        this.primaryId = primaryId;
        this.dbType = dbType;
        this.description = description;
        this.proteinAccessions = proteinAccessions;
    }
}
