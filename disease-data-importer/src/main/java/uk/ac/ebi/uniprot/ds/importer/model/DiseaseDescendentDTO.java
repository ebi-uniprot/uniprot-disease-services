package uk.ac.ebi.uniprot.ds.importer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DiseaseDescendentDTO {
    private long diseaseId;
    private long descendentId;

    public DiseaseDescendentDTO(long diseaseId, long descendentId){
        this.diseaseId = diseaseId;
        this.descendentId = descendentId;
    }

    @Override
    public String toString() {
        return "<" + diseaseId + ", " + descendentId + ">";
    }
}
