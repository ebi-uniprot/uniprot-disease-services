/*
 * Created by sahmad on 23/01/19 10:16
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ds_pathway")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pathway extends BaseEntity {

    private static final long serialVersionUID = -5081945044878223589L;
    @Column(name = "primary_id")
    private String primaryId;

    @Column(name = "description")
    private String desc;

    @Column(name = "db_type")
    private String dbType;//TODO probably make an enum

    @Column(name = "isoform_id")
    private String isoformId;

    @Column
    private String third;

    @Column
    private String fourth;

    @ManyToOne
    @JoinColumn(name = "ds_protein_id")
    private Protein protein;
}
