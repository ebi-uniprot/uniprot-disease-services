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
@Table(name = "ds_keyword")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Keyword extends BaseEntity {

    private static final long serialVersionUID = -8781798245088755242L;

    @Column(name = "key_id", nullable = false)
    private String keyId;

    @Column(name = "key_value", nullable = false)
    private String keyValue;

    @ManyToOne
    @JoinColumn(name = "ds_disease_id", nullable = false)
    private Disease disease;


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Keyword keyword = (Keyword) obj;
        return Objects.equals(getKeyId(), keyword.getKeyId())
                && Objects.equals(getKeyValue(), keyword.getKeyValue())
                && Objects.equals(getDisease().getDiseaseId(), keyword.getDisease().getDiseaseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyId(), getKeyValue(), getDisease().getDiseaseId());
    }
}
