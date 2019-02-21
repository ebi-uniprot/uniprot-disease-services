/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

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
    @JoinColumn(name="ds_disease_id", nullable = false)
    private Disease disease;

    @Column(name="source_name", nullable = false)
    private String source;

    public Synonym(String name, String source){
        this.name = name;
        this.source = source;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Synonym synonym = (Synonym) obj;
        return Objects.equals(getName(), synonym.getName())
                && Objects.equals(getSource(), synonym.getSource())
                && Objects.equals(getDisease().getDiseaseId(), synonym.getDisease().getDiseaseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSource(), getDisease().getDiseaseId());
    }
}
