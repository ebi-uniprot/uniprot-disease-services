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
@Table(name = "ds_evidence")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidence extends BaseEntity {

    private static final long serialVersionUID = 110658268790740113L;

    @Column(name = "evidence_id")
    private String evidenceId;

    @Column(name = "evidence_type")
    private String type; // TODO make an enum

    @Column(name = "evidence_attribute")
    private String attribute;

    @Column(name = "evidence_code")
    private String code;

    @Column(name = "use_eco_code")
    private Boolean useECOCode;

    @Column(name = "type_value")
    private String typeValue;

    @Column(name = "has_type_value")
    private Boolean hasTypeValue;

    @ManyToOne
    @JoinColumn(name="ds_variant_id")
    private Variant variant;
}
