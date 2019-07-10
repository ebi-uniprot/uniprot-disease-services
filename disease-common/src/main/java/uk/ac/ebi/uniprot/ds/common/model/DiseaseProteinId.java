package uk.ac.ebi.uniprot.ds.common.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class DiseaseProteinId implements Serializable {
    @Column(name = "ds_disease_id")
    private Long disease;

    @Column(name = "ds_protein_id")
    private Long protein;

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj == null || this.getClass() != obj.getClass()){
            return false;
        }

        DiseaseProteinId that = (DiseaseProteinId) obj;

        return Objects.equals(this.getDisease(), that.getDisease())
                && Objects.equals(this.getProtein(), that.getProtein());

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDisease(), this.getProtein());
    }
}
