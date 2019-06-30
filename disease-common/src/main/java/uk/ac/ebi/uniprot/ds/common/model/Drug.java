package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "ds_drug")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drug extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "source_type", nullable = false)
    private String sourceType;
    @Column(name = "source_id")
    private String sourceId;
    @Column(name = "molecule_type")
    private String moleculeType;
    @ManyToOne
    @JoinColumn(name = "ds_protein_cross_ref_id")
    private ProteinCrossRef proteinCrossRef;
    @Column(name = "clinical_trial_phase")
    private Integer clinicalTrialPhase;
    @Column(name = "mechanism_of_action")
    private String mechanismOfAction;
    @Column(name = "clinical_trial_link")
    private String clinicalTrialLink;
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DrugEvidence> drugEvidences;
    @Transient
    private Set<String> diseases;// to hold names of diseases those use this drug
    @Transient
    private Set<String> proteins;// to hold accessions of proteins those use this drug


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Drug crossRef = (Drug) obj;
        return Objects.equals(getName(), crossRef.getName())
                && Objects.equals(getSourceType(), crossRef.getSourceType())
                && Objects.equals(getSourceId(), crossRef.getSourceId())
                && Objects.equals(getMoleculeType(), crossRef.getMoleculeType())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSourceType(),
                getSourceId(), getMoleculeType());
    }

}
