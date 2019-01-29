/*
 * Created by sahmad on 23/01/19 10:41
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ds_variant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Variant extends BaseEntity {

    private static final long serialVersionUID = 1901616724895902919L;
    @Column(name = "original_sequence")
    private String origSeq;

    @Column(name = "alternate_sequence")
    private String altSeq;

    @Column(name = "feature_id")
    private String featureId;

    @Column(name = "variant_report")
    private String report;

    @Column(name = "feature_status")
    private String featureStatus; //TODO make it enum

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ds_feature_location_id")
    private FeatureLocation featureLocation;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ds_evidence_id")
    private Evidence evidence;

    @OneToOne
    @JoinColumn(name = "ds_protein_id")
    private Protein protein;

    @OneToOne
    @JoinColumn(name = "ds_disease_id")
    private Disease disease;
}
