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
@Table(name = "ds_cross_ref")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossRef extends BaseEntity{

    private static final long serialVersionUID = 2381013582362929616L;
    @Column(name = "ref_type")
    private String refType;

    @Column(name = "ref_id")
    private String refId;

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

        CrossRef crossRef = (CrossRef) obj;
        return Objects.equals(getRefType(), crossRef.getRefType())
                && Objects.equals(getRefId(), crossRef.getRefId())
                && Objects.equals(getDisease(), crossRef.getDisease());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRefType(), getRefId(), getDisease());
    }
}
