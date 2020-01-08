/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ds_disease_protein")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DiseaseProteinId.class)
public class DiseaseProtein  implements Serializable {

    private static final long serialVersionUID = -6896338892189706610L;

    @Id
    @JoinColumn(name = "ds_disease_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Disease disease;

    @Id
    @JoinColumn(name = "ds_protein_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Protein protein;

    @Column(name = "is_mapped", nullable = false)
    private Boolean isMapped;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DiseaseProtein that = (DiseaseProtein) obj;
        return Objects.equals(this.getDisease(), that.getDisease())
                && Objects.equals(this.getProtein(), that.getProtein());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDisease(), this.getProtein());
    }
}
