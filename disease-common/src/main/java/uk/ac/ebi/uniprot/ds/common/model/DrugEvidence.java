/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ds_drug_evidence")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugEvidence extends BaseEntity {
    private static final long serialVersionUID = 110658268790740113L;

    @Column(name = "ref_type")
    private String refType;

    @Column(name = "ref_url")
    private String refUrl;

    @ManyToOne
    @JoinColumn(name="ds_drug_id")
    private Drug drug;
}
