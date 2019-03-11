package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProteinCrossRefDTO {
    String primaryId;
    String dbType;
    String description;
    public ProteinCrossRefDTO(String primaryId, String dbType, String description){
        this.primaryId = primaryId;
        this.dbType = dbType;
        this.description = description;
    }
}
