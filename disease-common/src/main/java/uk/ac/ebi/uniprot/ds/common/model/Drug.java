package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;

import javax.persistence.*;

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

}
