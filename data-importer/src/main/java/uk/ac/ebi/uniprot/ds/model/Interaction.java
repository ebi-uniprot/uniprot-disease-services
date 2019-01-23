/*
 * Created by sahmad on 23/01/19 09:52
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ds_interaction")
@Getter
@Setter
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
