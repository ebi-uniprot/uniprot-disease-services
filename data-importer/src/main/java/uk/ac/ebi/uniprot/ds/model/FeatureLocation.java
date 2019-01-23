/*
 * Created by sahmad on 23/01/19 09:48
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ds_feature_location")
@Getter
@Setter
public class FeatureLocation extends BaseEntity {

    private static final long serialVersionUID = 4794420300156338467L;

    @Column(name = "start_modifier")
    private String startModifier;

    @Column(name = "end_modifier")
    private String endModifier;

    @Column(name = "start_id")
    private Integer startId;

    @Column(name ="end_id")
    private Integer endId;
}
