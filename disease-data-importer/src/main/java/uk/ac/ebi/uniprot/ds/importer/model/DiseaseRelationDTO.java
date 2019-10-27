package uk.ac.ebi.uniprot.ds.importer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DiseaseRelationDTO {
    private long parentId;
    private long childId;

    public DiseaseRelationDTO(long parentId, long childId){
        this.parentId = parentId;
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "<" + parentId + ", " + childId + ">";
    }
}
