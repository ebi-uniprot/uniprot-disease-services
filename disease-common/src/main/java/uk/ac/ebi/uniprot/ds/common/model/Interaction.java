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
@Table(name = "ds_interaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Interaction extends BaseEntity {

    private static final long serialVersionUID = -553557262535945394L;
    @Column(name = "interaction_type")
    private String type;
    @Column
    private String accession;
    @Column
    private String gene;

    @Column(name = "experiment_count")
    private Integer experimentCount;

    @Column(name = "first_interactor")
    private String firstInteractor;

    @Column(name="second_interactor")
    private String secondInteractor;

    @ManyToOne
    @JoinColumn(name = "ds_protein_id")
    private Protein protein;
}
