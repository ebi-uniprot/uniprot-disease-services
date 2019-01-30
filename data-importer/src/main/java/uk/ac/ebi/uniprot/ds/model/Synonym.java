/*
 * Created by sahmad on 23/01/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ds_synonym")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Synonym extends BaseEntity{
    private static final long serialVersionUID = -4832078587123331322L;

    @Column(name = "disease_name")
    private String name;

    @ManyToOne
    @JoinColumn(name="ds_disease_id")
    private Disease disease;

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Synonym synonym = (Synonym) obj;
        return Objects.equals(getName(), synonym.getName()) && Objects.equals(getDisease().getDiseaseId(), synonym.getDisease().getDiseaseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDisease().getDiseaseId());
    }
}
