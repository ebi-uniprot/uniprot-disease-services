/*
 * Created by sahmad on 23/01/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ds_synonym")
@Getter
@Setter
public class Synonym extends BaseEntity{
    private static final long serialVersionUID = -4832078587123331322L;

    @Column(name = "disease_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "ds_disease_id")
    private Disease disease;
}
