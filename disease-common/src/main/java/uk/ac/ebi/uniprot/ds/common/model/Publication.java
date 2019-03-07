/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ds_publication")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publication extends BaseEntity {

    private static final long serialVersionUID = 3248948784107372264L;

    @Column(name = "pub_type")
    private String pubType;

    @Column(name = "pub_id")
    private String pubId;

    @ManyToOne
    @JoinColumn(name = "ds_protein_id")
    private Protein protein;

    @ManyToOne
    @JoinColumn(name = "ds_disease_id")
    private Disease disease;

    public Publication(String pubType, String pubId, Protein protein){
        this.pubType = pubType;
        this.pubId = pubId;
        this.protein = protein;
    }

    public Publication(String pubType, String pubId, Disease disease){
        this.pubType = pubType;
        this.pubId = pubId;
        this.disease = disease;

    }
}
